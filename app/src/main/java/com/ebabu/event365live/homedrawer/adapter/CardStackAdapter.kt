package com.ebabu.event365live.homedrawer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ebabu.event365live.R
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser
import com.ebabu.event365live.utils.CommonUtils
import com.ebabu.event365live.utils.ConcaveRoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

class CardStackAdapter(
        private val position: Int,
        private val size: Int,
        private val paymentUserList: MutableList<PaymentUser>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var ticketViewLayoutBinding: RsvpTicketViewLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        ticketViewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_ticket_view_layout, parent, false)
        setShapeBackground()
        return ViewHolder(ticketViewLayoutBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val paymentUser = paymentUserList[position]
        holder.bind(paymentUser)
    }

    override fun getItemCount(): Int {
        return size
    }

    private fun setShapeBackground() {
        val cornerSize = ticketViewLayoutBinding.ivBackground!!.resources.getDimension(R.dimen._16sdp)
        ticketViewLayoutBinding.ivBackground!!.shapeAppearanceModel =
                ShapeAppearanceModel.builder()
                        .setAllCornerSizes(cornerSize)
                        .setTopRightCorner(ConcaveRoundedCornerTreatment())
                        .setTopLeftCorner(ConcaveRoundedCornerTreatment())
                        .setBottomRightCorner(ConcaveRoundedCornerTreatment())
                        .setBottomLeftCorner(ConcaveRoundedCornerTreatment())
                        .build()
    }

    class ViewHolder(private val ticketViewLayoutBinding: RsvpTicketViewLayoutBinding) : RecyclerView.ViewHolder(ticketViewLayoutBinding.root) {
        fun bind(paymentUser: PaymentUser) {
            ticketViewLayoutBinding.cardStackView!!.visibility = View.GONE
            ticketViewLayoutBinding.demo.visibility = View.VISIBLE
            ticketViewLayoutBinding.tvBookedTicketName.text = paymentUser.events.name;

            ticketViewLayoutBinding.tvEventDate.text = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(paymentUser.events.startDate, true)
            ticketViewLayoutBinding.tvEventTime.text = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.events.startDate) + " - " + CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.events.endDate)
            ticketViewLayoutBinding.tvEventVenueAddress.text = paymentUser.events.address[0].venueAddress
        }


    }

}