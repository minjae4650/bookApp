package com.example.bookapp.tab3

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bookapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var scheduleListView: ListView
    private lateinit var scheduleListHeader: TextView
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE)

        calendarView = rootView.findViewById(R.id.calendarView)
        scheduleListView = rootView.findViewById(R.id.scheduleListView)
        scheduleListHeader = rootView.findViewById(R.id.scheduleListHeader)
        val addScheduleButton = rootView.findViewById<Button>(R.id.addScheduleButton)
        val selectedDateTextView = rootView.findViewById<TextView>(R.id.selectedDateTextView)

        var selectedDate = dateFormat.format(Calendar.getInstance().time)
        selectedDateTextView.text = "선택된 날짜: $selectedDate"
        updateScheduleList(selectedDate)

        // 날짜 선택 이벤트
        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedDate = "${date.year}-${date.month}-${date.day}"
            selectedDateTextView.text = "선택된 날짜: $selectedDate"
            updateScheduleList(selectedDate)
        }

        // 일정 추가 버튼
        addScheduleButton.setOnClickListener {
            showAddScheduleDialog(selectedDate)
        }

        highlightSchedules()
        return rootView
    }

    private fun highlightSchedules() {
        val events = sharedPreferences.all.keys.mapNotNull {
            val parts = it.split("-")
            if (parts.size == 3) {
                val year = parts[0].toIntOrNull()
                val month = parts[1].toIntOrNull()
                val day = parts[2].toIntOrNull()
                if (year != null && month != null && day != null) {
                    CalendarDay.from(year, month, day)
                } else null
            } else null
        }.toSet()

        val decorator = EventDecorator(events, ContextCompat.getColor(requireContext(), R.color.main_color))
        calendarView.addDecorator(decorator)
    }

    private fun showAddScheduleDialog(date: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("$date 일정 추가")

        val input = EditText(requireContext())
        input.hint = "일정을 입력하세요"
        builder.setView(input)

        builder.setPositiveButton("추가") { _, _ ->
            val schedule = input.text.toString().trim()
            if (schedule.isNotEmpty()) {
                addSchedule(date, schedule)
                updateScheduleList(date)
                calendarView.invalidateDecorators()
                Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateScheduleList(date: String) {
        val scheduleText = sharedPreferences.getString(date, null)
        val schedules = scheduleText?.split("\n") ?: listOf()

        if (schedules.isNotEmpty()) {
            scheduleListHeader.visibility = View.VISIBLE
            scheduleListView.visibility = View.VISIBLE

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, schedules)
            scheduleListView.adapter = adapter
        } else {
            scheduleListHeader.visibility = View.GONE
            scheduleListView.visibility = View.GONE
        }
    }

    private fun addSchedule(date: String, schedule: String) {
        val existingSchedule = sharedPreferences.getString(date, "")
        val updatedSchedule = if (existingSchedule.isNullOrEmpty()) {
            schedule
        } else {
            "$existingSchedule\n$schedule"
        }
        sharedPreferences.edit().putString(date, updatedSchedule).apply()
        highlightSchedules()
    }
}
