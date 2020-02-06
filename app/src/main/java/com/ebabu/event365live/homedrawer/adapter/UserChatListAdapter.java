package com.ebabu.event365live.homedrawer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.bumptech.glide.Glide;
import com.ebabu.event365live.R;
import com.ebabu.event365live.bouncerecycler.RecyclerViewBouncy;
import com.ebabu.event365live.databinding.ChatCustomUserListBinding;
import com.ebabu.event365live.userinfo.activity.HostProfileActivity;
import com.ebabu.event365live.utils.CommonUtils;
import com.google.android.gms.common.util.DataUtils;

import java.util.List;

public class UserChatListAdapter extends RecyclerViewBouncy.Adapter<UserChatListAdapter.UserListHolder> {

    private Context context;
    private List<Message> messageList;
    private MessageDatabaseService messageDatabaseService;


    public UserChatListAdapter(List<Message> messageList, MessageDatabaseService messageDatabaseService) {
        this.messageList = messageList;
        this.messageDatabaseService = messageDatabaseService;
    }

    @NonNull
    @Override
    public UserListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ChatCustomUserListBinding listBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.chat_custom_user_list, parent, false);
        return new UserListHolder(listBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListHolder holder, int position) {
         Message message = messageList.get(position);
        Contact contact = null;
        Channel channel = null;

        if(message.getGroupId() == null){
            //this is a 1-to-1 message. Get the contact object and display its fields in the item view of your adapter
            contact = new AppContactService(context).getContactById(message.getContactIds());
            holder.listBinding.tvShowUsername.setText(contact.getDisplayName());
            if(contact.getUnreadCount() != 0){
                holder.listBinding.tvMsgCount.setVisibility(View.VISIBLE);
                holder.listBinding.tvMsgCount.setText(""+contact.getUnreadCount());
            }else {
                holder.listBinding.tvMsgCount.setVisibility(View.GONE);
            }
            if(message.getMessage() != null)
            holder.listBinding.tvShowMsg.setText(message.getMessage());

            if (CommonUtils.getTimeAgo(CommonUtils.getDate(message.getCreatedAtTime().toString()), context) != null) {
                if (!CommonUtils.getTimeAgo(CommonUtils.getDate(messageList.get(position).getCreatedAtTime().toString()), context).equalsIgnoreCase("0 year ago"))
                    holder.listBinding.tvShowChatTimeAgo.setText(CommonUtils.getTimeAgo(CommonUtils.getDate(messageList.get(position).getCreatedAtTime().toString()), context));
                else
                    holder.listBinding.tvShowChatTimeAgo.setText("just now");
            } else {
                holder.listBinding.tvShowChatTimeAgo.setText("just now");
            }

            Glide.with(context).load(contact.getImageURL()).placeholder(R.drawable.wide_loading_img).error(R.drawable.wide_error_img).into(holder.listBinding.imgUser);
            holder.listBinding.getRoot().setOnClickListener(v->{

                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, message.getContactIds());
                intent.putExtra(ConversationUIService.DISPLAY_NAME, message.getContactIds()); //put it for displaying the title.
                intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
                context.startActivity(intent);
            });


        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class UserListHolder extends RecyclerViewBouncy.ViewHolder implements View.OnClickListener {
        private ChatCustomUserListBinding listBinding;
        public UserListHolder(@NonNull ChatCustomUserListBinding listBinding) {
            super(listBinding.getRoot());
            this.listBinding = listBinding;
            listBinding.getRoot().setFocusable(false);
            listBinding.getRoot().setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }

}
