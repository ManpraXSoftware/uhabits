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

package org.mhabitx.uhabits.activities.habits.list.views

import android.content.*
import android.util.Log
import com.google.auto.factory.*
import org.mhabitx.androidbase.activities.*
import org.mhabitx.uhabits.core.models.*
import org.mhabitx.uhabits.core.models.Checkmark.*
import org.mhabitx.uhabits.core.preferences.*
import org.mhabitx.uhabits.core.utils.*
import java.util.*

@AutoFactory
class CheckmarkPanelView(
        @Provided @ActivityContext context: Context,
        @Provided preferences: Preferences,
        @Provided private val buttonFactory: CheckmarkButtonViewFactory
) : ButtonPanelView<CheckmarkButtonView>(context, preferences) {

    var values = IntArray(0)
        set(values) {
            field = values
            setupButtons()
        }

    var color = 0
        set(value) {
            field = value
            setupButtons()
        }

    var onToggle: (Timestamp) -> Unit = {}
        set(value) {
            field = value
            setupButtons()
        }

    override fun createButton(): CheckmarkButtonView = buttonFactory.create()

    @Synchronized
    override fun setupButtons() {
        val today = DateUtils.getToday()

        buttons.forEachIndexed { index, button ->
            val timestamp = today.minus(index + dataOffset)
            print("timestamp ==> " + timestamp.toString())
            print("dataOffset ==> " + dataOffset)
            print("index ==> " + index)
            print("index+offset ==> " + (index+dataOffset))
            print("values ==> " + Arrays.toString(values))
            button.value = when {
                index + dataOffset < values.size -> values[index + dataOffset]
                else -> UNCHECKED
            }
            button.color = color
            button.onToggle = { onToggle(timestamp) }
            print("color ==> " + color)
            print("value ==> " + button.value)
        }
    }
    fun print(msg:String){
        Log.d("MK==> ",""+msg)
    }
}
