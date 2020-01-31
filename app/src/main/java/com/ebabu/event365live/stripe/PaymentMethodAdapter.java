package com.ebabu.event365live.stripe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.PaymentMethodItemBinding;
import com.ebabu.event365live.utils.ShowToast;
import com.stripe.android.model.PaymentMethod;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerViewBouncy.Adapter<PaymentMethodAdapter.PaymentMethodHolder>{

    private Context context;
    private List<PaymentMethod> paymentMethodList;


    public PaymentMethodAdapter(List<PaymentMethod> paymentMethodList) {
        this.paymentMethodList = paymentMethodList;
        Log.d("fblablfa", "PaymentMethodAdapter: "+paymentMethodList.size());
    }


    public void updatePaymentList(PaymentMethod paymentMethod){
        paymentMethodList.add(0,paymentMethod);
        notifyItemInserted(0);
        Log.d("fblablfa", "updatePaymentList: "+paymentMethodList.size());
    }

    @NonNull
    @Override
    public PaymentMethodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        PaymentMethodItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.payment_method_item,parent,false);
        return new PaymentMethodHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethodList.get(position);
        PaymentMethod.Card card = paymentMethod.card;

        holder.itemBinding.pmId.setText(paymentMethod.id);
        holder.itemBinding.pmBrand.setText(card != null ? card.brand : "");
        holder.itemBinding.pmLast4.setText(card != null ? card.last4 : "");


        ShowToast.infoToast(context,paymentMethod.id);

    }

    @Override
    public int getItemCount() {
        return paymentMethodList.size();
    }

    class PaymentMethodHolder extends RecyclerViewBouncy.ViewHolder {
        PaymentMethodItemBinding itemBinding;
        public PaymentMethodHolder(PaymentMethodItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
