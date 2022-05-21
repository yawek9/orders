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

package xyz.yawek.orders.util;

import org.bukkit.Bukkit;
import xyz.yawek.orders.Orders;

public class TaskUtil {

    private static final Orders PLUGIN = Orders.getPlugin();

    public static int sync(Runnable runnable) {
        return Bukkit.getScheduler()
                .runTask(PLUGIN, runnable)
                .getTaskId();
    }

    public static int async(Runnable runnable) {
        return Bukkit.getScheduler()
                .runTaskAsynchronously(PLUGIN, runnable)
                .getTaskId();
    }

    public static int syncAfter(int ticks, Runnable runnable) {
        return Bukkit.getScheduler()
                .runTaskLater(PLUGIN, runnable, ticks)
                .getTaskId();
    }

    public static int asyncAfter(int ticks, Runnable runnable) {
        return Bukkit.getScheduler()
                .runTaskLaterAsynchronously(PLUGIN, runnable, ticks)
                .getTaskId();
    }

    public static int syncAt(long timestamp, Runnable runnable) {
        long afterSeconds = (timestamp - System.currentTimeMillis()) / 1000;
        if (afterSeconds < 0) return -1;
        return Bukkit.getScheduler()
                .runTaskLater(PLUGIN, runnable, afterSeconds * 20)
                .getTaskId();
    }

    public static int asyncAt(long timestamp, Runnable runnable) {
        long afterSeconds = (timestamp - System.currentTimeMillis()) / 1000;
        if (afterSeconds < 0) return -1;
        return Bukkit.getScheduler()
                .runTaskLaterAsynchronously(PLUGIN, runnable, afterSeconds * 20)
                .getTaskId();
    }

    public static int loopSync(long afterTicks, long delayTicks, Runnable runnable) {
        return Bukkit.getScheduler()
                .runTaskTimer(PLUGIN, runnable, afterTicks, delayTicks)
                .getTaskId();
    }

    public static int loopAsync(long afterTicks, long delayTicks, Runnable runnable) {
        return Bukkit.getScheduler()
                .runTaskTimerAsynchronously(PLUGIN, runnable, afterTicks, delayTicks)
                .getTaskId();
    }

}
