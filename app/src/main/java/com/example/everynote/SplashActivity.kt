package com.example.everynote

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.everynote.helpers.SQLiteHelper
import com.example.everynote.models.Note
import com.example.everynote.services.NotesService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection

@SuppressLint("CustomSplashScreen")
class SplashActivity  : AppCompatActivity() {

    private var userId: String? = null
    private lateinit var splash: ImageView
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var notes: ArrayList<Note>
    private lateinit var notesService: NotesService

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPrefs = getSharedPreferences("myapp_prefs", MODE_PRIVATE)
        userId = sharedPrefs.getString("UserId", null)

        splash = findViewById(R.id.splash)
        splash.alpha = 0f

        if (userId == null)
        {
            splash.animate().setDuration(1500).alpha(1f).withEndAction{
                intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
            return
        }
        sqliteHelper = SQLiteHelper(this)
        notesService = NotesService()
        notes = sqliteHelper.getAllNotes()


        val remoteNotes = arrayListOf<Note>()
        CoroutineScope(Dispatchers.IO).launch {
            val response = notesService.getAllNotesForUser(userId!!)
            if (response.code == HttpURLConnection.HTTP_OK) {

                val array = Gson().fromJson(response.message, Array<Note>::class.java)
                if (array.isEmpty())
                    return@launch

                remoteNotes.addAll(array)

                syncLocalOnlyNotes(notes, remoteNotes)
                syncRemoteOnlyNotes(notes, remoteNotes)
                syncNotesBasedOnDateTime(notes, remoteNotes)
            }
        }



        splash.animate().setDuration(1500).alpha(1f).withEndAction{
            intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }


    private fun syncLocalOnlyNotes(localNotes: ArrayList<Note>, remoteNotes: ArrayList<Note>) {
        for (localNote in localNotes) {
            val remoteExists = remoteNotes.any { n -> n.number == localNote.number }
            if (!remoteExists) {
                CoroutineScope(Dispatchers.IO).launch {
                    notesService.addNote(localNote)
                    notesService.upload(filesDir.absolutePath, localNote)

                    remoteNotes.add(localNote)
                }
            }
        }
    }

    private fun syncRemoteOnlyNotes(localNotes: ArrayList<Note>, remoteNotes: ArrayList<Note>) {
        for (remoteNote in remoteNotes) {
            val localExists = localNotes.any { n -> n.number == remoteNote.number }
            if (!localExists) {
                if(remoteNote.isDeleted == 0)
                {

                    remoteNote.userId = userId!!.toInt()

                    sqliteHelper.insert(remoteNote)
                    notesService.download(filesDir.absolutePath, remoteNote)

                    localNotes.add(remoteNote)
                }
            }
        }
    }

    private fun syncNotesBasedOnDateTime(
        localNotes: ArrayList<Note>,
        remoteNotes: ArrayList<Note>
    ) {
        for (localNote in localNotes) {
            val remoteNote = remoteNotes.find { rn -> rn.number == localNote.number }!!
            val localNoteTime = localNote.lastModified.toLong()
            val remoteNoteTime = remoteNote.lastModified.toLong()

            localNote.userId = userId!!.toInt()
            remoteNote.userId = userId!!.toInt()

            if (localNoteTime > remoteNoteTime) {
                if (localNote.isDeleted == 1) {
                    notesService.deleteNote(localNote)
                    notesService.deleteNoteFile(localNote)
                } else {
                    notesService.updateNote(localNote)
                    notesService.upload(filesDir.absolutePath, localNote)
                }
            } else if (remoteNoteTime > localNoteTime) {
                if (remoteNote.isDeleted == 1) {
                    sqliteHelper.delete(localNote.number)
                    deleteFile("${filesDir.absolutePath}/${localNote.body}")
                } else {

                    sqliteHelper.update(remoteNote)
                    notesService.download(filesDir.absolutePath, remoteNote)
                }
            }
        }
    }


}