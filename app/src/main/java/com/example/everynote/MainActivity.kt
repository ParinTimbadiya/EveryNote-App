package com.example.everynote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.everynote.adapters.NotesGridAdapter
import com.example.everynote.helpers.SQLiteHelper
import com.example.everynote.models.Note
import com.example.everynote.services.NotesService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var userId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesService: NotesService
    private lateinit var notes: ArrayList<Note>
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences("myapp_prefs", MODE_PRIVATE)
        userId = sharedPrefs.getString("UserId", null)

        if (userId == null) {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            return
        }

        findViewById<FloatingActionButton>(R.id.btnAddNote).setOnClickListener {
            intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
            finish()
        }

        sqliteHelper = SQLiteHelper(this)
        notesService = NotesService()
        notes = sqliteHelper.getAllNotes()

        init()

    }

    private fun init() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        recyclerView.adapter =
            NotesGridAdapter(this, notes, object : NotesGridAdapter.NoteItemClickListener {
                override fun onClick(note: Note) {
                    val intent = Intent(this@MainActivity, EditNoteActivity::class.java)
                    intent.putExtra("noteNumber", note.number)
                    startActivity(intent)
                    finish()
                }
            })
    }
}