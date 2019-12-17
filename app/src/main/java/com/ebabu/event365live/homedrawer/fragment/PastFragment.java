package com.ebabu.event365live.homedrawer.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.FragmentPastBinding;
import com.ebabu.event365live.homedrawer.adapter.PastAdapter;
import com.ebabu.event365live.homedrawer.modal.pastmodal.Past;
import com.ebabu.event365live.homedrawer.modal.pastmodal.FavoritesEventListModal;
import com.ebabu.event365live.httprequest.Constants;
import com.ebabu.event365live.utils.MyLoader;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastFragment extends Fragment{

    private FragmentPastBinding pastBinding;
    private PastAdapter pastAdapter;
    private MyLoader myLoader;
    public static FavoritesEventListModal modal;
    private FavoritesEventListModal favoritesEventListModal;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myLoader = new MyLoader(context);
        assert getArguments() != null;
        favoritesEventListModal = getArguments().getParcelable(Constants.favoritesList);
    }

    public PastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pastBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_past,container,false);
        initView();
        return pastBinding.getRoot();
    }
    private void initView(){
        if(favoritesEventListModal.getData().getPast().size() == 0){
                pastBinding.recyclerPastFavorites.setVisibility(View.GONE);
                pastBinding.layout.setVisibility(View.VISIBLE);
                return;
            }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        pastAdapter = new PastAdapter(getContext(),favoritesEventListModal.getData().getPast());
        pastBinding.recyclerPastFavorites.setLayoutManager(manager);
        pastBinding.recyclerPastFavorites.setAdapter(pastAdapter);
    }


}
