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

import java.util.Objects;

class ClickAction {

    private final int pageIndex;
    private final int slot;
    private final Runnable actionToExecute;
    private boolean shiftRequired = false;

    public ClickAction(int pageIndex, int slot, Runnable actionToExecute) {
        this.pageIndex = pageIndex;
        this.slot = slot;
        this.actionToExecute = actionToExecute;
    }

    public ClickAction(int pageIndex, int slot, Runnable actionToExecute, boolean shiftRequired) {
        this.pageIndex = pageIndex;
        this.slot = slot;
        this.actionToExecute = actionToExecute;
        this.shiftRequired = shiftRequired;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isShiftRequired() {
        return shiftRequired;
    }

    public void setShiftRequired(boolean shiftRequired) {
        this.shiftRequired = shiftRequired;
    }

    public void runAction() {
        actionToExecute.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickAction that = (ClickAction) o;
        return pageIndex == that.pageIndex && slot == that.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageIndex, slot);
    }

}
