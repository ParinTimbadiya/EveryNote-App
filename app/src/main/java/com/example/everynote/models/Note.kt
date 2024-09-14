package com.example.everynote.models

data class Note(
    var number: Int = 0,
    var title: String = "",
    var body: String = "",
    var lastModified: String = "",
    var isDeleted: Int = 0,
    var userId: Int = 0
) : java.io.Serializable