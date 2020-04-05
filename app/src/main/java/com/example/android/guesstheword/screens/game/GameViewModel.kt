package com.example.android.guesstheword.screens.game


import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)
class GameViewModel :ViewModel(){
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        private const val COUNTDOWN_PANIC_SECONDS = 10L

        const val COUNTDOWN_TIME = 240000L
    }
    // The current word
     private val _word = MutableLiveData<String>()
            val word : LiveData<String>
               get() = _word
    // The current score
     private val _score = MutableLiveData<Int>()
            val score: LiveData<Int>
              get() = _score
    private val _eventGameFinish = MutableLiveData<Boolean>()
            val eventGameFinish: LiveData<Boolean>
              get() = _eventGameFinish
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz
     private val _currentTime = MutableLiveData<Long>()
            val currentTime: LiveData<Long>
              get() = _currentTime
    val currentTimeString = Transformations.map(currentTime,{time ->
        DateUtils.formatElapsedTime(time)
    })
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

   private val timer:CountDownTimer

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

 init {
     _currentTime.value = COUNTDOWN_TIME
     resetList()
     nextWord()
     _score.value = 0
     _word.value = wordList.random().toString()
     _eventGameFinish.value = false

     //Create a timer instance from the CountDownTimer object
     timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

         override fun onTick(millisUntilFinished: Long) {
             _currentTime.value = (millisUntilFinished / ONE_SECOND)
             if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                 _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
             }
         }

         override fun onFinish() {
             _currentTime.value = DONE
             _eventBuzz.value = BuzzType.GAME_OVER
             _eventGameFinish.value = true

         }
     }
     timer.start()
 }

    private fun resetList() {
        wordList = mutableListOf(
                "الرصاصة لا تزال في جيبي",
                "الفيل الأزرق",
                "كازابلانكا",
                "هنا وسرور",
                "تراب الماس",
                "ياباني أصلي",
                "طلق صناعي",
                "حياتي مبهدله",
                "الحرب العلميه التالته",
                "فاصل ونعود",
                "حرب أيطاليا",
                "عريس من جهة أمنيه",
                "حملة فريزر",
                "حصل خير",
                "ظرف طارق",
                "بني ادم",
                "بوبوس",
                "من 30 سنه",
                "حرب كرموز",
                "عيلر ناري",
                "جواب اعتقال",
                "حماتي بتحبني",
                "عنتر ابن ابن ابن شداد"
        )
        wordList.shuffle()
    }
     fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
            _word.value = wordList.removeAt(0)
        }

     fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
         timer.cancel()
         timer.start()
    }

     fun onCorrect() {
         _score.value = (score.value)?.plus(1)
         _eventBuzz.value = BuzzType.CORRECT
        nextWord()
         timer.cancel()
         timer.start()
    }
    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }


    override fun onCleared() {
        super.onCleared()
         timer.cancel()
    }

}