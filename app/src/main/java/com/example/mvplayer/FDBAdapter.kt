package com.example.mvplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvplayer.databinding.Row2Binding

class FDBAdapter(val items:ArrayList<DBData>) : RecyclerView.Adapter<FDBAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun OnItemClick(holder:ViewHolder, view: View, data:DBData, position: Int) {
        }
    }
    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: Row2Binding): RecyclerView.ViewHolder(binding.root) {
        init{
            binding.root.setOnClickListener {
                itemClickListener!!.OnItemClick(this,it,items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = Row2Binding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            var url = "https://img.youtube.com/vi/"+items[position].videoid+"/sddefault.jpg"
            Glide.with(holder.itemView).load(url).into(imageView)
            textView.text = items[position].title
            textView2.text = items[position].playtime.toString()
            textView3.text = items[position].playdate
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(pos: Int): String {
        var str = items[pos].title
        items.removeAt(pos)
        notifyItemRemoved(pos)
        return str
    }
}