package ru.veider.nasapicture.ui.note.editor

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ru.veider.nasapicture.R
import ru.veider.nasapicture.databinding.NoteEditorBinding

class NoteEditor(private val position: Int, private var noteText: String, private var nodeType:Int, private val noteEditorEvent: NoteEditorEvent) : DialogFragment() {

    private lateinit var binder: NoteEditorBinding

    enum class Mode {
        EDIT, INSERT
    }

    interface NoteEditorEvent {
        fun updateNote(position: Int, noteText: String, nodeType:Int)
    }

    companion object {
        @JvmStatic
        fun getInstance(position: Int, noteText: String, noteType:Int, noteEditorEvent: NoteEditorEvent) = NoteEditor(position, noteText, noteType, noteEditorEvent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            binder = NoteEditorBinding.bind(requireActivity().layoutInflater.inflate(R.layout.note_editor, null))
            setView(binder.root)

            if (position < 0) {
                setTitle(R.string.note_insert_title)
            } else {
                setTitle(R.string.note_edit_title)
                binder.editText.setText(noteText, TextView.BufferType.EDITABLE)
            }

            setPositiveButton(R.string.note_editor_accept) { _, _ ->
                noteText = binder.editText.text.toString()
                noteEditorEvent.updateNote(position, noteText, nodeType)
            }

            setNegativeButton(R.string.note_editor_cancel) { _, _ ->

            }


        }


        return alertDialog.create()
    }
}