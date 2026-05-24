package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    var addNoteBtn: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var menuBtn: ImageButton? = null
    var noteAdapter: NoteAdapter? = null
    var filterChipGroup: ChipGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recyler_view)
        menuBtn = findViewById(R.id.menu_btn)
        filterChipGroup = findViewById(R.id.filter_chip_group)

        addNoteBtn!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, NoteDetailsActivity::class.java))
        }
        menuBtn!!.setOnClickListener { showMenu() }

        setupRecyclerView()
        setupFilterChips()
    }

    fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.title == "Logout") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                true
            } else {
                false
            }
        }
    }

    fun setupRecyclerView() {
        val query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        // 1. Configure Pinterest-style Staggered Grid (2 columns)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        noteAdapter = NoteAdapter(options, this)
        recyclerView?.adapter = noteAdapter
    }

    // 2. Setup Category filtering listener with dynamic tag loading
    private fun setupFilterChips() {
        filterChipGroup?.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            if (checkedId != null && checkedId != View.NO_ID) {
                val chip = group.findViewById<Chip>(checkedId)
                val tag = chip?.text?.toString() ?: "Todas"
                filterNotes(tag)
            } else {
                filterNotes("Todas")
            }
        }
    }

    private fun loadDynamicTags() {
        Utility.getCollectionReferenceForNotes().get()
            .addOnSuccessListener { querySnapshot ->
                val tagsSet = mutableSetOf<String>()
                for (document in querySnapshot.documents) {
                    val tag = document.getString("tag")
                    if (!tag.isNullOrEmpty()) {
                        tagsSet.add(tag)
                    }
                }
                populateFilterChips(tagsSet)
            }
            .addOnFailureListener {
                populateFilterChips(emptySet())
            }
    }

    private fun populateFilterChips(tags: Set<String>) {
        val group = filterChipGroup ?: return

        // Desliga listener temporariamente
        group.setOnCheckedStateChangeListener(null)
        group.removeAllViews()

        // Chip "Todas" sempre primeiro
        val allChip = Chip(this)
        allChip.text = "Todas"
        allChip.isCheckable = true
        allChip.isChecked = true
        group.addView(allChip)

        // Chips das tags
        tags.sorted().forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isCheckable = true
            group.addView(chip)
        }

        // Re-ativa listener
        setupFilterChips()
    }

    private fun getCurrentlyCheckedTag(): String? {
        val group = filterChipGroup ?: return null
        val checkedId = group.checkedChipId
        if (checkedId != View.NO_ID) {
            val chip = group.findViewById<Chip>(checkedId)
            return chip?.text?.toString()
        }
        return null
    }

    private fun filterNotes(tag: String) {
        val query = if (tag == "Todas") {
            Utility.getCollectionReferenceForNotes()
                .orderBy("timestamp", Query.Direction.DESCENDING)
        } else {
            Utility.getCollectionReferenceForNotes()
                .whereEqualTo("tag", tag)
                .orderBy("timestamp", Query.Direction.DESCENDING)
        }

        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        noteAdapter?.updateOptions(options)
    }

    override fun onStart() {
        super.onStart()
        noteAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter!!.stopListening()
    }

    override fun onResume() {
        super.onResume()
        noteAdapter!!.notifyDataSetChanged()
        loadDynamicTags()
    }
}