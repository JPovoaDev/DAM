package com.notes.notesproxmlviews

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.firestore.DocumentReference
import java.io.ByteArrayOutputStream
import java.io.File

class NoteDetailsActivity : AppCompatActivity() {
    var titleEditText: EditText? = null
    var contentEditText: EditText? = null
    var saveNoteBtn: ImageButton? = null
    var pageTitleTextView: TextView? = null
    var title: String? = null
    var content: String? = null
    var docId: String? = null
    var isEditMode: Boolean = false
    var deleteNoteTextViewBtn: TextView? = null

    // Premium dynamic fields
    var selectedImageUrl: String? = null
    var selectedColorHex: String? = "#FFFFFF"
    var selectedTag: String? = null

    // Layout elements
    var noteEditCard: LinearLayout? = null
    var imageContainer: FrameLayout? = null
    var noteDetailImageView: ImageView? = null
    var removeImageBtn: ImageButton? = null
    var tagChipGroup: ChipGroup? = null
    var colorSelectorLayout: LinearLayout? = null
    var galleryBtn: MaterialButton? = null
    var cameraBtn: MaterialButton? = null

    // Temp camera URI
    var imageUriForCamera: Uri? = null

    // Pastel colors palette
    val pastelColors = listOf(
        "#FFFFFF", // Clean White
        "#FFF9C4", // Pastel Yellow
        "#FFCCBC", // Pastel Orange
        "#F8BBD0", // Pastel Pink
        "#E1BEE7", // Pastel Purple
        "#C8E6C9", // Pastel Green
        "#B3E5FC", // Pastel Blue
        "#D7CCC8"  // Pastel Brown
    )

