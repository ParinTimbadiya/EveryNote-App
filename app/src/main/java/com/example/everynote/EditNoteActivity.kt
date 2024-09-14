package com.example.everynote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everynote.helpers.SQLiteHelper
import com.example.everynote.models.Note
import com.example.everynote.services.AuthService
import com.example.everynote.services.NotesService
import com.example.everynote.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream

class EditNoteActivity : AppCompatActivity() {

    private lateinit var textTitle: EditText
    private lateinit var textBody: EditText

    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView
    private lateinit var btnDelete: ImageView

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var path: String

    private lateinit var authService: AuthService
    private lateinit var notesService: NotesService

    private lateinit var note: Note

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val sharedPrefs = getSharedPreferences("myapp_prefs", MODE_PRIVATE)
        userId = sharedPrefs.getString("UserId", null)

        authService = AuthService()
        notesService = NotesService()

        sqliteHelper = SQLiteHelper(this)

        textTitle = findViewById(R.id.textTitle)
        textBody = findViewById(R.id.textBody)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack.setOnClickListener {
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val noteNumber = intent.getIntExtra("noteNumber", -1)
        if (noteNumber == -1) {
            btnSave.setOnClickListener { insert() }
        } else {
            fetchData(noteNumber)
            btnSave.setOnClickListener {
                updateData(noteNumber)

                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            btnDelete.setOnClickListener {

                delete(noteNumber)
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun insert() {


        val value = textBody.text.toString()

        val currentDateTime = TimeUtils.getCurrentTimeStamp().toString()
        val body = "${userId}_${currentDateTime}.txt"

        val temp = "${filesDir}/${body}"
        val file = FileOutputStream(temp)
        val bos = BufferedOutputStream(file)
        val writer = bos.writer()

        writer.write(value)
        writer.flush()
        file.close()

        val title = textTitle.text.toString()
        val note = Note(title = title, body = body, lastModified = currentDateTime)

        note.userId = userId?.toInt()!!
        note.number = sqliteHelper.insert(note)

        CoroutineScope(Dispatchers.IO).launch {
            val res = notesService.addNote(note)
            val response = notesService.upload(filesDir.absolutePath, note)

            if (res.code == 201) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note Title Added Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        " Something Went Wrong, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (response.code == 201) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note uploaded Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note does note uploaded, Something Went Wrong, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchData(number: Int) {
        val note = sqliteHelper.getNote(number)

        textTitle.setText(note.title)
        path = note.body

        val file = openFileInput(path)
        val bis = BufferedInputStream(file)
        val reader = bis.reader()
        val value = reader.readText()

        textBody.setText(value)
        file.close()
    }

    private fun updateData(number: Int) {

        val sharedPrefs = getSharedPreferences("myapp_prefs", MODE_PRIVATE)
        val userId = sharedPrefs.getString("UserId", null)

        val value = textBody.text.toString()
        val file = openFileOutput(path, MODE_PRIVATE)
        val bos = BufferedOutputStream(file)

        val writer = bos.writer()

        writer.write(value)
        writer.flush()
        file.close()

        val currentDateTime = TimeUtils.getCurrentTimeStamp().toString()
        val title = textTitle.text.toString()
        val note = Note(
            number = number,
            title = title,
            body = path,
            lastModified = currentDateTime,
            userId = userId?.toInt()!!
        )

        sqliteHelper.update(note)
        CoroutineScope(Dispatchers.IO).launch {
            val res = notesService.updateNote(note)

            if (res.code == 201) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note uploaded Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val response = notesService.upload(filesDir.absolutePath, note)

                if (response.code == 201) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditNoteActivity,
                            "Note uploaded Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditNoteActivity,
                            "Note does note uploaded, Something Went Wrong, Please Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note does note uploaded, Something Went Wrong, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun delete(number: Int){

        sqliteHelper.delete(number)

        note = sqliteHelper.getDeleteNote(number)
        note.userId = userId!!.toInt()
        CoroutineScope(Dispatchers.IO).launch {
            val res = notesService.deleteNote(note)

            if (res.code == 201) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note does not deleted, Something Went Wrong, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            val responce = notesService.deleteNoteFile(note)

            if (responce.code == 200) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note File deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditNoteActivity,
                        "Note File does not deleted, Something Went Wrong, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }

        // val location = "${filesDir.absolutePath}/$path"
        deleteFile(path)
    }
}