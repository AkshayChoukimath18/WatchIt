package com.example.watchit.ui.libraryList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchit.ui.repo.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryListViewModel @Inject constructor (private val repository: LibraryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryListUiState(true))
    val uiState: StateFlow<LibraryListUiState> = _uiState

    init {
        observeLibrary()
    }

    private fun observeLibrary(){
        viewModelScope.launch {
            repository.getLibraries().onStart {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }.catch { e ->
                _uiState.value = LibraryListUiState(
                    false,
                    error = e.message ?: "Unknown Error"
                )
            }.collect { libraries ->
                _uiState.value = LibraryListUiState(
                    false,
                    libraries,
                    null
                )
            }
        }
    }

    fun createLibrary(name: String, description: String?){
        viewModelScope.launch {
            try {
                repository.createLibrary(name, description)
            }catch(e: Error) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown Error")
            }
        }
    }
}