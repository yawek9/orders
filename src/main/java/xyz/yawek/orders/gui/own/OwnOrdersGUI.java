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

package xyz.yawek.orders.gui.own;

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

public final class OwnOrdersGUI extends ClickableGUI {

    private final Orders plugin;
    private SortingMode sortingMode = SortingMode.NEWEST_TO_OLDEST;
    private OrderStatus orderStatus = OrderStatus.ACTIVE;

    public OwnOrdersGUI(Orders plugin, Player player) {
        super(player);
        this.plugin = plugin;
    }

    private void loadButtons() {
        int pageCount = pagesCount();
        IntStream.range(0, pageCount).forEach(i -> {
            this.setInventoryIfNotExists(i, Bukkit.createInventory(
                    null, 54, plugin.getPluginConfig().ownOrdersGUITitle()));
            this.setContentIfPageExists(i, 45, returnButton());
            this.addAction(i, 45, () -> plugin.getOrdersGUIManager()
                    .openGUI(this.getPlayer()));
            this.setContentIfPageExists(i, 49, sortingButton());
            this.addAction(i, 49, () -> sortingMode
                    = SortingMode.next(sortingMode));
            this.setContentIfPageExists(i, 53, statusButton());
            this.addAction(i, 53, () -> orderStatus
                    = OrderStatus.next(orderStatus));
            if (i > 0) {
                this.setContentIfPageExists(i, 48, previousPageButton());
                this.addAction(i, 48, () -> this.open(getOpenedPage() - 1));
            }
            if (i != pageCount - 1) {
                this.setContentIfPageExists(i, 50, nextPageButton());
                this.addAction(i, 50, () -> this.open(getOpenedPage() + 1));
            }
        });
    }

    private void loadOrders() {
        removeAllOrders();

        List<Order> orders = plugin.getOrderManager()
                .getOrdersByStatusAndCreator(orderStatus, this.getPlayer());
        OrderManager.sortOrders(orders, sortingMode);
        if (orders.size() == 0) return;
        int pageIndex = 0;
        int loadedOrders = 0;
        for (int i = 10; i <= 43; i++) {
            if (i == 17 || i == 26 || i == 35) i+= 2;

            Order order = orders.get(loadedOrders);
            this.setContentIfPageExists(pageIndex, i, orderButtonItem(order));
            this.addAction(pageIndex, i, true, () ->
                    plugin.getActionHandler().cancelOrder(order));
            this.addAction(pageIndex, i, true,
                    orderStatus == OrderStatus.ACTIVE ?
                            () -> plugin.getActionHandler().cancelOrder(order) :
                            () -> plugin.getActionHandler().receiveItems(order));

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
                .getOrdersByStatusAndCreator(orderStatus, this.getPlayer()).size();
        if (orderCount == 0) return 1;
        return orderCount % 28 == 0 ? orderCount / 28
                : (int) Math.round(orderCount / 28.0 + 0.5);
    }

    @SuppressWarnings("ConstantConditions")
    private ItemStack orderButtonItem(Order order) {
        ItemStack itemStack = order.getItemStack();
        List<Component> lore = new ArrayList<>();
        if (itemStack.lore() != null) lore.addAll(itemStack.lore());
        String contractorName = null;
        if (order.getContractorUUID().isPresent()) {
            contractorName = Bukkit.getOfflinePlayer(order.getCreatorUUID()).getName();
        }
        lore.addAll(plugin.getPluginConfig().orderItemLore(
                order.getAmount(),
                order.getPayment(),
                Bukkit.getOfflinePlayer(order.getCreatorUUID()).getName(),
                contractorName,
                order.getExpirationTimestamp(),
                false, orderStatus == OrderStatus.ACTIVE));
        ItemStack outputItem = itemStack.clone();
        outputItem.lore(lore);
        return outputItem;
    }

    private ItemStack returnButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().returnButtonItem(),
                plugin.getPluginConfig().returnButtonDisplayName(),
                plugin.getPluginConfig().returnButtonLore());
    }

    private ItemStack sortingButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().sortingButtonItem(),
                plugin.getPluginConfig().sortingButtonDisplayName(),
                plugin.getPluginConfig().sortingButtonLore(sortingMode));
    }

    private ItemStack statusButton() {
        return InventoryUtils.inventoryItem(plugin.getPluginConfig().statusButtonItem(),
                plugin.getPluginConfig().statusButtonDisplayName(),
                plugin.getPluginConfig().statusButtonLore(orderStatus));
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
