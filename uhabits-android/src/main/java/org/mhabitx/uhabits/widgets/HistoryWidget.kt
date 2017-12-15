/*
 * Copyright (C) 2016 Alinson Santos Xavier <mhabitx@gmail.com>
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

package org.mhabitx.uhabits.widgets

import android.app.*
import android.content.*
import android.view.*
import org.mhabitx.uhabits.activities.common.views.*
import org.mhabitx.uhabits.core.models.*
import org.mhabitx.uhabits.utils.*
import org.mhabitx.uhabits.widgets.views.*

class HistoryWidget(
        context: Context,
        id: Int,
        private val habit: Habit
) : BaseWidget(context, id) {

    override fun getOnClickPendingIntent(context: Context): PendingIntent {
        return pendingIntentFactory.showHabit(habit)
    }

    override fun refreshData(view: View) {
        val widgetView = view as GraphWidgetView
        (widgetView.dataView as HistoryChart).apply {
            setColor(PaletteUtils.getColor(context, habit.color))
            setCheckmarks(habit.checkmarks.allValues)
        }
    }

    override fun buildView() =
            GraphWidgetView(context, HistoryChart(context)).apply {
                setTitle(habit.name)
            }

    override fun getDefaultHeight() = 250
    override fun getDefaultWidth() = 250
}