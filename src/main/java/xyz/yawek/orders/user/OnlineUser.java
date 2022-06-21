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

package xyz.yawek.orders.user;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class OnlineUser {

    private final Player player;
    private int ordersCount = 0;

    public OnlineUser(Player player, int ordersCount) {
        this.player = player;
        this.ordersCount = ordersCount;
    }

    public boolean canCreateOrder() {
        return ordersCount < getOrdersLimit();
    }

    public int getOrdersLimit() {
        for (PermissionAttachmentInfo p : player.getEffectivePermissions()) {
            if (!p.getPermission().startsWith("orders.limit")) continue;
            try {
                return Integer.parseInt(p.getPermission().split("\\.")[2]);
            } catch (Exception ignored) { }
        }
        return 0;
    }

}
