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
    private val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())

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
        selectedDateTextView.text = "Chosen date : $currentDate"
        updateScheduleList(currentDate)

        // Handle date selection
        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = "${date.year}-${date.month}-${date.day}"
            selectedDateTextView.text = "Chosen date : $selectedDate"
            updateScheduleList(selectedDate)
        }

        // 일정 추가 버튼
        val addScheduleButton = rootView.findViewById<Button>(R.id.addScheduleButton)
        addScheduleButton.setOnClickListener {
            val selectedDate = selectedDateTextView.text.toString().removePrefix("Chosen date : ").trim()
            showAddScheduleDialog(selectedDate)
        }


        highlightSchedules()

        highlightToday()

        return rootView
    }

    private fun highlightToday() {
        calendarView.addDecorator(TodayDecorator(requireContext()))
    }

    private fun highlightSchedules() {
        // 기존 데코레이터 제거
        calendarView.removeDecorators()

        // 현재 저장된 일정들을 다시 가져옴
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

        calendarView.addDecorator(EventDecorator(events, resources.getColor(R.color.pastel_red, null)))
    }

    private fun showAddScheduleDialog(date: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add schedule on $date")

        // 입력 필드를 감쌀 FrameLayout 생성
        val container = FrameLayout(requireContext())
        container.setPadding(45, 20, 45, 16) // 왼쪽, 위, 오른쪽, 아래 패딩 설정 (dp 단위)

        val input = EditText(requireContext())
        input.hint = "Put schedule here."

        // 입력 필드를 FrameLayout에 추가
        container.addView(input)
        builder.setView(container)

        builder.setPositiveButton("ADD") { _, _ ->
            val schedule = input.text.toString().trim()
            if (schedule.isNotEmpty()) {
                addSchedule(date, schedule)
                updateScheduleList(date)
                calendarView.invalidateDecorators()
                Toast.makeText(requireContext(), "Schedule is added now.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Fill the context.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateScheduleList(date: String) {
        val scheduleText = sharedPreferences.getString(date, null)
        val schedules = scheduleText?.split("\n")?.mapIndexed { index, title ->
            ScheduleAdapter.ScheduleItem(
                title = title,
                color = getColorForIndex(index)
            )
        }?.toMutableList() ?: mutableListOf()

        recyclerView.adapter = ScheduleAdapter(schedules) { scheduleToDelete ->
            deleteSchedule(date, scheduleToDelete) // 삭제 처리
        }
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

    private fun deleteSchedule(date: String, scheduleToDelete: String) {
        val existingSchedule = sharedPreferences.getString(date, "") ?: return
        val updatedSchedule = existingSchedule.split("\n").filter { it != scheduleToDelete }.joinToString("\n")

        if (updatedSchedule.isEmpty()) {
            // 모든 일정이 삭제된 경우 키 자체를 제거
            sharedPreferences.edit().remove(date).apply()
        } else {
            // 일정이 남아 있는 경우 업데이트
            sharedPreferences.edit().putString(date, updatedSchedule).apply()
        }

        // 캘린더 데코레이터 갱신
        highlightSchedules()
    }

    private fun moveToMilliApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=com.millies.library") // 밀리의 서재 패키지 이름
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Install the app <Millie's library>.", Toast.LENGTH_SHORT).show()
        }
    }
}
