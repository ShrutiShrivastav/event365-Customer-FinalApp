package com.ebabu.event365live.homedrawer.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebabu.event365live.R
import com.ebabu.event365live.databinding.FragmentEventNotificationBinding
import com.ebabu.event365live.homedrawer.adapter.NotificationListAdapter
import com.ebabu.event365live.homedrawer.modal.NotificationListModal
import com.ebabu.event365live.homedrawer.modal.NotificationListModal.NotificationList
import com.ebabu.event365live.httprequest.APICall
import com.ebabu.event365live.httprequest.APIs
import com.ebabu.event365live.httprequest.GetResponseData
import com.ebabu.event365live.utils.CommonUtils
import com.ebabu.event365live.utils.EndlessRecyclerViewScrollListener
import com.ebabu.event365live.utils.MyLoader
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class ProfileNotificationFragment : Fragment(), GetResponseData {

    private lateinit var myLoader: MyLoader
    private lateinit var binding: FragmentEventNotificationBinding
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener
    private var notificationLists: MutableList<NotificationList> = ArrayList()
    private lateinit var getNotificationLists: List<NotificationList>
    private var currentPage = 1


    override fun onAttach(context: Context) {
        super.onAttach(context)
        myLoader = MyLoader(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_notification, container, false)
        init()
        return binding.root
    }

    private fun init() {
        manager = LinearLayoutManager(requireContext())
        binding.recyclerNotificationList.layoutManager = manager
        notificationListAdapter = NotificationListAdapter(notificationLists)
        binding.recyclerNotificationList.adapter = notificationListAdapter
        showNotificationListRequest(currentPage)
    }

    private fun setupNotificationList(lists: List<NotificationList>) {
        notificationListAdapter.notifyDataSetChanged()
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(manager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (lists.isNotEmpty()) {
                    ++currentPage
                    showNotificationListRequest(currentPage)
                }
            }
        }
        binding.recyclerNotificationList.addOnScrollListener(endlessRecyclerViewScrollListener)
    }

    private fun showNotificationListRequest(currentPage: Int) {
        myLoader.show("")
        val notificationListCall = APICall.getApiInterface().getNotificationList(CommonUtils.getCommonUtilsInstance().deviceAuth, 50, currentPage, 3)
        APICall(requireContext()).apiCalling(notificationListCall, this, APIs.GET_ALL_NOTIFICATION_LIST)
    }

    override fun onSuccess(responseObj: JSONObject, message: String?, typeAPI: String?) {
        myLoader.dismiss()
        val notificationListModal = Gson().fromJson(responseObj.toString(), NotificationListModal::class.java)
        if (notificationListModal.data.notificationList.size == 0) {
            if (currentPage > 1) {
                setupNotificationList(notificationListModal.data.notificationList)
                return
            }
            binding.noNotificationCard.visibility = View.VISIBLE
            binding.recyclerNotificationList.visibility = View.GONE
        } else {
            getNotificationLists = prepareList(notificationListModal.data.notificationList)
            notificationLists.addAll(getNotificationLists)
            setupNotificationList(notificationLists)
        }
    }

    override fun onFailed(errorBody: JSONObject?, message: String?, errorCode: Int?, typeAPI: String?) {
        myLoader.dismiss()
    }

    private fun prepareList(lists: List<NotificationList>): List<NotificationList> {
        val uniqueList: MutableList<String> = ArrayList()
        for (i in lists.indices) {
            val list = lists[i]
            var flag = false
            for (j in notificationLists.size - 1 downTo 0) {
                val s = notificationLists[j].dateString
                val s2 = list.dateString
                if (s != null && s == s2) {
                    notificationLists.add(list)
                    flag = true
                    break
                }
            }
            if (!flag && !uniqueList.contains(list.dateString)) {
                uniqueList.add(list.dateString)
            }
        }
        val expectedList: MutableList<NotificationList> = ArrayList()
        for (getDateOnly in uniqueList) {
            val mItemHead = NotificationList()
            mItemHead.isHead = true
            mItemHead.dateString = getDateOnly
            expectedList.add(mItemHead)
            for (i in lists.indices) {
                val mItem = lists[i]
                if (getDateOnly == mItem.dateString) {
                    expectedList.add(mItem)
                }
            }
        }
        return expectedList
    }
}