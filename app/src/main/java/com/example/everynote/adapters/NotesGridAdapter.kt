package com.example.everynote.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.everynote.R.id
import com.example.everynote.R.layout
import com.example.everynote.models.Note
import java.io.BufferedInputStream
import java.io.FileInputStream

class NotesGridAdapter(
    private val activity: Activity,
    private val notes: ArrayList<Note>,
    private val clickListener: NoteItemClickListener
) : RecyclerView.Adapter<NotesGridAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(id.textTitle)
        val textBody: TextView = view.findViewById(id.textBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.textTitle.text = notes[position].title
        val path = notes[position].body

        val temp = "${activity.filesDir.canonicalFile}/${path}"
        val file = FileInputStream(temp)
        val bis = BufferedInputStream(file)
        val reader = bis.reader()

        val value = reader.readText()
        holder.textBody.text = value
        holder.itemView.setOnClickListener { clickListener.onClick(notes[position]) }

        file.close()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    interface NoteItemClickListener {
        fun onClick(note: Note)
    }
}