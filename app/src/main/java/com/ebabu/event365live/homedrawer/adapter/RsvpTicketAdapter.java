package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.GroupTicketInfo;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;


public class RsvpTicketAdapter extends PagerAdapter {
    private Context context;
    private List<PaymentUser> paymentUserList;
    private RsvpTicketViewLayoutBinding ticketViewLayoutBinding;
    private SaveTicketListener saveTicketListener;
    private View mCurrentView;
    private CardStackAdapter cardStackAdapter;
    private CancelTicketClickListener cancelTicketClickListener;

    public RsvpTicketAdapter(Context context, List<PaymentUser> paymentUserList, CancelTicketClickListener cancelTicketClickListener) {
        this.context = context;
        this.paymentUserList = paymentUserList;
        this.cancelTicketClickListener = cancelTicketClickListener;
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
        ticketViewLayoutBinding.getRoot().setTag(paymentUserList.get(position));
        PaymentUser paymentUser = paymentUserList.get(position);
        initialize(paymentUser, position);
        container.addView(ticketViewLayoutBinding.getRoot());
        return ticketViewLayoutBinding.getRoot();
    }

    private void initialize(PaymentUser paymentUser, int position) {
        CardStackLayoutManager manager = new CardStackLayoutManager(context);
        manager.setStackFrom(StackFrom.BottomAndRight);
//        manager.setVisibleCount(paymentUser.getEvents().getTicketBooked().size());
        manager.setVisibleCount(3);
        manager.setTranslationInterval(12.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.None);
        manager.setOverlayInterpolator(new LinearInterpolator());
        ticketViewLayoutBinding.cardStackView.setLayoutManager(manager);
        cardStackAdapter = new CardStackAdapter(position, paymentUser.getEvents().getTicketBooked().size(), paymentUserList, cancelTicketClickListener);
        ticketViewLayoutBinding.cardStackView.setAdapter(cardStackAdapter);
    }

    public interface SaveTicketListener {
        void frameView(RelativeLayout frameLayout);
    }

    public void saveTicketListener(SaveTicketListener saveTicketListener) {
        this.saveTicketListener = saveTicketListener;
    }

    public interface CancelTicketClickListener{
        public void onClickCancelButton(PaymentUser paymentUser, List<GroupTicketInfo> groupTicketInfo, int pos);
        public void onClickStackTicket(PaymentUser paymentUser, int pos);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mCurrentView = (View) object;
    }

    public void saveTicket() {
        mCurrentView.findViewById(R.id.ivShareTicketIcon).setVisibility(View.GONE);
        mCurrentView.findViewById(R.id.tvShare).setVisibility(View.GONE);
        saveTicketListener.frameView((RelativeLayout) mCurrentView);
    }


    @Override
    public int getItemPosition(@NonNull Object object) {

//            PaymentUser paymentUser = (PaymentUser) ((View) object).getTag();
//            int position = paymentUserList.indexOf(paymentUser);
//            if (position >= 0) {
//                return position;
//            } else {
                return POSITION_NONE;
//            }

    }

}
