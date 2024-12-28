package com.example.bookapp.tab3

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

    private val scheduleMap = HashMap<String, String>() // 날짜와 일정을 저장할 Map
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // 날짜 포맷

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        val calendarView = rootView.findViewById<CalendarView>(R.id.calendarView)
        val selectedDateTextView = rootView.findViewById<TextView>(R.id.selectedDateTextView)
        val scheduleEditText = rootView.findViewById<EditText>(R.id.scheduleEditText)
        val addScheduleButton = rootView.findViewById<Button>(R.id.addScheduleButton)
        val scheduleListTextView = rootView.findViewById<TextView>(R.id.scheduleListTextView)

        // 현재 날짜를 텍스트뷰에 표시
        val currentDate = Calendar.getInstance().time
        var selectedDate = dateFormat.format(currentDate)
        selectedDateTextView.text = "선택된 날짜: $selectedDate"

        // 초기 스케줄 표시
        scheduleListTextView.text = scheduleMap[selectedDate] ?: "등록된 일정이 없습니다."

        // 날짜 선택 이벤트
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            selectedDateTextView.text = "선택된 날짜: $selectedDate"
            scheduleListTextView.text = scheduleMap[selectedDate] ?: "등록된 일정이 없습니다."
        }

        // 일정 추가 버튼
        addScheduleButton.setOnClickListener {
            val newSchedule = scheduleEditText.text.toString().trim()
            if (newSchedule.isNotEmpty()) {
                val existingSchedules = scheduleMap[selectedDate]
                scheduleMap[selectedDate] = if (existingSchedules != null) {
                    "$existingSchedules\n$newSchedule"
                } else {
                    newSchedule
                }
                scheduleEditText.text.clear()
                scheduleListTextView.text = scheduleMap[selectedDate]
                Toast.makeText(requireContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "일정을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }
}
