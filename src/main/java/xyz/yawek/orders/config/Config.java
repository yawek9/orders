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

package xyz.yawek.orders.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import xyz.yawek.orders.Orders;
import xyz.yawek.orders.gui.SortingMode;
import xyz.yawek.orders.order.OrderStatus;
import xyz.yawek.orders.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private final Orders plugin;
    private final ConfigProvider configProvider;
    private final ConfigUtils configUtils;

    public Config(Orders plugin) {
        this.plugin = plugin;
        configProvider = new ConfigProvider(plugin);
        configUtils = new ConfigUtils(configProvider);
    }

    // Settings

    public long expirationTimestamp() {
        return configProvider.getLong("settings.expiration-time");
    }

    public double minPayment() {
        return configProvider.getDouble("settings.min-payment");
    }

    public double maxPayment() {
        return configProvider.getDouble("settings.max-payment");
    }

    public List<Material> blacklistedItems() {
        return configProvider.getStringList("settings.blacklisted-items")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toList());
    }

    // Chat messages

    public Component notFromConsole() {
        return configUtils.prefixedComponent("messages.chat.not-from-console");
    }

    public Component noPermission() {
        return configUtils.prefixedComponent("messages.chat.no-permission");
    }

    public Component pluginReloaded() {
        return configUtils.prefixedComponent("messages.chat.reloaded");
    }

    public Component ordersLimitReached(int limit) {
        return configUtils.prefixedComponent("messages.chat.orders-limit-reached",
                String.valueOf(limit));
    }

    public Component noItemInHand() {
        return configUtils.prefixedComponent("messages.chat.no-item-in-hand");
    }

    public Component amountNotCorrect(int maxStackSize) {
        return configUtils.prefixedComponent("messages.chat.amount-not-correct",
                String.valueOf(maxStackSize * 7));
    }

    public Component paymentNotCorrect() {
        return configUtils.prefixedComponent("messages.chat.payment-not-correct",
                String.format("%.2f", minPayment()), String.format("%.2f", maxPayment()));
    }

    public Component notEnoughMoney() {
        return configUtils.prefixedComponent("messages.chat.not-enough-money");
    }

    public Component blacklistedItem() {
        return configUtils.prefixedComponent("messages.chat.blacklisted-item");
    }

    public Component wrongItemName() {
        return configUtils.prefixedComponent("messages.chat.wrong-item-name");
    }

    public Component orderAdded(int amount, String itemName) {
        return configUtils.prefixedComponent("messages.chat.order-added",
                amount + "x " + itemName);
    }

    public Component addCommandUsage() {
        return configUtils.prefixedComponent("messages.chat.wrong-usage.add");
    }

    public Component orderCancelled(int amount, String itemName) {
        return configUtils.prefixedComponent("messages.chat.order-cancelled",
                amount + "x " + itemName);
    }

    public Component orderAlreadyCompleted() {
        return configUtils.prefixedComponent("messages.chat.order-already-completed");
    }

    public Component ownOrderCompletion() {
        return configUtils.prefixedComponent("messages.chat.own-order-completion");
    }

    public Component wrongItemsPut() {
        return configUtils.prefixedComponent("messages.chat.wrong-items-put");
    }

    public Component orderCompleted(String creatorName, int amount, String itemName, double payment) {
        return configUtils.prefixedComponent("messages.chat.order-completed",
                creatorName, amount + "x " + itemName, plugin.getEconomy().format(payment));
    }

    public Component itemsReceived(int amount, String itemName) {
        return configUtils.prefixedComponent("messages.chat.items-received",
                amount + "x " + itemName);
    }

    // GUIs

    public Component ordersGUITitle() {
        return configUtils.component("orders-gui.title");
    }

    public String ownButtonItem() {
        return configProvider.getString("orders-gui.own-button.item");
    }

    public Component ownButtonDisplayName() {
        return configUtils.component("orders-gui.own-button.display-name");
    }

    public List<Component> ownButtonLore() {
        return configUtils.componentList("orders-gui.own-button.lore");
    }

    public String sortingButtonItem() {
        return configProvider.getString("orders-gui.sorting-button.item");
    }

    public Component sortingButtonDisplayName() {
        return configUtils.component("orders-gui.sorting-button.display-name");
    }

    public List<Component> sortingButtonLore(SortingMode sortingMode) {
        List<Component> lore = new ArrayList<>();
        lore.add(ColorUtils.decorate(
                configProvider.getString("orders-gui.sorting-button.order")
                .replace("{}",
                        configProvider.getString("orders-gui.sorting-button."
                                + switch (sortingMode) {
                            case NEWEST_TO_OLDEST -> "newest-to-oldest";
                            case OLDEST_TO_NEWEST -> "oldest-to-newest";
                            case LOWEST_TO_HIGHEST_PAYMENT -> "lowest-to-highest";
                            case HIGHEST_TO_LOWEST_PAYMENT -> "highest-to-lowest";
                        }))));
        return lore;
    }

    public String infoButtonItem() {
        return configProvider.getString("orders-gui.info-button.item");
    }

    public Component infoButtonDisplayName() {
        return configUtils.component("orders-gui.info-button.display-name");
    }

    public List<Component> infoButtonLore() {
        return configUtils.componentList("orders-gui.info-button.lore");
    }

    public String previousPageButtonItem() {
        return configProvider.getString("orders-gui.previous-page-button.item");
    }

    public Component previousPageButtonDisplayName() {
        return configUtils.component("orders-gui.previous-page-button.display-name");
    }

    public List<Component> previousPageButtonLore() {
        return configUtils.componentList("orders-gui.previous-page-button.lore");
    }

    public String nextPageButtonItem() {
        return configProvider.getString("orders-gui.next-page-button.item");
    }

    public Component nextPageButtonDisplayName() {
        return configUtils.component("orders-gui.next-page-button.display-name");
    }

    public List<Component> nextPageButtonLore() {
        return configUtils.componentList("orders-gui.next-page-button.lore");
    }

    public List<Component> orderItemLore(int amount, double payment,
                                         String creatorName, @Nullable String contractorName,
                                         long expirationTimeStamp,
                                         boolean appendCreator, boolean appendExpiration) {
        List<Component> lore = new ArrayList<>(
                configProvider.getStringList("orders-gui.order-item.top-break")
                .stream()
                .map(ColorUtils::decorate)
                .toList());
        lore.add(ColorUtils.decorate(
                configProvider.getString("orders-gui.order-item.amount")
                .replace("{}", String.valueOf(amount))));
        lore.add(ColorUtils.decorate(
                configProvider.getString("orders-gui.order-item.payment")
                .replace("{}", plugin.getEconomy().format(payment))));
        if (appendCreator) {
            lore.add(ColorUtils.decorate(
                    configProvider.getString("orders-gui.order-item.creator")
                            .replace("{}", creatorName)));
        }
        if (contractorName != null) {
            lore.add(ColorUtils.decorate(
                    configProvider.getString("orders-gui.order-item.contractor")
                            .replace("{}", contractorName)));
        }
        if (appendExpiration) {
            lore.add(ColorUtils.decorate(
                    configProvider.getString("orders-gui.order-item.expiration")
                            .replace("{}", configUtils.formatTimeTo(expirationTimeStamp))));
        }
        lore.addAll(configProvider.getStringList("orders-gui.order-item.bottom-break")
                .stream()
                .map(ColorUtils::decorate)
                .toList());

        String descriptionKey = null;
        if (appendCreator && appendExpiration) {
            descriptionKey = "orders-gui.order-item.main-menu-lore";
        } else if (!appendCreator && appendExpiration) {
            descriptionKey = "orders-gui.order-item.own-menu-active-lore";
        } else if (!appendCreator) {
            descriptionKey = "orders-gui.order-item.own-menu-completed-lore";
        }
        if (descriptionKey != null) {
            lore.addAll(configProvider.getStringList(descriptionKey)
                    .stream()
                    .map(ColorUtils::decorate)
                    .toList());
        }

        lore.replaceAll(component ->
                component.decoration(TextDecoration.ITALIC, false));
        return lore;
    }

    public Component ownOrdersGUITitle() {
        return configUtils.component("orders-gui.own-gui-title");
    }

    public String returnButtonItem() {
        return configProvider.getString("orders-gui.return-button.item");
    }

    public Component returnButtonDisplayName() {
        return configUtils.component("orders-gui.return-button.display-name");
    }

    public List<Component> returnButtonLore() {
        return configUtils.componentList("orders-gui.return-button.lore");
    }

    public String statusButtonItem() {
        return configProvider.getString("orders-gui.status-button.item");
    }

    public Component statusButtonDisplayName() {
        return configUtils.component("orders-gui.status-button.display-name");
    }

    public List<Component> statusButtonLore(OrderStatus orderStatus) {
        List<Component> lore = new ArrayList<>();
        lore.add(ColorUtils.decorate(
                configProvider.getString("orders-gui.status-button.status")
                        .replace("{}",
                                configProvider.getString("orders-gui.status-button."
                                        + switch (orderStatus) {
                                    case ACTIVE -> "active";
                                    case COMPLETED -> "completed";
                                }))));
        return lore;
    }

    public Component completionGUITitle() {
        return configUtils.component("orders-gui.completion-gui-title");
    }

    public String completeButtonItem() {
        return configProvider.getString("orders-gui.complete-button.item");
    }

    public Component completeButtonDisplayName() {
        return configUtils.component("orders-gui.complete-button.display-name");
    }

    public List<Component> completeButtonLore() {
        return configUtils.componentList("orders-gui.complete-button.lore");
    }

    public String emptyItem() {
        return configProvider.getString("orders-gui.empty-item");
    }

}
