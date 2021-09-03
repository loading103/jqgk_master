package com.daqsoft.module_work.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.module_work.R
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.module_work.widget.tree.TreeNode
import com.daqsoft.module_work.widget.tree.TreeViewBinder

/**
 * @author zp
 * @package name：com.daqsoft.module_work.adapter
 * @date 10/5/2021 上午 10:41
 * @describe 部门 树
 */
class DepartmentTreeBinder  : TreeViewBinder<DepartmentTreeBinder.ViewHolder>() {

    inner class ViewHolder(view: View) : TreeViewBinder.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.name)
        val arrow = view.findViewById<ImageView>(R.id.arrow)
    }

    override fun getLayoutId(): Int {
        return R.layout.recycleview_department_item
    }

    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: ViewHolder, position: Int, node: TreeNode<*>) {
        val department = node.content as Department
        holder.name.text = department.depName

        val rotateDegree = if (node.isExpand) 90 else 0
        holder.arrow.rotation = rotateDegree.toFloat()

        if (node.isLeaf){
            holder.arrow.visibility = View.INVISIBLE
        } else{
            holder.arrow.visibility = View.VISIBLE
        }

    }


}