package com.vivavichi.vivapong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vivavichi.vivapong.databinding.ActivitySingleBinding
import com.vivavichi.vivapong.ui.link.LinkFragment

class SingleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, LinkFragment.newInstance())
                .commitNow()
        }
    }
}