package ir.es.mohammad.moneytracker.ui.addtransaction

import AddCategoryDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentAddTransactionBinding
import ir.es.mohammad.moneytracker.model.Category
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.ui.collectResult
import ir.es.mohammad.moneytracker.ui.launchAndRepeatWithViewLifecycle
import ir.es.mohammad.moneytracker.ui.showError
import ir.es.mohammad.moneytracker.util.Result
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction),
    AddCategoryDialog.OnCategoryAddedListener {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private var _categorySpinnerAdapter: ArrayAdapter<String>? = null
    private val categorySpinnerAdapter get() = _categorySpinnerAdapter!!
    private val addTransactionViewModel: AddTransactionViewModel by viewModels()
    private val calendar: Calendar by lazy { Calendar.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)

        if (addTransactionViewModel.categoriesFlow.value is Result.Loading)
            addTransactionViewModel.getCategories()

        initViews()
        observe()
    }

    private fun initViews() {
        with(binding) {
            btnBack.setOnClickListener { requireActivity().onBackPressed() }

            groupBtnTransactionType.check(R.id.btnIncome)
            btnIncome.setBackgroundTint(R.color.light_green)
            groupBtnTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
                changeBackground(checkedId, isChecked)
            }

            txtInputAmount.addTextChangedListener(afterTextChanged = { afterAmountTextChanged(it) })

            setSelectedDate()
            txtInputDate.setOnClickListener { showDatePickerDialog() }

            btnAdd.setOnClickListener { addTransaction() }

            spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (position + 1 == spinnerCategory.adapter.count) {
                        showAddCategoryDialog()
                        spinnerCategory.setSelection(0)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
    }

    private fun showAddCategoryDialog() {
        AddCategoryDialog().show(childFragmentManager, null)
    }

    private fun showDatePickerDialog() {
        val dateChangeListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(year, month, day)
                setSelectedDate()
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
            .setTextColor(R.color.blue_grey_700)
    }

    private fun setSelectedDate() {
        val pattern = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(pattern)
        binding.txtInputDate.setText(sdf.format(calendar.time))
    }

    private fun changeBackground(checkedId: Int, isChecked: Boolean) {
        with(binding) {
            if (checkedId == R.id.btnIncome) {
                if (isChecked)
                    btnIncome.setBackgroundTint(R.color.light_green)
                else
                    btnIncome.setBackgroundTint(R.color.transparent)
            } else {
                if (isChecked)
                    btnExpense.setBackgroundTint(R.color.light_red)
                else
                    btnExpense.setBackgroundTint(R.color.transparent)
            }
        }
    }

    private fun View.setBackgroundTint(@ColorRes color: Int) {
        val backgroundTintColor = ContextCompat.getColorStateList(requireContext(), color)
        this.backgroundTintList = backgroundTintColor
    }

    private fun afterAmountTextChanged(editable: Editable?) {
        with(binding) {
            val isBlank = editable?.isBlank() ?: true
            if (isBlank)
                txtInputAmount.error = getString(R.string.amount_warning)

            btnAdd.isEnabled = !isBlank
            val textColorId = if (btnAdd.isEnabled) R.color.blue_grey_700 else R.color.blue_grey_100
            val textColor = ContextCompat.getColor(requireContext(), textColorId)
            btnAdd.setTextColor(textColor)
        }
    }

    private fun addTransaction() {
        with(binding) {
            val transactionType =
                if (groupBtnTransactionType.checkedButtonId == R.id.btnIncome) TransactionType.INCOME
                else TransactionType.EXPENSE
            val amount = txtInputAmount.text.toString().toLong()
            val category = Category(spinnerCategory.selectedItem.toString())
            val transaction = Transaction(amount, transactionType, calendar.timeInMillis, category)
            addTransactionViewModel.addTransaction(transaction)
        }
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            launch {
                addTransactionViewModel.addTransactionFlow.collectResult(
                    onLoading = {},
                    onSuccess = { requireActivity().onBackPressed() },
                    onError = { errorMessage ->
                        showError(requireView(), errorMessage) { addTransaction() }
                    }
                )
            }
            launch {
                addTransactionViewModel.categoriesFlow.collectResult(
                    onLoading = {},
                    onSuccess = { categories ->
                        setCategorySpinnerItems(categories.map { category -> category.name })
                    },
                    onError = { errorMessage ->
                        showError(requireView(),
                            errorMessage) { addTransactionViewModel.getCategories() }
                    }
                )
            }
            launch {
                addTransactionViewModel.addCategoryFlow.collectResult(
                    onLoading = {},
                    onSuccess = {},
                    onError = { errorMessage ->
                        showError(requireView(), errorMessage) { showAddCategoryDialog() }
                    }
                )
            }
        }
    }

    private fun setCategorySpinnerItems(categories: List<String>) {
        _categorySpinnerAdapter = ArrayAdapter<String>(
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
        _binding = null
        _categorySpinnerAdapter = null
    }

    override fun onAddCategory(category: Category) {
        addTransactionViewModel.addCategory(category)
    }
}