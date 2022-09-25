package ir.es.mohammad.moneytracker.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ir.es.mohammad.moneytracker.databinding.DialogSelectDateBinding

class DateSelectionDialog : DialogFragment() {

    private var _binding: DialogSelectDateBinding? = null
    private val binding get() = _binding!!
    private lateinit var onDateSelectListener: OnDateSelectListener
    private var initDay = 0
    private var initMonth = 0
    private var initYear = 0
    private var dateType = 0

    companion object {
        const val DAY_ARG = "moneytracker.dateselectiondialog.day.arg"
        const val MONTH_ARG = "moneytracker.dateselectiondialog.month.arg"
        const val YEAR_ARG = "moneytracker.dateselectiondialog.year.arg"
        const val DATE_TYPE_ARG = "moneytracker.dateselectiondialog.datetype.arg"
    }

    interface OnDateSelectListener {
        fun onDateSelected(year: Int, month: Int, day: Int)
        fun onDestroyDialog() {}
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onDateSelectListener = requireParentFragment() as OnDateSelectListener
        initDay = requireArguments().getInt(DAY_ARG)
        initMonth = requireArguments().getInt(MONTH_ARG)
        initYear = requireArguments().getInt(YEAR_ARG)
        dateType = requireArguments().getInt(DATE_TYPE_ARG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSelectDateBinding.inflate(LayoutInflater.from(context))
        initViews()
        return MaterialAlertDialogBuilder(requireActivity())
            .setView(binding.root)
            .create()
    }

    private fun initViews() {
        with(binding) {
            btnDismiss.setOnClickListener { dismiss() }

            spinnerDays.setSelection(initDay - 1)
            spinnerMonths.setSelection(initMonth - 1)
            spinnerYears.setSelection(initYear)

            when (dateType) {
                1 -> { spinnerDays.gone(); txtDay.gone() }
                2 -> { spinnerDays.gone(); txtDay.gone(); spinnerMonths.gone(); txtMonth.gone() }
            }

            btnOk.setOnClickListener {
                val day = spinnerDays.selectedItem.toString().toInt()
                val month = spinnerMonths.selectedItemPosition + 1
                val year = spinnerYears.selectedItem.toString().toInt()
                onDateSelectListener.onDateSelected(year, month, day)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        onDateSelectListener.onDestroyDialog()
    }
}