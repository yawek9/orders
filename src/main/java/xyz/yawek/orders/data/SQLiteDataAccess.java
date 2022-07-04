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

import xyz.yawek.orders.Orders;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.order.OrderStatus;
import xyz.yawek.orders.util.ItemUtils;
import xyz.yawek.orders.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class SQLiteDataAccess implements DataAccess {

    private final Orders plugin;
    private Connection connection;

    public SQLiteDataAccess(Orders plugin) {
        this.plugin = plugin;
    }

    @Override
    public void openConnection() {
        File directory = plugin.getDataFolder();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File databaseFile = new File(directory, "data.db");
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                LogUtils.errorDataAccess("Unable to create SQLite database file.");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
            String sql = """
                        CREATE TABLE IF NOT EXISTS orders (
                          id INTEGER,
                          item_data NOT NULL,
                          amount DEFAULT 1,
                          payment DEFAULT 0.01,
                          creation_timestamp NOT NULL,
                          expiration_timestamp NOT NULL,
                          creator_uuid NOT NULL,
                          contractor_uuid DEFAULT NULL,
                          status NOT NULL DEFAULT 0,
                          PRIMARY KEY (id)
                        )""";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
            LogUtils.infoDataAccess("Successfully connected to the SQLite database.");
        } catch (Exception e) {
            LogUtils.errorDataAccess("Unable to connect to the SQLite database.");
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
            LogUtils.infoDataAccess("Database connection has been closed.");
        } catch (SQLException e) {
            LogUtils.infoDataAccess("Unable to close the connection " +
                    "with SQLite database.");
            e.printStackTrace();
        }
    }

    @Override
    public void addOrder(Order order) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          INSERT INTO orders (item_data, amount, payment, creation_timestamp,
                          expiration_timestamp, creator_uuid, contractor_uuid, status)
                          VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
            preparedStatement.setString(1,
                    ItemUtils.toBase64(order.getItemStack()));
            preparedStatement.setInt(2, order.getAmount());
            preparedStatement.setDouble(3, order.getPayment());
            preparedStatement.setLong(4, order.getCreationTimestamp());
            preparedStatement.setLong(5, order.getExpirationTimestamp());
            preparedStatement.setString(6, order.getCreatorUUID().toString());

            String contractorUUIDString = null;
            if (order.getContractorUUID().isPresent())
                contractorUUIDString = order.getContractorUUID().toString();

            preparedStatement.setString(7, contractorUUIDString);
            preparedStatement.setInt(8, order.getStatus().value);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to save order with id {}.",
                    String.valueOf(order.getId()));
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(Order order) {
        if (order.getId().isEmpty()) return;

        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          UPDATE orders SET item_data = ?, amount = ?, payment = ?, creation_timestamp = ?,
                          expiration_timestamp = ?, creator_uuid = ?, contractor_uuid = ?, status = ?
                          WHERE id = ?
                        """)) {
            preparedStatement.setString(1,
                    ItemUtils.toBase64(order.getItemStack()));
            preparedStatement.setInt(2, order.getAmount());
            preparedStatement.setDouble(3, order.getPayment());
            preparedStatement.setLong(4, order.getCreationTimestamp());
            preparedStatement.setLong(5, order.getExpirationTimestamp());
            preparedStatement.setString(6, order.getCreatorUUID().toString());

            String contractorUUIDString = null;
            if (order.getContractorUUID().isPresent())
                contractorUUIDString = order.getContractorUUID().get().toString();

            preparedStatement.setString(7, contractorUUIDString);
            preparedStatement.setInt(8, order.getStatus().value);
            preparedStatement.setLong(9, order.getId().get());
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to update order with id {}.",
                    String.valueOf(order.getId()));
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrder(Order order) {
        if (order.getId().isEmpty()) return;

        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          DELETE FROM orders WHERE id = ?
                        """)) {
            preparedStatement.setLong(1, order.getId().get());
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to delete order with id {}.",
                    String.valueOf(order.getId()));
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM orders WHERE status = ?")) {
            preparedStatement.setLong(1, status.value);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order(
                        ItemUtils.fromBase64(resultSet.getString(2)),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        UUID.fromString(resultSet.getString(7)),
                        OrderStatus.values()[resultSet.getInt(8)]);
                order.setId(resultSet.getLong(1));

                String contractorUUID = resultSet.getString(8);
                if (contractorUUID != null)
                    order.setContractorUUID(UUID.fromString(contractorUUID));

                orders.add(order);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get all orders with status {}.",
                    String.valueOf(status));
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatusAndCreator(OrderStatus status, UUID creatorUUID) {
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM orders WHERE status = ? AND creator_uuid = ?")) {
            preparedStatement.setLong(1, status.value);
            preparedStatement.setString(2, creatorUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order(
                        ItemUtils.fromBase64(resultSet.getString(2)),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        UUID.fromString(resultSet.getString(7)),
                        OrderStatus.values()[resultSet.getInt(9)]);
                order.setId(resultSet.getLong(1));

                String contractorUUID = resultSet.getString(8);
                if (contractorUUID != null)
                    order.setContractorUUID(UUID.fromString(contractorUUID));

                orders.add(order);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get all orders with status {}.",
                    String.valueOf(status));
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public int getOrderCount(String UUIDString) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(id) FROM orders WHERE creator_uuid = ?")) {
            preparedStatement.setString(1, UUIDString);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get order count " +
                            "for the player with uuid {}.", UUIDString);
            e.printStackTrace();
            return 100000;
        }
        return 0;
    }

}
