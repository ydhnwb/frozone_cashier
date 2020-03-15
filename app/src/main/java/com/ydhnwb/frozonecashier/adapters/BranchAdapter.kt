package com.ydhnwb.frozonecashier.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ydhnwb.frozonecashier.R
import com.ydhnwb.frozonecashier.models.Branch
import com.ydhnwb.frozonecashier.utils.BranchPopup
import com.ydhnwb.frozonecashier.utils.JusticeUtils
import com.ydhnwb.frozonecashier.viewmodels.SettingViewModel
import kotlinx.android.synthetic.main.list_item_branch.view.*

class BranchAdapter(private var branches: MutableList<Branch>, private var context: Context, private var dialog: BranchPopup, private var settingViewModel: SettingViewModel
) : RecyclerView.Adapter<BranchAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(branch: Branch, context: Context, d : BranchPopup, svm : SettingViewModel){
            itemView.branch_name.text = branch.name
            itemView.branch_desc.text = if (branch.description == null) context.resources.getString(R.string.info_no_desc) else branch.description
            itemView.setOnClickListener {
                JusticeUtils.setBranch(context, branch.id!!)
                JusticeUtils.setBranchName(context, branch.name.toString())
                svm.updateBranchName(branch.name.toString())
                d.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_branch, parent, false))

    override fun getItemCount() = branches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(branches[position], context, dialog, settingViewModel)
}