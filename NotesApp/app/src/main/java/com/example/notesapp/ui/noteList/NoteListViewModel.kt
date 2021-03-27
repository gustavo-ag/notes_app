package com.example.notesapp.ui.noteList

import androidx.lifecycle.*
import com.example.notesapp.data.entity.NoteEntity
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteListViewModel(
        private val repository: NoteRepository
) : ViewModel() {
//    private val _allNotesEvent = MutableLiveData<List<NoteEntity>>()
//    val allNotesEvent: LiveData<List<NoteEntity>>
//        get() = _allNotesEvent

    private val _searchTerm = MutableLiveData<String>()
    val searchTerm: LiveData<String>
        get() = _searchTerm

    private val _notes = Transformations.switchMap(_searchTerm) { term ->
            repository.search("%$term%")
    }
    val notes: LiveData<List<NoteEntity>>?
        get() = _notes

//    fun getAllNotes() = viewModelScope.launch {
//        _allNotesEvent.postValue(repository.getAllNotes())
//    }

    fun search(text: String = ""){
        _searchTerm.value = text
    }


}