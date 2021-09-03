package com.daqsoft.module_work.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_work.R
import com.daqsoft.module_work.adapter.VideoSurveillanceGroupTreeBinder
import com.daqsoft.module_work.repository.pojo.vo.Department
import com.daqsoft.module_work.repository.pojo.vo.VideoSurveillanceGroup
import com.daqsoft.module_work.widget.tree.TreeNode
import com.daqsoft.module_work.widget.tree.TreeViewAdapter
import com.daqsoft.mvvmfoundation.utils.ToolbarUtils
import com.daqsoft.mvvmfoundation.utils.dp
import com.lxj.xpopup.core.DrawerPopupView
import com.lxj.xpopup.util.XPopupUtils
import timber.log.Timber
import java.util.*

/**
 * @package name：com.daqsoft.module_work.widget
 * @date 14/7/2021 下午 1:56
 * @author zp
 * @describe
 */
class VideoSurveillanceGroupLeftDrawerPopup : DrawerPopupView {

    constructor(context: Context,title:String) : super(context){
        this.title = title
    }

    lateinit var title:String

    private var filterData : List<VideoSurveillanceGroup> ? = null

    val treeViewAdapter: TreeViewAdapter by lazy {
        TreeViewAdapter(arrayListOf(), arrayListOf(VideoSurveillanceGroupTreeBinder(), VideoSurveillanceGroupTreeBinder())).apply {
            setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener{
                override fun onClick(node: TreeNode<*>?, holder: RecyclerView.ViewHolder?): Boolean {
                    if (!node!!.isLeaf) {
                        onToggle(!node.isExpand, holder)
                    }else{
                        val groupHolder: VideoSurveillanceGroupTreeBinder.ViewHolder = holder as VideoSurveillanceGroupTreeBinder.ViewHolder
                        val position = groupHolder.absoluteAdapterPosition
                        val content = node.content as VideoSurveillanceGroup
                        content.isSelected = !content.isSelected
                        groupHolder.setSelectedPosition(content)
                        notifyDataSetChanged()
                        itemOnClickListener?.onClick(position, content)
                        this@VideoSurveillanceGroupLeftDrawerPopup.dismiss()
                    }
                    return false
                }

                override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder?) {
                    val groupHolder: VideoSurveillanceGroupTreeBinder.ViewHolder = holder as VideoSurveillanceGroupTreeBinder.ViewHolder
                    val arrow: ImageView = groupHolder.arrow
                    val rotateDegree = if (isExpand) 90 else -90
                    arrow.animate().rotationBy(rotateDegree.toFloat()).start()
                }
            })
        }
    }

    fun setData(data : List<VideoSurveillanceGroup>){
        this.filterData = data
        val nodes: MutableList<TreeNode<VideoSurveillanceGroup>> = mutableListOf()

        val all : TreeNode<VideoSurveillanceGroup> = TreeNode(VideoSurveillanceGroup(null,"-1","全部",-1))
        nodes.add(all)

        data.forEach {
            val node: TreeNode<VideoSurveillanceGroup> = TreeNode(it)
            nodes.add(node)
            traverse(it,node)
        }
        treeViewAdapter.refresh(nodes.toList())
    }

    private fun traverse(root: VideoSurveillanceGroup,node: TreeNode<VideoSurveillanceGroup>){
        if (!root.children.isNullOrEmpty()) {
            root.children.forEach {
                val cNode: TreeNode<VideoSurveillanceGroup> = TreeNode(it)
                node.addChild(cNode)
                traverse(it,cNode)
            }
        }
    }

    fun getData() = filterData

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_video_surveillance_group
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val container =  findViewById<ConstraintLayout>(R.id.container)
        val padding = 20.dp
        val extraPadding = 10.dp
        container.setPadding(padding+extraPadding,padding + ToolbarUtils.getStatusBarHeight(),padding+extraPadding,0)

        val title = findViewById<TextView>(R.id.tv_title)
        title.text = this.title

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            if(itemDecorationCount == 0){
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position > 0){
                            outRect.top = 20.dp
                        }
                    }
                })
            }
            adapter = treeViewAdapter
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.85f).toInt()
    }
    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){


    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int?,content : VideoSurveillanceGroup?)
    }


}