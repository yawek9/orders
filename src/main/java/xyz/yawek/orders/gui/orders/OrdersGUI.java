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

package xyz.yawek.orders.gui.orders;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.gui.ClickableGUI;
import xyz.yawek.orders.gui.SortingMode;
import xyz.yawek.orders.manager.OrderManager;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.order.OrderStatus;
import xyz.yawek.orders.util.InventoryUtils;
import xyz.yawek.orders.util.ItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public final class OrdersGUI extends ClickableGUI {

    private final Orders plugin;
    private SortingMode sortingMode = SortingMode.NEWEST_TO_OLDEST;

    public OrdersGUI(Orders plugin, Player player) {
        super(player);
        this.plugin = plugin;
    }

    private void loadButtons() {
        int pageCount = pagesCount();
        IntStream.range(0, pageCount).forEach(i -> {
            setInventoryIfNotExists(i, Bukkit.createInventory(
                    null, 54, plugin.getPluginConfig().ordersGUITitle()));
            setContentIfPageExists(i, 45, ownButton());
            addAction(i, 45, () -> plugin.getOwnOrdersGUIManager()
                    .openGUI(getPlayer()));
            setContentIfPageExists(i, 49, sortingButton());
            addAction(i, 49, () -> sortingMode
                    = SortingMode.next(sortingMode));
            setContentIfPageExists(i, 53, infoButton());
            if (i > 0) {
                setContentIfPageExists(i, 48, previousPageButton());
                addAction(i, 48, () -> open(getOpenedPage() - 1));
            }
            if (i != pageCount - 1) {
                setContentIfPageExists(i, 50, nextPageButton());
                addAction(i, 50, () -> open(getOpenedPage() + 1));
            }
        });
    }

    private void loadOrders() {
        removeAllOrders();

        List<Order> orders = plugin.getOrderManager()
                .getOrdersByStatus(OrderStatus.ACTIVE);
        OrderManager.sortOrders(orders, sortingMode);
        if (orders.size() == 0) return;
        int pageIndex = 0;
        int loadedOrders = 0;
        for (int i = 10; i <= 43; i++) {
            if (i == 17 || i == 26 || i == 35) i+= 2;

            Order order = orders.get(loadedOrders);
            this.setContentIfPageExists(pageIndex, i,
                    orderButtonItem(order));
            this.addAction(pageIndex, i, () -> plugin.getCompletionGUIManager()
                    .openGUI(getPlayer(), order));
            loadedOrders++;

            if (loadedOrders % 28 == 0) pageIndex++;
            if (i == 43) i = 9;
            if (loadedOrders >= orders.size()) return;
        }
    }

    private void removeAllOrders() {
        IntStream.rangeClosed(0, pagesCount()).forEach(i -> {
            for (int j = 10; j <= 43; j++) {
                if (j == 17 || j == 26 || j == 35) j += 2;

                this.setContentIfPageExists(i, j, null);
            }
        });
    }

    private void fillEmptySlots() {
        IntStream.range(0, pagesCount()).forEach(i ->
                this.getInventoryAtIndex(i).ifPresent(inventory -> InventoryUtils
                        .fillEmptySlots(inventory, 54, emptyItem())));
    }

    private int pagesCount() {
        int orderCount = plugin.getOrderManager()
                .getOrdersByStatus(OrderStatus.ACTIVE).size();
        if (orderCount == 0) return 1;
        return orderCount % 28 == 0 ? orderCount / 28
                : (int) Math.round(orderCount / 28.0 + 0.5);
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStack orderButtonItem(Order order) {
        ItemStack itemStack = order.getItemStack();
        List<Component> lore = new ArrayList<>();
        if (itemStack.lore() != null) lore.addAll(itemStack.lore());
        lore.addAll(plugin.getPluginConfig().orderItemLore(
                order.getAmount(),
                order.getPayment(),
                Bukkit.getOfflinePlayer(order.getCreatorUUID()).getName(),
                null,
                order.getExpirationTimestamp(),
                true, true));
        ItemStack outputItem = itemStack.clone();
        outputItem.lore(lore);
        return outputItem;
    }

    private ItemStack ownButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().ownButtonItem(),
                plugin.getPluginConfig().ownButtonDisplayName(),
                plugin.getPluginConfig().ownButtonLore());
    }

    private ItemStack sortingButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().sortingButtonItem(),
                plugin.getPluginConfig().sortingButtonDisplayName(),
                plugin.getPluginConfig().sortingButtonLore(sortingMode));
    }

    private ItemStack infoButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().infoButtonItem(),
                plugin.getPluginConfig().infoButtonDisplayName(),
                plugin.getPluginConfig().infoButtonLore());
    }

    private ItemStack previousPageButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().previousPageButtonItem(),
                plugin.getPluginConfig().previousPageButtonDisplayName(),
                plugin.getPluginConfig().previousPageButtonLore());
    }

    private ItemStack nextPageButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().nextPageButtonItem(),
                plugin.getPluginConfig().nextPageButtonDisplayName(),
                plugin.getPluginConfig().nextPageButtonLore());
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStack emptyItem() {
        return ItemUtils.applyMeta(new ItemStack(
                Material.matchMaterial(plugin.getPluginConfig().emptyItem())),
                Component.text(""),
                Collections.emptyList(), true);
    }

    @Override
    public void open(int index) {
        loadButtons();
        loadOrders();
        fillEmptySlots();
        super.open(index);
    }

    @Override
    public boolean runActionAttempt(int slot, boolean withShiftPressed,
                                    Inventory inventory) {
        if (super.runActionAttempt(slot, withShiftPressed, inventory)) {
            loadGUIIfOpened();
            return true;
        }
        return false;
    }

    @Override
    public void loadGUI() {
        this.clearActions();
        loadButtons();
        loadOrders();
        fillEmptySlots();
    }

}
