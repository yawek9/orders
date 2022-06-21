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

package xyz.yawek.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import xyz.yawek.AbstractTest;
import xyz.yawek.orders.config.Config;
import xyz.yawek.orders.gui.SortingMode;
import xyz.yawek.orders.order.OrderStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigTest extends AbstractTest {

    private Config config;

    @BeforeAll
    void setupConfig() {
        config = new Config(plugin);
    }

    @Test
    void testConfigCorrectness() {
        for (Method method : config.getClass().getDeclaredMethods()) {
            if (method.isSynthetic()) continue;

            LOGGER.info(method.getName());
            Object[][] params = new Object[7][];
            if (method.getParameterCount() == 1) {
                Class<?> type = method.getParameterTypes()[0];
                if (type.equals(int.class)) {
                    params[1] = new Object[]{1};
                } else if (type.equals(SortingMode.class)) {
                    params[1] = new Object[]{SortingMode.NEWEST_TO_OLDEST};
                } else if (type.equals(OrderStatus.class)) {
                    params[1] = new Object[]{OrderStatus.ACTIVE};
                }
            }
            params[2] = new Object[]{1, "a"};
            params[4] = new Object[]{"a", 1, "a", 1D};
            params[6] = new Object[]{1, 1.1, "a", 1L, true, true};
            try {
                assertNotNull(method.invoke(config,
                        params[method.getParameterCount()]));
            } catch (IllegalAccessException
                    | InvocationTargetException ignored) { }
        }
    }

}
