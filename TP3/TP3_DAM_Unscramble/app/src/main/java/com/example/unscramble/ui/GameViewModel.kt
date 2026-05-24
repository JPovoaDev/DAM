package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    // Game UI state
    // para os elementos conbinaveis possam detectar atualizacoes de estado
    //da interface e fazer com que osestado de tea sobrevia as mudancas de configuracao
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private lateinit var currentWord:String  // variavel para savar a plavra embaralhada atual
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }
    private fun pickRandomWordAndShuffle():String{
        //apanhar palavras random ate apanhar uma que ainda n foi usada
        currentWord = allWords.random()//apanha uma palavra random do array de palavras allWords
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()//se a currentWord ja tivesse sido usada chama denovo a funcao
        }else{
            //senao adicionamos a lista das palavras usadas e chamamos a funcao para as baralhar
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }
    private fun shuffleCurrentWord(word:String):String{
        //transforma a palavra em um array onde cda possicao é uma celula do array
        val tempWord = word.toCharArray()
        //vamos baralhar o array
        tempWord.shuffle()
        while ( String(tempWord).equals(word)){
            tempWord.shuffle()
        }
        return String(tempWord)

    }
    fun updateUserGuess(guessWord:String){
        userGuess = guessWord
    }
    //verifica se o guess é certo senao mete a fazia
    fun checkUserGuess(){

        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // se for errada mostra um erro
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")

    }
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }

        } else{
            // Normal round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reset user guess
        updateUserGuess("")
    }
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }


}