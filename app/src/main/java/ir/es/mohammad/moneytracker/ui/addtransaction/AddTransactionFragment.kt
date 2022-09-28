package ir.es.mohammad.moneytracker.ui.addtransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentAddTransactionBinding
import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.ui.*
import ir.es.mohammad.moneytracker.ui.util.viewBinding
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction),
    AddCategoryDialog.OnCategoryAddedListener {

    private val binding by viewBinding(FragmentAddTransactionBinding::bind)
    private var _categorySpinnerAdapter: ArrayAdapter<String>? = null
    private val categorySpinnerAdapter get() = _categorySpinnerAdapter!!
    private val viewModel: AddTransactionViewModel by viewModels()
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val args: AddTransactionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        if (args.transactionId != -1L)
            getAndSetInitTransaction()
        observe()
    }

    private fun getAndSetInitTransaction() {
        viewModel.getTransaction(args.transactionId)
        launchAndRepeatWithViewLifecycle {
            viewModel.transactionFlow.collectResult(
                onLoading = {},
                onSuccess = { transaction -> setInitTransactionToView(transaction) },
                onError = { errorMessage ->
                    showError(requireView(),
                        errorMessage) { viewModel.getTransaction(args.transactionId) }
                }
            )
        }
    }

    private fun setInitTransactionToView(transaction: Transaction) {
        with(binding) {
            val checkBtnId =
                if (transaction.transactionType == TransactionType.INCOME) R.id.btnIncome else R.id.btnExpense
            groupBtnTransactionType.check(checkBtnId)
            txtTitle.text = resources.getText(R.string.edit_transaction)
            txtInputAmount.setText(transaction.amount.toString())
            txtInputDate.setText(transaction.date.toFormattedDate())
            spinnerCategory.setSelection(transaction.category.id)
            txtInputNote.setText(transaction.note)
            calendar.timeInMillis = transaction.date
        }
    }

    private fun initViews() {
        with(binding) {
            btnBack.setOnClickListener { requireActivity().onBackPressed() }

            groupBtnTransactionType.check(R.id.btnIncome)
            btnIncome.setBackgroundTint(requireContext().applicationContext, R.color.income_background)
            groupBtnTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
                changeTransactionTypeBackground(checkedId, isChecked)
            }

            txtInputAmount.addTextChangedListener(afterTextChanged = { text ->
                afterAmountTextChanged(text)
            })

            txtInputDate.setText(calendar.timeInMillis.toFormattedDate())
            txtInputDate.setOnClickListener { showDatePickerDialog() }

            btnSave.setOnClickListener {
                val transaction = makeTransaction()
                if (args.transactionId == -1L) viewModel.addTransaction(transaction)
                else viewModel.editTransaction(transaction)
            }

            spinnerCategory.onItemSelectedListener = categorySpinnerListener
        }
    }

    private val categorySpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            if (position + 1 == binding.spinnerCategory.adapter.count) {
                showAddCategoryDialog()
                binding.spinnerCategory.setSelection(0)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }

    private fun changeTransactionTypeBackground(checkedId: Int, isChecked: Boolean) {
        with(binding) {
            val appContext = requireActivity().applicationContext
            when (checkedId) {
                R.id.btnIncome -> {
                    val color = if (isChecked) R.color.income_background else R.color.transparent
                    btnIncome.setBackgroundTint(appContext, color)
                }
                R.id.btnExpense -> {
                    val color = if (isChecked) R.color.expense_background else R.color.transparent
                    btnExpense.setBackgroundTint(appContext, color)
                }
            }
        }
    }

    private fun afterAmountTextChanged(editable: Editable?) {
        with(binding) {
            val isBlank = editable?.isBlank() ?: true
            if (isBlank) txtInputAmount.error = getString(R.string.amount_warning)

            btnSave.isEnabled = !isBlank
            val textColorId = if (btnSave.isEnabled) R.color.txt_icon else R.color.primary
            val textColor = ContextCompat.getColor(requireContext(), textColorId)
            btnSave.setTextColor(textColor)
        }
    }

    private fun showAddCategoryDialog() {
        AddCategoryDialog().show(childFragmentManager, null)
    }

    private fun showDatePickerDialog() {
        val dateChangeListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(year, month, day)
                binding.txtInputDate.setText(calendar.timeInMillis.toFormattedDate())
            }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateChangeListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val years = resources.getStringArray(R.array.years)
        val datePicker = datePickerDialog.datePicker
        datePicker.minDate =
            Calendar.getInstance().apply { set(years.first().toInt(), 0, 1) }.timeInMillis
        datePicker.maxDate =
            Calendar.getInstance().apply {
                set(years.last().toInt(), datePicker.month, datePicker.dayOfMonth)
            }.timeInMillis

        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(R.color.txt_icon)
    }

    private fun makeTransaction(): Transaction {
        with(binding) {
            val transactionType =
                if (groupBtnTransactionType.checkedButtonId == R.id.btnIncome) TransactionType.INCOME
                else TransactionType.EXPENSE
            val amount = txtInputAmount.text.toString().toLong()
            val category = Category(spinnerCategory.selectedItem.toString())
            val note = txtInputNote.text.toString()
            val id = args.transactionId.takeIf { id -> id != -1L } ?: 0
            return Transaction(amount, transactionType, calendar.timeInMillis, category, note, id)
        }
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.addTransactionFlow.collectResult(
                    onLoading = {},
                    onSuccess = { requireActivity().onBackPressed() },
                    onError = { errorMessage ->
                        showError(requireView(), errorMessage) { makeTransaction() }
                    }
                )
            }
            launch {
                viewModel.categoriesFlow.collectResult(
                    onLoading = {},
                    onSuccess = { categories ->
                        setCategorySpinnerItems(categories.map { category -> category.name })
                    },
                    onError = { errorMessage ->
                        showError(requireView(),
                            errorMessage) { viewModel.getCategories() }
                    }
                )
            }
            launch {
                viewModel.addCategoryFlow.collectResult(
                    onLoading = {},
                    onSuccess = { viewModel.getCategories() },
                    onError = { errorMessage ->
                        showError(requireView(), errorMessage) { showAddCategoryDialog() }
                    }
                )
            }
        }
    }

    private fun setCategorySpinnerItems(categories: List<String>) {
        _categorySpinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories)
            .apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                insert(resources.getString(R.string.add_category_spinner), count)
            }
        binding.spinnerCategory.adapter = categorySpinnerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _categorySpinnerAdapter = null
    }

    override fun onCategoryAdded(category: Category) {
        viewModel.addCategory(category)
    }
}