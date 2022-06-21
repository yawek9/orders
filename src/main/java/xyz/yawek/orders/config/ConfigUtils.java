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
import xyz.yawek.orders.util.ColorUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

class ConfigUtils {

    private final ConfigProvider configProvider;

    public ConfigUtils(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public Component prefixedComponent(String key, String... arguments) {
        String message = configProvider.getString(key);
        if (message == null) return null;
        for (String arg : arguments) {
            message = message.replaceFirst("\\{}", Matcher.quoteReplacement(arg));
        }
        return ColorUtils.decorate(Component.text(
                configProvider.getString("messages.chat.prefix")
                        + message));
    }

    public Component component(String key) {
        String message = configProvider.getString(key);
        if (message == null) return null;
        return ColorUtils.decorate(Component.text(message));
    }

    public List<Component> componentList(String key) {
        return configProvider.getStringList(key)
                .stream()
                .map(s -> ColorUtils.decorate(Component.text(s)))
                .collect(Collectors.toList());
    }

    public String formatTimeTo(long timestamp) {
        long millisTo = timestamp - System.currentTimeMillis();

        long days = TimeUnit.MILLISECONDS.toDays(millisTo);
        long daysInMillis = TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millisTo - daysInMillis);
        long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(
                millisTo - daysInMillis - hoursInMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(
                millisTo - daysInMillis - hoursInMillis
                        - TimeUnit.MINUTES.toMillis(minutes));
        return days + " " + configProvider.getString("orders-gui.order-item.days") + " "
                + hours + " " + configProvider.getString("orders-gui.order-item.hours") + " "
                + minutes + " " + configProvider.getString("orders-gui.order-item.minutes") + " "
                + seconds + " " + configProvider.getString("orders-gui.order-item.seconds") + " ";
    }

}
