package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.KitPlugin;
import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.repository.data.KitData;
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

    private final String name;
    private Duration cooldown;
    private long price;
    private ItemStack[] contents;

    private final Map<UUID, Instant> recipients;

    KitImpl(KitPlugin plugin, KitModule service, String name) {
        this.plugin = plugin;
        this.service = service;

        this.name = Objects.requireNonNull(name);
        this.cooldown = Duration.ZERO;
        this.contents = new ItemStack[0];
        this.recipients = new HashMap<>();
    }

    KitImpl(KitPlugin plugin, KitModule service, KitData data) {
        this.plugin = plugin;
        this.service = service;

        this.name = data.name();
        this.cooldown = Duration.ofMinutes(data.cooldownMinutes());
        this.price = data.price();
        this.contents = deserializeContents(data.contents());
        this.recipients = deserializeRecipients(data.recipients());

        purgeCooldown();
    }

    private ItemStack[] deserializeContents(String contents) {
        byte[] bytes = Base64.getDecoder().decode(contents);
        return ItemStack.deserializeItemsFromBytes(bytes);
    }

    private Map<UUID, Instant> deserializeRecipients(Map<UUID, String> recipients) {
        Map<UUID, Instant> map = new HashMap<>();

        for (Map.Entry<UUID, String> entry : recipients.entrySet()) {
            try {
                map.put(entry.getKey(), Instant.parse(entry.getValue()));
            } catch (DateTimeParseException e) { /* ignore */ }
        }

        return map;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack[] getContents() {
        return contents;
    }

    @Override
    public void setContents(ItemStack[] contents) {
        this.contents = Objects.requireNonNull(contents);
        this.service.dirty(name);
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public void setPrice(long price) {
        this.price = Math.max(0, price);
        this.service.dirty(name);
    }

    @Override
    public Duration getCooldownDuration() {
        return cooldown;
    }

    @Override
    public void setCooldownDuration(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown);
        this.service.dirty(name);
    }

    @Override
    public Instant probeCooldown(UUID recipient) {
        return recipients.computeIfPresent(recipient,
                // Remove cooldown if it has already ended.
                (key, value) -> Instant.now().isBefore(value) ? value : null);
    }

    @Override
    public void checkCooldown(UUID recipient) throws KitCooldownException {
        Instant endOfCooldown = probeCooldown(recipient);

        if (endOfCooldown != null)
            throw new KitCooldownException(endOfCooldown);
    }

    @Override
    public void applyCooldown(UUID recipient) {
        recipients.put(recipient, Instant.now().plus(cooldown));
        service.dirty(name);
    }

    @Override
    public void clearCooldown(UUID recipient) {
        recipients.remove(recipient);
        service.dirty(name);
    }

    @Override
    public void collect(Inventory recipient) throws InventoryFullException {
        int[] freeSlots = findFreeSlots(recipient);

        if (freeSlots.length < contents.length)
            throw new InventoryFullException(contents.length, freeSlots.length);

        for (int i = 0; i < contents.length; i++) {
            recipient.setItem(freeSlots[i], contents[i]);
        }
    }

    @Override
    public void redeem(Player recipient) throws KitCooldownException, InventoryFullException {
        checkCooldown(recipient.getUniqueId());
        collect(recipient.getInventory());
        applyCooldown(recipient.getUniqueId());
    }

    private int[] findFreeSlots(Inventory inv) {
        ItemStack[] storage = inv.getStorageContents();

        return IntStream.range(0, storage.length)
                .filter(i -> storage[i] == null || storage[i].isEmpty())
                .limit(contents.length)
                .toArray();
    }

    public void purgeCooldown() {
        this.recipients.values().removeIf(Instant.now()::isAfter);
    }

    /* ---- Serialization ---- */

    private String serializeContents() {
        byte[] nbt = ItemStack.serializeItemsAsBytes(contents);
        return Base64.getEncoder().encodeToString(nbt);
    }

    private Map<UUID, String> serializeRecipients() {
        Map<UUID, String> serializedRecipients = new HashMap<>();

        for (Map.Entry<UUID, Instant> entry : recipients.entrySet()) {
            serializedRecipients.put(entry.getKey(), entry.getValue().toString());
        }

        return serializedRecipients;
    }

    public KitData memento() {
        purgeCooldown();
        return new KitData(name, cooldown.toMinutes(), price, serializeContents(), serializeRecipients());
    }
}
