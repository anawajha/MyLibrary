package com.anawajha.mylibrary.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anawajha.mylibrary.EditBook
import com.anawajha.mylibrary.R
import com.anawajha.mylibrary.databinding.BookItemBinding
import com.anawajha.mylibrary.helpers.Helpers.Companion.getDate
import com.anawajha.mylibrary.model.Book
import com.squareup.picasso.Picasso

class BooksAdapter(val context: Context, val books: ArrayList<Book>) :RecyclerView.Adapter<BooksAdapter.BooksViewHolder>(){
    class BooksViewHolder(binding:BookItemBinding) :RecyclerView.ViewHolder(binding.root) {
        var cover = binding.imgBookCover
        var name = binding.tvBookName
        var author = binding.tvBookAuthor
        var year = binding.tvBookLaunchYear
        var price = binding.tvBookPrice
        var rate = binding.ratingBar
        var btnEdit = binding.btnEdit
        var rate_text = binding.tvRate
        var divider = binding.divider
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
       // holder.id.text = "${position +1}"
        Picasso.get().load(books[position].image.toString()).placeholder(R.drawable.ic_image_grey).into(holder.cover)
//        Log.d("Books","Book url :${books[position].image}")
        holder.name.text = books[position].name
        holder.author.text = books[position].author
        holder.year.text = getDate(books[position].year!!.seconds)
        holder.price.text = books[position].price.toString() + "$"
        holder.rate.rating = books[position].rate!!
        holder.rate_text.text = books[position].rate.toString()

        holder.btnEdit.setOnClickListener {
            val i = Intent(context,EditBook::class.java)
            i.putExtra("id",books[position].id)
            context.startActivity(i)
        }

        if (position == books.size -1){
            holder.divider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}