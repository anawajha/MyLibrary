package com.anawajha.mylibrary

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.anawajha.mylibrary.databinding.ActivityAddBookBinding
import com.anawajha.mylibrary.firebase.FirestoreOperations
import com.anawajha.mylibrary.helpers.Helpers.Companion.getDate
import com.anawajha.mylibrary.helpers.Helpers.Companion.showSnackBar
import com.anawajha.mylibrary.model.Book
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AddBook : AppCompatActivity() {
    lateinit var binding:ActivityAddBookBinding
    lateinit var timestamp:Timestamp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddBook.setOnClickListener {
            if (validate()){
                FirestoreOperations.addBook(
                    Book(null, binding.edBookName.text.toString(),binding.edBookAuthor.text.toString(),
                timestamp, binding.edPrice.text!!.toString().toDouble(),binding.ratingBar.rating)
                , this,findViewById(android.R.id.content))
            }
        }

        binding.edBookYear.setOnClickListener {
            val date = Calendar.getInstance()
            val piker = DatePickerDialog(
                this,
                { picker, y, m, d ->
                    binding.edBookYear.setText(y.toString())
                    timestamp = Timestamp(Date(y - 1899,0,0))
                    Log.d("Books","${getDate(timestamp.seconds)}")
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                piker.show()
        }
    }

    private fun validate():Boolean{
        if (binding.edBookName.text!!.isNotEmpty()){
            if (binding.edBookAuthor.text!!.isNotEmpty()){
                if (binding.edBookYear.text!!.isNotEmpty() && timestamp != null){
                    if (binding.edPrice.text!!.isNotEmpty()){
                        if (binding.ratingBar.rating != 0f){
                            return true
                        }else{
                            showSnackBar("Please add book rating",Snackbar.LENGTH_SHORT, Color.RED,findViewById(android.R.id.content))
                            return false
                        }
                    }else{
                        binding.tlPrice.error = "Price is required"
                    }
                }else{
                    binding.tlBookYear.error = "Launch year is required"
                }
            }else{
                binding.tlBookAuthor.error = "Book author is required"
            }
        }else{
            binding.tlBookName.error = "Book name is required"
        }
        return false
    }
}