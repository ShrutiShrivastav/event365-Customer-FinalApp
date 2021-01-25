package com.ebabu.event365live.home.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityRsvpticketBinding;
import com.ebabu.event365live.homedrawer.adapter.RsvpTicketAdapter;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.PaymentUser;
import com.ebabu.event365live.homedrawer.modal.rsvpmodal.RsvpBookedTicketModal;
import com.ebabu.event365live.httprequest.APICall;
import com.ebabu.event365live.httprequest.APIs;
import com.ebabu.event365live.httprequest.GetResponseData;
import com.ebabu.event365live.utils.CarouselEffectTransformer;
import com.ebabu.event365live.utils.CommonUtils;
import com.ebabu.event365live.utils.MyLoader;
import com.ebabu.event365live.utils.ShowToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class RSVPTicketFragment extends Fragment implements GetResponseData {

    private ActivityRsvpticketBinding rsvpTicketBinding;
    private RsvpTicketAdapter rsvpTicketAdapter;
    private View mCurrentView;
    private Activity activity;
    private Context context;
    private MyLoader myLoader;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        activity = (Activity) context;
        this.context = context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rsvpTicketBinding = DataBindingUtil.inflate(inflater, R.layout.activity_rsvpticket, container, false);
        showBookedTicketRequest();
        return rsvpTicketBinding.getRoot();
    }

    private void setupRsvpShowTicket(List<PaymentUser> paymentUserList) {
        rsvpTicketAdapter = new RsvpTicketAdapter(context, paymentUserList);
        rsvpTicketBinding.rsvpViewpager.setPageMargin(20);
        rsvpTicketBinding.rsvpViewpager.setClipToPadding(false);
        rsvpTicketBinding.rsvpViewpager.setPadding(40, 0, 40, 0);
        rsvpTicketBinding.rsvpViewpager.setAdapter(rsvpTicketAdapter);
        rsvpTicketBinding.rsvpViewpager.setPageTransformer(false, new CarouselEffectTransformer(context));
        rsvpTicketAdapter.saveTicketListener(frameLayout -> {
            mCurrentView = frameLayout;
            BitmapDrawable mDrawable = new BitmapDrawable(getResources(), viewToBitmap(frameLayout));
            Bitmap mBitmap = mDrawable.getBitmap();
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    mBitmap, "Design", null);
            Uri uri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, "Booked Ticket");
            startActivity(Intent.createChooser(share, "Share Your Design!"));
        });
    }

    private void showBookedTicketRequest() {
        myLoader.show("");
        Call<JsonElement> bookedTicketCall = APICall.getApiInterface().getUserBookedTicket(CommonUtils.getCommonUtilsInstance().getDeviceAuth());
        new APICall(context).apiCalling(bookedTicketCall, this, APIs.GET_USER_TICKET_BOOKED);
    }

    @Override
    public void onSuccess(JSONObject responseObj, String message, String typeAPI) {
        myLoader.dismiss();
        rsvpTicketBinding.noDataFoundContainer.setVisibility(View.GONE);
        rsvpTicketBinding.rsvpViewpager.setVisibility(View.VISIBLE);
        RsvpBookedTicketModal bookedTicketModal = new Gson().fromJson(responseObj.toString(), RsvpBookedTicketModal.class);
        if (bookedTicketModal.getData().getPaymentUser() != null && bookedTicketModal.getData().getPaymentUser().size() > 0) {
            setupRsvpShowTicket(bookedTicketModal.getData().getPaymentUser());
            return;
        }
        showNoDataDialog();
    }

    @Override
    public void onFailed(JSONObject errorBody, String message, Integer errorCode, String typeAPI) {
        myLoader.dismiss();
        if (errorCode != null && errorCode == 406) {
            showNoDataDialog();
        }
    }

    private void showNoDataDialog() {
        rsvpTicketBinding.noDataFoundContainer.setVisibility(View.VISIBLE);
        rsvpTicketBinding.rsvpViewpager.setVisibility(View.GONE);
        ((TextView) rsvpTicketBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setText(getString(R.string.no_ticket_you_booked_yet));
        ((TextView) rsvpTicketBinding.noDataFoundContainer.findViewById(R.id.tvShowNoDataFound)).setTextColor(Color.WHITE);
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentView != null) {
            mCurrentView.findViewById(R.id.ivShareTicketIcon).setVisibility(View.VISIBLE);
            mCurrentView.findViewById(R.id.tvShare).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                rsvpTicketAdapter.saveTicket();
                return;
            }
            ShowToast.infoToast(context, "Permission Denied");
        }
    }
}
