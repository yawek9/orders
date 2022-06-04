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

package xyz.yawek.orders.order;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class Order {

    private final long id;
    private final ItemStack itemStack;
    private final int amount;
    private final double payment;
    private final long expirationTimestamp;
    private final UUID creator;
    private final OrderStatus status;
    private UUID contractor;

    public Order(long id, ItemStack itemStack, int amount, double payment,
                 long expirationTimestamp, UUID creator, OrderStatus status) {
        this.id = id;
        this.itemStack = itemStack;
        this.amount = amount;
        this.payment = payment;
        this.expirationTimestamp = expirationTimestamp;
        this.creator = creator;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public double getPayment() {
        return payment;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public UUID getCreator() {
        return creator;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Optional<UUID> getContractor() {
        if (contractor == null) return Optional.empty();
        return Optional.of(contractor);
    }

    public void setContractor(UUID contractor) {
        this.contractor = contractor;
    }

}
