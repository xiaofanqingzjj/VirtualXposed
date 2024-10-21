package com.fortune.va.lib

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.mylibrary.R
import java.util.ArrayList


/**
 * A simple menu Activity
 *
 * @author fortune
 */
open class MenuFragment: SimpleFragment() {

    private var menus = ArrayList<Menu>()

    private lateinit var recycler_view: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_menu)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view = findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager =   object : LinearLayoutManager(activity) {
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
          startActivity(Intent(context, targetClazz))
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
//            FragmentContainerActivity.show(context!!, name, targetClazz)
        })

        recycler_view?.adapter?.notifyDataSetChanged()
    }


}