package com.ebabu.event365live.ticketbuy.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.ebabu.event365live.ticketbuy.SelectTicketActivity;
import com.ebabu.event365live.ticketbuy.modal.ticketmodal.FinalSelectTicketModal;

import java.util.ArrayList;
import java.util.List;

public class VipTicketAdapter extends RecyclerView.Adapter<VipTicketAdapter.VipTicketHolder> {

    private Context context;
    private List<FinalSelectTicketModal.Ticket> ticketList;
    private SelectedVipTicketListener selectedVipTicketListener;
    ArrayAdapter<Integer> spinnerAdapter;
    public VipTicketAdapter(Context context, List<FinalSelectTicketModal.Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
        selectedVipTicketListener = (SelectedVipTicketListener) context;
    }

    @NonNull
    @Override
    public VipTicketAdapter.VipTicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_seating_layout, parent, false);
        return new VipTicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VipTicketAdapter.VipTicketHolder holder, int position) {
        FinalSelectTicketModal.Ticket  ticketModal = ticketList.get(position);
        /* here ticket flag denotes vip info ticket and vip seating ticket ie. 0 = vip info ticket, 1 = vip seating ticket*/

        Log.d("nflkanfnakls", ticketModal.getId()+" vipregular: "+ticketModal.getPricePerTicket());

            if (ticketModal.getRegularTicketFlag() == 0){
            Log.d("nflkanfklna", "getRegularTicketFlag: ");
            holder.vipInfoContainer.setVisibility(View.VISIBLE);
            holder.vipSeatingContainer.setVisibility(View.GONE);
            holder.tvVipInfoTitle.setText(ticketModal.getTicketName());
            holder.tvVipInfoDes.setText(ticketModal.getDescription());
            holder.tvVipInfoPrice.setText("$" + ticketModal.getPricePerTicket());
            if(ticketModal.getTotalQuantity() != null)
            setQuantity(ticketModal.getTotalQuantity(), holder, 0,ticketModal);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }
        if (ticketModal.getRegularTicketFlag() == 1) {
            Log.d("nflkanfklna", "onBindViewHolder: ");
            holder.vipSeatingContainer.setVisibility(View.VISIBLE);
            holder.vipInfoContainer.setVisibility(View.GONE);
            holder.tvSeatingTitle.setText(ticketModal.getTicketName());
            holder.tvSeatingTicketDes.setText(ticketModal.getDescription());
            holder.tvSeatingTicketPrice.setText("$" + ticketModal.getPricePerTicket());
            setQuantity(ticketModal.getTotalQuantity(), holder, 1,ticketModal);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("nklfknaslfnklas", ticketModal.getId()+" getRegularTicketFlag: ");
//                    selectedVipTicketListener.getVipSelectedTicket((int)holder.spinnerSeatingSelectQty.getSelectedItem(),ticketModal);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class VipTicketHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener{
        TextView tvVipInfoTitle, tvVipInfoDes, tvVipInfoPrice, tvSeatingTitle, tvSeatingTicketPrice, tvSeatingTicketDes;
        Spinner spinnerVipInfoQty, spinnerSeatingSelectQty;
        RelativeLayout vipInfoContainer, vipSeatingContainer;

        public VipTicketHolder(@NonNull View itemView) {
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

            //itemView.setOnClickListener(this);
//            spinnerVipInfoQty.setSelection(0,false);
//            spinnerVipInfoQty.setSelection(0,false);

            spinnerVipInfoQty.setSelected(false);
            spinnerSeatingSelectQty.setSelected(false);
            spinnerVipInfoQty.setOnItemSelectedListener(this);
            spinnerSeatingSelectQty.setOnItemSelectedListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(position != 0)
                Log.d("jflkanflanflla", "onItemSelected: "+position);
                //selectedVipTicketListener.getSelectedTicketListener(position,ticketList.get(getAdapterPosition()).getPricePerTicket(),parent.getSelectedItemPosition());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void setQuantity(int no, VipTicketHolder holder, int flag,FinalSelectTicketModal.Ticket  ticketModal) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <= ticketModal.getTotalQuantity(); i++) {
//            if(i == 0)
//            arrayList.add("Select Ticket");
//            else
                arrayList.add(i);
        }
        spinnerAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_dropdown_item_1line, arrayList);
        if (flag == 1) {
            holder.spinnerSeatingSelectQty.setAdapter(spinnerAdapter);
            holder.spinnerSeatingSelectQty.setSelected(false);
            holder.spinnerSeatingSelectQty.setSelection(0,true);
//            holder.spinnerSeatingSelectQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        selectedVipTicketListener.getSelectedTicketListener(position,ticketModal.getPricePerTicket());
//                    //Log.d(TAG, "onItemSelected: ");
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
            return;
        }
        holder.spinnerVipInfoQty.setAdapter(spinnerAdapter);
        holder.spinnerVipInfoQty.setSelected(false);
        holder.spinnerVipInfoQty.setSelection(0,true);
//        holder.spinnerVipInfoQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("nflkanklfna", ticketModal.getTicketName()+" spinnerVipInfoQty: "+ticketModal.getId());
//                selectedVipTicketListener.getSelectedTicketListener(position,ticketModal.getPricePerTicket());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }


}
