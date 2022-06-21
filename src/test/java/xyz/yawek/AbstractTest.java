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

package xyz.yawek;

import net.milkbowl.vault.economy.Economy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import xyz.yawek.orders.Orders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractTest {

    protected static final Logger LOGGER = Logger.getLogger(AbstractTest.class.getName());

    protected Orders plugin;

    @BeforeAll
    void setup() {
        this.plugin = mock(Orders.class);
        when(plugin.getDataFolder())
                .thenReturn(Path.of("test/").toFile());

        Economy economy = mock(Economy.class);
        when(plugin.getEconomy())
                .thenReturn(economy);
        when(economy.format(anyDouble()))
                .thenReturn("$");
    }

    @AfterAll
    void removeFiles() throws IOException {
        Files.walk(plugin.getDataFolder().toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

}
