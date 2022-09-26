package ir.es.mohammad.moneytracker.ui.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentHomeBinding
import ir.es.mohammad.moneytracker.model.DateType
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.ui.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), DateSelectionDialog.DateSelectionListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _transactionAdapter: TransactionAdapter? = null
    private val transactionAdapter get() = _transactionAdapter!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        initViews()
        observe()
    }

    private fun initViews() {
        with(binding) {
            _transactionAdapter =
                TransactionAdapter() { transaction -> navigateToTransaction(transaction.id) }
            recyclerViewTransactions.apply {
                adapter = transactionAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean = false
                }
            }

            spinnerDateType.onItemSelectedListener = spinnerDateTypeListener

            btnDateSelection.setOnClickListener {
                showDateSelectionDialog()
                btnDateSelection.isClickable = false
            }

            btnAddTransaction.setOnClickListener { navigateToTransaction() }

            groupBtnTransactionType.check(R.id.btnAll)
            btnAll.setBackgroundTint(requireContext().applicationContext, R.color.blue_grey_100)
            groupBtnTransactionType.addOnButtonCheckedListener { _, checkedId, isChecked ->
                changeTransactionTypeBackground(checkedId, isChecked)
            }
            btnAll.setOnClickListener { viewModel.shownTransactionsType = null }
            btnIncome.setOnClickListener {
                viewModel.shownTransactionsType = TransactionType.INCOME
            }
            btnExpense.setOnClickListener {
                viewModel.shownTransactionsType = TransactionType.EXPENSE
            }
        }
    }

    private fun changeTransactionTypeBackground(checkedId: Int, isChecked: Boolean) {
        with(binding) {
            val appContext = requireActivity().applicationContext
            when (checkedId) {
                R.id.btnIncome -> {
                    val color = if (isChecked) R.color.light_green else R.color.transparent
                    btnIncome.setBackgroundTint(appContext, color)
                }
                R.id.btnAll -> {
                    val color = if (isChecked) R.color.blue_grey_100 else R.color.transparent
                    btnAll.setBackgroundTint(appContext, color)
                }
                R.id.btnExpense -> {
                    val color = if (isChecked) R.color.light_red else R.color.transparent
                    btnExpense.setBackgroundTint(appContext, color)
                }
            }
        }
    }

    private val spinnerDateTypeListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            viewModel.dateType =
                DateType.valueOf(binding.spinnerDateType.getItemAtPosition(position).toString().uppercase())
        }

        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    }

    private fun navigateToTransaction(transactionId: Long = -1) {
        findNavController()
            .navigate(HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                transactionId))
    }

    private fun showDateSelectionDialog() {
        DateSelectionDialog().apply {
            val years = this@HomeFragment.resources.getStringArray(R.array.years)
            val (year, month, day) = viewModel.getYearMonthDay()
            arguments = bundleOf(
                DateSelectionDialog.DAY_ARG to day,
                DateSelectionDialog.MONTH_ARG to month,
                DateSelectionDialog.YEAR_ARG to (year - years.first().toInt()),
                DateSelectionDialog.DATE_TYPE_ARG to viewModel.dateType.ordinal
            )
        }.show(childFragmentManager, null)
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.transactionFlow.collectResult(
                    onLoading = {},
                    onSuccess = { transactions -> onTransactionsReceived(transactions) },
                    onError = { errorMsg ->
                        showError(requireView(), errorMsg) { viewModel.setTransactionByType() }
                    }
                )
            }

            launch {
                viewModel.formattedDate.collect { date -> binding.btnDateSelection.text = date }
            }
        }
    }

    private fun onTransactionsReceived(transactions: List<Transaction>) {
        if (transactions.isNotEmpty())
            binding.txtNoTransaction.gone()
        else
            binding.txtNoTransaction.visible()
        transactionAdapter.submitList(transactions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _transactionAdapter = null
    }

    override fun onDateSelected(year: Int, month: Int, day: Int) {
        viewModel.setCalendar(year, month, day)
    }

    override fun onDestroyDialog() {
        super.onDestroyDialog()
        _binding?.btnDateSelection?.isClickable = true
    }
}
