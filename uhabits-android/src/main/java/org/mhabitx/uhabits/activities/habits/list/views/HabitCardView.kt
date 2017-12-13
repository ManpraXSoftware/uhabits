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

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Build.VERSION_CODES.M
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import org.mhabitx.androidbase.activities.ActivityContext
import org.mhabitx.uhabits.R
import org.mhabitx.uhabits.activities.common.views.RingView
import org.mhabitx.uhabits.core.models.Habit
import org.mhabitx.uhabits.core.models.ModelObservable
import org.mhabitx.uhabits.core.models.Timestamp
import org.mhabitx.uhabits.core.ui.screens.habits.list.ListHabitsBehavior
import org.mhabitx.uhabits.core.utils.DateUtils
import org.mhabitx.uhabits.utils.PaletteUtils
import org.mhabitx.uhabits.utils.dp
import org.mhabitx.uhabits.utils.sres

@AutoFactory
class HabitCardView(
        @Provided @ActivityContext context: Context,
        @Provided private val checkmarkPanelFactory: CheckmarkPanelViewFactory,
        @Provided private val numberPanelFactory: NumberPanelViewFactory,
        @Provided private val multiTypePanelFactory: MultiTypePanelViewFactory,
        @Provided private val behavior: ListHabitsBehavior
) : FrameLayout(context),
        ModelObservable.Listener {

    var buttonCount
        get() = checkmarkPanel.buttonCount
        set(value) {
            checkmarkPanel.buttonCount = value
            numberPanel.buttonCount = value
            multiPanel.buttonCount = value
        }

    var dataOffset = 0
        set(value) {
            field = value
            checkmarkPanel.dataOffset = value
            numberPanel.dataOffset = value
            multiPanel.dataOffset = value
        }

    var habit: Habit? = null
        set(newHabit) {
            if (isAttachedToWindow) {
                field?.observable?.removeListener(this)
                newHabit?.observable?.addListener(this)
            }
            field = newHabit
            if (newHabit != null) copyAttributesFrom(newHabit)
        }

    var score
        get() = scoreRing.percentage.toDouble()
        set(value) {
            scoreRing.percentage = value.toFloat()
            scoreRing.precision = 1.0f / 16
        }

    var unit
        get() = numberPanel.units
        set(value) {
            numberPanel.units = value
        }

    var values
        get() = checkmarkPanel.values
        set(values) {
            checkmarkPanel.values = values
            numberPanel.values = values.map { it / 1000.0 }.toDoubleArray()
            multiPanel.values = values
        }

    var threshold: Double
        get() = numberPanel.threshold
        set(value) {
            numberPanel.threshold = value
            multiPanel.target = value.toInt()
        }

    private var checkmarkPanel: CheckmarkPanelView
    private var numberPanel: NumberPanelView
    private var multiPanel: MultiTypePanelView
    private var innerFrame: LinearLayout
    private var label: TextView
    private var scoreRing: RingView

    init {
        scoreRing = RingView(context).apply {
            val thickness = dp(3f)
            val margin = dp(8f).toInt()
            val ringSize = dp(15f).toInt()
            layoutParams = LinearLayout.LayoutParams(ringSize, ringSize).apply {
                setMargins(margin, 0, margin, 0)
                gravity = Gravity.CENTER
            }
            setThickness(thickness)
        }

        label = TextView(context).apply {
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f)
            if (SDK_INT >= M) breakStrategy = Layout.BREAK_STRATEGY_BALANCED
        }

        checkmarkPanel = checkmarkPanelFactory.create().apply {
            onToggle = { timestamp ->
                triggerRipple(timestamp)
                habit?.let { behavior.onToggle(it, timestamp) }
            }
        }

        numberPanel = numberPanelFactory.create().apply {
            visibility = GONE
            onEdit = { timestamp ->
                triggerRipple(timestamp)
                habit?.let { behavior.onEdit(it, timestamp) }
            }
        }
        multiPanel = multiTypePanelFactory.create().apply {
            visibility = GONE
            onTap = { timestamp ->
                triggerRipple(timestamp)
                habit?.let { behavior.onChange(it, timestamp,true) }
            }
            onLongTap = { timestamp ->
                triggerRipple(timestamp)
                habit?.let { behavior.onChange(it, timestamp,false) }
            }
        }

        innerFrame = LinearLayout(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            if (SDK_INT >= LOLLIPOP) elevation = dp(1f)

            addView(scoreRing)
            addView(label)
            addView(checkmarkPanel)
            addView(numberPanel)
            addView(multiPanel)

            setOnTouchListener { v, event ->
                if (SDK_INT >= LOLLIPOP)
                    v.background.setHotspot(event.x, event.y)
                false
            }
        }

        clipToPadding = false
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val margin = dp(3f).toInt()
        setPadding(margin, 0, margin, margin)
        addView(innerFrame)
    }

    override fun onModelChange() {
        Handler(Looper.getMainLooper()).post {
            habit?.let { copyAttributesFrom(it) }
        }
    }

    override fun setSelected(isSelected: Boolean) {
        super.setSelected(isSelected)
        updateBackground(isSelected)
    }

    fun triggerRipple(timestamp: Timestamp) {
        val today = DateUtils.getToday()
        val offset = timestamp.daysUntil(today) - dataOffset
        val button: View = when (habit?.isMultiple) {
            true -> multiPanel.buttons[offset]
            else -> checkmarkPanel.buttons[offset]
        }
        val y = button.height / 2.0f
        val x = when (habit?.isMultiple) {
            true -> multiPanel.x + button.x + (button.width / 2).toFloat()
            else -> checkmarkPanel.x + button.x + (button.width / 2).toFloat()
        }

        triggerRipple(x, y)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        habit?.observable?.addListener(this)
    }

    override fun onDetachedFromWindow() {
        habit?.observable?.removeListener(this)
        super.onDetachedFromWindow()
    }

    private fun copyAttributesFrom(h: Habit) {

        fun getActiveColor(habit: Habit): Int {
            return when (habit.isArchived) {
                true -> sres.getColor(R.attr.mediumContrastTextColor)
                false -> PaletteUtils.getColor(context, habit.color)
            }
        }

        val c = getActiveColor(h)
        label.apply {
            text = h.name
            setTextColor(c)
        }
        scoreRing.apply {
            color = c
        }
        checkmarkPanel.apply {
            color = c
            visibility = when (h.isNumerical || h.isMultiple) {
                true -> View.GONE
                false -> View.VISIBLE
            }
        }
        multiPanel.apply {
            color = c
            limit=h.frequency.numerator
            target = h.targetValue.toInt()
            visibility = when (h.isMultiple) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }
        numberPanel.apply {
            color = c
            units = h.unit
            threshold = h.targetValue
            visibility = when (h.isNumerical) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }
    }

    private fun triggerRipple(x: Float, y: Float) {
        val background = innerFrame.background
        if (SDK_INT >= LOLLIPOP) background.setHotspot(x, y)
        background.state = intArrayOf(android.R.attr.state_pressed,
                android.R.attr.state_enabled)
        Handler().postDelayed({ background.state = intArrayOf() }, 25)
    }

    private fun updateBackground(isSelected: Boolean) {
        if (SDK_INT < LOLLIPOP) {
            val background = when (isSelected) {
                true -> sres.getDrawable(R.attr.selectedBackground)
                false -> sres.getDrawable(R.attr.cardBackground)
            }
            innerFrame.setBackgroundDrawable(background)
            return
        }

        val background = when (isSelected) {
            true -> R.drawable.selected_box
            false -> R.drawable.ripple
        }
        innerFrame.setBackgroundResource(background)
    }
}
