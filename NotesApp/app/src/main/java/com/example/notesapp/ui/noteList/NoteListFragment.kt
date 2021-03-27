package com.example.notesapp.ui.noteList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.data.AppDatabase
import com.example.notesapp.data.dao.NoteDao
import com.example.notesapp.data.entity.NoteEntity
import com.example.notesapp.databinding.NoteListFragmentBinding
import com.example.notesapp.extensions.navigateWithAnimations
import com.example.notesapp.repository.DatabaseDataSource
import com.example.notesapp.repository.NoteRepository

class NoteListFragment : Fragment() {
    private var _binding: NoteListFragmentBinding? = null
    private val binding get() = _binding!!


    private val viewModel: NoteListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val noteDao: NoteDao =
                        AppDatabase.getInstance(requireContext()).noteDao
                val repository: NoteRepository = DatabaseDataSource(noteDao)
                return NoteListViewModel(repository) as T
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewListeners()
        observeViewModelEvents()
    }

    private fun configureViewListeners() {
        binding.run {
            fabAdd.setOnClickListener {
                findNavController().navigateWithAnimations(R.id.action_noteListFragment_to_noteFragment)
            }

            inputSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    search(s.toString())
                }

            })
        }
    }

    private fun observeViewModelEvents(){
//        viewModel.allNotesEvent.observe(viewLifecycleOwner, Observer { allNotes ->
//            val noteAdapter = NoteAdapter().apply {
//                gotItemClickListener = { note ->
//                    val directions = NoteListFragmentDirections
//                        .actionNoteListFragmentToNoteFragment(note)
//                    findNavController().navigateWithAnimations(directions)
//
//                }
//            }
//            noteAdapter.submitList(allNotes)
//            binding.recyclerView.adapter = ConcatAdapter(noteAdapter)
//            binding.recyclerView.setHasFixedSize(true)
//            binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        })

        viewModel.notes?.observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null) {
                showNotes(notes)
            }
        })

        if (viewModel.notes?.value == null){
            search()
        }

    }

    private fun search(text: String = ""){
        viewModel.search(text)
    }

    private fun showNotes(notes: List<NoteEntity>) {
        val noteAdapter = NoteAdapter().apply {
            gotItemClickListener = { note ->
                val directions = NoteListFragmentDirections
                        .actionNoteListFragmentToNoteFragment(note)
                findNavController().navigateWithAnimations(directions)

            }
        }
        noteAdapter.submitList(notes)
        binding.recyclerView.adapter = ConcatAdapter(noteAdapter)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

//    override fun onResume() {
//        super.onResume()
//        viewModel.getAllNotes()
//    }

}