package com.dipitvasdev.FoodRush.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dipitvasdev.FoodRush.R
import com.dipitvasdev.FoodRush.activities.MenuActivity
import com.dipitvasdev.FoodRush.database.ResDatabase
import com.dipitvasdev.FoodRush.database.ResEntity
import com.squareup.picasso.Picasso


class FavouritesAdaptor(val context: Context, var itemList: List<ResEntity>) :
    RecyclerView.Adapter<FavouritesAdaptor.ViewHolderFav>() {
    class ViewHolderFav(view: View) : RecyclerView.ViewHolder(view) {


        val imageViewRestaurant: ImageView = view.findViewById(R.id.imageViewRestaurant)
        val textViewRestaurantName: TextView = view.findViewById(R.id.textViewRestaurantName)
        val textViewPricePerPerson: TextView = view.findViewById(R.id.textViewPricePerPerson)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val textViewfavourite: TextView = view.findViewById(R.id.textViewfavourite)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFav {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_dashboard, parent, false)
        return ViewHolderFav(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouritesAdaptor.ViewHolderFav, position: Int) {
        val restaurant =
            itemList[position]
        val resEntity = ResEntity(
            restaurant.restaurant_Id,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.cost_for_one,
            restaurant.restaurantImage
        )
        val resList = arrayListOf<ResEntity>()
        val checkFav = FavouritesAdaptor.DBAsyncTask(context, resEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.textViewfavourite.setTag("liked")
            holder.textViewfavourite.setBackgroundResource(R.drawable.ic_isfav)
        } else {
            holder.textViewfavourite.setTag("unliked")
            holder.textViewfavourite.setBackgroundResource(R.drawable.ic_favourites)
        }
        holder.textViewfavourite.setOnClickListener {
            val async = DBAsyncTask(context, resEntity, 3).execute()
            val result = async.get()
            if (result) {
                holder.textViewfavourite.setTag("unliked")
                Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
                val manager: FragmentManager =
                    (context as AppCompatActivity).supportFragmentManager
                val currentFragment: Fragment? =
                    manager.findFragmentById(R.id.frameLayout)
                val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
                if (currentFragment != null) {
                    fragmentTransaction.detach(currentFragment)
                }
                if (currentFragment != null) {
                    fragmentTransaction.attach(currentFragment)
                }
                fragmentTransaction.commit()

            } else {
                Toast.makeText(context as Context, "Some Error Occurred", Toast.LENGTH_LONG).show()

            }
        }
        holder.llContent.setOnClickListener(View.OnClickListener {

            println(holder.textViewRestaurantName.getTag().toString())

            val intent = Intent(context, MenuActivity::class.java)

            intent.putExtra("restaurantId", holder.textViewRestaurantName.getTag().toString())

            intent.putExtra("restaurantName", holder.textViewRestaurantName.text.toString())


            context.startActivity(intent)


        })


        holder.textViewRestaurantName.setTag(restaurant.restaurant_Id + "")
        holder.textViewRestaurantName.text = restaurant.restaurantName
        holder.textViewPricePerPerson.text = restaurant.cost_for_one + "/Person "
        holder.textViewRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_launcher_foreground)
            .into(holder.imageViewRestaurant);


    }

    class DBAsyncTask(val context: Context, val resEntity: ResEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: ResEntity? =
                        db.resDao().getRestaurantById(resEntity.restaurant_Id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {

                    db.resDao().insertRestaurant(resEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.resDao().deleteRestaurant(resEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}
