package com.example.bookapp.tab3

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.bookapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator(context: Context) : DayViewDecorator {
    private val today: CalendarDay = CalendarDay.today()
    private val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.calendar_circle_today)
    private val textColor = ContextCompat.getColor(context, R.color.black)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == today
    }

    override fun decorate(view: DayViewFacade?) {
        view?.apply {
            setBackgroundDrawable(backgroundDrawable!!)
            addSpan(ForegroundColorSpan(textColor))
        }
    }
}
