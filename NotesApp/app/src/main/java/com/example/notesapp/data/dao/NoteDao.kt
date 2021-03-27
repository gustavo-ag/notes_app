package com.example.notesapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notesapp.data.entity.NoteEntity

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT*FROM notes ORDER BY id DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("SELECT*FROM notes WHERE title LIKE :query ORDER BY title")
    fun search(query: String): LiveData<List<NoteEntity>>
}