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

package xyz.yawek.orders.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.orders.Orders;

import java.util.Collections;
import java.util.List;

public abstract class PermissibleCommand implements ExecutableCommand {

    protected final Orders plugin;
    private final String permission;

    public PermissibleCommand(Orders plugin, String permission) {
        this.plugin = plugin;
        this.permission = permission;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(plugin.getPluginConfig().noPermission());
            return;
        }
        handle(sender, args);
    }

    @Override
    public @NotNull List<String> suggest(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) return Collections.emptyList();
        return getSuggestions(sender, args);
    }

    public String getPermission() {
        return permission;
    }

    protected abstract void handle(CommandSender sender, String[] args);

    protected abstract @NotNull List<String> getSuggestions(CommandSender sender, String[] args);

}
