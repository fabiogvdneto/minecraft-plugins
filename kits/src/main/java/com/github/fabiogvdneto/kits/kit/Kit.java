package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface Kit {

    String getName();

    ItemStack[] getContents();

    void setContents(ItemStack[] contents);

    long getPrice();

    void setPrice(long price);

    Duration getCooldownDuration();

    void setCooldownDuration(Duration cooldown);

    /**
     * Returns the instant when the cooldown will end, or null if the cooldown already ended.
     * @param recipient player requesting the kit
     * @return cooldown for that player
     */
    Instant probeCooldown(UUID recipient);

    /**
     * Throws an exception if cooldown is still active.
     * @param recipient player requesting the kit
     * @throws KitCooldownException if cooldown is still active
     */
    void checkCooldown(UUID recipient) throws KitCooldownException;

    /**
     * Applies the cooldown specified by cooldown duration
     * @param recipient cooldown owner
     */
    void applyCooldown(UUID recipient);

    /**
     * Clears the cooldown of the given player.
     * @param recipient cooldown owner
     */
    void clearCooldown(UUID recipient);

    /**
     * Collect the kit immediately without waiting for the cooldown to finish.
     * @param target where to add the content of the kit
     * @throws InventoryFullException if the inventory has not enough space
     */
    void collect(Inventory target) throws InventoryFullException;

    /**
     * Redeem the kit if no cooldown is set.
     * @param recipient who is redeeming the kit
     * @throws KitCooldownException if the cooldown is active
     * @throws InventoryFullException if the player's inventory has not enough space
     */
    void redeem(Player recipient) throws KitCooldownException, InventoryFullException;

}
