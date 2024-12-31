package com.example.bookapp.tab3

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var selectedDateTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = rootView.findViewById(R.id.calendarView)
        selectedDateTextView = rootView.findViewById(R.id.tv_selected_date)
        recyclerView = rootView.findViewById(R.id.rv_schedule_list)

        // SharedPreferences for storing schedules
        sharedPreferences = requireContext().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE)
        val moveToMilliButton = rootView.findViewById<FloatingActionButton>(R.id.millieFloatingButton) // 수정된 부분
        moveToMilliButton.setOnClickListener {
            moveToMilliApp()
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize calendar
        val currentDate = dateFormat.format(Calendar.getInstance().time)
        selectedDateTextView.text = "선택된 날짜: $currentDate"
        updateScheduleList(currentDate)

        // Handle date selection
        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = "${date.year}-${date.month}-${date.day}"
            selectedDateTextView.text = "선택된 날짜: $selectedDate"
            updateScheduleList(selectedDate)
        }

        // 일정 추가 버튼
        val addScheduleButton = rootView.findViewById<Button>(R.id.addScheduleButton)
        addScheduleButton.setOnClickListener {
            val selectedDate = selectedDateTextView.text.toString().removePrefix("선택된 날짜: ").trim()
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

        calendarView.addDecorator(EventDecorator(events, resources.getColor(R.color.pastel_blue_primary, null)))
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
        val schedules = scheduleText?.split("\n")?.mapIndexed { index, title ->
            ScheduleAdapter.ScheduleItem(
                title = title,
                color = getColorForIndex(index)
            )
        } ?: listOf()

        recyclerView.adapter = ScheduleAdapter(schedules)
    }

    private fun getColorForIndex(index: Int): Int {
        val colors = listOf(
            resources.getColor(R.color.pastel_red, null),
            resources.getColor(R.color.pastel_green, null),
            resources.getColor(R.color.pastel_yellow, null),
            resources.getColor(R.color.pastel_purple, null)
        )
        return colors[index % colors.size] // 색상을 반복적으로 할당
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

    private fun moveToMilliApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=com.millies.library") // 밀리의 서재 패키지 이름
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "밀리의 서재 앱을 설치해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
