package com.example.watchit.ui.libraryDetails

import androidx.lifecycle.SavedStateHandle
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
class LibraryDetailsViewModel @Inject constructor
    (private val repository: LibraryRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val libraryId: Long = savedStateHandle.get<Long>("libraryId") ?: -1L
    val libId = libraryId

    private val _uiState = MutableStateFlow(LibraryDetailsUiState(true))
    val uiState: StateFlow<LibraryDetailsUiState> = _uiState

    init {
        if (libraryId != -1L){
            observeLibrary()
        }

    }

    private fun observeLibrary(){
        viewModelScope.launch {
            repository.getLibraryWithMovies(libraryId).onStart {
                _uiState.value = _uiState.value.copy(isLoading = true, name = "", movies =emptyList())
            }.catch { e ->
                _uiState.value = LibraryDetailsUiState(
                    false,
                    error = e.message ?: "Unknow Error"
                )
            }.collect { libraryWithMoviesDomain ->
                libraryWithMoviesDomain?.let {
                    _uiState.value = LibraryDetailsUiState(
                        false,
                        name = libraryWithMoviesDomain.library.name,
                        movies = libraryWithMoviesDomain.movies
                    )
                }
            }
        }
    }

    fun removeMovie(movieId: Long){
        viewModelScope.launch {
            try {
                repository.removeMovieFromLibrary(libraryId, movieId)
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addMovieFromUrl(url: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAdding = true)

            val result = repository.addMovieFromUrl(libraryId, url)
            _uiState.value = _uiState.value.copy(isAdding = false)
            result.exceptionOrNull()?.let { e ->
                _uiState.value = _uiState.value.copy(error = e.message ?: "Failed to add movie" )
            }
        }
    }
}