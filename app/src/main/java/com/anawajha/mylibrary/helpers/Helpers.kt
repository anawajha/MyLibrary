package com.anawajha.mylibrary.helpers

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Helpers {
    companion object{
        fun showSnackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT, color: Int,view: View) {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setBackgroundTint(color).show()
        }

        fun getDate(seconds: Long): String? {
            val formatter = SimpleDateFormat("yyyy")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = seconds * 1000
            return formatter.format(calendar.time)
        }
    }
}