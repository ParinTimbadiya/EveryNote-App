package com.example.everynote.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ApiRequest
{
    companion object
    {
//        @JvmStatic val BASE_URL = "http://192.168.0.2:8084/api"
        @JvmStatic val BASE_URL = "http://172.28.0.1:8889/api"
        @JvmStatic val LOGIN_URL = "$BASE_URL/auth"
        @JvmStatic val REGISTER_URL = "$BASE_URL/Register"
        @JvmStatic val ADD_NOTE_URL = "$BASE_URL/AddNote"
        @JvmStatic val UPDATE_NOTE_URL = "$BASE_URL/UpdateNote"
        @JvmStatic val UPLOAD_NOTE_URL = "$BASE_URL/UploadNote"
        @JvmStatic val DELETE_NOTE_URL = "$BASE_URL/DeleteNote"
        @JvmStatic val DELETE_NOTE_FILE_URL = "$BASE_URL/DeleteNoteFile"
        @JvmStatic val GET_ALL_NOTES_URL = "$BASE_URL/GetAllNote"
        @JvmStatic val DOWNLOAD_NOTE_URL = "$BASE_URL/DownloadNote"

        @JvmStatic
        fun get(url: String): ApiResponse
        {
            return send(Request.Builder().url(url).build())
        }

        @JvmStatic
        fun post(url: String, body: String): ApiResponse
        {
            return send(Request.Builder().url(url).post(body.toRequestBody()).build())
        }

        @JvmStatic
        fun put(url: String, body: String): ApiResponse
        {
            return send(Request.Builder().url(url).put(body.toRequestBody()).build())
        }

        @JvmStatic
        fun delete(url: String): ApiResponse
        {
            return send(Request.Builder().url(url).delete().build())
        }

        @JvmStatic
        private fun send(request: Request): ApiResponse {
            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            return ApiResponse(
                code = response.code,
                message = response.body.string()
            )
        }
    }
}