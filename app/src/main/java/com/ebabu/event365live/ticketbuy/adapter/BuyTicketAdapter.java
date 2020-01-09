package com.ebabu.event365live.ticketbuy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.ticketbuy.modal.RegularTicketInfo;
import com.ebabu.event365live.ticketbuy.modal.RegularTicketSeatingInfo;
import com.ebabu.event365live.ticketbuy.modal.TicketSelectionModal;
import com.ebabu.event365live.ticketbuy.modal.VipTableSeatingInfo;
import com.ebabu.event365live.ticketbuy.modal.VipTicketInfo;

import java.util.ArrayList;
import java.util.List;

public class BuyTicketAdapter extends RecyclerView.Adapter<BuyTicketAdapter.VipTicketHolder> {

    private Context context;
    private List<?> ticketList;
    private SelectedVipTicketListener selectedVipTicketListener;
    ArrayAdapter<Integer> spinnerAdapter;

    public BuyTicketAdapter(Context context, List<?> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
        selectedVipTicketListener = (SelectedVipTicketListener) context;
        Log.d("fsafsafsa", "VipTicketAdapter: "+ticketList.size());
    }

    @NonNull
    @Override
    public BuyTicketAdapter.VipTicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_seating_layout, parent, false);
        return new BuyTicketAdapter.VipTicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyTicketAdapter.VipTicketHolder holder, int position) {

        if (ticketList instanceof VipTicketInfo) {
            VipTicketInfo ticketModal = (VipTicketInfo) ticketList.get(position);
            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getId(), Double.parseDouble(ticketModal.getPricePerTicket()));

        } else if (ticketList instanceof VipTableSeatingInfo) {
            VipTableSeatingInfo ticketModal = (VipTableSeatingInfo) ticketList.get(position);
            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getId(), Double.parseDouble(ticketModal.getPricePerTicket()));

        } else if (ticketList instanceof RegularTicketInfo) {
            RegularTicketInfo ticketModal = (RegularTicketInfo) ticketList.get(position);
            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getId(), Double.parseDouble(ticketModal.getPricePerTicket()));

        } else if (ticketList instanceof RegularTicketSeatingInfo) {
            RegularTicketSeatingInfo ticketModal = (RegularTicketSeatingInfo) ticketList.get(position);
            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getId(), Double.parseDouble(ticketModal.getPricePerTicket()));
        }


        /* here ticket flag denotes vip info ticket and vip seating ticket ie. 0 = vip info ticket, 1 = vip seating ticket*/


    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class VipTicketHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener{
        TextView tvTicketName, tvTicketDes, tvTicketPrice, tvSeatingTitle, tvSeatingTicketPrice, tvSeatingTicketDes;
        Spinner spinnerTicketQty;
        RelativeLayout vipInfoContainer;

        public VipTicketHolder(@NonNull View itemView) {
            super(itemView);
            spinnerTicketQty = itemView.findViewById(R.id.spinnerTicketQty);
            tvTicketName = itemView.findViewById(R.id.tvTicketName);
            tvTicketPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvTicketDes = itemView.findViewById(R.id.tvTicketDes);
            vipInfoContainer = itemView.findViewById(R.id.vipRegularContainer);

            spinnerTicketQty.setSelected(false);
            spinnerTicketQty.setOnItemSelectedListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(position != 0)
                Log.d("jflkanflanflla", "onItemSelected: "+position);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void setQuantity(BuyTicketAdapter.VipTicketHolder holder, String ticketType, int totalTicket, int ticketId, double price) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <= totalTicket; i++) {
            arrayList.add(i);
        }
        spinnerAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_dropdown_item_1line, arrayList);
            holder.spinnerTicketQty.setAdapter(spinnerAdapter);
            holder.spinnerTicketQty.setSelected(false);
            holder.spinnerTicketQty.setSelection(0,true);
            holder.spinnerTicketQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVipTicketListener.getSelectedTicketListener(ticketId,ticketType,price,(int)parent.getSelectedItem());
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
    }

    private void setData(VipTicketHolder holder, String ticketName, String des, double ticketPrice){
        holder.spinnerTicketQty.setVisibility(View.VISIBLE);
        holder.vipInfoContainer.setVisibility(View.GONE);
        holder.tvSeatingTitle.setText(ticketName);
        holder.tvSeatingTicketDes.setText(des);
        holder.tvSeatingTicketPrice.setText("$" + ticketPrice);
    }

}
