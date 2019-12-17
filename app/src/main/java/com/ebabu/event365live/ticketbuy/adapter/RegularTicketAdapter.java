package com.ebabu.event365live.ticketbuy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.ticketbuy.modal.RegularTicketInfo;
import com.ebabu.event365live.ticketbuy.modal.ticketmodal.FinalSelectTicketModal;

import java.util.ArrayList;
import java.util.List;

public class RegularTicketAdapter extends RecyclerView.Adapter<RegularTicketAdapter.RegularTicketHolder> {

    private Context context;
    private List<FinalSelectTicketModal.Ticket> ticketList;

    public RegularTicketAdapter(Context context, List<FinalSelectTicketModal.Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public RegularTicketAdapter.RegularTicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_seating_layout,parent,false);
        return new RegularTicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegularTicketAdapter.RegularTicketHolder holder, int position) {
        FinalSelectTicketModal.Ticket ticketModal = ticketList.get(position);
        /*
        * getRegular Ticket Flag shows 0 - Regular ticket info, 1- regular ticket seating
        * */

            if (ticketModal.getRegularTicketFlag() == 0) {
                Log.d("nflkanfklna", "RegularTicketAdapter: ");
                holder.vipInfoContainer.setVisibility(View.VISIBLE);
                holder.vipSeatingContainer.setVisibility(View.GONE);
                holder.tvVipInfoTitle.setText(ticketModal.getTicketName());
                holder.tvVipInfoDes.setText(ticketModal.getDescription());
                holder.tvVipInfoPrice.setText("$" + ticketModal.getPricePerTicket());
                setQuantity(ticketModal.getTotalQuantity(), holder, 0);
            }
        if (ticketModal.getRegularTicketFlag() == 1) {
            Log.d("nflkanfklna", "RegularTicketAdapter: ");
            holder.vipSeatingContainer.setVisibility(View.VISIBLE);
            holder.vipInfoContainer.setVisibility(View.GONE);
            holder.tvSeatingTitle.setText(ticketModal.getTicketName());
            holder.tvSeatingTicketDes.setText(ticketModal.getDescription());
            holder.tvSeatingTicketPrice.setText("$" + ticketModal.getPricePerTicket());
            setQuantity(ticketModal.getTotalQuantity(), holder, 1);

            Log.d("fnlanfklnal", ticketModal.getTicketType() + " onBindViewHolder: " + ticketModal.getParsonPerTable());
        }


    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class RegularTicketHolder extends RecyclerView.ViewHolder {
        TextView tvVipInfoTitle, tvVipInfoDes, tvVipInfoPrice, tvSeatingTitle, tvSeatingTicketPrice, tvSeatingTicketDes;
        Spinner spinnerVipInfoQty, spinnerSeatingSelectQty;

        RelativeLayout vipInfoContainer, vipSeatingContainer;

        public RegularTicketHolder(@NonNull View itemView) {
            super(itemView);
            spinnerVipInfoQty = itemView.findViewById(R.id.spinnerVipInfoQty);
            tvVipInfoTitle = itemView.findViewById(R.id.tvVipInfoTitle);
            tvVipInfoPrice = itemView.findViewById(R.id.tvVipInfoPrice);
            tvVipInfoDes = itemView.findViewById(R.id.tvVipInfoDes);
            vipInfoContainer = itemView.findViewById(R.id.vipRegularContainer);
            vipSeatingContainer = itemView.findViewById(R.id.vipSeatingContainer);

            tvSeatingTitle = itemView.findViewById(R.id.tvSeatingTitle);
            tvSeatingTicketPrice = itemView.findViewById(R.id.tvSeatingTicketPrice);
            tvSeatingTicketDes = itemView.findViewById(R.id.tvSeatingTicketDes);
            spinnerSeatingSelectQty = itemView.findViewById(R.id.spinnerSeatingSelectQty);
        }
    }
    private void setQuantity(int no, RegularTicketHolder holder, int flag) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <= no; i++) {
            arrayList.add(i);
        }
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_dropdown_item_1line, arrayList);
        if (flag == 1) {
            holder.spinnerSeatingSelectQty.setAdapter(spinnerAdapter);
            return;
        }
        holder.spinnerVipInfoQty.setAdapter(spinnerAdapter);
    }
}
