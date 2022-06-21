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

public enum SortingMode {

    NEWEST_TO_OLDEST(0),
    OLDEST_TO_NEWEST(1),
    LOWEST_TO_HIGHEST_PAYMENT(2),
    HIGHEST_TO_LOWEST_PAYMENT(3);

    public final int value;

    SortingMode(int value) {
        this.value = value;
    }

    public static SortingMode next(SortingMode sortingMode) {
        return switch (sortingMode.value) {
            case 0 -> OLDEST_TO_NEWEST;
            case 1 -> LOWEST_TO_HIGHEST_PAYMENT;
            case 2 -> HIGHEST_TO_LOWEST_PAYMENT;
            default -> NEWEST_TO_OLDEST;
        };
    }

}
