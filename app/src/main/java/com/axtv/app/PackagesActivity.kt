package com.axtv.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axtv.app.databinding.ActivityPackagesBinding

class PackagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityPackagesBinding.inflate(layoutInflater).root)
    }
}
