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
import xyz.yawek.orders.util.ItemUtil;
import xyz.yawek.orders.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                          id NOT NULL,
                          item_data NOT NULL,
                          amount DEFAULT 1,
                          payment DEFAULT 0.01,
                          expiration_timestamp DEFAULT NULL,
                          creator_uuid NOT NULL,
                          contractor_uuid DEFAULT NULL,
                          status DEFAULT 0,
                          PRIMARY KEY (id)
                        )""";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
            LogUtils.infoDataAccess("Successfully connected to the SQLite database.");
        } catch (ClassNotFoundException | SQLException e) {
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
    public Optional<Order> getOrderById(long id) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM orders WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order(
                        id,
                        ItemUtil.fromBase64(resultSet.getString(2)),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getLong(5),
                        UUID.fromString(resultSet.getString(6)),
                        OrderStatus.values()[resultSet.getInt(8)]);

                String contractorUUID = resultSet.getString(7);
                if (contractorUUID != null)
                    order.setContractor(UUID.fromString(contractorUUID));

                return Optional.of(order);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get order by id {}.",
                    String.valueOf(id));
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void saveOrder(Order order) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          INSERT INTO orders (id, item_data, amount, payment,
                          expiration_timestamp, creator_uuid, contractor_uuid, status)
                          VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                          ON CONFLICT(id) DO UPDATE SET
                          id = ?,
                          item_data = ?,
                          amount = ?,
                          payment = ?,
                          expiration_timestamp = ?,
                          creator_uuid = ?,
                          contractor_uuid = ?,
                          status = ?
                        """)) {
            preparedStatement.setLong(1, order.getId());
            preparedStatement.setString(2,
                    ItemUtil.toBase64(order.getItemStack()));
            preparedStatement.setInt(3, order.getAmount());
            preparedStatement.setDouble(4, order.getPayment());
            preparedStatement.setLong(5, order.getExpirationTimestamp());
            preparedStatement.setString(6, order.getCreator().toString());

            String contractorUUIDString = null;
            if (order.getContractor().isPresent())
                contractorUUIDString = order.getContractor().toString();

            preparedStatement.setString(7, contractorUUIDString);
            preparedStatement.setInt(8, order.getStatus().value);
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to save order with id {}.",
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
                        resultSet.getInt(1),
                        ItemUtil.fromBase64(resultSet.getString(2)),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getLong(5),
                        UUID.fromString(resultSet.getString(6)),
                        OrderStatus.values()[resultSet.getInt(8)]);

                String contractorUUID = resultSet.getString(7);
                if (contractorUUID != null)
                    order.setContractor(UUID.fromString(contractorUUID));

                orders.add(order);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get all orders with status {}.",
                    String.valueOf(status));
            e.printStackTrace();
        }
        return orders;
    }

}
