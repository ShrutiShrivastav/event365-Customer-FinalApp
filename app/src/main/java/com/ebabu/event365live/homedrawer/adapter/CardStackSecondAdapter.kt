package com.ebabu.event365live.homedrawer.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ebabu.event365live.R
import com.ebabu.event365live.databinding.DialogCancelTicketConfirmationBinding
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding
import com.ebabu.event365live.homedrawer.adapter.RsvpTicketAdapter.CancelTicketClickListener
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.TicketBooked
import com.ebabu.event365live.httprequest.Constants
import com.ebabu.event365live.userinfo.activity.EventDetailsActivity
import com.ebabu.event365live.utils.CommonUtils
import com.ebabu.event365live.utils.ConcaveRoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import jp.wasabeef.glide.transformations.BlurTransformation

class CardStackSecondAdapter(
        private val position: Int,
        private val size: Int,
        private val paymentUserList: MutableList<TicketBooked>,
        private val paymentUser11: PaymentUser,
        private var cancelTicketClickListener: CancelTicketClickListener? = null
) : RecyclerView.Adapter<CardStackSecondAdapter.ViewHolder>() {

    private lateinit var ticketViewLayoutBinding: RsvpTicketViewLayoutBinding
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        ticketViewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_ticket_view_layout, parent, false)
        setShapeBackground()
        return ViewHolder(mContext, ticketViewLayoutBinding, cancelTicketClickListener, paymentUser11)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val paymentUser = paymentUserList[position]

        Log.v("RSVP_TEST","position> " + position)
        Log.v("RSVP_TEST","I value> " + i)

        holder.bind(paymentUser, position)
    }

    override fun getItemCount(): Int {
        return size
    }

    private fun setShapeBackground() {
        val cornerSize = ticketViewLayoutBinding.ivBackground!!.resources.getDimension(R.dimen._12sdp)
        ticketViewLayoutBinding.ivBackground!!.shapeAppearanceModel =
                ShapeAppearanceModel.builder()
                        .setAllCornerSizes(cornerSize)
                        .setTopRightCorner(ConcaveRoundedCornerTreatment())
                        .setTopLeftCorner(ConcaveRoundedCornerTreatment())
                        .setBottomRightCorner(ConcaveRoundedCornerTreatment())
                        .setBottomLeftCorner(ConcaveRoundedCornerTreatment())
                        .build()
    }

    class ViewHolder(private val mContext: Context, private val ticketViewLayoutBinding: RsvpTicketViewLayoutBinding, private val cancelTicketClickListener: CancelTicketClickListener? = null, val paymentUser22: PaymentUser) : RecyclerView.ViewHolder(ticketViewLayoutBinding.root) {
        fun bind(ticketBooked: TicketBooked, pos : Int) {
            ticketViewLayoutBinding.cardStackView!!.visibility = View.GONE
            ticketViewLayoutBinding.demo.visibility = View.VISIBLE
            ticketViewLayoutBinding.tvBookedTicketName.text = paymentUser22.events.name.capitalizeWords()

            ticketViewLayoutBinding.tvEventDate.text = CommonUtils.getCommonUtilsInstance().getDateMonthYearName(paymentUser22.events.startDate, true)
            ticketViewLayoutBinding.tvEventTime.text = CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser22.events.startDate) + " - " + CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser22.events.endDate)
            ticketViewLayoutBinding.tvEventVenueAddress.text = paymentUser22.events.address[0].venueAddress

            // showTicketNoWithName(paymentUser.events.ticketBooked);

            try{
                ticketViewLayoutBinding.ivBackground?.let {
                    Glide.with(mContext).load(paymentUser22.events.eventImages[0].eventImage).into(it)
                }
            }catch (e : java.lang.Exception){
                e.printStackTrace()
                try {
                    ticketViewLayoutBinding.ivBackground?.let {
                        Glide.with(mContext).load(R.drawable.bg).into(it)
                    }
                }catch (e1 : java.lang.Exception){
                    e1.printStackTrace()
                }
            }

            try {

                var ticketCount = 0;

                ticketCount = paymentUser22.events.ticketBooked.size
//                for (i in 0 until paymentUser22.events.ticketBooked.size) {
//                    ticketCount = ticketCount + paymentUser22.events.ticketBooked.get(i).totalQuantity
//                }

                ticketViewLayoutBinding.tvPurchaseText?.text = mContext.getString(R.string.purchased_in_group)
                        .replace("TICKET_NUMBER", "Ticket " + (pos + 1) + " of " + ticketCount)

                ticketViewLayoutBinding.tvShowDescription?.text = paymentUser22.events.description2

                ticketViewLayoutBinding.tvTicketType?.text = ticketBooked.ticketType + " - $" + ticketBooked.pricePerTicket

                ticketViewLayoutBinding.tvPurchaseText?.visibility = View.VISIBLE
                ticketViewLayoutBinding.ticketTypeContainer?.visibility = View.VISIBLE
                ticketViewLayoutBinding.ivShowBarCode.visibility = View.VISIBLE
                ticketViewLayoutBinding.tvCancelButton?.visibility = View.VISIBLE
                ticketViewLayoutBinding.shareContainer.visibility = View.VISIBLE
                ticketViewLayoutBinding.tvTicketNumber?.visibility = View.VISIBLE
                ticketViewLayoutBinding.tvTicketNumber?.text = paymentUser22.events.ticketBooked[pos].ticketInfo[0].ticketNumber
                ticketViewLayoutBinding.tvEventCode?.text = mContext.getString(R.string.event_code) + " #" + paymentUser22.events.eventCode

                if(ticketBooked.status.equals(Constants.CANCELLED)){
                    ticketViewLayoutBinding.tvCancelButton!!.text = "Cancelled!"

                    val radius: Float = 10f
                    val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                    ticketViewLayoutBinding.tvTicketNumber?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    ticketViewLayoutBinding.tvTicketNumber?.getPaint()?.setMaskFilter(filter)
                    Glide.with(mContext)
                            .load(getBarCode(paymentUser22.qRkey))
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(10, 3)))
                            .into(ticketViewLayoutBinding.ivShowBarCode)
                }else{
                    ticketViewLayoutBinding.tvCancelButton!!.text = "Cancel"
                    Glide.with(mContext).load(getBarCode(paymentUser22.qRkey)).into(ticketViewLayoutBinding.ivShowBarCode)
                }

            } catch (e: Exception) {
                e.printStackTrace();
            }


            ticketViewLayoutBinding.shareContainer.setOnClickListener {
                if (!checkWritePermission()) return@setOnClickListener
//                saveTicket();
                val mDrawable = BitmapDrawable(mContext.getResources(), viewToBitmap(ticketViewLayoutBinding.ticketFrameContainer))
                val mBitmap: Bitmap = mDrawable.bitmap
                val path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                        mBitmap, "Design", null)
                val uri = Uri.parse(path)
                val share = Intent(Intent.ACTION_SEND)
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                share.putExtra(Intent.EXTRA_TEXT, "Booked Ticket")
                mContext.startActivity(Intent.createChooser(share, "Share Your Design!"))
            }


            ticketViewLayoutBinding.tvCancelButton?.setOnClickListener {
                if(!ticketBooked.status.equals(Constants.CANCELLED)) {
                    cancelTicketDialog(paymentUser22.qRkey, pos)
                }
            }

            ticketViewLayoutBinding.tvBookedTicketName.setOnClickListener(View.OnClickListener { openEventDetail(paymentUser22.events.id) })

        }

        fun openEventDetail(eventId : Int){
            val eventIntent = Intent(mContext, EventDetailsActivity::class.java)
            eventIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            eventIntent.putExtra(Constants.ApiKeyName.eventId, eventId)
            mContext.startActivity(eventIntent)
        }

        fun viewToBitmap(view: View): Bitmap? {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun cancelTicketDialog(qrKey: String, pos : Int) {
            val dialog: AlertDialog
            val builder = AlertDialog.Builder(mContext)
            val dialogLogoutBinding: DialogCancelTicketConfirmationBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_cancel_ticket_confirmation, null, false)
            builder.setView(dialogLogoutBinding.getRoot())
            dialog = builder.create()
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()
            dialogLogoutBinding.llYesBtn.setOnClickListener { view ->
                dialog.dismiss()
                cancelTicketClickListener?.onClickCancelButton(paymentUser22, pos)

            }
            dialogLogoutBinding.llNoBtn.setOnClickListener { dialog.dismiss() }
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