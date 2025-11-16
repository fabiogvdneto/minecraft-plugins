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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

class SimpleKit implements Kit {

    private final KitPlugin plugin;
    private final KitService service;

    private final Map<UUID, Instant> cooldownMap;
    private final String name;
    private Duration cooldown;
    private long price;
    private ItemStack[] contents;

    SimpleKit(KitPlugin plugin, KitService service, String name) {
        this.plugin = plugin;
        this.service = service;

        this.name = Objects.requireNonNull(name);
        this.cooldown = Duration.ZERO;
        this.contents = new ItemStack[0];
        this.cooldownMap = new HashMap<>();
    }

    SimpleKit(KitPlugin plugin, KitService service, KitData data) {
        this.plugin = plugin;
        this.service = service;

        this.name = data.name();
        this.cooldown = data.cooldown();
        this.price = data.price();
        this.contents = ItemStack.deserializeItemsFromBytes(data.contents());
        this.cooldownMap = new HashMap<>(data.availability());

        purgeCooldown();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack[] getContents() {
        return this.contents;
    }

    @Override
    public void setContents(ItemStack[] contents) {
        this.contents = Objects.requireNonNull(contents);
        this.service.dirty(this.name);
    }

    @Override
    public long getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(long price) {
        this.price = Math.max(0, price);
        this.service.dirty(this.name);
    }

    @Override
    public Duration getCooldownDuration() {
        return this.cooldown;
    }

    @Override
    public void setCooldownDuration(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown);
        this.service.dirty(this.name);
    }

    @Override
    public Instant probeCooldown(UUID recipient) {
        return this.cooldownMap.computeIfPresent(recipient,
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
        this.cooldownMap.put(recipient, Instant.now().plus(this.cooldown));
        this.service.dirty(this.name);
    }

    @Override
    public void clearCooldown(UUID recipient) {
        this.cooldownMap.remove(recipient);
        this.service.dirty(this.name);
    }

    @Override
    public void collect(Inventory recipient) throws InventoryFullException {
        int[] freeSlots = findFreeSlots(recipient);

        if (freeSlots.length < this.contents.length)
            throw new InventoryFullException(this.contents.length, freeSlots.length);

        for (int i = 0; i < this.contents.length; i++) {
            recipient.setItem(freeSlots[i], this.contents[i]);
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
        this.cooldownMap.values().removeIf(Instant.now()::isAfter);
    }

    public KitData memento() {
        purgeCooldown();
        byte[] contentsNBT = ItemStack.serializeItemsAsBytes(this.contents);
        return new KitData(this.name, this.cooldown, this.price, contentsNBT, Map.copyOf(this.cooldownMap));
    }
}
