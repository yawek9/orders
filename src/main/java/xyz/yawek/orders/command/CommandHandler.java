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

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.orders.Orders;

import java.util.*;

public class CommandHandler implements CommandExecutor, Listener {

    private final Orders plugin;
    private final Map<String, ExecutableCommand> commandMap = new HashMap<>();

    public CommandHandler(Orders plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String s,
                             @NotNull String[] args) {
        if (!sender.hasPermission("orders.orders")) {
            sender.sendMessage(plugin.getPluginConfig().noPermission());
            return false;
        }
        if (args.length == 0 || !commandMap.containsKey(args[0])) {
            // base command usage
            return false;
        }
        commandMap.get(args[0]).execute(sender,
                Arrays.copyOfRange(args, 1, args.length));
        return false;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onTabComplete(AsyncTabCompleteEvent e) {
        CommandSender sender = e.getSender();
        String[] args = e.getBuffer().split(" ");
        if (args.length == 1) {
            List<String> firstArguments = new ArrayList<>();
            for (String commandString : commandMap.keySet()) {
                if (commandMap.get(commandString)
                        instanceof PermissibleCommand permissibleCommand) {
                    if (sender.hasPermission(permissibleCommand.getPermission()))
                        firstArguments.add(commandString);
                } else {
                    firstArguments.add(commandString);
                }
            }
            e.setCompletions(firstArguments);
        } else if (args.length > 1 && commandMap.containsKey(args[0])) {
            e.setCompletions(commandMap.get(args[0]).suggest(sender,
                    Arrays.copyOfRange(args, 1, args.length)));
        } else {
            e.setCompletions(Collections.emptyList());
        }
    }

}
