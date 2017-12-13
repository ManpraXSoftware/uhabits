package org.mhabitx.uhabits.activities.habits.list.views

import android.content.Context
import android.util.Log
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import org.mhabitx.androidbase.activities.ActivityContext
import org.mhabitx.uhabits.core.models.Checkmark
import org.mhabitx.uhabits.core.models.Timestamp
import org.mhabitx.uhabits.core.preferences.Preferences
import org.mhabitx.uhabits.core.utils.DateUtils
import java.util.*

/**
 * Created by Prateek on 13-12-2017.
 */
@AutoFactory
class MultiTypePanelView(@Provided @ActivityContext context: Context,
                         @Provided preferences: Preferences,
                         @Provided private val buttonFactory: MultiButtonViewFactory
) : ButtonPanelView<MultiButtonView>(context, preferences) {

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
    var target: Int = 0
        set(value) {
            field = value
            setupButtons()
        }
    var limit: Int = 72
        set(value) {
            field = value
            setupButtons()
        }

    var onTap: (Timestamp) -> Unit = {}
        set(value) {
            field = value
            setupButtons()
        }
    var onLongTap: (Timestamp) -> Unit = {}
        set(value) {
            field = value
            setupButtons()
        }

    override fun createButton(): MultiButtonView = buttonFactory.create()

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
                else -> Checkmark.UNCHECKED
            }
            button.color = color
            button.target = target
            button.limit = limit
            button.onTap = { onTap(timestamp) }
            button.onLongTap = { onLongTap(timestamp) }
            print("color ==> " + color)
            print("target ==> " + target)
            print("value ==> " + button.value)
        }
    }
    fun print(msg:String){
        Log.d("MK==> ","MultiType: "+msg)
    }
}