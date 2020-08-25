package com.example.atm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_others.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OthersActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others)
        val editPhoneNumber = findViewById<EditText>(R.id.enterPhoneNumberChange)

        fun showDialog() {
            val builder = AlertDialog.Builder(this@OthersActivity)
            builder.setTitle(R.string.dialog_title)
            builder.setMessage(R.string.dialog_message)
            builder.setPositiveButton("OK") { dialogInterface, which ->
                val phoneNumberChangeIntent =
                    Intent(
                        this@OthersActivity,
                        AccountNumberActivity::class.java
                    )
                phoneNumberChangeIntent.addCategory(Intent.CATEGORY_HOME)
                phoneNumberChangeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(phoneNumberChangeIntent)
                finish()

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }

        fun readNewPhoneNumber() {
            val stringPhoneNumber = editPhoneNumber.text.toString()
            val phoneNumber = stringPhoneNumber.toLong()
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
            val getAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
            db = DetailsDatabase.getAppDataBase(this)
            GlobalScope.launch {
                if (!(db?.details()?.isPhoneNumberExist(phoneNumber)!!)) {
                    db?.details()?.updatePhoneNumber(phoneNumber, getAccountNumber)
                    this@OthersActivity.runOnUiThread { showDialog() }


                } else {
                    this@OthersActivity.runOnUiThread {
                        Toast.makeText(
                            this@OthersActivity,
                            "Sorry,Enter valid phone number",
                            Toast.LENGTH_LONG
                        ).show()
                        enterPhoneNumberChange.text?.clear()
                    }
                }
            }
        }
        buttonPhoneNumberChange.setOnClickListener {
            readNewPhoneNumber()
        }
        enterPhoneNumberChange.setOnKeyListener(View.OnKeyListener { view, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                readNewPhoneNumber()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return@OnKeyListener true
            }
            false
        })
        buttonCancelPhoneNumberChange.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@OthersActivity.getSharedPreferences(
                    "account_number",
                    Context.MODE_PRIVATE
                )

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val pinChangeCancelIntent =
                Intent(this@OthersActivity, AccountNumberActivity::class.java)
            pinChangeCancelIntent.addCategory(Intent.CATEGORY_HOME)
            pinChangeCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(pinChangeCancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }
}