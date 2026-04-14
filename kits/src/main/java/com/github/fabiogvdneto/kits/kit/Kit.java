package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import com.github.fabiogvdneto.kits.exception.KitRecipientNotFoundException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.UUID;

public interface Kit {

    /**
     *
     * @return the string that identifies this kit.
     */
    String getID();

    /**
     *
     * @return the contents of this kit.
     */
    ItemStack[] getContents();

    /**
     *
     * @param contents new contents of this kit.
     */
    void setContents(ItemStack[] contents);

    /**
     *
     * @return the price of this kit.
     */
    long getPrice();

    /**
     *
     * @param price new price of this kit.
     */
    void setPrice(long price);

    /**
     *
     * @return the cooldown duration of this kit.
     */
    Duration getCooldownDuration();

    /**
     *
     * @param cooldown new cooldown duration of this kit.
     */
    void setCooldownDuration(Duration cooldown);

    /**
     *
     * @param uid the unique id of the recipient.
     * @return
     */
    KitRecipient getRecipient(UUID uid) throws KitRecipientNotFoundException;

    /**
     * Collect the kit immediately bypassing all requirements.
     * @param target where to add the content of the kit
     * @throws InventoryFullException if the inventory has not enough space
     */
    void collect(Inventory target) throws InventoryFullException;

    /**
     * Redeem the kit.
     * @param recipient who is redeeming the kit
     * @throws KitCooldownException if the cooldown is active
     * @throws InventoryFullException if the player's inventory has not enough space
     */
    void redeem(Player recipient) throws KitCooldownException, InventoryFullException;

}
