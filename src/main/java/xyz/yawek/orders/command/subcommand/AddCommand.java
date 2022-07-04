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

import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.command.PermissibleCommand;
import xyz.yawek.orders.manager.OrderManager;
import xyz.yawek.orders.user.OnlineUser;

import java.util.Collections;
import java.util.List;

public class AddCommand extends PermissibleCommand {

    public AddCommand(Orders plugin) {
        super(plugin, "orders.add", true);
    }

    @Override
    protected void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPluginConfig().notFromConsole());
            return;
        }
        OrderManager orderManager = plugin.getOrderManager();
        OnlineUser user = plugin.getUserManager().getUser(player);
        if (args.length == 3 && args[0].equals("hand")) {
            ItemStack itemToAdd = player.getInventory()
                    .getItemInMainHand();
            if (!checkArgumentsCorrectness(user, sender, itemToAdd, args)) return;
            double payment = Double.parseDouble(args[2]);
            if (!plugin.getEconomy().has(player, payment)) {
                sender.sendMessage(plugin.getPluginConfig().notEnoughMoney());
                return;
            }
            int amount = Integer.parseInt(args[1]);
            orderManager.addOrder(itemToAdd, amount, payment, player.getUniqueId());

            PaperComponents.flattener().flatten(
                    Component.translatable(itemToAdd.translationKey()),
                    itemName -> sender.sendMessage(
                            plugin.getPluginConfig().orderAdded(amount, itemName)));
            return;
        }
        if (args.length == 3) {
            Material material = Material.matchMaterial(args[0]);
            if (material == null || material.equals(Material.AIR)) {
                sender.sendMessage(plugin.getPluginConfig().wrongItemName());
                return;
            }
            ItemStack itemToAdd = new ItemStack(material, 1);
            if (!checkArgumentsCorrectness(user, sender, itemToAdd, args)) return;
            double payment = Double.parseDouble(args[2]);
            if (!plugin.getEconomy().has(player, payment)) {
                sender.sendMessage(plugin.getPluginConfig().notEnoughMoney());
                return;
            }
            int amount = Integer.parseInt(args[1]);
            orderManager.addOrder(itemToAdd, amount, payment, player.getUniqueId());

            PaperComponents.flattener().flatten(
                    Component.translatable(itemToAdd.translationKey()),
                    itemName -> sender.sendMessage(
                            plugin.getPluginConfig().orderAdded(amount, itemName)));
            return;
        }
        sender.sendMessage(plugin.getPluginConfig().addCommandUsage());
    }

    @Override
    protected @NotNull List<String> getSuggestions(CommandSender sender, String[] args) {
        return Collections.singletonList("hand");
    }

    private boolean checkArgumentsCorrectness(OnlineUser user, CommandSender sender,
                                              ItemStack itemToOrder, String[] args) {
        OrderManager orderManager = plugin.getOrderManager();

        if (!user.canCreateOrder()) {
            sender.sendMessage(plugin.getPluginConfig()
                    .ordersLimitReached(user.getOrdersLimit()));
            return false;
        }
        if (itemToOrder.getType().equals(Material.AIR)) {
            sender.sendMessage(plugin.getPluginConfig().noItemInHand());
            return false;
        }
        if (!orderManager.isAmountCorrect(args[1], itemToOrder.getMaxStackSize())) {
            sender.sendMessage(plugin.getPluginConfig()
                    .amountNotCorrect(itemToOrder.getMaxStackSize()));
            return false;
        }
        if (!orderManager.isPaymentCorrect(args[2])) {
            sender.sendMessage(plugin.getPluginConfig().paymentNotCorrect());
            return false;
        }
        if (!orderManager.isItemCorrect(itemToOrder)) {
            sender.sendMessage(plugin.getPluginConfig().blacklistedItem());
            return false;
        }
        return true;
    }

}
