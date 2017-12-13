package org.mhabitx.uhabits.activities.habits.list.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.view.HapticFeedbackConstants
import android.view.View
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import org.mhabitx.androidbase.activities.ActivityContext
import org.mhabitx.androidbase.utils.InterfaceUtils
import org.mhabitx.androidbase.utils.StyledResources
import org.mhabitx.uhabits.R
import org.mhabitx.uhabits.core.preferences.Preferences
import org.mhabitx.uhabits.utils.showMessage

/**
 * Created by Mukesh Kumar Maurya on 07-12-2017.
 */
@AutoFactory
class MultiButtonView(
        @Provided @ActivityContext context: Context,
        @Provided val preferences: Preferences
) : View(context),
        View.OnClickListener,
        View.OnLongClickListener {
    init {
        isFocusable = false
        setOnClickListener(this)
        setOnLongClickListener(this)
        isHapticFeedbackEnabled = true
    }
    var color: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    var value: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var target: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var limit: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var onTap: () -> Unit = {}
    var onLongTap: () -> Unit = {}
    private var drawer = Drawer()


    override fun onClick(v: View) {
        if (value < limit) { //target<=Limit
            value++
            onTap()
            showMessage("taped -> value: "+value)
            invalidate()
        }else showMessage("Limit must not exceed.")
    }


    override fun onLongClick(v: View): Boolean {
        if (value > 0) {
            value--
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            showMessage("longTapped-> value: "+value)
            onLongTap()
            invalidate()
        }else showMessage("Value must be positive.")
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawer.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = InterfaceUtils.getDimension(context, R.dimen.checkmarkWidth).toInt()
        val height = InterfaceUtils.getDimension(context, R.dimen.checkmarkHeight).toInt()
        setMeasuredDimension(width, height)
    }

    private inner class Drawer {
        private val BOLD_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        private val NORMAL_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
        private val em: Float
        private val rect: RectF = RectF()
        private val sr = StyledResources(context)
        private val lightGrey: Int
        private val darkGrey: Int
        //  private val lowContrastColor = sres.getColor(R.attr.lowContrastTextColor)

        private val pRegular: TextPaint = TextPaint().apply {
            textSize = InterfaceUtils.getDimension(context, R.dimen.smallerTextSize)
            typeface = NORMAL_TYPEFACE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        private val pBold: TextPaint = TextPaint().apply {
            textSize = InterfaceUtils.getDimension(context, R.dimen.smallTextSize)
            typeface = BOLD_TYPEFACE
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        init {
            em = pBold.measureText("m")
            lightGrey = sr.getColor(R.attr.lowContrastTextColor)
            darkGrey = sr.getColor(R.attr.mediumContrastTextColor)
        }

        fun draw(canvas: Canvas) {
            val activeColor = when {
                value == 0 -> lightGrey
                value < target -> darkGrey
                else -> color
            }

            val label = "" + value
            pBold.color = activeColor
            pRegular.color = activeColor
            rect.set(0f, 0f, width.toFloat(), height.toFloat())
            rect.offset(0f, 0.4f * em)
            canvas.drawText(label, rect.centerX(), rect.centerY(), pBold)
        }
    }
}