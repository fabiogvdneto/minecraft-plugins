package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.*;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import com.github.fabiogvdneto.kits.repository.data.KitRecipientData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.IntStream;

class KitImpl implements Kit {

    private final KitPlugin plugin;
    private final KitModule service;

    private final String id;

    private ItemStack[] contents;
    private double price;
    private Duration cooldown;
    private int redeemLimit;

    private final Map<UUID, KitRecipient> recipients;

    KitImpl(KitPlugin plugin, KitModule service, String id) {
        this.plugin = plugin;
        this.service = service;

        this.id = Objects.requireNonNull(id);
        this.contents = new ItemStack[0];

        this.cooldown = Duration.ZERO;
        this.redeemLimit = -1;
        this.price = 0.0;

        this.recipients = new HashMap<>();
    }

    KitImpl(KitPlugin plugin, KitModule service, KitData data) {
        this.plugin = plugin;
        this.service = service;

        this.id = data.id();

        this.cooldown = Duration.ofMinutes(data.cooldownMinutes());
        this.redeemLimit = data.redeemLimit();
        this.price = data.price();

        this.contents = deserializeContents(data.contents());
        this.recipients = deserializeRecipients(data.recipients());
    }

    private ItemStack[] deserializeContents(String contents) {
        byte[] bytes = Base64.getDecoder().decode(contents);
        return ItemStack.deserializeItemsFromBytes(bytes);
    }

    private Map<UUID, KitRecipient> deserializeRecipients(List<KitRecipientData> recipients) {
        Map<UUID, KitRecipient> map = new HashMap<>();

        for (KitRecipientData data : recipients) {
            try {
                map.put(data.uid(), new KitRecipientImpl(this, data));
            } catch (DateTimeParseException e) { /* ignore */ }
        }

        return map;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = Math.max(0, price);
        this.service.dirty(id);
    }

    @Override
    public Duration getCooldownDuration() {
        return cooldown;
    }

    @Override
    public void setCooldownDuration(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown);
        this.service.dirty(id);
    }

    @Override
    public int getRedeemLimit() {
        return redeemLimit;
    }

    @Override
    public void setRedeemLimit(int redeemLimit) {
        this.redeemLimit = redeemLimit;
        this.service.dirty(id);
    }

    @Override
    public ItemStack[] getContents() {
        return contents;
    }

    @Override
    public void setContents(ItemStack[] contents) {
        this.contents = Objects.requireNonNull(contents);
        this.service.dirty(id);
    }

    private KitRecipient createRecipient(UUID uid) {
        return recipients.computeIfAbsent(uid, key -> new KitRecipientImpl(this, key));
    }

    @Override
    public KitRecipient getRecipient(UUID uid) throws KitRecipientNotFoundException {
        KitRecipient recipient = recipients.get(uid);

        if (recipient == null)
            throw new KitRecipientNotFoundException(uid);

        return recipient;
    }

    private int[] findEmptySlots(Inventory target) {
        ItemStack[] storage = target.getStorageContents();

        return IntStream.range(0, storage.length)
                .filter(i -> storage[i] == null || storage[i].isEmpty())
                .limit(contents.length)
                .toArray();
    }

    private void performPayment(Player player) throws PlayerInsufficientFundsException {
        if (!plugin.getEconomy().withdrawPlayer(player, price).transactionSuccess())
            throw new PlayerInsufficientFundsException(price);
    }

    @Override
    public void collect(Inventory target) throws InventoryFullException {
        int[] emptySlots = findEmptySlots(target);

        if (emptySlots.length < contents.length)
            throw new InventoryFullException(contents.length, emptySlots.length);

        for (int i = 0; i < contents.length; i++) {
            target.setItem(emptySlots[i], contents[i]);
        }
    }

    @Override
    public void redeem(Player player) throws KitCooldownException, KitLimitException, InventoryFullException {
        if (player.hasPermission(plugin.getSettings().getAdminPermission())) {
            collect(player.getInventory());
            return;
        }

        KitRecipientImpl recipient = (KitRecipientImpl) createRecipient(player.getUniqueId());

        recipient.checkCooldown();
        recipient.checkLimit(redeemLimit);

        performPayment(player);
        collect(player.getInventory());
        recipient.applyCooldown(cooldown);
        recipient.increaseRedeemCount();

        service.dirty(id);
    }

    /* ---- Serialization ---- */

    private String serializeContents() {
        byte[] nbt = ItemStack.serializeItemsAsBytes(contents);
        return Base64.getEncoder().encodeToString(nbt);
    }

    private List<KitRecipientData> serializeRecipients() {
        return recipients.values().stream()
                .filter(r -> r.getNextRedeemTime().isAfter(Instant.now()))
                .map(r -> ((KitRecipientImpl) r).memento())
                .toList();
    }

    public KitData memento() {
        return new KitData(id, price, cooldown.toMinutes(), redeemLimit, serializeContents(), serializeRecipients());
    }
}
