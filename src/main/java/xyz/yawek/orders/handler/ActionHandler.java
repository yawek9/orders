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

package xyz.yawek.orders.handler;

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.config.Config;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.order.OrderStatus;
import xyz.yawek.orders.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

public class ActionHandler {

    private final Orders plugin;

    public ActionHandler(Orders plugin) {
        this.plugin = plugin;
    }

    public void completeOrder(Player player, Order order, List<ItemStack> itemStacks) {
        Config config = plugin.getPluginConfig();
        if (order.getStatus().equals(OrderStatus.COMPLETED)) {
            player.sendMessage(config.orderAlreadyCompleted());
            player.closeInventory();
            return;
        }
        int amount = 0;
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isSimilar(order.getItemStack())) {
                if (itemStacks.size() > 0) {
                    InventoryUtils.returnItemsOrDrop(player,
                            itemStacks.toArray(new ItemStack[0]));
                }
                player.sendMessage(config.wrongItemsPut());
                player.closeInventory();
                return;
            }
            amount += itemStack.getAmount();
        }
        if (amount != order.getAmount()) {
            if (itemStacks.size() > 0) {
                InventoryUtils.returnItemsOrDrop(player,
                        itemStacks.toArray(new ItemStack[0]));
            }
            player.sendMessage(config.wrongItemsPut());
            player.closeInventory();
            return;
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setContractorUUID(player.getUniqueId());
        plugin.getOrderManager().updateOrder(order);
        plugin.getEconomy().depositPlayer(player, order.getPayment());

        PaperComponents.flattener().flatten(
                Component.translatable(order.getItemStack().translationKey()),
                itemName -> player.sendMessage(
                        config.orderCompleted(
                                Bukkit.getOfflinePlayer(order.getCreatorUUID()).getName(),
                                order.getAmount(),
                                itemName,
                                order.getPayment())));
        player.closeInventory();
    }

    public void cancelOrder(Order order) {
        if (order.getStatus() == OrderStatus.COMPLETED) {
            return;
        }
        Player player = Bukkit.getPlayer(order.getCreatorUUID());
        plugin.getOrderManager().removeOrder(order);

        if (player == null) return;
        plugin.getEconomy().depositPlayer(player, order.getPayment());
        PaperComponents.flattener().flatten(
                Component.translatable(order.getItemStack().translationKey()),
                itemName -> player.sendMessage(
                        plugin.getPluginConfig().orderCancelled(
                                order.getAmount(),
                                itemName)));
    }

    public void receiveItems(Order order) {
        if (order.getStatus() == OrderStatus.ACTIVE) {
            return;
        }
        Player player = Bukkit.getPlayer(order.getCreatorUUID());
        if (player == null) return;
        List<ItemStack> itemStacks = new ArrayList<>();
        int amount = order.getAmount();
        while (amount != 0) {
            ItemStack itemStack = order.getItemStack().clone();
            if (amount >= itemStack.getMaxStackSize()) {
                itemStack.setAmount(itemStack.getMaxStackSize());
                amount -= itemStack.getMaxStackSize();
            } else {
                itemStack.setAmount(amount);
                amount = 0;
            }
            itemStacks.add(itemStack);
        }
        plugin.getOrderManager().removeOrder(order);
        InventoryUtils.returnItemsOrDrop(player, itemStacks.toArray(new ItemStack[0]));
        PaperComponents.flattener().flatten(
                Component.translatable(order.getItemStack().translationKey()),
                itemName -> player.sendMessage(
                        plugin.getPluginConfig().itemsReceived(
                                order.getAmount(),
                                itemName)));
    }

}
