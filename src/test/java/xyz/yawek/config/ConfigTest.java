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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
            Object[][] params = new Object[1][];
            params[0] = new Object[]{};
            assertDoesNotThrow(() -> {
                try {
                    LOGGER.info(method.getName());
                    assertNotNull(method.invoke(config,
                            params[method.getParameterCount()]));
                } catch (IllegalAccessException
                        | InvocationTargetException ignored) {}
            });
        }
    }

}
