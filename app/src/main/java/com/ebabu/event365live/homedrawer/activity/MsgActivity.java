package com.ebabu.event365live.homedrawer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.applozic.mobicomkit.api.conversation.ApplozicConversation;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.MessageListHandler;
import com.ebabu.event365live.R;
import com.ebabu.event365live.databinding.ActivityMsgBinding;
import com.ebabu.event365live.homedrawer.adapter.UserChatListAdapter;
import com.ebabu.event365live.utils.MyLoader;

import java.util.List;

public class MsgActivity extends AppCompatActivity {

    MyLoader myLoader;
    private MessageDatabaseService messageDatabaseService;
    private ActivityMsgBinding msgBinding;
    private boolean isFromSwipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgBinding = DataBindingUtil.setContentView(this,R.layout.activity_msg);
        myLoader = new MyLoader(this);
        msgBinding.refreshListSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFromSwipe = true;
                initiateChat();
            }
        });
    }

    private void initiateChat(){
        if(!isFromSwipe)
            myLoader.show("");
        messageDatabaseService = new MessageDatabaseService(this);
        ApplozicConversation.getLatestMessageList(MsgActivity.this,false, (messageList, e) -> {

            if (!isFromSwipe)
                myLoader.dismiss();
            Log.d("fbalfnkla", "initiateChat: " + messageList.size());
            if (e != null) {
                if (messageList.size() > 0) {
                    msgBinding.refreshListSwipe.setRefreshing(false);
                    setAdapter(messageList);
                    msgBinding.dataNotFound.setVisibility(View.GONE);
                    msgBinding.showChatUserListRecycler.setVisibility(View.VISIBLE);
                } else {
                    msgBinding.refreshListSwipe.setRefreshing(false);
                    msgBinding.showChatUserListRecycler.setVisibility(View.GONE);
                    msgBinding.dataNotFound.setVisibility(View.VISIBLE);
                }
            } else {
                msgBinding.refreshListSwipe.setRefreshing(false);
                msgBinding.showChatUserListRecycler.setVisibility(View.GONE);
                msgBinding.dataNotFound.setVisibility(View.VISIBLE);
            }
        });

        isFromSwipe = false;
    }

    void setAdapter(List<Message> messageList){
        UserChatListAdapter userChatListAdapter = new UserChatListAdapter(messageList,messageDatabaseService);
        msgBinding.showChatUserListRecycler.setAdapter(userChatListAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        initiateChat();
    }

    public void backBtnOnClick(View view) {
        finish();
    }
}
