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

package org.mhabitx.uhabits.receivers;

import org.mhabitx.uhabits.*;
import org.mhabitx.uhabits.core.models.*;
import org.mhabitx.uhabits.core.preferences.*;
import org.mhabitx.uhabits.core.reminders.*;
import org.mhabitx.uhabits.core.ui.*;
import org.mhabitx.uhabits.core.utils.*;
import org.junit.*;

import static org.mockito.Mockito.*;

public class ReminderControllerTest extends BaseAndroidJVMTest
{

    private ReminderController controller;

    private ReminderScheduler reminderScheduler;

    private NotificationTray notificationTray;

    private Preferences preferences;

    @Override
    public void setUp()
    {
        super.setUp();

        reminderScheduler = mock(ReminderScheduler.class);
        notificationTray = mock(NotificationTray.class);
        preferences = mock(Preferences.class);

        controller = new ReminderController(reminderScheduler,
            notificationTray, preferences);
    }

    @Test
    public void testOnDismiss() throws Exception
    {
        verifyNoMoreInteractions(reminderScheduler);
        verifyNoMoreInteractions(notificationTray);
        verifyNoMoreInteractions(preferences);
    }

    @Test
    public void testOnSnooze() throws Exception
    {
        Habit habit = mock(Habit.class);
        long now = timestamp(2015, 1, 1);
        long nowTz = DateUtils.applyTimezone(now);
        DateUtils.setFixedLocalTime(now);
        when(preferences.getSnoozeInterval()).thenReturn(15L);

        controller.onSnooze(habit);

        verify(reminderScheduler).schedule(habit, nowTz + 900000);
        verify(notificationTray).cancel(habit);
    }

    @Test
    public void testOnShowReminder() throws Exception
    {
        Habit habit = mock(Habit.class);
        controller.onShowReminder(habit, Timestamp.ZERO.plus(100), 456);
        verify(notificationTray).show(habit, Timestamp.ZERO.plus(100), 456);
        verify(reminderScheduler).scheduleAll();
    }

    @Test
    public void testOnBootCompleted() throws Exception
    {
        controller.onBootCompleted();
        verify(reminderScheduler).scheduleAll();
    }
}