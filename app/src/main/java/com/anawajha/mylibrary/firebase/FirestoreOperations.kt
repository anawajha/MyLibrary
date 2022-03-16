package com.anawajha.mylibrary.firebase

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.google.firebase.storage.ktx.storage

class FirestoreOperations {
    companion object{

        @SuppressLint("StaticFieldLeak")
        private val fire = Firebase.firestore
        private val storage = Firebase.storage
        private val ref = storage.reference
        private val TAG = "Books"

        fun addBook(book: Book,context:Context,uri: Uri){
            var progress = ProgressDialog(context)
            progress.setTitle("Loading")
            progress.show()
            fire.collection("Books").add(book.toMap()).addOnSuccessListener {
                uploadImage(it.id,context,uri)
                progress.dismiss()
                Toast.makeText(context,"Book added successfully",Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context,MainActivity::class.java))
                    AddBook().finish()
            }.addOnFailureListener {
                progress.dismiss()
                Toast.makeText(context,"Book add failed",Toast.LENGTH_SHORT).show()
            }
        }// addBook

        fun updateBook(path:String, book:Book, context: Context,uri: Uri?){
            var progress = ProgressDialog(context)
            progress.setTitle("Loading")
            progress.show()
            fire.collection("Books").document(path).update(book.toMapAsUpdate()).addOnSuccessListener {
                if (uri != null){
                    uploadImage(path,context,uri)
                    deleteImage(book.imagePath!!)
                }
                progress.dismiss()
                Toast.makeText(context,"Book updated successfully",Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context,MainActivity::class.java))
                EditBook().finish()
            }.addOnFailureListener {
                progress.dismiss()
                Toast.makeText(context,"Book update failed",Toast.LENGTH_SHORT).show()
            }
        }// updateBook

        private fun updateField(id:String, map:HashMap<String,Any>){
            fire.collection("Books").document(id).update(map).addOnSuccessListener {
            }.addOnFailureListener {
                Log.d(TAG,id)
            }
        }

        fun deleteBook(context: Context, path: String, imagePath:String) {
            fire.collection("Books").document(path).delete().addOnSuccessListener {
                deleteImage(imagePath)
                context.startActivity(Intent(context,MainActivity::class.java))
                EditBook().finish()
                Toast.makeText(context,"Book deleted successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context,"Book delete failed",Toast.LENGTH_SHORT).show()
            }
        }// deleteBook

        private fun uploadImage(id:String, context: Context, uri:Uri):Boolean{
            var deleted = false
            val path = ref.child("images/${Timestamp.now().seconds*1000}${uri.lastPathSegment}")
            path.putFile(uri).addOnCompleteListener {
              if (it.isComplete && it.isSuccessful){
      //            Toast.makeText(context,"Image uploaded successfully",Toast.LENGTH_SHORT).show()
       //   Log.d(TAG,"image name : ${path.name}")
                  path.downloadUrl.addOnSuccessListener { uri->
                     val map = hashMapOf<String,Any>("image" to uri.toString(), "imagePath" to uri.path!!.substring(uri.path!!.indexOf("images")))
                      updateField(id,map)
                      Log.d(TAG,"image name : ${uri.path!!.substring(uri.path!!.indexOf("images"))}")
                      deleted = true
                  }
              }else{
                  Log.d(TAG,it.exception!!.localizedMessage)
                  Toast.makeText(context,"Image upload failed",Toast.LENGTH_SHORT).show()
                  deleted = false
              }
            }
            return deleted
        }// uploadImage

        private fun deleteImage(imagePath: String){
            storage.reference.child(imagePath).delete().addOnSuccessListener {
                Log.d(TAG,"image deleted with path : $imagePath")
            }
        }
    }// companion object



}