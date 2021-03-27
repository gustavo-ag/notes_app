package com.example.notesapp.ui.note

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.R
import com.example.notesapp.data.AppDatabase
import com.example.notesapp.data.dao.NoteDao
import com.example.notesapp.databinding.NoteFragmentBinding
import com.example.notesapp.extensions.hideKeyboard
import com.example.notesapp.repository.DatabaseDataSource
import com.example.notesapp.repository.NoteRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_miscellaneous.*
import kotlinx.android.synthetic.main.layout_miscellaneous.view.*
import kotlinx.android.synthetic.main.note_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {
    private var _binding: NoteFragmentBinding? = null
    private val binding get() = _binding!!


    private val viewModel: NoteViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val noteDao: NoteDao =
                        AppDatabase.getInstance(requireContext()).noteDao
                val repository: NoteRepository = DatabaseDataSource(noteDao)
                return NoteViewModel(repository) as T
            }
        }
    }

    private val args: NoteFragmentArgs by navArgs()
    private var selectedNoteColor: String = "#333333"
    private var selectedImage = ""

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null){
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    try {
                        val inputStream = context?.contentResolver?.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageNote.setImageBitmap(bitmap)
                        imageNote.visibility = View.VISIBLE

                        selectedImage = getPathFromUri(selectedImageUri)
                    } catch (e: Exception){
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.note?.let { note ->
            binding.run {
                inputNoteTitle.setText(note.title)
                inputNoteSubtitle.setText(note.subtitle)
                textDateTime.text = note.dateTime
                noteText.setText(note.text)
                selectedNoteColor = if (note.color.isNotEmpty()) note.color else selectedNoteColor
                selectedImage = note.imagePath
                imageDelete.visibility = View.VISIBLE
            }
        }

        configureListeners()
        observeEvents()
        initMiscellaneous()
        setSubtitleIndicatorColor()
        setImage()
    }

    private fun configureListeners(){
        binding.run {
            imageBack.setOnClickListener {
                findNavController().popBackStack()
            }

            imageSave.setOnClickListener {
                val title = inputNoteTitle.text.toString()
                val dateTime = textDateTime.text.toString()
                val subtitle = inputNoteSubtitle.text.toString()
                val text = noteText.text.toString()
                val imagePath = selectedImage
                if (title.isEmpty() || subtitle.isEmpty() || text.isEmpty()){
                    Snackbar.make(requireView(), "Note can't be empty", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addOrUpdateNote(args.note?.id ?: 0, title = title, dateTime = dateTime, subtitle = subtitle, text = text, imagePath, selectedNoteColor, "")
                }
            }
            textDateTime.text =
                SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                    .format(Date())

            imageDelete.setOnClickListener {
                viewModel.deleteNote(args.note?.id ?: 0)
            }

        }
    }

    private fun observeEvents() {
        viewModel.noteStateEventData.observe(viewLifecycleOwner, Observer { noteState ->
            when (noteState) {
                is NoteViewModel.NoteState.Inserted,
                    is NoteViewModel.NoteState.Updated,
                    is NoteViewModel.NoteState.Deleted -> {
                    clearFields()
                    hideKeyBoard()
                    requireView().requestFocus()

                    findNavController().popBackStack()
                    }
            }
        })

        viewModel.messageEventData.observe(viewLifecycleOwner, Observer { stringResId ->
//            Snackbar.make(requireView(), stringResId, Snackbar.LENGTH_LONG).show()
            Log.e(NoteFragment::class.java.simpleName, "Inserted")
            Toast.makeText(context, stringResId, Toast.LENGTH_LONG).show()
        })
    }

    private fun clearFields() {
        binding.run {
            inputNoteTitle.text?.clear()
            inputNoteSubtitle.text?.clear()
            noteText.text?.clear()
        }
    }

    private fun hideKeyBoard() {
        val parentActivity = requireActivity()
        if (parentActivity is AppCompatActivity) {
            parentActivity.hideKeyboard()
        }
    }

    private fun initMiscellaneous(){
        binding.run {
            val bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous)
            layoutMiscellaneous.textMiscellaneous.setOnClickListener {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else{
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            viewColor1.setOnClickListener {
                selectedNoteColor = "#333333"
                imageColor1.setImageResource(R.drawable.ic_done)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setSubtitleIndicatorColor()
            }
            viewColor2.setOnClickListener {
                selectedNoteColor = "#FDBE3B"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(R.drawable.ic_done)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setSubtitleIndicatorColor()
            }
            viewColor3.setOnClickListener {
                selectedNoteColor = "#FF4842"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(R.drawable.ic_done)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(0)
                setSubtitleIndicatorColor()
            }
            viewColor4.setOnClickListener {
                selectedNoteColor = "#3A52Fc"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(R.drawable.ic_done)
                imageColor5.setImageResource(0)
                setSubtitleIndicatorColor()
            }
            viewColor5.setOnClickListener {
                selectedNoteColor = "#000000"
                imageColor1.setImageResource(0)
                imageColor2.setImageResource(0)
                imageColor3.setImageResource(0)
                imageColor4.setImageResource(0)
                imageColor5.setImageResource(R.drawable.ic_done)
                setSubtitleIndicatorColor()
            }

            layoutAddImage.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                if (context?.let { it1 ->
                            ContextCompat.checkSelfPermission(
                                    it1, android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        } != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_STORAGE_PERMISSION
                    )
                } else {
                    selectImage()
                }
            }
        }
    }

    private fun setSubtitleIndicatorColor(){
        val gradientDrawable = binding.viewSubtitleIndicator.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor))
    }

    private fun setImage() {
        val imageNote = binding.imageNote
        if (selectedImage.isNotEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImage))
            imageNote.visibility = View.VISIBLE
        }
    }

    private fun selectImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResult.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String {
        var filePath = ""
        val cursor = context?.contentResolver
            ?.query(contentUri, null, null, null, null)
        if (cursor == null){
            filePath = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }

        return filePath
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }

}