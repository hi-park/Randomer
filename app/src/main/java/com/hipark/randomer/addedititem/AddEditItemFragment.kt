package com.hipark.randomer.addedititem

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hipark.randomer.Injection
import com.hipark.randomer.R
import com.hipark.randomer.data.Item
import com.hipark.randomer.data.Source.ItemsDataSource
import com.hipark.randomer.data.Source.ItemsRepository
import com.hipark.randomer.util.showSnackBar

class AddEditItemFragment : Fragment() {

    private lateinit var title: TextView
    private lateinit var description: TextView
    private var itemId: String? = null

    private lateinit var itemRepository : ItemsRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        itemRepository = Injection.provideItemRepository(requireContext())

        itemId = arguments?.getString(AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID)

        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_item_done)?.apply {
            setOnClickListener {
                saveItem(title.text.toString(), description.text.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.add_edit_item_fragment, container, false)
        with(root) {
            title = findViewById(R.id.add_task_title)
            description = findViewById(R.id.add_task_description)
        }
        return root
    }

    override fun onResume() {
        super.onResume()

        if(itemId != null) {
            populateItem()
        }
    }

    fun populateItem() {
        if(itemId == null) {
            throw RuntimeException("populateItem() was called but item is new.")
        }
        itemRepository.getItem(itemId!!, object : ItemsDataSource.GetItemCallback {
            override fun onItemLoaded(item: Item) {
                setTitle(item.title)
                setDescription(item.description)
            }

            override fun onDataNotAvailable() {
                showEmptyItemError()
            }
        })
    }

    fun saveItem(title:String, description: String) {

        if(itemId == null) {
            createItem(title, description)
        } else {
            updateItem(title, description)
        }
    }


    fun showEmptyItemError() {
        title.showSnackBar(getString(R.string.empty_item_message), Snackbar.LENGTH_LONG)
    }

    fun showItemsList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun setDescription(description: String) {
        this.description.text = description
    }

    private fun createItem(title: String, description: String) {

        val newItem = Item(title, description)

        if(newItem.isEmpty) {
            showEmptyItemError()
        } else {
            itemRepository.saveItem(newItem)
            showItemsList()
        }
    }

    private fun updateItem(title: String, description: String) {

        if(itemId == null) {
            throw RuntimeException("updateitem() was called but item is new.")
        }
        itemRepository.saveItem(Item(title, description, itemId!!))
        showItemsList()
    }

    companion object {
        const val ARGUMENT_EDIT_ITEM_ID = "EDIT_ITEM_ID"

        fun newInstance(itemId: String?) =
                AddEditItemFragment().apply {
                    arguments = Bundle().apply {
                        putString(AddEditItemFragment.ARGUMENT_EDIT_ITEM_ID, itemId)
                    }
                }
    }
}