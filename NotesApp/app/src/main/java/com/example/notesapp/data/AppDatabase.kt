package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.data.dao.NoteDao
import com.example.notesapp.data.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance: AppDatabase? = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "app_database"
                    ).build()
                }

                return instance
            }
        }
    }
}