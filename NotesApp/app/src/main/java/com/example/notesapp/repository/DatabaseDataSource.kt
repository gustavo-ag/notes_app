package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.data.dao.NoteDao
import com.example.notesapp.data.entity.NoteEntity

class DatabaseDataSource(private val noteDao: NoteDao): NoteRepository {
    override suspend fun insertNote(title: String, dateTime: String, subtitle: String, text: String, imagePath: String, color: String, webLink: String): Long {
        val note = NoteEntity(
                title = title,
                dateTime = dateTime,
                subtitle = subtitle,
                text = text,
                imagePath = imagePath,
                color = color,
                webLink = webLink
        )

        return noteDao.insert(note)
    }

    override suspend fun updateNote(id: Long, title: String, dateTime: String, subtitle: String, text: String, imagePath: String, color: String, webLink: String) {
        val note = NoteEntity(
                id, title, dateTime, subtitle, text, imagePath, color, webLink
        )

        noteDao.update(note)
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.delete(id)
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        return noteDao.getAllNotes()
    }

    override fun search(text: String): LiveData<List<NoteEntity>> {
        return noteDao.search(text)
    }
}