package ir.es.mohammad.moneytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.es.mohammad.moneytracker.data.Repository
import ir.es.mohammad.moneytracker.model.DateType
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Month
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var calendar: Calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
        set(value) {
            field = value
            setFormattedDate()
        }

    var dateType: DateType = DateType.DAILY
        set(value) {
            field = value
            setFormattedDate()
            collectTransactionFlow()
        }

    var shownTransactionsType: TransactionType? = null
        set(value) {
            field = value
            setTransactionByType()
        }

    init {
        collectTransactionFlow()
    }

    private lateinit var allTransactions: List<Transaction>
    private lateinit var incomeTransactions: List<Transaction>
    private lateinit var expenseTransactions: List<Transaction>

    private val _transactionFlow: MutableStateFlow<Result<List<Transaction>>> =
        MutableStateFlow(Result.Loading())
    val transactionFlow = _transactionFlow.asStateFlow()

    private val _formattedDate: MutableStateFlow<String> = MutableStateFlow(getFormattedDate())
    val formattedDate = _formattedDate.asStateFlow()

    private fun collectTransactionFlow() {
        viewModelScope.launch {
            val (startDate, endDate) = getStartAndEndDate()
            repository.getTransactionsByDate(startDate, endDate).collect { transactionsResult ->
                if (transactionsResult.isSuccess) {
                    emitSuccessTransactions(transactionsResult)
                } else _transactionFlow.emit(transactionsResult)
            }
        }
    }

    private fun emitSuccessTransactions(transactionsResult: Result<List<Transaction>>) {
        allTransactions = transactionsResult.data!!
        setTransactionByType()
    }

    fun setTransactionByType() {
        viewModelScope.launch {
            if (!this@HomeViewModel::allTransactions.isInitialized)
                collectTransactionFlow()
            else {
                when (shownTransactionsType) {
                    null -> {
                        _transactionFlow.emit(Result.Success(allTransactions))
                        setIncomeTransactions()
                        setExpenseTransactions()
                    }
                    TransactionType.INCOME -> {
                        setIncomeTransactions()
                        _transactionFlow.emit(Result.Success(incomeTransactions))
                        setExpenseTransactions()
                    }
                    TransactionType.EXPENSE -> {
                        setExpenseTransactions()
                        _transactionFlow.emit(Result.Success(expenseTransactions))
                        setIncomeTransactions()
                    }
                }
            }
        }
    }

    private fun setIncomeTransactions() {
        incomeTransactions =
            allTransactions.filter { transaction -> transaction.transactionType == TransactionType.INCOME }
    }

    private fun setExpenseTransactions() {
        expenseTransactions =
            allTransactions.filter { transaction -> transaction.transactionType == TransactionType.EXPENSE }
    }

    fun setCalendar(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        setFormattedDate()
        collectTransactionFlow()
    }

    fun getYearMonthDay() =
        Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

    private fun getStartAndEndDate(): Pair<Long, Long> {
        val newCalendar = calendar.clone() as Calendar
        if (dateType >= DateType.MONTHLY) {
            newCalendar.set(Calendar.DAY_OF_MONTH, 1)
            if (dateType == DateType.YEARLY) newCalendar.set(Calendar.MONTH, 0)
        }
        val startDate = newCalendar.timeInMillis

        val fieldToIncrease = when (dateType) {
            DateType.DAILY -> Calendar.DAY_OF_MONTH
            DateType.MONTHLY -> Calendar.MONTH
            DateType.YEARLY -> Calendar.YEAR
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
            DateType.DAILY -> "$year, $monthName, $day"
            DateType.MONTHLY -> "$year, $monthName"
            DateType.YEARLY -> "$year"
        }
    }

    private fun setFormattedDate() {
        viewModelScope.launch {
            _formattedDate.emit(getFormattedDate())
        }
    }
}