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

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class PageableGUI {

    private final List<Inventory> inventories = new ArrayList<>();

    public boolean containsInventory(Inventory inventory) {
        return inventories.contains(inventory);
    }

    protected Optional<Integer> getIndexIfExists(Inventory inventory) {
        if (!containsInventory(inventory)) return Optional.empty();
        return Optional.of(inventories.indexOf(inventory));
    }

    protected Optional<Inventory> getInventoryAtIndex(int index) {
        if (index < 0 || inventories.size() <= index)
            return Optional.empty();
        return Optional.of(inventories.get(index));
    }

    protected void addInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    protected void setInventoryIfNotExists(int index, Inventory inventory) {
        if (index < 0 || getInventoryAtIndex(index).isPresent()) return;
        if (index >= inventories.size()) {
            addInventory(inventory);
            return;
        }
        inventories.set(index, inventory);
    }

    protected void setContentIfPageExists(int pageIndex, int slot, ItemStack itemStack) {
        this.getInventoryAtIndex(pageIndex)
                .ifPresent(inventory -> inventory.setItem(slot, itemStack));
    }

    public Optional<ItemStack> getItemAtIndex(int pageIndex, int slot) {
        Optional<Inventory> inventoryOptional = getInventoryAtIndex(pageIndex);
        if (inventoryOptional.isEmpty()) return Optional.empty();
        return Optional.ofNullable(inventoryOptional.get().getItem(slot));
    }

    public abstract void loadGUI();

}
