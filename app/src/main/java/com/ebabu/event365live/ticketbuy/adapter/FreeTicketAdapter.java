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
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.listener.SelectedVipTicketListener;
import com.ebabu.event365live.ticketbuy.modal.FreeTicket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FreeTicketAdapter extends RecyclerView.Adapter<FreeTicketAdapter.TicketHolder>{

    private Context context;
    private List<FreeTicket> freeTicketList;
    private SelectedVipTicketListener selectedVipTicketListener;

    public FreeTicketAdapter(Context context, List<FreeTicket> freeTicketList) {
        this.context = context;
        this.freeTicketList = freeTicketList;
        selectedVipTicketListener = (SelectedVipTicketListener) context;
    }

    @NonNull
    @Override
    public TicketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.table_seating_layout,parent,false);
        View view = LayoutInflater.from(context).inflate(R.layout.free_ticket_layout,parent,false);
        return new TicketHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketHolder holder, int position) {
        FreeTicket freeTicket = freeTicketList.get(position);
        /* ticket will not show if total quantity of each ticket value zero*/
        if(freeTicket.getTotalQuantity() !=0){
            holder.tvShowTicketName.setText(freeTicket.getTicketName());
            holder.tvTicketDes.setText(freeTicket.getDescription());
            holder.tvTicketPrice.setVisibility(View.GONE);
            setQuantity(freeTicket,holder);
            return;
        }
        holder.ticketContainer.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return freeTicketList.size();
    }

    class TicketHolder extends RecyclerView.ViewHolder {
        Spinner spinnerSelectQty;
        TextView tvShowTicketName,tvTicketPrice,tvTicketDes;
        RelativeLayout ticketContainer;
        TicketHolder(@NonNull View itemView) {
            super(itemView);
            spinnerSelectQty = itemView.findViewById(R.id.spinnerSelectQty);
            tvShowTicketName = itemView.findViewById(R.id.tvShowTicketName);
            tvTicketPrice = itemView.findViewById(R.id.tvTicketPrice);
            tvTicketDes = itemView.findViewById(R.id.tvTicketDes);
            ticketContainer = itemView.findViewById(R.id.ticketContainer);
        }
    }

    private void setQuantity(FreeTicket freeTicket,TicketHolder holder){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i=0;i<=freeTicket.getTotalQuantity();i++)
        { arrayList.add(i) ;
        }
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(context,android.R.layout.simple_dropdown_item_1line,arrayList);
        holder.spinnerSelectQty.setAdapter(spinnerAdapter);
        holder.spinnerSelectQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // selectedVipTicketListener.getSelectedTicketListener(freeTicket.getId(), context.getString(R.string.free_ticket),0,(int)parent.getSelectedItem(),0,position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


}
