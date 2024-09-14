package com.example.everynote.services

import com.example.everynote.models.Note
import com.example.everynote.utils.ApiRequest
import com.example.everynote.utils.ApiResponse
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.*
import java.io.File
import java.io.FileOutputStream


class NotesService {

    fun upload(filesDir: String, note: Note): ApiResponse {

        val file = File("$filesDir/${note.body}")
        val body =  MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", note.body,
                file.asRequestBody("text/plain".toMediaType())
            )
            .build()

        val request = Request
            .Builder()
            .url(ApiRequest.UPLOAD_NOTE_URL)
            .post(body)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        return ApiResponse(response.code, response.body.string())
    }

    fun download(location: String, note: Note) {
        val url = ApiRequest.DOWNLOAD_NOTE_URL.plus("?userId=${note.userId}&noteNumber=${note.number}")
        val request: Request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val file = FileOutputStream("$location/${note.body}")

                val sink: BufferedSink = file.sink().buffer()
                sink.writeAll(response.body.source())
                sink.close()
            }
        })
    }

    fun addNote(note: Note): ApiResponse
    {
        return ApiRequest.post(ApiRequest.ADD_NOTE_URL, Gson().toJson(note))
    }

    fun updateNote(note: Note): ApiResponse
    {
        return ApiRequest.post(ApiRequest.UPDATE_NOTE_URL, Gson().toJson(note))
    }

    fun deleteNote(note: Note): ApiResponse
    {
        return ApiRequest.post(ApiRequest.DELETE_NOTE_URL, Gson().toJson(note))
    }

    fun deleteNoteFile(note: Note): ApiResponse
    {
        return ApiRequest.post(ApiRequest.DELETE_NOTE_FILE_URL, Gson().toJson(note))
    }

    fun getAllNotesForUser(userId: String): ApiResponse
    {
        return ApiRequest.get(ApiRequest.GET_ALL_NOTES_URL.plus("?userId=$userId"))
    }
}