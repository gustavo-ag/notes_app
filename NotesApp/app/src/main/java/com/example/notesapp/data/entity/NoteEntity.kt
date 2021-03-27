package com.example.notesapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class NoteEntity (
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        @ColumnInfo(name = "title")
        val title: String = "",
        val dateTime: String,
        val subtitle: String,
        val text: String,
        val imagePath: String,
        val color: String,
        val webLink: String
): Parcelable