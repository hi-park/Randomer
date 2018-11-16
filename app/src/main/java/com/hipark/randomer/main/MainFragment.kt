package com.hipark.randomer.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.hipark.randomer.Injection
import com.hipark.randomer.R
import com.hipark.randomer.addedititem.AddEditItemActivity
import com.hipark.randomer.data.Item
import com.hipark.randomer.data.Source.ItemsDataSource
import com.hipark.randomer.data.Source.ItemsRepository
import com.hipark.randomer.itemdetail.ItemDetailActivity
import com.hipark.randomer.util.showSnackBar
import java.util.*

class MainFragment : Fragment() {

    private lateinit var randomItemView: LinearLayout
    private lateinit var itemTitleView: TextView
    private lateinit var itemDescriptionView: TextView

    private lateinit var noItemsView: View
    private lateinit var noItemMainView: TextView
    private lateinit var noItemAddView: TextView
    private lateinit var itemsView: LinearLayout

    private lateinit var itemsRepository: ItemsRepository

    private var firstLoad = true

    internal var itemListener: ItemListener = object : ItemListener {

        override fun onItemClick(clickedItem: Item) {

            openItemDetails(clickedItem)
        }
    }

    private val listAdapter = ItemAdapter(ArrayList(0), itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.main_fragment, container, false)

        itemsRepository = Injection.provideItemRepository(requireContext())

        setHasOptionsMenu(true)

        with(root) {
            val listView = findViewById<ListView>(R.id.items_list).apply {
                adapter = listAdapter
            }

            findViewById<ScrollChildSwipeRefreshLayout>(R.id.refresh_layout).apply {
                setColorSchemeColors(
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                    ContextCompat.getColor(requireContext(), R.color.colorAccent),
                    ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                )

                scrollUpChild = listView
                setOnRefreshListener { loadItems(false) }
            }

            randomItemView = findViewById(R.id.randomItem)
            itemTitleView = findViewById(R.id.itemTitle)
            itemDescriptionView = findViewById(R.id.itemDescription)

            itemsView = findViewById(R.id.itemsLL)

            noItemsView = findViewById(R.id.noItems)
            noItemMainView = findViewById(R.id.noItemsMain)
            noItemAddView = (findViewById<TextView>(R.id.noItemsAdd)).also {
                it.setOnClickListener { showAddItem() }
            }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.fab_add_item).apply {
            setOnClickListener {
                addNewItem()
            }
        }

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val randomPressed = item.itemId == R.id.menu_random

        if(randomPressed)
            showRandomItem()
        else
            loadItems(false)

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.itemmain_fragment_menu, menu)
    }

    override fun onResume() {
        super.onResume()

        loadItems(false)
    }

    fun openItemDetails(requestedItem : Item) {
        showItemDetailUi(requestedItem.id)
    }

    fun addNewItem() {
        val intent = Intent(context, AddEditItemActivity::class.java)
        startActivityForResult(intent, AddEditItemActivity.REQUEST_ADD_ITEM)
    }

    fun loadItems(forceUpdate: Boolean) {
        loadItems(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    private fun loadItems(forceUpdate: Boolean, showLoadingUI: Boolean) {

        if(showLoadingUI) {
            setLoadingIndicator(true)
        }

        if(forceUpdate) {
            itemsRepository.refreshItems()
        }

        itemsRepository.getItems(object: ItemsDataSource.LoadItemsCallback {
            override fun onItemsLoaded(items: List<Item>) {

                val itemsToShow = ArrayList<Item>()

                for(item in items) {
                    itemsToShow.add(item)
                }

                if(showLoadingUI) {
                    setLoadingIndicator(false)
                }

                processItems(itemsToShow)
            }

            override fun onDataNotAvaiilable() {

                showLoadingItemsError()
            }
        })
    }

    private fun processItems(items: List<Item>) {

        if(items.isEmpty()) {
            processEmptyItems()
        } else {
            showitems(items)
        }
    }

    private fun processEmptyItems() {

        listAdapter.items = ArrayList<Item>()
        showNoItems()
    }

    fun showRandomItem() {

        val size = listAdapter.items.size

        if(size == 0) {
            return;
        }

        val index = Random().nextInt(size)
        val item = listAdapter.items[index]

        itemTitleView.text = item.titleForList
        itemDescriptionView.text = item.description

        randomItemView.visibility = View.VISIBLE
        itemsView.visibility = View.GONE
        noItemsView.visibility = View.GONE
    }

    fun showNoItems() {
        showNoItemsViews(resources.getString(R.string.no_items_all), false)
    }

    fun showNoItemsViews(mainText: String, showAddView: Boolean) {
        itemsView.visibility = View.GONE
        noItemsView.visibility = View.VISIBLE
        randomItemView.visibility = View.GONE

        noItemMainView.text = mainText
        noItemAddView.visibility = if(showAddView) View.VISIBLE else View.GONE
    }

    fun showitems(items: List<Item>) {
        listAdapter.items = items

        itemsView.visibility = View.VISIBLE
        noItemsView.visibility = View.GONE
        randomItemView.visibility = View.GONE
    }

    fun showItemDetailUi(itemId: String) {
        val intent = Intent(context, ItemDetailActivity::class.java).apply {
            putExtra(ItemDetailActivity.EXTRA_ITEM_ID, itemId)
        }
        startActivity(intent)
    }

    fun showLoadingItemsError() {
        showMessage(getString(R.string.loading_items_error))
    }

    private fun showMessage(message: String) {
        view?.showSnackBar(message, Snackbar.LENGTH_LONG)
    }

    fun setLoadingIndicator(active: Boolean) {
        val root = view ?: return
        with(root.findViewById<SwipeRefreshLayout>(R.id.refresh_layout)) {
            post({
                isRefreshing = active
            })
        }
    }

    fun showAddItem() {
        val intent = Intent(context, AddEditItemActivity::class.java)
        startActivityForResult(intent, AddEditItemActivity.REQUEST_ADD_ITEM)
    }

    private class ItemAdapter(items: List<Item>, private val itemListener: ItemListener)
        : BaseAdapter() {

        var items : List<Item> = items
            set(items) {
                field = items
                notifyDataSetChanged()
            }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {

            val item = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item, viewGroup, false)

            with(rowView.findViewById<TextView>(R.id.title)) {
                text = item.titleForList
            }

            rowView.setOnClickListener {
                itemListener.onItemClick(item)
            }

            return rowView
        }

        override fun getItem(i: Int) = items[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getCount() = items.size
    }

    interface ItemListener {
        fun onItemClick(clickedItem: Item)
    }

    companion object {
        fun newInstance() = MainFragment()
    }

}