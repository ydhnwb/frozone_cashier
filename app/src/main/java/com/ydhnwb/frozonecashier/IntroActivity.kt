package com.ydhnwb.frozonecashier

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import java.util.*

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val sliderPage = SliderPage().apply {
            description = resources.getString(R.string.info_intro_1)
            descColor = Color.parseColor("#494949")
            imageDrawable = R.drawable.ic_doodle_app_development
            bgColor = Color.parseColor("#FFFFFF")
        }

        val sliderPage2 = SliderPage().apply {
            description = resources.getString(R.string.info_intro_2)
            descColor = Color.parseColor("#494949")
            imageDrawable = R.drawable.ic_doodle_payment_processed
            bgColor = Color.parseColor("#FFFFFF")
        }

        val sliderPage3 = SliderPage().apply {
            description = resources.getString(R.string.info_intro_3)
            descColor = Color.parseColor("#494949")
            imageDrawable = R.drawable.ic_doodle_ecommerce
            bgColor = Color.parseColor("#FFFFFF")
        }

        addSlide(AppIntroFragment.newInstance(sliderPage))
        addSlide(AppIntroFragment.newInstance(sliderPage2))
        addSlide(AppIntroFragment.newInstance(sliderPage3))
        setFadeAnimation()
        skipButtonEnabled = false
        isVibrateOn = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        JusticeUtils.setFirstTime(this@IntroActivity, false).also {
            val generatedId = UUID.randomUUID().toString()
            println("Device id -> $generatedId")
            JusticeUtils.setDeviceId(generatedId, this)
            JusticeUtils.setDefaultPin(this@IntroActivity)
            JusticeUtils.setBranch(this, 0)
            JusticeUtils.setBranchName(this, null)
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }
    }

}