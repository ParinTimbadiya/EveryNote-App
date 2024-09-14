package com.example.everynote.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.everynote.models.Note

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "notes__db"
        private const val DATABASE_VERSION = 1
        private const val TBL_NOTES = "Notes"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TBL_NOTES (Number INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, Body TEXT NOT NULL, LastModified TEXT NOT NULL, IsDeleted INTEGER DEFAULT 0)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun getAllNotes(): ArrayList<Note> {

        val noteList: ArrayList<Note> = ArrayList()
        val db = this.readableDatabase

        db.rawQuery("SELECT * FROM $TBL_NOTES WHERE `IsDeleted` = 0", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    noteList.add(
                        Note(
                            number = cursor.getInt(0),
                            title = cursor.getString(1),
                            body = cursor.getString(2),
                            lastModified = cursor.getString(3),
                            isDeleted = cursor.getInt(4)
                        )
                    )
                } while (cursor.moveToNext())
            }

            return noteList
        }
    }

    fun getNote(Number: Int): Note {
        val note = Note()
        val db = this.readableDatabase

        db.rawQuery("SELECT * FROM $TBL_NOTES WHERE `Number` = ? AND `IsDeleted` = 0",
            arrayOf(Number.toString())
        ).use { cursor ->
            if (cursor != null) {
                cursor.moveToFirst()
                note.number = cursor.getInt(0)
                note.title = cursor.getString(1)
                note.body = cursor.getString(2)
                note.lastModified = cursor.getString(3)
                note.isDeleted = cursor.getInt(4)
            }
        }

        return note
    }


    fun getDeleteNote(Number: Int): Note {
        val note = Note()
        val db = this.readableDatabase

        db.rawQuery("SELECT * FROM $TBL_NOTES WHERE `Number` = ?",
            arrayOf(Number.toString())
        ).use { cursor ->
            if (cursor != null) {
                cursor.moveToFirst()
                note.number = cursor.getInt(0)
                note.title = cursor.getString(1)
                note.body = cursor.getString(2)
                note.lastModified = cursor.getString(3)
                note.isDeleted = cursor.getInt(4)
            }
        }

        return note
    }

    fun insert(note: Note): Int {
        val db = this.writableDatabase

        val query = "INSERT INTO $TBL_NOTES (`Title`,`Body`, `LastModified`, `IsDeleted`) VALUES (?,?,?,0)"
        db?.execSQL(query, arrayOf(note.title, note.body, note.lastModified))

        db?.rawQuery("SELECT LAST_INSERT_ROWID() AS `LastNoteNumber` FROM `Notes`", null).use { cursor ->
            cursor!!.moveToFirst()
            return cursor.getInt(0)
        }
    }

    fun update(note: Note){
        val db = this.writableDatabase

        val query = "UPDATE $TBL_NOTES SET `Title` = ?, `LastModified` = ? WHERE `Number` = ? "
        db?.execSQL(query, arrayOf(note.title, note.lastModified, note.number))

    }

    fun delete(number: Int) {
        val db = this.writableDatabase

        val query = "UPDATE $TBL_NOTES SET `IsDeleted` = 1 WHERE `Number` = ?"
        db?.execSQL(query, arrayOf(number))
    }
}
