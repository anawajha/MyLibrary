package com.anawajha.mylibrary.helpers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

abstract class Utilities {
    companion object{
        private val TAG = "Books"
         fun getFile(context: Context?, uri: Uri): File? {
            val dfn:File = File(context!!.filesDir.path + File.pathSeparator + queryName(context,uri))
            try {
                context.contentResolver.openInputStream(uri).use {
                    createFileFromStream(it!!,dfn)
                }
            }catch (e:Exception){
                Log.d(TAG,e.localizedMessage)
            }
            return dfn

        }

         private fun createFileFromStream(fis: InputStream, dfn: File?) {
            try {
                FileOutputStream(dfn).use { os ->
                    val buffer = ByteArray(4096)
                    var length: Int
                    while (fis.read(buffer).also { length = it } > 0) {
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                }
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage)
            }
        }

         private fun queryName(context: Context, uri: Uri): String? {
            val rc: Cursor = context.contentResolver.query(uri,null,null,null,null)!!
            val nameIndex:Int = rc.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            rc.moveToFirst()
            val name:String = rc.getString(nameIndex)
            rc.close()
            return name
        }
    }
}