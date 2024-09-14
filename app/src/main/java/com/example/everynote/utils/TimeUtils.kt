package com.example.everynote.utils

import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    companion object {
        fun getCurrentDateTime(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val time = Calendar.getInstance().time
            return formatter.format(time)
        }

        fun getCurrentTimeStamp(): Long {
            val time = Calendar.getInstance().time
            return time.time
        }
    }
}

