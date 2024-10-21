package com.fortune.va.lib

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * Menu
 */
data class Menu(var name: String? = null, var click: (()->Unit)? = null)


class MenuAdapter(var menus: MutableList<Menu>) : RecyclerView.Adapter<MenuAdapter.ItemVA>() {


    class ItemVA(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVA {
        val itemView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        return ItemVA(itemView);
    }

    override fun getItemCount(): Int {
        return menus.size;
    }

    override fun onBindViewHolder(holder: ItemVA, position: Int) {
        val menu = menus[position];
        val text = holder.itemView.findViewById<TextView>(android.R.id.text1);
        text.text = menu.name;
        holder.itemView.setOnClickListener {
            menu.click?.invoke()
        }
    }
}