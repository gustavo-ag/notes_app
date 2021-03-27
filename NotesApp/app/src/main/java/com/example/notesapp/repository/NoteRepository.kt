package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.data.entity.NoteEntity

interface NoteRepository {
    suspend fun insertNote(
            title: String,
            dateTime: String,
            subtitle: String,
            text: String,
            imagePath: String,
            color: String,
            webLink: String
    ): Long

    suspend fun updateNote(
            id: Long,
            title: String,
            dateTime: String,
            subtitle: String,
            text: String,
            imagePath: String,
            color: String,
            webLink: String
    )

    suspend fun deleteNote(id: Long)

    suspend fun getAllNotes(): List<NoteEntity>

    fun search(text: String): LiveData<List<NoteEntity>>
}