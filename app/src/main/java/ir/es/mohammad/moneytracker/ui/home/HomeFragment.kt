package ir.es.mohammad.moneytracker.ui.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentHomeBinding
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.ui.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), DateSelectionDialog.OnDateSelectListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _transactionAdapter: TransactionAdapter? = null
    private val transactionAdapter get() = _transactionAdapter!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        if (savedInstanceState == null)
            homeViewModel.getTransactionsByDate()

        initViews()
        observe()
    }

    private fun initViews() {
        with(binding) {
            _transactionAdapter = TransactionAdapter()
            recyclerViewTransactions.apply {
                adapter = transactionAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean = false
                }
            }

            spinnerDateType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    homeViewModel.dateType = position
                    homeViewModel.getTransactionsByDate()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

            btnDateSelection.setOnClickListener {
                showDateSelectionDialog()
                btnDateSelection.isClickable = false
            }

            btnAddTransaction.setOnClickListener {
                findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment())
            }
        }
    }

    private fun showDateSelectionDialog() {
        DateSelectionDialog().apply {
            val years = this@HomeFragment.resources.getStringArray(R.array.years)
            val (year, month, day) = homeViewModel.getYearMonthDay()
            arguments = bundleOf(
                DateSelectionDialog.DAY_ARG to day,
                DateSelectionDialog.MONTH_ARG to month,
                DateSelectionDialog.YEAR_ARG to (year - years.first().toInt()),
                DateSelectionDialog.DATE_TYPE_ARG to homeViewModel.dateType
            )
        }.show(childFragmentManager, null)
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            launch {
                homeViewModel.transactionFlow.collectResult(
                    onLoading = {},
                    onSuccess = { transactions -> onTransactionsReceived(transactions) },
                    onError = { errorMsg ->
                        showError(requireView(), errorMsg) { homeViewModel.getTransactionsByDate() }
                    }
                )
            }

            launch {
                homeViewModel.formattedDate.collect { date -> binding.btnDateSelection.text = date }
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
        homeViewModel.setCalendar(year, month, day)

        homeViewModel.getTransactionsByDate()
    }

    override fun onDestroyDialog() {
        super.onDestroyDialog()
        _binding?.btnDateSelection?.isClickable = true
    }
}
