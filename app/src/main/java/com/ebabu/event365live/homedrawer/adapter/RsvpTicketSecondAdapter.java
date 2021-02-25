package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.RsvpTicketViewLayoutBinding;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.TicketBooked;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.List;

public class RsvpTicketSecondAdapter extends PagerAdapter {
    private Context context;
    private List<TicketBooked> ticketBookedList;
    private RsvpTicketViewLayoutBinding ticketViewLayoutBinding;
    private SaveTicketListener saveTicketListener;
    private View mCurrentView;
    private CardStackSecondAdapter cardStackAdapter;
    private RsvpTicketAdapter.CancelTicketClickListener cancelTicketClickListener;
    private PaymentUser paymentUser11;

    public RsvpTicketSecondAdapter(Context context, List<TicketBooked> ticketBookedList, RsvpTicketAdapter.CancelTicketClickListener cancelTicketClickListener, PaymentUser paymentUser) {
        this.context = context;
        this.ticketBookedList = ticketBookedList;
        this.cancelTicketClickListener = cancelTicketClickListener;
        this.paymentUser11 = paymentUser;
        //Collections.reverse(this.paymentUserList);
    }

    @Override
    public int getCount() {
        return ticketBookedList.size();
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
        TicketBooked paymentUser = ticketBookedList.get(position);
        initialize(paymentUser, position);
        container.addView(ticketViewLayoutBinding.getRoot());
        return ticketViewLayoutBinding.getRoot();
    }

    private void initialize(TicketBooked paymentUser, int position) {
        CardStackLayoutManager manager = new CardStackLayoutManager(context);
        manager.setStackFrom(StackFrom.BottomAndRight);
//        manager.setVisibleCount(paymentUser.getEvents().getTicketBooked().size());
        manager.setVisibleCount(1);
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

//        cardStackAdapter = new CardStackSecondAdapter(position, paymentUser.getEvents().getTicketBooked().size()
//                , paymentUserList, cancelTicketClickListener);

        cardStackAdapter = new CardStackSecondAdapter(position, 1
                , ticketBookedList, paymentUser11, cancelTicketClickListener);
        ticketViewLayoutBinding.cardStackView.setAdapter(cardStackAdapter);
    }

    public interface SaveTicketListener {
        void frameView(RelativeLayout frameLayout);
    }

    public void saveTicketListener(SaveTicketListener saveTicketListener) {
        this.saveTicketListener = saveTicketListener;
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

}
