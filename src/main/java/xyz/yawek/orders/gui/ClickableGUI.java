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
import org.bukkit.inventory.Inventory;
import xyz.yawek.orders.util.TaskUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class ClickableGUI extends PerPlayerGUI {

    private final Set<ClickAction> clickActions = new HashSet<>();
    private boolean cancelClick = true;
    private final Set<Integer> cancelClickExemptions = new HashSet<>();
    private int ticksBetweenClick = 5;
    private boolean clickedYet = false;

    public ClickableGUI(Player player) {
        super(player);
    }

    public void setCancelClick(boolean cancelClick) {
        this.cancelClick = cancelClick;
    }

    public void addCancelClickExemptions(int... slots) {
        for (int i : slots) {
            cancelClickExemptions.add(i);
        }
    }

    public void removeCancelClickExemptions(int... slots) {
        for (int i : slots) {
            cancelClickExemptions.remove(i);
        }
    }

    public boolean shouldCancelClick(int slot) {
        return cancelClick && !cancelClickExemptions.contains(slot);
    }

    public void setTicksBetweenClick(int ticksBetweenClick) {
        this.ticksBetweenClick = ticksBetweenClick;
    }

    public void addAction(int pageIndex, int slot, Runnable runnable) {
        ClickAction action = new ClickAction(pageIndex, slot, runnable);
        clickActions.remove(action);
        clickActions.add(action);
    }

    public void addAction(int pageIndex, int slot,
                          boolean shiftRequired, Runnable runnable) {
        ClickAction action = new ClickAction(pageIndex, slot, runnable, shiftRequired);
        clickActions.remove(action);
        clickActions.add(action);
    }

    public void clearActions() {
        clickActions.clear();
    }

    public boolean runActionAttempt(int slot, boolean withShiftPressed,
                                    Inventory inventory) {
        if (clickedYet) return false;
        Optional<Integer> pageIndexOptional = this.getIndexIfExists(inventory);
        if (pageIndexOptional.isEmpty()) return false;
        int pageIndex = pageIndexOptional.get();
        clickActions.stream()
                .filter(clickAction ->
                        clickAction.getPageIndex() == pageIndex
                                && clickAction.getSlot() == slot
                                && (!clickAction.isShiftRequired() || withShiftPressed))
                .findFirst()
                .ifPresent(clickAction -> {
                    setClickedYet(true);
                    TaskUtil.syncAfter(ticksBetweenClick, () -> setClickedYet(false));
                    clickAction.runAction();
                });
        return true;
    }

    private void setClickedYet(boolean clickedYet) {
        this.clickedYet = clickedYet;
    }

}
