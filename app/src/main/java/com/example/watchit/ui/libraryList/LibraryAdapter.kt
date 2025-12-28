package com.example.watchit.ui.libraryList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.domain.model.Library

class LibraryAdapter(
    private val onClick: (Library) -> Unit
): ListAdapter<Library, LibraryAdapter.LibraryViewHolder>(Diff) {

    object Diff: DiffUtil.ItemCallback<Library>(){
        override fun areItemsTheSame(
            oldItem: Library,
            newItem: Library
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Library,
            newItem: Library
        ): Boolean {
            return oldItem == newItem
        }

    }
    inner class LibraryViewHolder(view: View, private val onClick: (Library) -> Unit): RecyclerView.ViewHolder(view){
        private val name = view.findViewById<TextView>(R.id.textLibraryName)
        private val description = view.findViewById<TextView>(R.id.textLibraryDescription)
        private var currentLibrary: Library? = null

        init {
            itemView.setOnClickListener {
                currentLibrary?.let(onClick)
            }
        }

        fun bind(library: Library){
            name.text = library.name
            description.text = library.description ?: ""
            currentLibrary = library
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibraryAdapter.LibraryViewHolder = LibraryViewHolder(LayoutInflater.from(parent.context
    ).inflate(R.layout.item_library, parent, false), onClick)

    override fun onBindViewHolder(holder: LibraryAdapter.LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}