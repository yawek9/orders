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

package xyz.yawek.orders.gui.completion;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.gui.ClickableGUIManager;
import xyz.yawek.orders.order.Order;
import xyz.yawek.orders.util.InventoryUtils;

import java.util.stream.IntStream;

public class CompletionGUIManager extends ClickableGUIManager {

    public CompletionGUIManager(Orders plugin) {
        super(plugin, false);
    }

    public void openGUI(Player player, Order order) {
        if (order.getCreatorUUID().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getPluginConfig().ownOrderCompletion());
            return;
        }
        CompletionGUI gui = new CompletionGUI(plugin, player, order);
        this.add(gui);
        gui.open(0);
    }

    @Override
    protected void onInventoryClick(InventoryClickEvent e) {
        super.onInventoryClick(e);
    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent e) {
        super.onInventoryClose(e);
        this.findGUIIfContains(e.getInventory())
                .ifPresent(clickableGUI -> {
                    IntStream.rangeClosed(10, 16)
                            .forEach(i -> clickableGUI.getItemAtIndex(0, i)
                                    .ifPresent(itemStack -> InventoryUtils.returnItemsOrDrop(
                                            clickableGUI.getPlayer(), itemStack)));
                    this.remove(clickableGUI);
                });
    }

}
