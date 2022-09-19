package ir.es.mohammad.moneytracker.ui.addtransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentAddTransactionBinding
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.util.collectResult
import ir.es.mohammad.moneytracker.util.launchAndRepeatWithViewLifecycle
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val addTransactionViewModel: AddTransactionViewModel by viewModels()
    private val calendar: Calendar by lazy { Calendar.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTransactionBinding.bind(view)

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

            txtInputDate.setOnClickListener { showDatePickerDialog() }

            btnAdd.setOnClickListener { addTransaction() }
        }
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
            val transaction = Transaction(amount, transactionType, calendar.timeInMillis)
            addTransactionViewModel.addTransaction(transaction)
        }
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            addTransactionViewModel.transactionFlow.collectResult(
                onLoading = {},
                onSuccess = { requireActivity().onBackPressed() },
                onError = { errorMessage ->
                    val message = errorMessage ?: getString(R.string.unknown_error)
                    val actionMessage = getString(R.string.try_again)
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                        .setAction(actionMessage) { addTransaction() }.show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}