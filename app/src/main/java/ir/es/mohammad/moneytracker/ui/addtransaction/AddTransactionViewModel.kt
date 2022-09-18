package ir.es.mohammad.moneytracker.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.es.mohammad.moneytracker.data.TransactionsRepository
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(private val transactionsRepository: TransactionsRepository) :
    ViewModel() {
    private val _transactionFlow: MutableStateFlow<Result<Unit>> =
        MutableStateFlow(Result.Loading())
    val transactionFlow = _transactionFlow.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.addTransaction(transaction).collect {
                _transactionFlow.emit(it)
            }
        }
    }
}