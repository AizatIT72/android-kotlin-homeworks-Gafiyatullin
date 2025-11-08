package ru.itis.android.homework3.viewmodel

import androidx.lifecycle.ViewModel
import ru.itis.android.homework3.model.Note
import androidx.compose.runtime.mutableStateListOf

class NotesViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> get() = _notes

    fun addNote(note: Note) {
        _notes.add(note)
    }
}