    // Activity Result Launchers
    val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val base64Str = uriToBase64(it)
            if (base64Str != null) {
                selectedImageUrl = base64Str
                displayBase64Image(selectedImageUrl, noteDetailImageView!!, imageContainer!!)
            } else {
                Utility.showToast(this, "Erro ao processar imagem")
            }
        }
    }

    val takePictureWithCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && imageUriForCamera != null) {
            val base64Str = uriToBase64(imageUriForCamera!!)
            if (base64Str != null) {
                selectedImageUrl = base64Str
                displayBase64Image(selectedImageUrl, noteDetailImageView!!, imageContainer!!)
            } else {
                Utility.showToast(this, "Erro ao processar foto")
            }
        }
    }

    val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera()
        } else {
            Utility.showToast(this, "Permissão de câmara é necessária para tirar fotos")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        // Bind existing views
        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        pageTitleTextView = findViewById(R.id.page_title)
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn)

        // Bind premium views
        noteEditCard = findViewById(R.id.note_edit_card)
        imageContainer = findViewById(R.id.image_container)
        noteDetailImageView = findViewById(R.id.note_detail_image_view)
        removeImageBtn = findViewById(R.id.remove_image_btn)
        tagChipGroup = findViewById(R.id.tag_chip_group)
        colorSelectorLayout = findViewById(R.id.color_selector_layout)
        galleryBtn = findViewById(R.id.gallery_btn)
        cameraBtn = findViewById(R.id.camera_btn)

        // Receive data from Intent
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")
        selectedImageUrl = intent.getStringExtra("imageUrl")
        selectedColorHex = intent.getStringExtra("colorHex") ?: "#FFFFFF"
        selectedTag = intent.getStringExtra("tag")

        if (docId != null && !docId!!.isEmpty()) {
            isEditMode = true
        }

        // Setup values
        titleEditText!!.setText(title)
        contentEditText!!.setText(content)
        
        if (isEditMode) {
            pageTitleTextView!!.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn!!.visibility = View.VISIBLE
        }

        // Update UI states based on received properties
        updateCardBackground()
        displayBase64Image(selectedImageUrl, noteDetailImageView!!, imageContainer!!)
        preselectTag(selectedTag)
        setupColorSelector()

        // Set up Listeners
        saveNoteBtn!!.setOnClickListener { saveNote() }
        deleteNoteTextViewBtn!!.setOnClickListener { deleteNoteFromFirebase() }
        removeImageBtn!!.setOnClickListener {
            selectedImageUrl = null
            imageContainer!!.visibility = View.GONE
        }

        galleryBtn!!.setOnClickListener {
            pickImageFromGallery.launch("image/*")
        }

        cameraBtn!!.setOnClickListener {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }

        setupTagGroup()
    }

    private fun setupColorSelector() {
        colorSelectorLayout?.removeAllViews()
        val density = resources.displayMetrics.density
        val sizeInPx = (36 * density).toInt()
        val marginInPx = (8 * density).toInt()

        pastelColors.forEach { colorStr ->
            val cardView = com.google.android.material.card.MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                    setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                }
                radius = sizeInPx / 2f
                strokeWidth = if (selectedColorHex == colorStr) (3 * density).toInt() else 0
                strokeColor = Color.parseColor("#444444")
                setCardBackgroundColor(Color.parseColor(colorStr))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    selectedColorHex = colorStr
                    updateCardBackground()
                    setupColorSelector() // Re-render selectors to show active border
                }
            }
            colorSelectorLayout?.addView(cardView)
        }
    }

    private fun updateCardBackground() {
        noteEditCard?.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(selectedColorHex ?: "#FFFFFF")
        )
    }

    private fun setupTagGroup() {
        val addTagChip = layoutInflater.inflate(R.layout.chip_choice_item, tagChipGroup, false) as Chip
        addTagChip.apply {
            id = View.generateViewId()
            text = "+ Nova Tag"
            isCheckable = false
            setChipIconResource(R.drawable.ic_baseline_add_24)
            setOnClickListener {
                showAddTagDialog()
            }
        }
        tagChipGroup?.addView(addTagChip)

        tagChipGroup?.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedId = checkedIds.first()
                val chip = group.findViewById<Chip>(checkedId)
                if (chip != null && chip != addTagChip) {
                    selectedTag = chip.text.toString()
                }
            } else {
                selectedTag = null
            }
        }
    }

    private fun preselectTag(tag: String?) {
        if (tag.isNullOrEmpty()) return
        var found = false
        for (i in 0 until tagChipGroup!!.childCount) {
            val chip = tagChipGroup!!.getChildAt(i) as? Chip
            if (chip != null && chip.text.toString().equals(tag, ignoreCase = true)) {
                chip.isChecked = true
                selectedTag = chip.text.toString()
                found = true
                break
            }
        }
        if (!found) {
            val newChip = layoutInflater.inflate(R.layout.chip_choice_item, tagChipGroup, false) as Chip
            newChip.text = tag
            newChip.isChecked = true
            tagChipGroup?.addView(newChip)
            selectedTag = tag
        }
    }

    private fun showAddTagDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Adicionar Nova Tag")

        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            hint = "Nome da tag (ex: Faculdade)"
            setPadding(40, 40, 40, 40)
        }
        
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(48, 16, 48, 16)
            }
            addView(input, params)
        }
        builder.setView(container)

        builder.setPositiveButton("Adicionar") { dialog, _ ->
            val newTag = input.text.toString().trim()
            if (newTag.isNotEmpty()) {
                addNewTagChip(newTag)
            } else {
                Utility.showToast(this, "A tag não pode estar vazia")
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addNewTagChip(tag: String) {
        for (i in 0 until tagChipGroup!!.childCount) {
            val chip = tagChipGroup!!.getChildAt(i) as? Chip
            if (chip != null && chip.text.toString().equals(tag, ignoreCase = true)) {
                chip.isChecked = true
                selectedTag = chip.text.toString()
                return
            }
        }

        val newChip = layoutInflater.inflate(R.layout.chip_choice_item, tagChipGroup, false) as Chip
        newChip.text = tag
        newChip.isChecked = true
        
        val addTagChipIndex = (tagChipGroup!!.childCount - 1).coerceAtLeast(0)
        tagChipGroup?.addView(newChip, addTagChipIndex)
        selectedTag = tag
    }

    private fun launchCamera() {
        try {
            val uri = createTempImageFileUri()
            imageUriForCamera = uri
            takePictureWithCamera.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Utility.showToast(this, "Erro ao iniciar câmara")
        }
    }

    private fun createTempImageFileUri(): Uri {
        val tempFile = File.createTempFile("temp_note_image", ".jpg", cacheDir).apply {
            deleteOnExit()
        }
        return androidx.core.content.FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            tempFile
        )
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return null

            bitmap = resizeBitmap(bitmap, 800)

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun displayBase64Image(base64Str: String?, imageView: ImageView, container: View) {
        if (base64Str.isNullOrEmpty()) {
            container.visibility = View.GONE
            return
        }
        try {
            val decodedString = Base64.decode(base64Str, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            if (decodedByte != null) {
                imageView.setImageBitmap(decodedByte)
                container.visibility = View.VISIBLE
            } else {
                container.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            container.visibility = View.GONE
        }
    }

    fun saveNote() {
        val noteTitle = titleEditText!!.text.toString()
        val noteContent = contentEditText!!.text.toString()
        if (noteTitle.isEmpty()) {
            titleEditText!!.error = "Title is required"
            return
        }

        val note = Note()
        note.setTitle(noteTitle)
        note.setContent(noteContent)
        note.setTimestamp(now())
        note.setImageUrl(selectedImageUrl)
        note.setColorHex(selectedColorHex)
        note.setTag(selectedTag)

        saveNoteToFirebase(note)
    }

    fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            documentReference = Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while saving note")
                }
            }
        })
    }

    fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(
            docId.toString()
        )
        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                }
            }
        })
    }
}