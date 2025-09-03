package com.github.fabiogvdneto.kits.kit;

import com.github.fabiogvdneto.kits.exception.InventoryFullException;
import com.github.fabiogvdneto.kits.exception.KitCooldownException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public interface Kit {

    String getName();

    Duration getCooldown();

    long getPrice();

    ItemStack[] getContents();

    void setCooldown(Duration cooldown);

    void setPrice(long price);

    void setContents(ItemStack[] contents);

    void collect(Inventory target) throws InventoryFullException;

    void redeemNow(Player recipient) throws InventoryFullException;

    void redeem(Player recipient) throws KitCooldownException, InventoryFullException;

}
