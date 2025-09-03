package com.github.fabiogvdneto.kits.kit;

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

    private final Map<UUID, Instant> cooldownMap;
    private final String name;
    private Duration cooldown;
    private long price;
    private ItemStack[] contents;

    public SimpleKit(String name) {
        this.name = Objects.requireNonNull(name);
        this.cooldown = Duration.ZERO;
        this.contents = new ItemStack[0];
        this.cooldownMap = new HashMap<>();
    }

    public SimpleKit(KitData data) {
        this.name = data.name();
        this.cooldown = data.cooldown();
        this.price = data.price();
        this.contents = ItemStack.deserializeItemsFromBytes(data.contents());
        this.cooldownMap = new HashMap<>(data.availability());

        purgeCooldown();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Duration getCooldown() {
        return cooldown;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public ItemStack[] getContents() {
        return contents;
    }

    @Override
    public void setCooldown(Duration cooldown) {
        this.cooldown = Objects.requireNonNull(cooldown);
    }

    @Override
    public void setPrice(long price) {
        this.price = Math.max(0, price);
    }

    @Override
    public void setContents(ItemStack[] contents) {
        this.contents = Objects.requireNonNull(contents);
    }

    private void applyCooldown(UUID recipient) {
        cooldownMap.put(recipient, Instant.now().plus(cooldown));
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
    public void redeemNow(Player recipient) throws InventoryFullException {
        collect(recipient.getInventory());
        applyCooldown(recipient.getUniqueId());
    }

    @Override
    public void redeem(Player recipient) throws KitCooldownException, InventoryFullException {
        Instant endOfCooldown = cooldownMap.computeIfPresent(recipient.getUniqueId(),
                // Remove cooldown if it has already ended.
                (key, value) -> Instant.now().isBefore(value) ? value : null);

        if (endOfCooldown != null)
            throw new KitCooldownException(endOfCooldown);

        redeemNow(recipient);
    }

    private int[] findFreeSlots(Inventory inv) {
        ItemStack[] storage = inv.getStorageContents();

        return IntStream.range(0, storage.length)
                .filter(i -> storage[i] == null || storage[i].isEmpty())
                .limit(contents.length)
                .toArray();
    }

    public void purgeCooldown() {
        cooldownMap.values().removeIf(Instant.now()::isAfter);
    }

    public KitData memento() {
        purgeCooldown();
        byte[] contentsNBT = ItemStack.serializeItemsAsBytes(this.contents);
        return new KitData(name, cooldown, price, contentsNBT, Map.copyOf(cooldownMap));
    }
}
