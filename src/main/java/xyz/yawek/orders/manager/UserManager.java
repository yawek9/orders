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
import xyz.yawek.orders.data.DataProvider;
import xyz.yawek.orders.user.OnlineUser;

public class UserManager {

    private final DataProvider dataProvider;

    public UserManager(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public OnlineUser getUser(Player player) {
        return new OnlineUser(player, dataProvider
                .getOrderCount(player.getUniqueId().toString()));
    }

}
