package com.ebabu.event365live.homedrawer.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ebabu.event365live.R
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.TicketBooked
import com.ebabu.event365live.utils.CommonUtils
import com.ebabu.event365live.utils.ConcaveRoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class CardStackAdapter(
        private val position: Int,
        private val size: Int,
        private val paymentUserList: MutableList<PaymentUser>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private lateinit var ticketViewLayoutBinding: RsvpTicketViewLayoutBinding
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        ticketViewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_ticket_view_layout, parent, false)
        setShapeBackground()
        return ViewHolder(mContext, ticketViewLayoutBinding)
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

    class ViewHolder(private val mContext: Context, private val ticketViewLayoutBinding: RsvpTicketViewLayoutBinding) : RecyclerView.ViewHolder(ticketViewLayoutBinding.root) {
        fun bind(paymentUser: PaymentUser) {
            ticketViewLayoutBinding.cardStackView!!.visibility = View.GONE
            ticketViewLayoutBinding.demo.visibility = View.VISIBLE
            ticketViewLayoutBinding.tvBookedTicketName.text = paymentUser.events.name.capitalizeWords()

            ticketViewLayoutBinding.tvEventDate.text = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(paymentUser.events.startDate, true)
            ticketViewLayoutBinding.tvEventTime.text = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.events.startDate) + " - " + CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.events.endDate)
            ticketViewLayoutBinding.tvEventVenueAddress.text = paymentUser.events.address[0].venueAddress
            Glide.with(mContext).load(getBarCode(paymentUser.qRkey)).into(ticketViewLayoutBinding.ivShowBarCode)
            showTicketNoWithName(paymentUser.events.ticketBooked);
            ticketViewLayoutBinding.shareContainer.setOnClickListener {
                if (!checkWritePermission()) return@setOnClickListener
//                saveTicket();
            }
        }

        private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }


        private fun getBarCode(barCodeEncodedInfo: String): Bitmap? {
            var bitmap: Bitmap? = null
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix = multiFormatWriter.encode(barCodeEncodedInfo, BarcodeFormat.QR_CODE, 200, 200)
                val barcodeEncoder = BarcodeEncoder()
                bitmap = barcodeEncoder.createBitmap(bitMatrix)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            return bitmap
        }

        private fun showTicketNoWithName(ticketBookedList: List<TicketBooked>) {
            for (i in ticketBookedList.indices) {
                val ticketBooked = ticketBookedList[i]
                val view = LayoutInflater.from(mContext).inflate(R.layout.ticket_name_view, null)
                when {
                    ticketBooked.ticketType.equals(mContext.getString(R.string.free_normal), ignoreCase = true) -> {
                        setImg(R.drawable.free_ticket_icon, view)
                    }
                    ticketBooked.ticketType.equals(mContext.getString(R.string.vip_normal), ignoreCase = true) -> {
                        setImg(R.drawable.normal_vip_icon, view)
                    }
                    ticketBooked.ticketType.equals(mContext.getString(R.string.vip_table_seating), ignoreCase = true) -> {
                        setImg(R.drawable.vip_ticket_icon, view)
                    }
                    ticketBooked.ticketType.equals(mContext.getString(R.string.regular_normal), ignoreCase = true) -> {
                        setImg(R.drawable.regular_ticket_icon, view)
                    }
                    ticketBooked.ticketType.equals(mContext.getString(R.string.regular_table_seating), ignoreCase = true) -> {
                        setImg(R.drawable.regular_seating_icon, view)
                    }
                }
                (view.findViewById<View>(R.id.tvShowVipBookedTicketNo) as TextView).text = ticketBooked.totalQuantity.toString() + " " + getBookedTicketName(ticketBooked.ticketType) + " $" + ticketBooked.pricePerTicket
                ticketViewLayoutBinding.showTicketContainer.addView(view)
            }
        }

        private fun setImg(drawableImg: Int, view: View) {
            val ivSetTicketIcon = view.findViewById<ImageView>(R.id.ivSetTicketIcon)
            Glide.with(mContext).load(drawableImg).into(ivSetTicketIcon)
        }

        private fun checkWritePermission(): Boolean {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((mContext as Activity?)!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
                return false
            }
            return true
        }


        @Synchronized
        private fun getBookedTicketName(ticketType: String): String {
            var ticketName = ""
            when (ticketType) {
                "freeNormal" -> ticketName = "Free Ticket"
                "vipNormal" -> ticketName = "VIP Ticket"
                "vipTableSeating" -> ticketName = "VIP Table Seating Ticket"
                "regularNormal" -> ticketName = "Regular Normal Ticket"
                "regularTableSeating" -> ticketName = "Regular Table Seating Ticket"
            }
            return ticketName
        }
    }

}