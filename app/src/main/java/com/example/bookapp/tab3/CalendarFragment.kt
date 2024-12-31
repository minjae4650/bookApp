package com.example.bookapp.tab3

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.bookapp.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE)

        val calendarView = rootView.findViewById<CalendarView>(R.id.calendarView)
        val selectedDateTextView = rootView.findViewById<TextView>(R.id.selectedDateTextView)
        val scheduleEditText = rootView.findViewById<EditText>(R.id.scheduleEditText)
        val addScheduleButton = rootView.findViewById<Button>(R.id.addScheduleButton)
        val scheduleListTextView = rootView.findViewById<TextView>(R.id.scheduleListTextView)

        // 현재 날짜를 텍스트뷰에 표시
        val currentDate = Calendar.getInstance().time
        var selectedDate = dateFormat.format(currentDate)
        selectedDateTextView.text = "선택된 날짜: $selectedDate"

        // 선택된 날짜의 일정 불러오기
        scheduleListTextView.text = getSchedule(selectedDate)

        // 날짜 선택 이벤트
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            selectedDateTextView.text = "선택된 날짜: $selectedDate"
            scheduleListTextView.text = getSchedule(selectedDate)
        }

        // 일정 추가 버튼
        addScheduleButton.setOnClickListener {
            val newSchedule = scheduleEditText.text.toString().trim()
            if (newSchedule.isNotEmpty()) {
                val updatedSchedule = addSchedule(selectedDate, newSchedule)
                scheduleListTextView.text = updatedSchedule
                scheduleEditText.text.clear()
                Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    // SharedPreferences에서 특정 날짜의 일정 불러오기
    private fun getSchedule(date: String): String {
        return sharedPreferences.getString(date, "등록된 일정이 없습니다.") ?: "등록된 일정이 없습니다."
    }

    // 특정 날짜에 일정 추가 후 업데이트된 일정 반환
    private fun addSchedule(date: String, newSchedule: String): String {
        val existingSchedule = getSchedule(date)
        val updatedSchedule = if (existingSchedule == "등록된 일정이 없습니다.") {
            newSchedule
        } else {
            "$existingSchedule\n$newSchedule"
        }
        sharedPreferences.edit().putString(date, updatedSchedule).apply()
        return updatedSchedule
    }
}