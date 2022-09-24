package ir.es.mohammad.moneytracker.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.es.mohammad.moneytracker.data.Repository
import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    private val _addTransactionFlow: MutableStateFlow<Result<Unit>> =
        MutableStateFlow(Result.Loading())
    val addTransactionFlow = _addTransactionFlow.asStateFlow()

    private val _addCategoryFlow: MutableStateFlow<Result<Unit>> =
        MutableStateFlow(Result.Loading())
    val addCategoryFlow = _addCategoryFlow.asStateFlow()

    private val _categoriesFlow: MutableStateFlow<Result<List<Category>>> =
        MutableStateFlow(Result.Loading())
    val categoriesFlow = _categoriesFlow.asStateFlow()

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction).collect { _addTransactionFlow.emit(it) }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { _categoriesFlow.emit(it) }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            repository.addCategory(category).collect { _addCategoryFlow.emit(it) }
        }
    }
}