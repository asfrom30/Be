package com.doyoon.android.bravenewworld.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseHelper;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.presenter.fragment.abst.RecyclerFragment;
import com.doyoon.android.bravenewworld.util.Const;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class UserChatFragment extends RecyclerFragment<Chat> {

    public static String TAG = UserChatFragment.class.getSimpleName();

    public List<Chat> chatList = new ArrayList<>();
    private EditText inputEditText;

    /* Shared Preference */
    public static String chatAccessKey = null;

    public static UserChatFragment instance;

    public static UserChatFragment getInstance(){
        if (instance == null) {
            instance = new UserChatFragment();
        }
        return instance;
    }

    private UserChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "User Chat Fragment is onCreateView again");

        View view = super.onCreateView(inflater, container, savedInstanceState);

        inputEditText = (EditText) view.findViewById(R.id.chat_input_text_editText);

        /* Add Listener */
        view.findViewById(R.id.chat_input_emoji_imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmojiBtn();
            }
        });
        view.findViewById(R.id.chat_send_imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendBtn();
            }
        });
        return view;
    }

    private void onSendBtn(){
        if (chatAccessKey == null) {
            Log.i(TAG, "Chat Access Key is null, you can not add Chat");
            return;
        }
        String inputText = inputEditText.getText().toString();
        if (inputText == null) {
            return;
        }
        Chat chat = new Chat(inputText, Const.MY_USER_KEY);

        FirebaseDao.insert(chat, chatAccessKey);
    }

    private void onEmojiBtn(){

    }

    public void runChatService(){
        if (chatAccessKey == null) {
            return;
        }

        String modelDir = FirebaseHelper.getModelDir(Const.RefKey.CHAT, chatAccessKey);

        FirebaseDatabase.getInstance().getReference(modelDir).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chatList.add(chat);
                notifySetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public CustomViewHolder throwCustomViewHolder(View view) {
        return new CustomViewHolder(view) {
            private TextView textViewMessage;
            private TextView textViewName;

            @Override
            public void dependencyInjection(View itemView, Chat chat) {
                textViewMessage = (TextView) itemView.findViewById(R.id.chat_message_textView);
                textViewName = (TextView) itemView.findViewById(R.id.chat_name_textView);
            }

            @Override
            public void updateRecyclerItemView(View view, Chat chat) {
                textViewName.setText(chat.getOwnerKey());
                textViewMessage.setText(chat.getMessage());
            }

            @Override
            public void onClick(View v) {

            }
        };
    }

    @Override
    public List<Chat> throwDataList() {
        return chatList;
    }

    @Override
    public int throwFragmentLayoutResId() {
        return R.layout.fragment_user_chat;
    }

    @Override
    public int throwRecyclerViewResId() {
        return R.id.chat_recycler_view;
    }

    @Override
    public int throwItemLayoutId() {
        return R.layout.item_chat;
    }

}
