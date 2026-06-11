package com.example.splitexpenses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitexpenses.ui.ExploreUiState
import com.example.splitexpenses.ui.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.example.splitexpenses.BuildConfig

class ExploreViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    private val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    init {
        loadExploreData()
    }

    private fun loadExploreData() {
        val mockPlaces = listOf(
            Place("1", "Restaurante Maré", "Cascais", 4.8, listOf("Aberto agora", "Comida local")),
            Place("2", "Praia do Guincho", "Cascais", 4.5, listOf("Surf", "Natureza")),
            Place("3", "Hotel Avenida", "Lisboa", 4.9, listOf("Centro", "Luxo"))
        )
        val mockCategories = listOf("Tudo", "Praias", "Restaurantes", "Hotéis")

        _uiState.update { 
            it.copy(
                places = mockPlaces,
                categories = mockCategories,
                isLoading = false
            )
        }
    }

    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(600) // Debounce para não inundar a rede enquanto digita
            performSearch(query, _uiState.value.selectedCategory)
        }
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        performSearch(_uiState.value.searchQuery, category)
    }

    private fun performSearch(query: String, category: String) {
        if (query.trim().length < 3) {
            loadExploreData()
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val results = if (GEMINI_API_KEY.isNotBlank()) {
                fetchPlacesFromGemini(query, category, GEMINI_API_KEY)
            } else {
                null
            }

            // Se o Gemini não estiver configurado ou falhar, usa o fallback inteligente
            val finalResults = results ?: generateMockPlaces(query, category)

            _uiState.update { 
                it.copy(
                    places = finalResults,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun fetchPlacesFromGemini(city: String, category: String, apiKey: String): List<Place>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val categoryPrompt = when (category) {
                "Praias" -> "beaches and coastal spots"
                "Restaurantes" -> "restaurants and cafes"
                "Hotéis" -> "hotels and hostels"
                else -> "tourist attractions, restaurants, and hotels"
            }

            val prompt = """
                Give me a list of 5 real and popular places for '$categoryPrompt' in '$city'.
                Please write names and locations in Portuguese.
                Return ONLY a JSON array, without markdown blocks, conforming to this schema:
                [
                  {
                    "id": "unique_id_string",
                    "name": "Place Name",
                    "location": "Area or Street, City, Country",
                    "rating": 4.5,
                    "tags": ["Tag1", "Tag2"]
                  }
                ]
            """.trimIndent()

            val jsonRequest = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonRequest.toString())
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(responseText)
                val candidates = jsonResponse.getJSONArray("candidates")
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                var text = parts.getJSONObject(0).getString("text").trim()
                
                // Limpar tags de código markdown
                if (text.startsWith("```")) {
                    text = text.substringAfter("\n").substringBeforeLast("```").trim()
                    if (text.startsWith("json")) {
                        text = text.removePrefix("json").trim()
                    }
                }

                val placesArray = JSONArray(text)
                val list = mutableListOf<Place>()
                for (i in 0 until placesArray.length()) {
                    val obj = placesArray.getJSONObject(i)
                    val id = obj.optString("id", i.toString())
                    val name = obj.optString("name")
                    val location = obj.optString("location")
                    val rating = obj.optDouble("rating", 4.0)
                    val tagsArray = obj.optJSONArray("tags")
                    val tags = mutableListOf<String>()
                    if (tagsArray != null) {
                        for (j in 0 until tagsArray.length()) {
                            tags.add(tagsArray.getString(j))
                        }
                    }
                    list.add(Place(id, name, location, rating, tags))
                }
                list
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateMockPlaces(city: String, category: String): List<Place> {
        val formattedCity = city.trim().replaceFirstChar { it.uppercase() }
        
        val restaurantNames = listOf(
            "Cervejaria %s", "Taverna do Bairro (%s)", "Restaurante Bella Vista", 
            "Pizzaria da Praça, %s", "Cantinho do Chef (%s)"
        )
        val beachNames = listOf(
            "Praia de %s", "Praia Grande (%s)", "Praia da Baía", 
            "Praia das Dunas (%s)", "Praia do Sol"
        )
        val hotelNames = listOf(
            "%s Grand Hotel", "Hotel Avenida %s", "%s Central Hostel", 
            "Pousada do Castelo, %s", "Boutique Hotel %s"
        )
        val genericNames = listOf(
            "Miradouro de %s", "Jardim Municipal de %s", "Castelo de %s", 
            "Centro Histórico de %s", "Museu de Arte de %s"
        )
        
        val restaurantTags = listOf("Comida Local", "Aberto agora", "Bom para Grupos", "Terraço", "Peixe Fresco")
        val beachTags = listOf("Famílias", "Surf", "Areia Fina", "Natureza", "Fácil Acesso")
        val hotelTags = listOf("Wi-Fi Grátis", "Piscina", "Pequeno Almoço", "Vista Cidade", "Estacionamento")
        val genericTags = listOf("Cultura", "Vistas", "Fotografia", "História", "Ar Livre")
        
        val selectedNames = when (category) {
            "Restaurantes" -> restaurantNames
            "Praias" -> beachNames
            "Hotéis" -> hotelNames
            else -> genericNames
        }
        
        val selectedTags = when (category) {
            "Restaurantes" -> restaurantTags
            "Praias" -> beachTags
            "Hotéis" -> hotelTags
            else -> genericTags
        }
        
        return List(5) { i ->
            val namePattern = selectedNames[i % selectedNames.size]
            val placeName = if (namePattern.contains("%s")) {
                String.format(namePattern, formattedCity)
            } else {
                "$namePattern, $formattedCity"
            }
            
            val rating = 4.0 + (i + 1) * 0.1 + (formattedCity.hashCode() % 10) * 0.01
            val roundedRating = Math.round(rating * 10.0) / 10.0
            
            val tag1 = selectedTags[(i * 2) % selectedTags.size]
            val tag2 = selectedTags[(i * 2 + 1) % selectedTags.size]
            
            Place(
                id = "mock_${category}_${formattedCity}_$i",
                name = placeName,
                location = "Zona Central, $formattedCity",
                rating = minOf(5.0, roundedRating),
                tags = listOf(tag1, tag2)
            )
        }
    }
}
