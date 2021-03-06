package com.isaiahvonrundstedt.fokus.components.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.fokus.R
import com.isaiahvonrundstedt.fokus.components.delegates.SwipeDelegate
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseListAdapter

class ItemSwipeCallback<T : SwipeDelegate>(context: Context, private var t: T)
    : ItemTouchHelper.Callback() {

    private var icon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_outline_delete_24)
    private var background: ColorDrawable = ColorDrawable(Color.parseColor("#ea4335"))

    override fun getMovementFlags(recyclerView: RecyclerView,
                                  viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.START)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        t.onSwipe(viewHolder.adapterPosition, direction)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 20

        with(background) {
            setBounds(itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top,
                itemView.right, itemView.bottom)
            draw(c)
        }

        icon?.let {
            it.setTint(Color.WHITE)

            val iconMargin: Int = (itemView.height - it.intrinsicHeight) / 2

            val iconTop: Int = itemView.top + (itemView.height - it.intrinsicHeight) / 2
            val iconBottom: Int = iconTop + it.intrinsicHeight
            val iconLeft: Int = itemView.right - iconMargin - it.intrinsicWidth
            val iconRight: Int = itemView.right - iconMargin

            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            it.draw(c)
        }
    }
}