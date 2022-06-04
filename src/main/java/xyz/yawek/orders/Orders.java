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

import org.bukkit.plugin.java.JavaPlugin;
import xyz.yawek.orders.command.CommandHandler;
import xyz.yawek.orders.config.Config;
import xyz.yawek.orders.data.DataProvider;

public class Orders extends JavaPlugin {

    private static Orders plugin;
    private Config config;
    private DataProvider dataProvider;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        plugin = this;

        config = new Config(this);
        dataProvider = new DataProvider(this);
        dataProvider.setup();

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

    public static Orders getPlugin() {
        return plugin;
    }

    public Config getPluginConfig() {
        return config;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

}
