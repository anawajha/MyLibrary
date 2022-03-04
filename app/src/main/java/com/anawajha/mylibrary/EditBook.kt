package com.anawajha.mylibrary

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.anawajha.mylibrary.databinding.ActivityEditBookBinding
import com.anawajha.mylibrary.firebase.FirestoreOperations
import com.anawajha.mylibrary.helpers.Helpers
import com.anawajha.mylibrary.helpers.Helpers.Companion.getDate
import com.anawajha.mylibrary.model.Book
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz.id
import java.util.*

class EditBook : AppCompatActivity() {
    lateinit var binding:ActivityEditBookBinding
    lateinit var fire:FirebaseFirestore
    var book:Book? = null
    var path:String? = null
    var timestamp:Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var id = intent.getStringExtra("id")
        Log.d("Books","Edit id :$id")

        fire = Firebase.firestore

            fire.collection("Books").whereEqualTo("id",id).get().addOnSuccessListener {
                   book = it.documents.first().toObject(Book::class.java)
                    it.let {
                        path = it.documents.first().id
                        binding.edBookName.setText(book?.name.toString())
                        binding.edBookAuthor.setText(book?.author.toString())
                        binding.edBookYear.setText(getDate(book?.year!!.seconds))
                        binding.edPrice.setText(book?.price.toString())
                        binding.ratingBar.rating = book?.rate!!.toFloat()
                    }
            }// getSingleBookWithId

        binding.btnEditBook.setOnClickListener {
            if (path != null){
                if(timestamp == null){
                    timestamp = book!!.year!!
                }
                FirestoreOperations.updateBook(path!!, Book(null,binding.edBookName.text.toString(),
                    binding.edBookAuthor.text.toString(),timestamp,binding.edPrice.text.toString().toDouble(),binding.ratingBar.rating),this)            }
        }

        binding.btnDelete.setOnClickListener {
            if (path != null)
                FirestoreOperations.deleteBook(this,path!!)
        }

        binding.edBookYear.setOnClickListener {
            val date = Calendar.getInstance()
            val piker = DatePickerDialog(
                this,
                { picker, y, m, d ->
                    binding.edBookYear.setText(y.toString())
                    timestamp = Timestamp(Date(y - 1899,0,0))
                    Log.d("Books","${getDate(timestamp!!.seconds)}")
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
            piker.show()
        }
        /**
         * realtime
                fire.collection("Books").whereEqualTo("id",id).addSnapshotListener { value, error ->
            value.let {
                book = value!!.documents.first().toObject(Book::class.java)
                path = value.documents.first().id
                binding.edBookName.setText(book?.name.toString())
                binding.edBookAuthor.setText(book?.author.toString())
                binding.edBookYear.setText(getDate(book?.year!!.seconds))
                binding.edPrice.setText(book?.price.toString())
                binding.ratingBar.rating = book?.rate!!.toFloat()
            }
            error.let {
            }
        }
                **/
    }

    fun dataChanged(){
        if (binding.edBookName.text.toString() == book!!.name){
            if (binding.edBookAuthor.text.toString() == book!!.author){
                if (binding.edBookYear.text.toString() == getDate(book!!.year!!.seconds)){
                    if (binding.edPrice.text as Double == book!!.price){
                        if (binding.ratingBar.rating == book!!.rate){
                            FirestoreOperations.updateBook(path!!, Book(null,binding.edBookName.text.toString(),
                                binding.edBookAuthor.text.toString(),timestamp,binding.edPrice.text as Double,binding.ratingBar.rating),this)
                        }
                    }
                }
            }
        }
    }

    fun validate():Boolean{
        if (binding.edBookName.text!!.isNotEmpty()){
            if (binding.edBookAuthor.text!!.isNotEmpty()){
                if (binding.edBookYear.text!!.isNotEmpty() && timestamp != null){
                    if (binding.edPrice.text!!.isNotEmpty()){
                        if (binding.ratingBar.rating != 0f){
                            return true
                        }else{
                            Helpers.showSnackBar(
                                "Please add book rating",
                                Snackbar.LENGTH_SHORT,
                                Color.RED,
                                findViewById(R.id.content)
                            )
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