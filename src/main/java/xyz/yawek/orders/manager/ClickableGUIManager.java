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

package xyz.yawek.orders.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.gui.ClickableGUI;
import xyz.yawek.orders.util.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class ClickableGUIManager implements Listener, EventExecutor {

    protected final Orders plugin;
    private final List<ClickableGUI> guis = new ArrayList<>();

    protected ClickableGUIManager(Orders plugin, boolean refresh) {
        this.plugin = plugin;

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvent(
                InventoryClickEvent.class, this,
                EventPriority.NORMAL, this, plugin);
        pluginManager.registerEvent(
                InventoryCloseEvent.class, this,
                EventPriority.NORMAL, this, plugin);
        pluginManager.registerEvent(
                PlayerQuitEvent.class, this,
                EventPriority.NORMAL, this, plugin);

        if (refresh) {
            TaskUtil.loopSync(0, 10,
                    () -> guis.forEach(ClickableGUI::loadGUIIfOpened));
        }
    }

    protected void add(ClickableGUI gui) {
        guis.add(gui);
    }

    protected void remove(ClickableGUI gui) {
        guis.remove(gui);
    }

    protected Optional<ClickableGUI> findGUIIfContains(Inventory inventory) {
        return guis.stream()
                .filter(clickableGUI -> clickableGUI.containsInventory(inventory))
                .findFirst();
    }

    protected Optional<ClickableGUI> findGUIIfMatches(Player player) {
        return guis.stream()
                .filter(clickableGUI -> clickableGUI.getPlayer().equals(player))
                .findFirst();
    }

    protected void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();

        Optional<ClickableGUI> guiOptional = findGUIIfContains(inv);
        if (guiOptional.isEmpty()) return;

        ClickableGUI gui = guiOptional.get();
        if (gui.shouldCancelClick(e.getSlot())) e.setCancelled(true);
        gui.runActionAttempt(e.getSlot(), e.isShiftClick(), inv);
    }

    protected void onInventoryClose(InventoryCloseEvent e) {
        findGUIIfContains(e.getInventory())
                .ifPresent(clickableGUI -> clickableGUI.setClosed());
    }

    protected void onPlayerQuit(PlayerQuitEvent e) {
        findGUIIfMatches(e.getPlayer()).ifPresent(guis::remove);
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (event instanceof InventoryClickEvent e) {
            onInventoryClick(e);
        } else if (event instanceof InventoryCloseEvent e) {
            onInventoryClose(e);
        } else if (event instanceof PlayerQuitEvent e) {
            onPlayerQuit(e);
        }
    }

}
