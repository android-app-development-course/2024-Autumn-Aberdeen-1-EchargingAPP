package com.zzh.echarging

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zzh.mylibrary.VehicleKeyboardHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



fun EditText.bindVehicleKeyboard() {
    VehicleKeyboardHelper.bind(this)
}

abstract class BaseBindingActivity<T : ViewDataBinding>() : AppCompatActivity() {

    protected lateinit var binding: T

    abstract var layoutRes: Int

    private lateinit var picker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, layoutRes)
        picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                onImagePick(it)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(getApplyWindowInsetsView()) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    open fun onImagePick(uri: Uri) {

    }

    protected fun pickImage() {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    protected fun getApplyWindowInsetsView(): View {
        return findViewById(R.id.main)
    }
}

fun logE(s: String) {
    Log.e("zzh", s)
}

abstract class AbsBindingRVAdapter<T : ViewDataBinding, D : Any> :
    RecyclerView.Adapter<AbsBindingRVAdapter.BindingViewHolder<T>>() {

    open class BindingViewHolder<T : ViewDataBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)

    abstract val data: MutableList<D>

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<T> {
        return generateBindingViewHolder(parent, viewType)
    }

    open fun submitList(list: List<D>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        bindDefaultData(holder, position)
    }

    override fun getItemCount(): Int {
        logE("data.size: ${data.size}")
        return data.size
    }

    protected open fun generateBindingViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<T> {
        return BindingViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                getLayoutRes(),
                parent,
                false
            )
        )
    }

    protected open fun bindDefaultData(holder: BindingViewHolder<T>, position: Int) {
        holder.binding.setVariable(getVariableId(), data[position])
    }

    abstract fun getLayoutRes(): Int

    abstract fun getVariableId(): Int
}

interface DataBindingAbility<T : ViewDataBinding> {
    val layoutRes: Int

    fun getDataBinding(): T
}

abstract class BaseBindingFragment<T : ViewDataBinding>() : Fragment(), DataBindingAbility<T> {

    protected lateinit var binding: T

    abstract override var layoutRes: Int

    private lateinit var picker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                onImagePick(it)
            }
        }
    }

    protected fun onImagePick(uri: Uri) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun getDataBinding(): T {
        return binding
    }
}

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding>() : BottomSheetDialogFragment(),
    DataBindingAbility<T> {
    protected lateinit var binding: T

    abstract override var layoutRes: Int

    private lateinit var picker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let {
                onImagePick(it)
            }
        }
    }

    protected open fun onImagePick(uri: Uri) {

    }

    protected fun pickImage() {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun getDataBinding(): T {
        return binding
    }
}

fun Context.getDao() = ChargingDatabase.getInstance(this).chargingDao()

fun Fragment.getDao() = ChargingDatabase.getInstance(requireContext()).chargingDao()

fun AppCompatActivity.runOnWorkThread(block: () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) {
        block()
    }
}

fun Fragment.runOnWorkThread(block: () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) {
        block()
    }
}

fun Fragment.runOnUiThread(block: () -> Unit) {
    requireActivity().runOnUiThread(block)
}

fun Context.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(s: String) {
    requireContext().toast(s)
}
