package com.daqsoft.module_work.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.module_work.R
import com.daqsoft.library_common.bean.Employee
import com.daqsoft.module_work.widget.tree.TreeNode
import com.daqsoft.module_work.widget.tree.TreeViewBinder

/**
 * @author zp
 * @package name：com.daqsoft.module_work.adapter
 * @date 10/5/2021 上午 10:41
 * @describe 员工 树
 */
class StaffTreeBinder : TreeViewBinder<StaffTreeBinder.ViewHolder>() {

    private var  selectedList = mutableListOf<Int>()

    inner class ViewHolder(view: View) : TreeViewBinder.ViewHolder(view){

        val avatar = view.findViewById<ImageView>(R.id.avatar)
        val name = view.findViewById<TextView>(R.id.name)
        val position = view.findViewById<TextView>(R.id.position)
        val call = view.findViewById<ImageView>(R.id.call)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
    }

    override fun getLayoutId(): Int {
        return R.layout.recycleview_address_book_item
    }

    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: ViewHolder, position: Int, node: TreeNode<*>) {
        val staff = node.content as Employee
        holder.name.text = staff.name
    }


}