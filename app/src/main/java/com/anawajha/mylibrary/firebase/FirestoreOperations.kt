package com.anawajha.mylibrary.firebase

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.anawajha.mylibrary.AddBook
import com.anawajha.mylibrary.EditBook
import com.anawajha.mylibrary.MainActivity
import com.anawajha.mylibrary.helpers.Helpers.Companion.showSnackBar
import com.anawajha.mylibrary.model.Book
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreOperations {
    companion object{// companion object

        private val fire = Firebase.firestore
        fun addBook(book: Book,context:Context,view: View){
            var progress = ProgressDialog(context)
            progress.setTitle("Loading")
            progress.show()
            fire.collection("Books").add(book.toMap()).addOnSuccessListener {
                Log.d("Books","new book added with id : ${it.id}")
                progress.dismiss()
                showSnackBar("Book added successfully",Snackbar.LENGTH_SHORT, Color.GREEN,view)
                context.startActivity(Intent(context,MainActivity::class.java))
                AddBook().finish()
            }.addOnFailureListener {
                progress.dismiss()
                showSnackBar("Book added failed",Snackbar.LENGTH_SHORT, Color.RED,view)
            }
        }// addBook

        fun updateBook(path:String, book:Book, context: Context){
            var progress = ProgressDialog(context)
            progress.setTitle("Loading")
            progress.show()
            fire.collection("Books").document(path).update(book.toMapAsUpdate()).addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(context,"Book updated successfully",Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context,MainActivity::class.java))
                EditBook().finish()
            }.addOnFailureListener {
                progress.dismiss()
                Toast.makeText(context,"Book update failed",Toast.LENGTH_SHORT).show()
            }
        }// updateBook

        fun deleteBook(context: Context, path: String) {
            fire.collection("Books").document(path).delete().addOnSuccessListener {
                context.startActivity(Intent(context,MainActivity::class.java))
                EditBook().finish()
                Toast.makeText(context,"Book deleted successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context,"Book delete failed",Toast.LENGTH_SHORT).show()
            }
        }// deleteBook

//        fun getSingleBook(id:String) : Book? {
//            var book :Book?
//
//
//            return book
//        }
    }


}