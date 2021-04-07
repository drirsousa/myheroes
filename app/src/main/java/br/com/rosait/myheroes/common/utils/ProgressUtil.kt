package br.com.rosait.myheroes.common.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import br.com.rosait.myheroes.R

class ProgressUtil {

    companion object {

        private var progressDialog: Dialog? = null

        private var isProgressShow = false

        fun showProgressDialog(context: Context) {

            if (!isProgressShow) {
                val dialog = Dialog(context)
                val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
                dialog.setContentView(inflate)
                dialog.setCancelable(false)
                dialog.window!!.setBackgroundDrawable(
                        ColorDrawable(Color.TRANSPARENT))

                progressDialog = dialog

                isProgressShow = true

                progressDialog?.show()
            }
        }

        fun hideProgressDialog() {

            if (progressDialog != null) {
                progressDialog?.dismiss()

                isProgressShow = false
            }
        }
    }
}