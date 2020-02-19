package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.RsvpBookedTicketModal;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.TicketBooked;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RsvpTicketAdapter extends PagerAdapter{
    private Context context;
    private List<PaymentUser> paymentUserList;
    private RsvpTicketViewLayoutBinding ticketViewLayoutBinding;
    private SaveTicketListener saveTicketListener;
    private View mCurrentView;

    public RsvpTicketAdapter(Context context, List<PaymentUser> paymentUserList) {
        this.context = context;
        this.paymentUserList = paymentUserList;
    }

    @Override
    public int getCount() {
        return paymentUserList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ticketViewLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.rsvp_ticket_view_layout, container, false);
        PaymentUser paymentUser = paymentUserList.get(position);
        ticketViewLayoutBinding.tvBookedTicketName.setText(paymentUser.getEvents().getName());

        ticketViewLayoutBinding.tvEventDate.setText(CommonUtils.getCommonUtilsInstance().getDateMonthYearName(paymentUser.getEvents().getStartDate(), true));
        ticketViewLayoutBinding.tvEventTime.setText(CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.getEvents().getStartDate()) + " - "+CommonUtils.getCommonUtilsInstance().getStartEndEventTime(paymentUser.getEvents().getEndDate()));
        ticketViewLayoutBinding.tvEventVenueAddress.setText(paymentUser.getEvents().getAddress().get(0).getVenueAddress());
        Glide.with(context).load(getBarCode(paymentUser.getQRkey())).into(ticketViewLayoutBinding.ivShowBarCode);
        container.addView(ticketViewLayoutBinding.getRoot());
        showTicketNoWithName(paymentUser.getEvents().getTicketBooked());

        ticketViewLayoutBinding.shareContainer.setOnClickListener(v->{
            mCurrentView.findViewById(R.id.ivShareTicketIcon).setVisibility(View.GONE);
            mCurrentView.findViewById(R.id.tvShare).setVisibility(View.GONE);
            saveTicketListener.frameView((RelativeLayout) mCurrentView);

        });

        return ticketViewLayoutBinding.getRoot();
    }

    private Bitmap getBarCode(String barCodeEncodedInfo){
        Bitmap bitmap = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barCodeEncodedInfo, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private synchronized String getBookedTicketName(String ticketType){
                String ticketName = "";
            switch (ticketType){
                case "freeNormal":
                    ticketName = "Free Ticket";
                    break;
                case "vipNormal":
                    ticketName = "VIP Ticket";
                    break;

                case "vipTableSeating":
                    ticketName = "VIP Table Seating Ticket";
                    break;

                case "regularNormal":
                    ticketName = "Regular Normal Ticket";
                    break;

                case "regularTableSeating":
                    ticketName = "Regular Table Seating Ticket";
                    break;
            }
        return ticketName;
    }

    private void showTicketNoWithName(List<TicketBooked> ticketBookedList){
        for(int i=0;i<ticketBookedList.size();i++){
            TicketBooked ticketBooked = ticketBookedList.get(i);
            View view = LayoutInflater.from(context).inflate(R.layout.ticket_name_view,null);
            if(ticketBooked.getTicketType().equalsIgnoreCase(context.getString(R.string.free_normal))){
                setImg(R.drawable.free_ticket_icon,view);
            }else if(ticketBooked.getTicketType().equalsIgnoreCase(context.getString(R.string.vip_normal))){
                setImg(R.drawable.normal_vip_icon,view);
            }else if(ticketBooked.getTicketType().equalsIgnoreCase(context.getString(R.string.vip_table_seating))){
                setImg(R.drawable.vip_ticket_icon,view);
            }else if(ticketBooked.getTicketType().equalsIgnoreCase(context.getString(R.string.regular_normal))){
                setImg(R.drawable.regular_ticket_icon,view);
            }else if(ticketBooked.getTicketType().equalsIgnoreCase(context.getString(R.string.regular_table_seating))){
                setImg(R.drawable.regular_seating_icon,view);
            }
            ((TextView)view.findViewById(R.id.tvShowVipBookedTicketNo)).setText(ticketBooked.getTotalQuantity()+" "+getBookedTicketName(ticketBooked.getTicketType())+" $"+ticketBooked.getPricePerTicket());
            ticketViewLayoutBinding.showTicketContainer.addView(view);
        }
    }



    public interface SaveTicketListener{
        void frameView(RelativeLayout frameLayout);
    }

    public void saveTicketListener(SaveTicketListener saveTicketListener){
        this.saveTicketListener = saveTicketListener;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mCurrentView = (View) object;
    }

    private void setImg(int drawableImg, View view){
        ImageView ivSetTicketIcon = view.findViewById(R.id.ivSetTicketIcon);
        Glide.with(context).load(drawableImg).into(ivSetTicketIcon);
    }
}
