package com.anawajha.mylibrary

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.anawajha.mylibrary.databinding.ActivityAddBookBinding
import com.anawajha.mylibrary.firebase.FirestoreOperations
import com.anawajha.mylibrary.helpers.Helpers.Companion.getDate
import com.anawajha.mylibrary.helpers.Helpers.Companion.showSnackBar
import com.anawajha.mylibrary.helpers.Utilities
import com.anawajha.mylibrary.model.Book
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddBook : AppCompatActivity() {
    lateinit var binding:ActivityAddBookBinding
    lateinit var timestamp:Timestamp
    private val TAG = "Books"
    private var imageUri:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddBook.setOnClickListener {
            if (validate()){
               FirestoreOperations.addBook(
                   Book(null, binding.edBookName.text.toString(),binding.edBookAuthor.text.toString(),
                       timestamp, binding.edPrice.text!!.toString().toDouble(),binding.ratingBar.rating)
                   , this,imageUri!!)
            }
        }

        binding.edBookYear.setOnClickListener {
            val date = Calendar.getInstance()
            val piker = DatePickerDialog(
                this,
                { picker, y, m, d ->
                    binding.edBookYear.setText(y.toString())
                    timestamp = Timestamp(Date(y - 1899,0,0))
                    Log.d(TAG,"${getDate(timestamp.seconds)}")
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                piker.show()
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    Log.d(TAG, "${uri.toString()}")
                    val file = Utilities.getFile(applicationContext, uri!!)
                    var new_uri = Uri.fromFile(file)
                    Log.d(TAG, "${new_uri.toString()}")
                    binding.edImagePath.setText(new_uri.lastPathSegment)

                    var metadata = storageMetadata {
                        contentType = "image/jpg"
                    }
                    imageUri = new_uri
                }
            }

        binding.edImagePath.setOnClickListener { ed ->
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select an image"))
        }
    }


    private fun validate():Boolean{
        if (binding.edBookName.text!!.isNotEmpty()){
            if (binding.edBookAuthor.text!!.isNotEmpty()){
                if (binding.edBookYear.text!!.isNotEmpty() && timestamp != null){
                    if (binding.edPrice.text!!.isNotEmpty()){
                        if (binding.ratingBar.rating != 0f){
                            return imageUri != null
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