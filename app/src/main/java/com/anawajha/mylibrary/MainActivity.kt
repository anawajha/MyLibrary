package com.anawajha.mylibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.anawajha.mylibrary.adapter.BooksAdapter
import com.anawajha.mylibrary.databinding.ActivityMainBinding
import com.anawajha.mylibrary.firebase.FirestoreOperations
import com.anawajha.mylibrary.model.Book
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var fire:FirebaseFirestore
    lateinit var adapter:BooksAdapter
    var books = ArrayList<Book>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fire = Firebase.firestore

        binding.fabAddBook.setOnClickListener {
            startActivity(Intent(this,AddBook::class.java))
        }

        adapter = BooksAdapter(this,books)
        binding.rvBooks.adapter = adapter
        binding.rvBooks.layoutManager = LinearLayoutManager(this)

        getBooks()
    }

    // realtime
    private fun getBooks(){
        fire.collection("Books").addSnapshotListener { value, error ->
            error.let {
                return@let
            }
            value.let {
                value?.documentChanges!!.forEach { dc ->
                    if (dc.type == DocumentChange.Type.ADDED){
                        books.add(dc.document.toObject(Book::class.java))
                    }else if(dc.type == DocumentChange.Type.MODIFIED){
                        val book = dc.document.toObject(Book::class.java)
                        val i = books.indexOfFirst {
                            it.id == book.id
                        }
                        books.set(i,book)
                    }else if(dc.type == DocumentChange.Type.REMOVED){
                        books.removeIf { b ->
                            b.id == dc.document.toObject(Book::class.java).id
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }// getBooks

}