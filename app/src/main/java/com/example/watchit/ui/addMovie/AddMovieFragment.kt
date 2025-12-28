package com.example.watchit.ui.addMovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.watchit.R
import com.example.watchit.data.remote.LinkPreview
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AddMovieFragment : Fragment() {
    private val viewModel: AddMovieViewModel by viewModels()

    private lateinit var webPreviewHidden: WebView
    private lateinit var spinnerLibrary: Spinner
    private lateinit var editUrl: EditText
    private lateinit var textPreviewTitle: TextView
    private lateinit var imagePreview: ImageView
    private lateinit var description: TextView
    private lateinit var progressPreview: ProgressBar
    private lateinit var buttonFetch: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonAddLibrary: ImageButton

    private lateinit var webViewScraper: WebViewLinkPreviewScraper
    private lateinit var toolBar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_movie,container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ---- findViewById wiring (matches your XML) ----
        webPreviewHidden = view.findViewById(R.id.webPreviewHidden)
        editUrl          = view.findViewById(R.id.editUrl)
        spinnerLibrary   = view.findViewById(R.id.spinnerLibrary)
        textPreviewTitle = view.findViewById(R.id.textPreviewTitle)
        imagePreview     = view.findViewById(R.id.imagePreview)
        description      = view.findViewById(R.id.description)
        progressPreview  = view.findViewById(R.id.progressPreview)
        buttonFetch      = view.findViewById(R.id.buttonFetch)
        buttonSave       = view.findViewById(R.id.buttonSave)
        buttonAddLibrary       = view.findViewById(R.id.addIcon)
        toolBar =view.findViewById(R.id.toolBar)

        // Scraper instance (uses its own internal WebView)
        webViewScraper = WebViewLinkPreviewScraper(requireContext())
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolBar)

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupListeners()
        observeUiState()
        val initialState = viewModel.uiState.value
        if (initialState.url.isNotBlank()) {
            // Make sure the EditText shows it (in case renderState hasn't run yet)
            editUrl.setText(initialState.url)
            editUrl.setSelection(initialState.url.length)

            // Reuse your existing fetch logic – NO new method
            buttonFetch.performClick()
        }
    }

    // -----------------------------
    // UI → ViewModel wiring
    // -----------------------------

    private fun setupListeners() {
        // You already update url in VM from initUrl() using sharedUrl, so
        // we only need to push changes when user actually edits or taps fetch.

        // Optional: on focus loss, sync URL
        editUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = editUrl.text.toString().trim()
                viewModel.onUrlChanged(url)
            }
        }

        buttonAddLibrary.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_library, null)
            val editLibraryName = dialogView.findViewById<EditText>(R.id.editLibraryName)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel") {d, _ -> d.dismiss()}
                .create()
            dialog.setOnShowListener {
                val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                saveButton.setOnClickListener {
                    val editName = editLibraryName.text.toString().trim()
                    if (editName.isEmpty()){
                        editLibraryName.error = "Required!!!"
                        return@setOnClickListener
                    }
                    viewModel.createLibrary(editName)
                    dialog.dismiss()
                }
            }
            dialog.show()

        }

        // Fetch preview button
        buttonFetch.setOnClickListener {
            val url = editUrl.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener

            viewModel.onUrlChanged(url)

            if (viewModel.isWebViewProvider(url)) {
                viewModel.onFetching()
                webViewScraper.load(url) { preview: LinkPreview? ->
                    if (preview == null) {
                        viewModel.fetchingCompleted()
                    } else {
                        viewModel.applyWebViewPreview(preview)
                    }
                }
            } else {
                viewModel.fetchPreview()
            }
        }

        // Library selection
        spinnerLibrary.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val state = viewModel.uiState.value
                val libs = state.libraries
                if (position in libs.indices) {
                    viewModel.onLibrarySelected(libs[position].id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        // Save button
        buttonSave.setOnClickListener {
            viewModel.save()
        }


    }

    // -----------------------------
    // ViewModel → UI wiring
    // -----------------------------

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: AddMovieUiState) {
        // URL field – keep in sync with state.url
        val currentUrl = editUrl.text?.toString().orEmpty()
        if (currentUrl != state.url) {
            editUrl.setText(state.url)
            editUrl.setSelection(state.url.length)
        }

        // Title
        textPreviewTitle.text = state.title.orEmpty()

        // Description
        if (state.description.isNullOrBlank()) {
            description.isVisible = false
        } else {
            description.isVisible = true
            description.text = state.description
        }

        // Poster image
        if (!state.posterUrl.isNullOrBlank()) {
            imagePreview.load(state.posterUrl)
        } else {
            imagePreview.setImageDrawable(null)
        }

        // Loading state
        progressPreview.isVisible = state.isFetching || state.isSaving

        // Error handling – you don't have an error TextView, so Toast is fine
        state.error?.let { msg ->
            if (msg.isNotBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Libraries → Spinner
        if (state.libraries.isNotEmpty()) {
            val names = state.libraries.map { it.name }  // assumes Library has 'name'
            val existingAdapter = spinnerLibrary.adapter as? ArrayAdapter<String>

            // Only reset adapter if size changed; prevents flicker
            if (existingAdapter == null || existingAdapter.count != names.size) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    names
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                spinnerLibrary.adapter = adapter
            }

            val selectedIndex =
                state.libraries.indexOfFirst { it.id == state.selectedLibraryId }
            if (selectedIndex >= 0 && selectedIndex != spinnerLibrary.selectedItemPosition) {
                spinnerLibrary.setSelection(selectedIndex)
            }
        }

        // Close screen after save
        if (state.closeAfterSave) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clean up internal WebView to avoid leaks
        webViewScraper.destroy()
    }
}