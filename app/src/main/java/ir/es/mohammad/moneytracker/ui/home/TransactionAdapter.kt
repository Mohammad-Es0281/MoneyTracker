package ir.es.mohammad.moneytracker.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.es.mohammad.moneytracker.R
import ir.es.mohammad.moneytracker.databinding.ItemTransactionBinding
import ir.es.mohammad.moneytracker.model.Transaction
import ir.es.mohammad.moneytracker.model.TransactionType
import ir.es.mohammad.moneytracker.ui.toFormattedDate

class TransactionAdapter(
    private val onTransactionSelected: (transaction: Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.CustomViewHolder>(UserDiffCallBack()) {

    inner class CustomViewHolder(private val itemViewBinding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bind(transaction: Transaction) {
            with(itemViewBinding) {
                root.setOnClickListener { onTransactionSelected(transaction) }
                val isIncome = transaction.transactionType == TransactionType.INCOME
                val prefix = if (isIncome) "+ " else "- "
                val textColor = if (isIncome) R.color.income_foreground else R.color.expense_foreground
                txtAmount.text = prefix + transaction.amount.toString()
                txtAmount.setTextColor(ContextCompat.getColor(itemViewBinding.root.context, textColor))
                txtDate.text = transaction.date.toFormattedDate()
                txtCategory.text = transaction.category.name
            }
        }
    }

    private class UserDiffCallBack : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CustomViewHolder(ItemTransactionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}