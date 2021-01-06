package com.ebabu.event365live.ticketbuy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ebabu.event365live.R;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.ticketbuy.modal.ticketmodal.FinalSelectTicketModal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BuyTicketAdapter extends RecyclerView.Adapter<BuyTicketAdapter.VipTicketHolder> {

    private Context context;
    private List<FinalSelectTicketModal.Ticket> finalSelectTicketModals;
    private SelectedVipTicketListener selectedVipTicketListener;
    ArrayAdapter<Integer> spinnerAdapter;

    public BuyTicketAdapter(Context context, List<FinalSelectTicketModal.Ticket> finalSelectTicketModals) {
        this.context = context;
        this.finalSelectTicketModals = finalSelectTicketModals;
        selectedVipTicketListener = (SelectedVipTicketListener) context;
        Log.d("fsafsafsa", "VipTicketAdapter: "+finalSelectTicketModals.size());
    }

    @NonNull
    @Override
    public BuyTicketAdapter.VipTicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_seating_layout, parent, false);
        return new BuyTicketAdapter.VipTicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyTicketAdapter.VipTicketHolder holder, int position) {

        FinalSelectTicketModal.Ticket ticketModal = finalSelectTicketModals.get(position);

        if(!ticketModal.getSeatingTable()){
            holder.seatingContainer.setVisibility(View.GONE);
            holder.vipInfoContainer.setVisibility(View.VISIBLE);
            holder.tvTicketName.setText(ticketModal.getTicketName());
            if (ticketModal.getDescription() != null && !TextUtils.isEmpty(ticketModal.getDescription())) {
                holder.tvTicketDes.setVisibility(View.VISIBLE);
                holder.tvTicketDes.setText(ticketModal.getDescription());
            } else holder.tvTicketDes.setVisibility(View.GONE);

            holder.tvTicketPrice.setText("$" + ticketModal.getPricePerTicket());
            //setQuantity(holder,ticketModal.getSeatingTable(), ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getTicketId(),ticketModal.getPricePerTicket(), ticketModal.getParsonPerTable(), position+1);
            setQuantity(holder,finalSelectTicketModals, position);

        }else {
            holder.seatingContainer.setVisibility(View.VISIBLE);
            holder.vipInfoContainer.setVisibility(View.GONE);

            holder.tvSeatingTicketName.setText(ticketModal.getTicketName());
            if (ticketModal.getDescription() != null && !TextUtils.isEmpty(ticketModal.getDescription())) {
                holder.tvSeatingTicketDes.setVisibility(View.VISIBLE);
                holder.tvSeatingTicketDes.setText(ticketModal.getDescription());
            } else holder.tvSeatingTicketDes.setVisibility(View.INVISIBLE);


            holder.tvShowFullPrice.setText("Full Price $" + ticketModal.getPricePerTable());
            //holder.tvSeatingDisTicketPrice.setText("$" + getDiscountPrice(ticketModal.getPricePerTable(),ticketModal.getDisPercentage()));
            holder.tvSeatingDisTicketPrice.setText("$" + ticketModal.getDiscountedPrice());
            holder.tvShowPercentageDiscount.setText("("+ticketModal.getDisPercentage()+"% to be paid now)");
            holder.tvShowPerPersonTable.setText("X "+ticketModal.getParsonPerTable());
            holder.tvTicketPrice.setText("$" + ticketModal.getPricePerTicket());
          //  setQuantity(holder,ticketModal.getSeatingTable(), ticketModal.getTicketType(), ticketModal.getNoOfTables(), ticketModal.getTicketId(),discountPrice,ticketModal.getParsonPerTable(),position+1);
            setQuantity(holder,finalSelectTicketModals, position);
        }


//        if (ticketList instanceof VipTicketInfo) {
//            VipTicketInfo ticketModal = (VipTicketInfo) ticketList.get(position);
//            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
//            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getTicketId(), Double.parseDouble(ticketModal.getPricePerTicket()));
//
//        } else if (ticketList instanceof VipTableSeatingInfo) {
//            VipTableSeatingInfo ticketModal = (VipTableSeatingInfo) ticketList.get(position);
//            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
//            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getTicketId(), Double.parseDouble(ticketModal.getPricePerTicket()));
//
//        } else if (ticketList instanceof RegularTicketInfo) {
//            RegularTicketInfo ticketModal = (RegularTicketInfo) ticketList.get(position);
//            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
//            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getTicketId(), Double.parseDouble(ticketModal.getPricePerTicket()));
//
//        } else if (ticketList instanceof RegularTicketSeatingInfo) {
//            RegularTicketSeatingInfo ticketModal = (RegularTicketSeatingInfo) ticketList.get(position);
//            setData(holder, ticketModal.getTicketName(), ticketModal.getDescription(), Double.parseDouble(ticketModal.getPricePerTicket()));
//            setQuantity(holder, ticketModal.getTicketType(), ticketModal.getTotalQuantity(), ticketModal.getTicketId(), Double.parseDouble(ticketModal.getPricePerTicket()));
//        }


        /* here ticket flag denotes vip info ticket and vip seating ticket ie. 0 = vip info ticket, 1 = vip seating ticket*/

    }
    @Override
    public int getItemCount() {
        return finalSelectTicketModals.size();
    }

    class VipTicketHolder extends RecyclerView.ViewHolder implements Spinner.OnItemSelectedListener{
        TextView tvTicketName, tvTicketDes, tvTicketPrice,tvSeatingTicketName,tvSeatingTicketDes,tvShowFullPrice,tvSeatingDisTicketPrice,tvShowPercentageDiscount,tvShowPerPersonTable;
        Spinner spinnerTicketQty,spinnerSeatingTicketQty;
        RelativeLayout vipInfoContainer;
        LinearLayout seatingContainer;

        public VipTicketHolder(@NonNull View itemView) {
            super(itemView);
            spinnerTicketQty = itemView.findViewById(R.id.spinnerTicketQty);
            tvTicketName = itemView.findViewById(R.id.tvTicketName);
            tvTicketPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvTicketDes = itemView.findViewById(R.id.tvTicketDes);
            vipInfoContainer = itemView.findViewById(R.id.vipRegularContainer);

            /*seating views*/
            tvSeatingTicketName = itemView.findViewById(R.id.tvSeatingTicketName);
            tvSeatingTicketDes = itemView.findViewById(R.id.tvSeatingTicketDes);
            tvShowFullPrice = itemView.findViewById(R.id.tvShowFullPrice);
            tvSeatingDisTicketPrice = itemView.findViewById(R.id.tvSeatingDisTicketPrice);
            tvShowPercentageDiscount = itemView.findViewById(R.id.tvShowPercentageDiscount);
            tvShowPerPersonTable = itemView.findViewById(R.id.tvShowPerPersonTable);
            spinnerSeatingTicketQty = itemView.findViewById(R.id.spinnerSeatingTicketQty);
            seatingContainer = itemView.findViewById(R.id.seatingContainer);

            spinnerTicketQty.setSelected(false);
            spinnerTicketQty.setOnItemSelectedListener(this);
            spinnerSeatingTicketQty.setSelected(false);
            spinnerSeatingTicketQty.setOnItemSelectedListener(this);
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

    private void setQuantity(BuyTicketAdapter.VipTicketHolder holder,List<FinalSelectTicketModal.Ticket> ticketList, int itemPosition) {
       FinalSelectTicketModal.Ticket ticketModal  = ticketList.get(itemPosition);
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <= ticketModal.getTotalQuantity(); i++) {
            arrayList.add(i);
        }
        spinnerAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_dropdown_item_1line, arrayList);
        if (ticketModal.getSeatingTable() != null && !ticketModal.getSeatingTable()) {
            holder.spinnerTicketQty.setAdapter(spinnerAdapter);
            holder.spinnerTicketQty.setSelected(false);
            holder.spinnerTicketQty.setSelection(0, true);
            holder.spinnerTicketQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVipTicketListener.getSelectedTicketListener(ticketList, itemPosition, (int) parent.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            holder.spinnerSeatingTicketQty.setAdapter(spinnerAdapter);
            holder.spinnerSeatingTicketQty.setSelected(false);
            holder.spinnerSeatingTicketQty.setSelection(0, true);
            holder.spinnerSeatingTicketQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVipTicketListener.getSelectedTicketListener(ticketList, itemPosition, (int) parent.getSelectedItem());
                    Log.d("nfklanfklnla", " seating ticket: " + (int) parent.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            }
    }

    private void setData(VipTicketHolder holder, Boolean isSeatingTable, String ticketName, String des, double ticketPrice){
    }

    public float getDiscountPrice(int pricePerTable, int discountPercentage){
        return pricePerTable * discountPercentage / 100;
    }
}
