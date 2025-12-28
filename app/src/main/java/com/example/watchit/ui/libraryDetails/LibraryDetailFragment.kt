package com.example.watchit.ui.libraryDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LibraryDetailFragment: Fragment() {

    private val libDetailViewModel: LibraryDetailsViewModel by viewModels()
    private val args: LibraryDetailFragmentArgs by navArgs()

    private lateinit var title: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryDetailAdapter
    private lateinit var fabMovie: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById<TextView>(R.id.textLibraryTitle)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMovies)
        fabMovie = view.findViewById<FloatingActionButton>(R.id.fabAddMovie)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LibraryDetailAdapter { movie ->
            openMovieLink(movie.sourceUrl)
        }
        fabMovie.setOnClickListener {
            showMovieAddDialog()
        }
        recyclerView.adapter = adapter

        collectUiState()
    }

    private fun showMovieAddDialog() {
        val action = LibraryDetailFragmentDirections.actionLibraryDetailFragmentToAddMovieFragment(
            libraryId = libDetailViewModel.libId,
            sharedUrl = ""
        )
        findNavController().navigate(action)
    }

    private fun openMovieLink(sourceUrl: String) {
//        val uri = Uri.parse(sourceUrl)
//
//        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//
//        if (intent.resolveActivity(requireContext().packageManager)!= null){
//            startActivity(intent)
//        }else{
//            startActivity(
//                Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl.replace("nflx://","https://")))
//            )
//        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl))
            startActivity(intent)
        }catch (e: Exception){
            Toast.makeText(requireContext(), "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                libDetailViewModel.uiState.collect { movie ->
                    title.text = movie.name
                    try {
                        adapter.submitList(movie.movies)
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }
}