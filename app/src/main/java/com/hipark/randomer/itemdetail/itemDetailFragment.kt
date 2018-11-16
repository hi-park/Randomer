package com.hipark.randomer.itemdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hipark.randomer.Injection
import com.hipark.randomer.R
import com.hipark.randomer.addedititem.AddEditItemActivity
import com.hipark.randomer.addedititem.AddEditItemFragment
import com.hipark.randomer.data.Item
import com.hipark.randomer.data.Source.ItemsDataSource
import com.hipark.randomer.data.Source.ItemsRepository

class ItemDetailFragment : Fragment() {

    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailDelete : Button

    private lateinit var itemId : String
    private lateinit var itemRepository : ItemsRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        itemId  = arguments!!.getString(ARGUMENT_ITEM_ID)
        itemRepository = Injection.provideItemRepository(requireContext())

        val root = inflater.inflate(R.layout.fragment_itemdetail, container, false)

        with(root) {

            detailTitle = findViewById(R.id.item_detail_title)
            detailDescription = findViewById(R.id.item_detail_description)
            detailDelete = findViewById(R.id.item_detail_delete)
            detailDelete.setOnClickListener {
                deleteItem()
            }
        }

        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task)?.setOnClickListener {
            editItem()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        openItem()
    }

    fun openItem() {

        if(itemId.isEmpty()) {
            showMissingItem()
            return
        }

        setLoadingIndicator(true)
        itemRepository.getItem(itemId, object: ItemsDataSource.GetItemCallback {
            override fun onItemLoaded(item: Item) {
                setLoadingIndicator(false)
                showItem(item)
            }
            override fun onDataNotAvailable() {
                showMissingItem()
            }
        })
    }

    fun editItem() {
        if(itemId.isEmpty()) {
            showMissingItem()
            return
        }
        showEditItem(itemId)
    }

    fun deleteItem() {

        if(itemId.isEmpty()) {
            showMissingItem()
            return
        }
        itemRepository.deleteItem(itemId)
        showTaskDeleted()

    }

    fun showEditItem(itemId: String) {

        val intent = Intent(context, AddEditItemActivity::class.java)
        intent.putExtra(AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID, itemId)
        startActivityForResult(intent, REQUEST_EDIT_ITEM)
    }

    fun showMissingItem() {
        detailTitle.text = ""
        detailDescription.text = getString(R.string.no_data)
    }

    fun showTaskDeleted() {
        activity?.finish()
    }

    fun showItem(item: Item) {

        if(itemId.isEmpty()) {
            hideTitle()
            hideDescription()
        } else {
            showTitle(item.title)
            showDescription(item.description)
        }
    }

    fun hideTitle() {
        detailTitle.visibility = View.GONE
    }

    fun hideDescription() {
        detailDescription.visibility = View.GONE
    }

    fun showTitle(title: String) {
        with(detailTitle) {
            visibility = View.VISIBLE
            text = title
        }
    }

    fun showDescription(description: String) {
        with(detailDescription) {
            detailDescription.visibility = View.VISIBLE
            text = description
        }
    }

    fun setLoadingIndicator(activity: Boolean) {

        if(activity) {
            detailTitle.text = ""
            detailDescription.text = getString(R.string.loading)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_EDIT_ITEM) {
            if(resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }

    companion object {

        private val ARGUMENT_ITEM_ID = "ITEM_ID"
        private val REQUEST_EDIT_ITEM = 1

        fun newInstance(itemId: String?) =
                ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGUMENT_ITEM_ID, itemId)
                    }
                }
    }
}