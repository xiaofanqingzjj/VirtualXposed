package com.fortune.va.lib

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.mylibrary.R


/**
 * A simple menu Activity
 *
 * @author fortune
 */
open class MenuActivity: AppCompatActivity() {

    private var menus = mutableListOf<Menu>()


    private lateinit var recycler_view: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        recycler_view = findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager =   object : LinearLayoutManager(this) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
        recycler_view.adapter = MenuAdapter(menus)

    }

    /**
     * add menu
     *
     * @param name menu title
     * @param click menu click listener
     */
    open fun addMenu(name: String?, click:(()->Unit)? = null) {
        menus.add(Menu(name, click))
        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Add menu
     *
     * @param name menu title
     * @param targetClazz menu jump activity class
     */
    fun addMenu(name: String?, targetClazz: Class<out Activity>) {
        menus.add(Menu(name) {
          startActivity(Intent(this@MenuActivity, targetClazz))
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }

    /**
     * Add menu by fragment
     *
     * @param name menu title
     * @param targetClazz menu jump fragment class
     */
    fun addMenuByFragment(name: String?, targetClazz: Class<out Fragment>) {
        menus.add(Menu(name) {
//            FragmentContainerActivity.show(this, name, targetClazz)
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }


}