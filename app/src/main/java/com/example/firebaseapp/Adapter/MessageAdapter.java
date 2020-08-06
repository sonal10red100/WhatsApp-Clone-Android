package com.example.firebaseapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.Model.Chat;
import com.example.firebaseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Chat> mChat;
    private String imageURL;
    private FirebaseUser fUser;

    public static final int MSG_LEFT=0;
    public static final int MSG_RIGHT=1;

    public MessageAdapter(Context context, List<Chat> mChat, String imageURL) {
        this.context = context;
        this.mChat = mChat;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType==MSG_RIGHT){
           View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
           return new MessageAdapter.ViewHolder(view);
       }else{
           View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
           return new MessageAdapter.ViewHolder(view);
       }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Log.e("-------------",mChat.get(position).toString());
        Chat chat=mChat.get(position);
        Log.e("-------------",chat.getMessage());
        holder.show_msg.setText(chat.getMessage());

        if(imageURL.equals("default")){
            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(imageURL).into(holder.profile_img);
        }

        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            }else{
                holder.txt_seen.setText("Delivered");
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_msg;
        public ImageView profile_img;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            show_msg=itemView.findViewById(R.id.show_msg);
            Log.e("-------+++++++++",show_msg.getText().toString());
            profile_img=itemView.findViewById(R.id.profile_img);
            txt_seen=itemView.findViewById(R.id.txt_seen_status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fUser.getUid())){
            return MSG_RIGHT;
        }else{
            return MSG_LEFT;
        }
    }
}
