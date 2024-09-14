package com.example.everynote.services

import com.example.everynote.models.User
import com.example.everynote.utils.ApiRequest
import com.example.everynote.utils.ApiResponse
import com.google.gson.Gson

class AuthService
{
    fun login(user: User): ApiResponse
    {
        return ApiRequest.post(ApiRequest.LOGIN_URL, Gson().toJson(user))
    }

    fun register(user: User): ApiResponse
    {
        return ApiRequest.post(ApiRequest.REGISTER_URL, Gson().toJson(user))
    }

}