package com.example.watchit.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchit.ui.repo.LibraryRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repositoryImpl: LibraryRepositoryImpl) : ViewModel() {

    private val homeLibraryId = 1L

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun getMoviesWithLibrary(){
        viewModelScope.launch {
            repositoryImpl.getAllLibraryWithMovies()
                .collect { list ->
                    _uiState.update {
                        HomeUiState(
                            sections = list.map { it ->
                                LibrarySection(
                                    libraryTitle = it.library.name,
                                    movies = it.movies
                                )
                            }
                        )
                    }
                }
        }
    }

}