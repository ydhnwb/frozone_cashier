package com.ydhnwb.frozonecashier

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.ydhnwb.frozonecashier.utils.JusticeUtils

import kotlinx.android.synthetic.main.activity_prompt_pin.*
import kotlinx.android.synthetic.main.content_prompt_pin.*

class PromptPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        pin_view.setPinViewEventListener{pinview, bool ->
            val pinEntered = pinview.value.toString()
            if(pinEntered.length == 4){
                val currentPIN = JusticeUtils.getPin(this@PromptPinActivity)
                if(currentPIN.equals(pinEntered)){
                    startActivity(Intent(this, SettingActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@PromptPinActivity, "NOT OK", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
