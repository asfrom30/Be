package com.doyoon.android.bravenewworld.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.presenter.Presenter;
import com.doyoon.android.bravenewworld.presenter.base.fragment.RecyclerFragment;
import com.doyoon.android.bravenewworld.presenter.interfaces.ChatUIController;
import com.doyoon.android.bravenewworld.util.Const;
import com.doyoon.android.bravenewworld.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/10/2017.
 */

public class ChatFragment extends RecyclerFragment<Chat> implements ChatUIController{

    public static String TAG = ChatFragment.class.getSimpleName();

    public String chatAccessKey;
    public List<Chat> chatList = new ArrayList<>();
    private EditText inputEditText;
    private ImageButton emojiBtn, sendBtn;

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logLifeCycle(TAG, "on Create");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "onCreateView()");

        /* Link to Presenter */
        Presenter.getInstance().setChatUIController(this);

        /* View setting */
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.dependencyInjection(view);
        this.addWidgetsListener();

        this.chatAccessKey = Presenter.getInstance().getCurrentChatAccessKey();
        if (this.chatAccessKey == null || "".equals(this.chatAccessKey)) {
            Log.e(TAG, "button Disabled");
            updateBtnEnabled(false);
        } else {
            Log.e(TAG, this.chatAccessKey);
            updateBtnEnabled(true);
        }

        return view;
    }

    @Override
    public void onResume() {
        LogUtil.logLifeCycle(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "On save Instance");
    }

    @Override
    public void addChat(Chat chat) {
        chatList.add(chat);
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
        inputEditText.setText("");
    }

    private void onEmojiBtn(){

    }

    private void updateBtnEnabled(boolean bool){
        emojiBtn.setEnabled(bool);
        sendBtn.setEnabled(bool);
        inputEditText.setEnabled(bool);
    }

    private void dependencyInjection(View view){
        inputEditText = (EditText) view.findViewById(R.id.chat_input_text_editText);
        emojiBtn = (ImageButton) view.findViewById(R.id.chat_input_emoji_imageButton);
        sendBtn = (ImageButton) view.findViewById(R.id.chat_send_imageButton);
    }

    private void addWidgetsListener() {
        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmojiBtn();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendBtn();
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

    @Override
    public void scrollEndCallback() {

    }

}
