package ru.veider.nasapicture.ui.note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import ru.veider.nasapicture.R
import ru.veider.nasapicture.repository.note.Note
import ru.veider.nasapicture.ui.note.editor.NoteEditor

class NoteAdapter(private var notes: ArrayList<Note>, val noteFragment: NoteFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            Note1Holder(layoutInflater.inflate(R.layout.note1_item, parent, false) as View)
        } else {
            Note2Holder(layoutInflater.inflate(R.layout.note2_item, parent, false) as View)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1)
            (holder as Note1Holder).onBind(notes[position])
        else
            (holder as Note2Holder).onBind(notes[position])
    }

    override fun getItemViewType(position: Int): Int {
        return notes[position].type
    }

    override fun getItemCount() = notes.size

    inner class Note1Holder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        lateinit var note: Note
        private var textEditor = itemView.findViewById<TextView>(R.id.note)
        private var editButton = itemView.findViewById<ImageView>(R.id.edit_button)

        init {
            editButton.setOnClickListener {
                NoteEditor.getInstance(adapterPosition, note.noteText, note.type, noteFragment)
                    .show(noteFragment.parentFragmentManager, "NOTE_EDITOR")
            }
        }

        fun onBind(note: Note) {
            this.note = note
            textEditor.text = note.noteText
        }

        override fun onItemSelected() {
            itemView.background = AppCompatResources.getDrawable(noteFragment.requireContext(), R.drawable.selected_note_decorator)
        }

        override fun onItemClear() {
            itemView.background = AppCompatResources.getDrawable(noteFragment.requireContext(), R.drawable.note_decorator)
        }
    }

    inner class Note2Holder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        lateinit var note: Note
        private var textEditor = itemView.findViewById<TextView>(R.id.note)
        private var editButton = itemView.findViewById<ImageView>(R.id.edit_button)

        init {
            editButton.setOnClickListener {
                NoteEditor.getInstance(adapterPosition, note.noteText, note.type, noteFragment)
                    .show(noteFragment.parentFragmentManager, "NOTE_EDITOR")
            }
        }

        fun onBind(note: Note) {
            this.note = note
            textEditor.text = note.noteText
        }

        override fun onItemSelected() {
            itemView.background = AppCompatResources.getDrawable(noteFragment.requireContext(), R.drawable.selected_note_decorator)
        }

        override fun onItemClear() {
            itemView.background = AppCompatResources.getDrawable(noteFragment.requireContext(), R.drawable.note_decorator)
        }
    }
}