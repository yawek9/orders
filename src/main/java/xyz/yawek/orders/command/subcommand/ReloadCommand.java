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

package xyz.yawek.orders.command.subcommand;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.command.PermissibleCommand;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends PermissibleCommand {

    public ReloadCommand(Orders plugin) {
        super(plugin, "orders.reload");
    }

    @Override
    protected void handle(CommandSender sender, String[] args) {
        plugin.reload();
        sender.sendMessage(plugin.getPluginConfig().pluginReloaded());
    }

    @Override
    protected @NotNull List<String> handleSuggestion(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
