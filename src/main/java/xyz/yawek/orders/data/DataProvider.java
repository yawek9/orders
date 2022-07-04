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

package xyz.yawek.orders.data;

import org.bukkit.entity.Player;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.order.OrderStatus;

import java.util.List;

public class DataProvider {

    private final DataAccessProvider dataAccessProvider;

    public DataProvider(Orders plugin) {
        dataAccessProvider = new DataAccessProvider(plugin);
    }

    public void setup() {
        dataAccessProvider.load();
    }

    public void shutdown() {
        dataAccessProvider.unload();
    }

    public void reload() {
        shutdown();
        setup();
    }

    public void saveOrder(Order order) {
        DataAccess data = dataAccessProvider.getDataAccess();
        if (order.getId().isEmpty()) {
            data.addOrder(order);
        } else {
            data.updateOrder(order);
        }
    }

    public void removeOrder(Order order) {
        dataAccessProvider.getDataAccess().deleteOrder(order);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return dataAccessProvider
                .getDataAccess()
                .getOrdersByStatus(status);
    }

    public List<Order> getOrdersByStatusAndCreator(
            OrderStatus status, Player creator) {
        return dataAccessProvider
                .getDataAccess()
                .getOrdersByStatusAndCreator(status, creator.getUniqueId());
    }

    public int getOrderCount(String UUIDString) {
        return dataAccessProvider
                .getDataAccess()
                .getOrderCount(UUIDString);
    }

}
