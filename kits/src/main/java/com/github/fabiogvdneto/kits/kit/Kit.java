package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitLimitException;
import com.github.fabiogvdneto.kits.exception.PlayerInsufficientFundsException;
import com.github.fabiogvdneto.kits.repository.data.KitData;
import com.github.fabiogvdneto.kits.repository.data.KitRecipientData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.IntStream;

public class Kit {

    private final KitPlugin plugin;
    private final KitModule service;

    private final String name;

    private ItemStack[] contents;
    private double price;
    private Duration cooldown;
    private int redeemLimit;

    private final Map<UUID, KitRecipient> recipients;

    protected Kit(KitPlugin plugin, KitModule service, String name) {
        this.plugin = plugin;
        this.service = service;

        this.name = Objects.requireNonNull(name);
        this.contents = new ItemStack[0];

        this.cooldown = Duration.ZERO;
        this.redeemLimit = -1;
        this.price = 0.0;

        this.recipients = new HashMap<>();
    }

    protected Kit(KitPlugin plugin, KitModule service, KitData data) {
        this.plugin = plugin;
        this.service = service;

        this.name = data.name();

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
                map.put(data.uid(), new KitRecipient(this, data));
            } catch (DateTimeParseException e) { /* ignore */ }
        }

        return map;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = Objects.requireNonNull(contents);
        this.service.dirty(name);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0, price);
        this.service.dirty(name);
    }

    public Duration getCooldownDuration() {
        return cooldown;
    }

    public void setCooldownDuration(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown);
        this.service.dirty(name);
    }

    public int getRedeemLimit() {
        return redeemLimit;
    }

    public void setRedeemLimit(int redeemLimit) {
        this.redeemLimit = redeemLimit;
        this.service.dirty(name);
    }

    private KitRecipient createRecipient(UUID uid) {
        return recipients.computeIfAbsent(uid, key -> new KitRecipient(this, key));
    }

    private int[] findEmptySlots(Inventory target) {
        ItemStack[] storage = target.getStorageContents();

        return IntStream.range(0, storage.length)
                .filter(i -> storage[i] == null || storage[i].isEmpty())
                .limit(contents.length)
                .toArray();
    }

    private void checkPayment(Economy eco, Player player) throws PlayerInsufficientFundsException {
        if (eco != null && !eco.has(player, price))
            throw new PlayerInsufficientFundsException(price);
    }

    private void performPayment(Economy eco, Player player) {
        if (eco != null) { eco.withdrawPlayer(player, price); }
    }

    /**
     * Collect the kit immediately bypassing all requirements.
     * @param target where to add the content of the kit
     * @throws InventoryFullException if the inventory has not enough space
     */
    public void collect(Inventory target) throws InventoryFullException {
        int[] emptySlots = findEmptySlots(target);

        if (emptySlots.length < contents.length)
            throw new InventoryFullException(contents.length, emptySlots.length);

        for (int i = 0; i < contents.length; i++) {
            target.setItem(emptySlots[i], contents[i]);
        }
    }

    /**
     * Redeems the kit if all conditions are met:
     * 1. The player must not be on cooldown.
     * 2. The kit redeem limit must not be surpassed.
     * 3. The player must have enough funds to pay for the kit.
     * @param player who is redeeming the kit
     * @throws KitCooldownException if the cooldown is active
     * @throws KitLimitException if the kit redeem limit was reached
     * @throws PlayerInsufficientFundsException if the player has not enough money
     * @throws InventoryFullException if the player's inventory has not enough space
     */
    public void redeem(Player player) throws KitCooldownException, KitLimitException, PlayerInsufficientFundsException, InventoryFullException {
        Economy eco = plugin.getEconomy();
        KitRecipient recipient = createRecipient(player.getUniqueId());

        recipient.checkLimit(redeemLimit);
        recipient.checkCooldown();

        checkPayment(eco, player);
        collect(player.getInventory());
        performPayment(eco, player);

        recipient.increaseRedeemCount();
        recipient.applyCooldown(cooldown);

        service.dirty(name);
    }

    public boolean isRedeemable(UUID player) {
        try {
            KitRecipient recipient = recipients.get(player);

            if (recipient != null) {
                recipient.checkCooldown();
                recipient.checkLimit(redeemLimit);
            }

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /* ---- Serialization ---- */

    private String serializeContents() {
        byte[] nbt = ItemStack.serializeItemsAsBytes(contents);
        return Base64.getEncoder().encodeToString(nbt);
    }

    private List<KitRecipientData> serializeRecipients() {
        Instant now = Instant.now();

        return recipients.values().stream()
                .filter(r -> (redeemLimit >= 0 && r.getRedeemCount() > 0) || r.getNextRedeemTime().isAfter(now))
                .map(KitRecipient::memento)
                .toList();
    }

    protected KitData memento() {
        return new KitData(name, price, cooldown.toMinutes(), redeemLimit, serializeContents(), serializeRecipients());
    }
}
