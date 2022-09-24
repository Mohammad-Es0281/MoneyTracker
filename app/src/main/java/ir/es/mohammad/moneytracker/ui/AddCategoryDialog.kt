import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ir.es.mohammad.moneytracker.databinding.DialogAddCategoryBinding
import ir.es.mohammad.moneytracker.model.Category

class AddCategoryDialog : DialogFragment() {

    private var _binding: DialogAddCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var onCategoryAddedListener: OnCategoryAddedListener

    interface OnCategoryAddedListener {
        fun onAddCategory(category: Category)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onCategoryAddedListener = requireParentFragment() as OnCategoryAddedListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCategoryBinding.inflate(LayoutInflater.from(context))
        initViews()
        return MaterialAlertDialogBuilder(requireActivity())
            .setView(binding.root)
            .create()
    }

    private fun initViews() {
        with(binding) {
            btnDismiss.setOnClickListener { dismiss() }

            txtInputCategoryName.addTextChangedListener(
                onTextChanged = { text, _, _, _ -> btnOk.isEnabled = text?.isNotBlank() ?: false }
            )

            btnOk.setOnClickListener {
                val category = Category(txtInputCategoryName.text.toString())
                onCategoryAddedListener.onAddCategory(category)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}