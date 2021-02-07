package br.com.rosait.marvelcharacters.common.base

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.rosait.marvelcharacters.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    fun showMessage(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.lbl_warning))
        builder.setMessage(message)
        builder.setNeutralButton("OK", DialogInterface.OnClickListener { _, _ ->
            dialog.dismiss()
        })

        dialog = builder.create()
        dialog.show()
    }
}