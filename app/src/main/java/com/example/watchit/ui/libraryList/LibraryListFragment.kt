package com.example.watchit.ui.libraryList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import com.example.watchit.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryListFragment: Fragment() {

    private val liblistViewModel: LibraryListViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerLibraries)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddLibrary)

        adapter = LibraryAdapter { library ->
            val action = LibraryListFragmentDirections
                .actionLibraryListToLibraryDetails(library.id)

            findNavController().navigate(action)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            val action = LibraryListFragmentDirections.actionLibraryListToAddMovie(libraryId = -1L, sharedUrl = "")
            findNavController().navigate(action)
        }

        collectUiState()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                liblistViewModel.uiState.collect { state ->
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    adapter.submitList(state.libraries)

                    state.error?.let {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}