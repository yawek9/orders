/*
 * This file is part of Orders, licensed under GNU GPLv3 license.
 * Copyright (C) 2022 yawek9
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yawek.orders.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.util.InventoryUtils;
import xyz.yawek.orders.util.ItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class CompletionGUI extends ClickableGUI {

    private final Orders plugin;
    private final Order order;

    public CompletionGUI(Orders plugin, Player player, Order order) {
        super(player);
        this.plugin = plugin;
        this.order = order;

        loadGUI();
    }

    private ItemStack returnButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().returnButtonItem(),
                plugin.getPluginConfig().returnButtonDisplayName(),
                plugin.getPluginConfig().returnButtonLore());
    }

    private ItemStack completeButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().completeButtonItem(),
                plugin.getPluginConfig().completeButtonDisplayName(),
                plugin.getPluginConfig().completeButtonLore());
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStack orderItem() {
        ItemStack itemStack = order.getItemStack();
        List<Component> lore = new ArrayList<>();
        if (itemStack.lore() != null) lore.addAll(itemStack.lore());
        lore.addAll(plugin.getPluginConfig().orderItemLore(
                order.getAmount(),
                order.getPayment(),
                Bukkit.getOfflinePlayer(order.getCreatorUUID()).getName(),
                null,
                order.getExpirationTimestamp(),
                true, false));
        ItemStack outputItem = itemStack.clone();
        outputItem.lore(lore);
        return outputItem;
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStack emptyItem() {
        return ItemUtils.applyMeta(new ItemStack(
                        Material.matchMaterial(plugin.getPluginConfig().emptyItem())),
                Component.text(""),
                Collections.emptyList(), true);
    }

    @Override
    public void loadGUI() {
        this.setInventoryIfNotExists(0, Bukkit.createInventory(
                null, 27, plugin.getPluginConfig().completionGUITitle()));
        this.setContentIfPageExists(0, 21, returnButton());
        this.addAction(0, 21, () -> plugin.getOrdersGUIManager()
                .openGUI(this.getPlayer()));
        this.setContentIfPageExists(0, 22, orderItem());
        this.setContentIfPageExists(0, 23, completeButton());
        this.addAction(0, 23, () -> {
            List<ItemStack> itemStacks = new ArrayList<>();
            IntStream.rangeClosed(10, 16).forEach(i -> {
                this.getItemAtIndex(0, i).ifPresent(itemStacks::add);
                this.setContentIfPageExists(0, i, null);
            });
            plugin.getActionHandler().completeOrder(getPlayer(), order, itemStacks);
        });
        this.getInventoryAtIndex(0).ifPresent(inventory ->
                InventoryUtils.fillEmptySlots(inventory, 27, emptyItem()));
        IntStream.rangeClosed(10, 16).forEach(i -> {
            this.setContentIfPageExists(0, i, null);
            this.addCancelClickExemptions(i);
        });
    }

}
