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

package xyz.yawek.orders.manager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.gui.SortingMode;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.order.OrderStatus;

import java.util.List;
import java.util.UUID;

public class OrderManager {

    private final Orders plugin;

    public OrderManager(Orders plugin) {
        this.plugin = plugin;
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        removeExpiredOrders();
        return plugin.getDataProvider().getOrdersByStatus(status);
    }

    public List<Order> getOrdersByStatusAndCreator(OrderStatus status, Player creator) {
        removeExpiredOrders();
        return plugin.getDataProvider()
                .getOrdersByStatusAndCreator(status, creator);
    }

    public void addOrder(ItemStack itemStack, int amount,
                         double payment, UUID creatorUUID) {
        itemStack = itemStack.clone();
        itemStack.setAmount(1);
        Order order = new Order(
                itemStack,
                amount,
                payment,
                System.currentTimeMillis(),
                System.currentTimeMillis()
                        + plugin.getPluginConfig().expirationTimestamp() * 1000,
                creatorUUID,
                OrderStatus.ACTIVE);
        plugin.getDataProvider().saveOrder(order);
    }

    public void updateOrder(Order order) {
        plugin.getDataProvider().saveOrder(order);
    }

    public void removeOrder(Order order) {
        plugin.getDataProvider().removeOrder(order);
    }

    public boolean isItemCorrect(ItemStack itemStack) {
        return !plugin.getPluginConfig().blacklistedItems()
                .contains(itemStack.clone().getType());
    }

    public boolean isAmountCorrect(String amountString, int maxStackSize) {
        int amount;
        try {
            amount = Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            return false;
        }
        return amount > 0 && amount <= maxStackSize * 7;
    }

    public boolean isPaymentCorrect(String paymentString) {
        double payment;
        try {
            payment = Double.parseDouble(paymentString);
        } catch (NumberFormatException e) {
            return false;
        }
        return payment > 0 && payment <= plugin.getPluginConfig().maxPayment()
                && payment >= plugin.getPluginConfig().minPayment();
    }

    public static void sortOrders(List<Order> input, SortingMode sortingMode) {
        input.sort((o1, o2) -> switch (sortingMode) {
            case OLDEST_TO_NEWEST -> Long.compare(o2.getCreationTimestamp(),
                    o1.getCreationTimestamp());
            case LOWEST_TO_HIGHEST_PAYMENT -> Double.compare(o1.getPayment(),
                    o2.getPayment());
            case HIGHEST_TO_LOWEST_PAYMENT -> Double.compare(o2.getPayment(),
                    o1.getPayment());
            default -> Long.compare(o1.getCreationTimestamp(),
                    o2.getCreationTimestamp());
        });
    }

    private void removeExpiredOrders() {
        plugin.getDataProvider().getOrdersByStatus(OrderStatus.ACTIVE)
                .stream()
                .filter(order -> order.getExpirationTimestamp()
                        <= System.currentTimeMillis())
                .forEach(this::removeOrder);
    }

}
