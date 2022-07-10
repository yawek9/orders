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

package xyz.yawek.orders.gui;

import org.bukkit.entity.Player;
import xyz.yawek.orders.util.TaskUtil;

abstract class PerPlayerGUI extends PageableGUI {

    private final Player player;
    private int openedPage;

    public PerPlayerGUI(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOpenedPage() {
        return openedPage;
    }

    public void open(int index) {
        TaskUtil.sync(() -> {
            player.closeInventory();
            openedPage = index;
            this.getInventoryAtIndex(index).ifPresent(player::openInventory);
        });
    }

    public void setClosed() {
        openedPage = -1;
    }

    public void loadGUIIfOpened() {
        if (getOpenedPage() == -1) return;
        this.loadGUI();
    }

}
