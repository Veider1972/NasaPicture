package ru.veider.nasapicture.ui.note

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.veider.nasapicture.R
import ru.veider.nasapicture.databinding.NoteFragmentBinding
import ru.veider.nasapicture.repository.note.Note
import ru.veider.nasapicture.ui.note.editor.NoteEditor
import java.util.*
import kotlin.collections.ArrayList

class NoteFragment : Fragment(), NoteEditor.NoteEditorEvent {

    lateinit var binding: NoteFragmentBinding
    lateinit var notes: ArrayList<Note>

    companion object {
        fun newInstance() = NoteFragment()
    }

    private lateinit var viewModel: NoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.note_fragment, container, false)
        binding = NoteFragmentBinding.bind(view)
        binding.notesList.layoutManager = LinearLayoutManager(requireContext())
        binding.addNote.apply {
            setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage(context.getString(R.string.note_type_query))
                    .setPositiveButton("Тип 1") { _, _ ->
                        NoteEditor.getInstance(-1, "", 1, this@NoteFragment).show(parentFragmentManager, "NOTE_EDITOR")
                    }
                    .setNegativeButton("Тип 2") { _, _ ->
                        NoteEditor.getInstance(-1, "", 2, this@NoteFragment).show(parentFragmentManager, "NOTE_EDITOR")
                    }
                    .setNeutralButton(R.string.note_editor_cancel) { _, _ -> }
                    .show()
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun isLongPressDragEnabled() = true

            override fun isItemViewSwipeEnabled() = true

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val movementsFlag = ItemTouchHelper.START or ItemTouchHelper.END
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, movementsFlag)
            }

            override fun onMove(recyclerView: RecyclerView, fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder): Boolean {
                Collections.swap(notes, fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
                binding.notesList.adapter?.notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
                viewModel.putNotes(notes)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val dialog = MaterialAlertDialogBuilder(this@NoteFragment.requireContext())
                dialog.setMessage(getString(R.string.delete_note_question))
                dialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    notes.removeAt(position)
                    (binding.notesList.adapter as NoteAdapter).notifyItemRemoved(position)
                    viewModel.putNotes(notes)
                }
                dialog.setNegativeButton(getString(R.string.no)
                ) { _, _ ->
                    (binding.notesList.adapter as NoteAdapter).notifyItemChanged(position)
                }
                dialog.show()
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if(actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
                    (viewHolder as ItemTouchHelperViewHolder).onItemSelected()
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                (viewHolder as ItemTouchHelperViewHolder).onItemClear()
                super.clearView(recyclerView, viewHolder)
            }
        }).attachToRecyclerView(binding.notesList)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java].apply {
            getNotes().observe(viewLifecycleOwner) { notes ->
                this@NoteFragment.notes = notes
                binding.notesList.adapter = NoteAdapter(notes, this@NoteFragment)
            }
        }
    }

    override fun updateNote(position: Int, noteText: String, nodeType: Int) {
        if (position < 0) {
            notes.add(Note(nodeType, noteText))
            binding.notesList.adapter?.notifyItemChanged(notes.lastIndex)
        } else {
            notes[position].noteText = noteText
            binding.notesList.adapter?.notifyItemChanged(position)
        }
        viewModel.putNotes(notes)
    }
}
