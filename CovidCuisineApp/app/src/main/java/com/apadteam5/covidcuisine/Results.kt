package com.apadteam5.covidcuisine

import android.content.Context
import android.graphics.Color
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_categories_main.*
import kotlinx.android.synthetic.main.activity_results_main.*
import kotlinx.android.synthetic.main.category_item.*

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.apadteam5.covidcuisine.ui.main.SectionsPagerAdapter

class Results : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_categories_main)
        recycler_view_categories.layoutManager = LinearLayoutManager(this)
        recycler_view_categories.adapter = ResultsAdapter()
    }
    private class ResultsAdapter: RecyclerView.Adapter<ResultsItemViewHolder>() {
        override fun getItemCount(): Int {
            return 5
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val customView = layoutInflater.inflate(R.layout.category_item, parent, false)

            /*val blueView = View(parent?.context)
            blueView.setBackgroundColor(Color.BLUE)
            blueView.minimumHeight = 100*/
            return ResultsItemViewHolder(customView)
        }

        override fun onBindViewHolder(holder: ResultsItemViewHolder, position: Int) {

        }
    }
    private class ResultsItemViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView) {

    }

}
