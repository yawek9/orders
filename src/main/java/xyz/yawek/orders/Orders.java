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

package xyz.yawek.orders;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.yawek.orders.command.CommandHandler;
import xyz.yawek.orders.config.Config;
import xyz.yawek.orders.data.DataProvider;
import xyz.yawek.orders.gui.completion.CompletionGUIManager;
import xyz.yawek.orders.gui.orders.OrdersGUIManager;
import xyz.yawek.orders.gui.own.OwnOrdersGUIManager;
import xyz.yawek.orders.handler.ActionHandler;
import xyz.yawek.orders.manager.*;

public class Orders extends JavaPlugin {

    private static Orders plugin;
    private Economy economy;

    private Config config;
    private DataProvider dataProvider;
    private UserManager userManager;
    private OrderManager orderManager;
    private OrdersGUIManager ordersGUIManager;
    private OwnOrdersGUIManager ownOrdersGUIManager;
    private CompletionGUIManager completionGUIManager;
    private ActionHandler actionHandler;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        plugin = this;

        setupEconomy();
        config = new Config(this);
        dataProvider = new DataProvider(this);
        dataProvider.setup();
        userManager = new UserManager(dataProvider);
        orderManager = new OrderManager(this);
        ordersGUIManager = new OrdersGUIManager(this);
        ownOrdersGUIManager = new OwnOrdersGUIManager(this);
        completionGUIManager = new CompletionGUIManager(this);
        actionHandler = new ActionHandler(this);

        CommandHandler commandHandler = new CommandHandler(this);
        getServer().getPluginManager()
                .registerEvents(commandHandler, this);
        getCommand("orders").setExecutor(commandHandler);
    }

    @Override
    public void onDisable() {
        dataProvider.shutdown();
    }

    public void reload() {
        config = new Config(this);
        dataProvider.reload();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> serviceProvider = getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        if (serviceProvider == null) {
            return;
        }
        economy = serviceProvider.getProvider();
    }

    public static Orders getPlugin() {
        return plugin;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Config getPluginConfig() {
        return config;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public OrdersGUIManager getOrdersGUIManager() {
        return ordersGUIManager;
    }

    public OwnOrdersGUIManager getOwnOrdersGUIManager() {
        return ownOrdersGUIManager;
    }

    public CompletionGUIManager getCompletionGUIManager() {
        return completionGUIManager;
    }

    public ActionHandler getActionHandler() {
        return actionHandler;
    }

}
