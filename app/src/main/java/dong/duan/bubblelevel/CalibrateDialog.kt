package dong.duan.bubblelevel

import android.content.Context
import dong.duan.bubblelevel.databinding.DialogCalibrateAppBinding

class CalibrateDialog (context: Context,val onDialog: OnCalibrateEvent) :BaseDialog(context) {
   var binding = DialogCalibrateAppBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
        binding.icClose.setOnClickListener {
            dismiss()
        }
        binding.btnReset.setOnClickListener {
            onDialog.onReset()
            dismiss()
        }
        binding.btnCalirate.setOnClickListener {
            onDialog.onCalibrate()
            dismiss()
        }
    }
}

interface OnCalibrateEvent{
    fun onCalibrate()
    fun onReset()
}