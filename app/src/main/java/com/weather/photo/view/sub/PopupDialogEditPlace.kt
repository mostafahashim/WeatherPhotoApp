package com.weather.photo.view.sub

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.weather.photo.R
import com.weather.photo.databinding.PopupDialogEditLocationBinding
import com.weather.photo.databinding.PopupDialogSureBinding
import com.weather.photo.observer.OnAskUserAction
import com.weather.photo.observer.OnEditPlaceData
import com.weather.photo.view.activity.baseActivity.BaseActivity

class PopupDialogEditPlace : BaseDialogFragment() {

    internal var activity: BaseActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            activity = context
        }
    }

    lateinit var binding: PopupDialogEditLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity == null) activity = getActivity() as BaseActivity?
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.popup_dialog_edit_location,
                container,
                false
            )
        binding.lifecycleOwner = this
        return binding.root
    }

    lateinit var onEditPlaceData: OnEditPlaceData

    fun setOnEditPlaceDataObserver(onEditPlaceData: OnEditPlaceData) {
        this.onEditPlaceData = onEditPlaceData
    }

    internal lateinit var dialog: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity == null)
            activity = requireActivity() as BaseActivity?
        dialog = Dialog(requireActivity())
        dialog = Dialog(requireActivity(), R.style.FullScreenDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeViews()
        setListener()
    }


    var placeName = ""

    fun initializeViews() {
        getArgumentsData()
        setData()
    }

    fun getArgumentsData() {
        if (arguments != null) {
            placeName = requireArguments().getString("placeName", "")
            binding.edttextName.setText(placeName)
        }
    }

    fun setData() {
        binding.edttextName.setText(placeName)
    }

    fun setListener() {
        binding.btnDialogSureSubmit.setOnClickListener {
            if (binding.edttextName.text.toString().trim().isNotEmpty()) {
                if (::onEditPlaceData.isInitialized)
                    onEditPlaceData.onEditPlaceName(binding.edttextName.text.toString())
                dismissAllowingStateLoss()
            } else {
                binding.edttextName.error = getString(R.string.required_input)
            }
        }

        binding.btnDialogSureCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

    }

}