package app.vtcnews.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.*
import app.vtcnews.myapplication.databinding.ActivityMainBinding
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

class MainActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val controller = CatController()
        val list = List(10){DataItem()}
        controller.setData(list)
        controller.context = applicationContext

        binding.rvcat.setController(controller)
        EpoxyVisibilityTracker().attach(binding.rvcat)
    }
}