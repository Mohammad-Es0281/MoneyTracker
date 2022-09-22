package ir.es.mohammad.moneytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.es.mohammad.moneytracker.data.Repository
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private var calendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
        set(value) {
            field = value
            setFormattedDate()
        }

    var dateType: Int = 0
        set(value) {
            field = value
            setFormattedDate()
        }

    private val _transactionFlow: MutableStateFlow<Result<List<Transaction>>> =
        MutableStateFlow(Result.Loading())
    val transactionFlow = _transactionFlow.asStateFlow()

    private val _formattedDate: MutableStateFlow<String> = MutableStateFlow(getFormattedDate())
    val formattedDate = _formattedDate.asStateFlow()

    fun setCalendar(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        setFormattedDate()
    }

    fun getYearMonthDay() =
        Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

    fun getTransactionsByDate() {
        viewModelScope.launch {
            val (startDate, endDate) = getStartAndEndDate()
            repository.getTransactionsByDate(startDate, endDate).collect {
                _transactionFlow.emit(it)
            }
        }
    }

    private fun getStartAndEndDate(): Pair<Long, Long> {
        val newCalendar = calendar.clone() as Calendar
        if (dateType >= 1) {
            newCalendar.set(Calendar.DAY_OF_MONTH, 1)
            if (dateType == 2) newCalendar.set(Calendar.MONTH, 0)
        }
        val startDate = newCalendar.timeInMillis

        val fieldToIncrease = when (dateType) {
            0 -> Calendar.DAY_OF_MONTH
            1 -> Calendar.MONTH
            2 -> Calendar.YEAR
            else -> throw Exception()
        }
        newCalendar.add(fieldToIncrease, 1)
        val endDate = newCalendar.timeInMillis
        return Pair(startDate, endDate)
    }

    private fun getFormattedDate(): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.US)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return when (dateType) {
            0 -> "$year, $monthName, $day"
            1 -> "$year, $monthName"
            2 -> "$year"
            else -> throw Exception()
        }
    }

    private fun setFormattedDate() {
        viewModelScope.launch {
            _formattedDate.emit(getFormattedDate())
        }
    }
}