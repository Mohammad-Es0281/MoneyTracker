package ir.es.mohammad.moneytracker.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.FragmentHomeBinding
import ir.es.mohammad.moneytracker.util.collectResult
import ir.es.mohammad.moneytracker.util.gone
import ir.es.mohammad.moneytracker.util.launchAndRepeatWithViewLifecycle
import ir.es.mohammad.moneytracker.util.visible

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _transactionAdapter: TransactionAdapter? = null
    private val transactionAdapter get() = _transactionAdapter!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        if (savedInstanceState == null)
            homeViewModel.getTransacrions()

        initViews()
        observe()
    }

    private fun initViews() {
        with(binding) {
            _transactionAdapter = TransactionAdapter()
            recyclerViewTransactions.apply {
                adapter = transactionAdapter
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }

            btnAddTransaction.setOnClickListener {
                findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment())
            }
        }
    }

    private fun observe() {
        launchAndRepeatWithViewLifecycle {
            homeViewModel.transactionFlow.collectResult(
                onLoading = {},
                onSuccess = { transactions ->
                    if (transactions.isNotEmpty()) {
                        transactionAdapter.submitList(transactions)
                        binding.txtNoTransaction.gone()
                    }
                    else
                        binding.txtNoTransaction.visible()
                },
                onError = { errorMessage ->
                    val message = errorMessage ?: getString(R.string.unknown_error)
                    val actionMessage = getString(R.string.try_again)
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                        .setAction(actionMessage) { homeViewModel.getTransacrions() }.show()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _transactionAdapter = null
    }
}