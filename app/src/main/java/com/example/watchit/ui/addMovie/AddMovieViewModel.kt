package com.example.watchit.ui.addMovie

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watchit.data.remote.LinkPreview
import com.example.watchit.domain.model.LibraryVisibility
import com.example.watchit.ui.repo.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMovieViewModel @Inject constructor(
    private val repository: LibraryRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(AddMovieUiState())
    val uiState: StateFlow<AddMovieUiState> = _uiState

    private val initialLibraryId: Long = savedStateHandle["libraryId"] ?: -1L
    private val sharedUrl: String = savedStateHandle["sharedUrl"] ?: ""

    init {
        observeLibraries()
        initUrl()
    }

    private fun initUrl() {
        if (sharedUrl.isNotBlank()){
            _uiState.update {
                it.copy(url = sharedUrl)
            }
        }
    }

    fun isWebViewProvider(url: String): Boolean{
        val normalized = if (!url.contains("://")) "https://$url" else url
        val host = Uri.parse(normalized).host.orEmpty().lowercase()
        return listOf("jio", "jiohotstar", "jiocinema","sonyliv","hotstar").any { it in host }
    }

    private fun normalizePosterUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null

        // Zee5 / Cloudinary-style parameter replacement
        return raw
            .replace("f_avif", "f_webp", ignoreCase = true)
            .replace("f_auto", "f_webp", ignoreCase = true)
    }


    fun isOkHttpOnly(url: String): Boolean{
        return !isWebViewProvider(url)
    }

    fun onUrlChanged(newUrl: String){
        _uiState.update {
            it.copy(url = newUrl)
        }
    }

    fun onLibrarySelected(selected: Long){
        _uiState.update {
            it.copy(
                selectedLibraryId = selected
            )
        }
    }

    fun fetchPreview() {
        val url = _uiState.value.url.trim()
        if (url.isEmpty()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isFetching = true, error = null)
            }
                val result = repository.previewLink(url)
                result
                    .onSuccess { preview ->
                        _uiState.update {
                            it.copy(
                                isFetching = false,
                                error = null,
                                title = preview.title ?: "Untitled",
                                description = preview.description,
                                posterUrl = normalizePosterUrl(preview.imageUrl)
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isFetching = false,
                                error = e.message ?: "Failed to fetch"
                            )
                        }
                    }
            Log.d("Preview_Debug", result.toString())
        }
    }

    fun applyWebViewPreview(preview: LinkPreview?){
        if (preview == null) return
        _uiState.update {
            it.copy(
                title = preview.title ?: it.title,
                posterUrl = normalizePosterUrl(preview.imageUrl) ?: it.posterUrl,
                description = preview.description ?: null,
                isFetching = false,
                error = null
            )
        }
    }

    fun createLibrary(name: String){
        val trimmed = name.trim()
        if (trimmed.isEmpty()){
            _uiState.update {
                it.copy(
                    error = "Need valid library name"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val newId = repository.createLibrary(
                    name = trimmed,
                    description = null,
                    visibility = LibraryVisibility.PRIVATE
                )

                _uiState.update {
                    it.copy(
                        selectedLibraryId = newId,
                        error = null
                    )
                }
            }catch (e: Exception){
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to create library"
                    )
                }
            }
        }
    }
    fun onFetching(){
        _uiState.update {
            it.copy(
                isFetching = true,
                error = null
            )
        }
    }

    fun fetchingCompleted(){
        _uiState.update {
            it.copy(
                isFetching = false,
                error = null
            )
        }
    }

    private fun observeLibraries() {
        viewModelScope.launch {
            repository.getLibraries().collect { libraries ->
                val selected = initialLibraryId.takeIf { it != -1L }
                    ?: libraries.firstOrNull()?.id

                _uiState.update {
                    it.copy(
                        libraries = libraries,
                        selectedLibraryId = selected
                    )
                }
            }
        }
    }

    fun save(){
        val state = _uiState.value
        val libraryId = state.selectedLibraryId
        val url = state.url

        if (libraryId == null || url.isEmpty()){
            _uiState.update {
                it.copy(
                    error = "Library and Url required"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            val result= repository.addMovieFromUrl(libraryId, url)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message,
                    closeAfterSave = result.isSuccess
                )
            }
        }
    }



}