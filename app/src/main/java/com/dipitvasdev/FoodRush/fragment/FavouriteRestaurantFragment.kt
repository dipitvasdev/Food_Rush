package com.dipitvasdev.FoodRush.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.adapter.FavouritesAdaptor
import com.dipitvasdev.FoodRush.database.ResDatabase
import com.dipitvasdev.FoodRush.database.ResEntity


class FavouriteRestaurantFragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: FavouritesAdaptor
    var restaurantInfoList = listOf<ResEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerViewFavouriteRestaurant)
        progressLayout = view.findViewById(R.id.favourite_progress)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(activity)
        restaurantInfoList = RetrieveFavourites(activity as Context).execute().get()
        if (activity!=null){
            progressLayout.visibility=View.GONE
            recyclerAdaptor=FavouritesAdaptor(activity as Context,restaurantInfoList)
            recyclerFavourite.adapter=recyclerAdaptor
            recyclerFavourite.layoutManager=layoutManager
           }
          return view
    }
        class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<ResEntity>>() {
            override fun doInBackground(vararg params: Void?): List<ResEntity> {
                val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()
                return db.resDao().getAllRestaurants()
            }


        }
    }

