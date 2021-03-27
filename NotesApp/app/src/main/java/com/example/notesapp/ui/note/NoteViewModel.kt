package com.example.notesapp.ui.note

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.R
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
        private val repository: NoteRepository
) : ViewModel() {

    private val _noteStateEventData = MutableLiveData<NoteState>()
    val noteStateEventData: LiveData<NoteState>
        get() = _noteStateEventData

    private val _messageEventData = MutableLiveData<Int>()
    val messageEventData: LiveData<Int>
        get() = _messageEventData

    fun addOrUpdateNote(
            id: Long = 0,
            title: String,
            dateTime: String,
            subtitle: String,
            text: String,
            imagePath: String,
            color: String,
            webLink: String
    ) = viewModelScope.launch {
        if (id > 0){
            updateNote(id, title, dateTime, subtitle, text, imagePath, color, webLink)
        } else {
            insertNote(title, dateTime, subtitle, text, imagePath, color, webLink)
        }
    }

    private fun updateNote(
            id: Long,
            title: String,
            dateTime: String,
            subtitle: String,
            text: String,
            imagePath: String,
            color: String,
            webLink: String
    ) = viewModelScope.launch {
        try {
            repository.updateNote(id, title, dateTime, subtitle, text, imagePath, color, webLink)

            _noteStateEventData.value = NoteState.Updated
            _messageEventData.value = R.string.updated_successfully
        } catch (e: Exception){
            _messageEventData.value = R.string.note_error_to_update
            Log.e(TAG, e.toString())
        }
    }

    private fun insertNote(
            title: String,
            dateTime: String,
            subtitle: String,
            text: String,
            imagePath: String,
            color: String,
            webLink: String
    ) = viewModelScope.launch {
        try {
            val id = repository.insertNote(title, dateTime, subtitle, text, imagePath, color, webLink)
            if (id > 0){
                _noteStateEventData.value = NoteState.Inserted
                _messageEventData.value = R.string.note_inserted_successfully
            }
        } catch (e: Exception){
            _messageEventData.value = R.string.note_error_to_insert
            Log.e(TAG, e.toString())
        }
    }

    fun deleteNote(id: Long) = viewModelScope.launch {
        try {
            if (id > 0){
                repository.deleteNote(id)
                _noteStateEventData.value = NoteState.Deleted
                _messageEventData.value = R.string.note_deleted_successfully
            }
        } catch (e: Exception){
            _messageEventData.value = R.string.note_error_to_delete
            Log.e(TAG, e.toString())
        }

    }

    sealed class NoteState {
        object Inserted : NoteState()
        object Updated : NoteState()
        object Deleted: NoteState()
    }

    companion object {
        private val TAG = NoteViewModel::class.java.simpleName
    }
}