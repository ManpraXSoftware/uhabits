/*
 * Copyright (C) 2017 Alinson Santos Xavier <mhabitx@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mhabitx.uhabits.acceptance;

import org.mhabitx.uhabits.*;
import org.junit.*;

import static org.mhabitx.uhabits.acceptance.steps.CommonSteps.*;
import static org.mhabitx.uhabits.acceptance.steps.WidgetSteps.*;
import static org.mhabitx.uhabits.acceptance.steps.WidgetSteps.clickText;

public class WidgetTest extends BaseUserInterfaceTest
{
    @Test
    public void shouldCreateAndToggleCheckmarkWidget() throws Exception
    {
        longPressHomeScreen();
        clickWidgets();
        scrollToHabits();
        dragWidgetToHomescreen();
        clickText("Wake up early");
        verifyCheckmarkWidgetIsShown();
        clickCheckmarkWidget();

        launchApp();
        clickText("Wake up early");
        verifyDisplaysText("5%");

        pressHome();
        clickCheckmarkWidget();

        launchApp();
        clickText("Wake up early");
        verifyDisplaysText("0%");
    }
}
