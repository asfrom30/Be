package com.doyoon.android.bravenewworld.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.firebase.FirebaseDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.presenter.AppPresenter;
import com.doyoon.android.bravenewworld.presenter.UserStatusPresenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ChatUIController;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DOYOON on 7/18/2017.
 */

/* 좌우로 정렬하기 */
/* 말풍선*/

/* 이거 리팩토링해서 채팅방으로 재사용하기... 인터페이스로 분리해서 oop에 넣어 놓을 것.. */

public class ChatFragment extends Fragment implements ChatUIController {

    private static String TAG = ChatFragment.class.getSimpleName();

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppPresenter.getInstance().setChatUIController(this);
    }

    private String chatAccessKey;
    private ChatAdapter chatAdapter;
    private ListView listView;
    private EditText inputEditText;
    private ImageButton emojiBtn, sendBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        /* Set AppPresenter */
        AppPresenter.getInstance().setChatUIController(this);
        this.chatAccessKey = AppPresenter.getInstance().getCurrentChatAccessKey();

        /* View setting */
        View view = inflater.inflate(R.layout.fragment_user_chat, container, false);
        this.dependencyInjection(view);
        this.addWidgetsListener();
        updateBtnEnabled();

        return view;
    }



    private void dependencyInjection(View view) {
        if (listView == null) {
            listView = (ListView) view.findViewById(R.id.chat_list_view);
        }


        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(getContext());
            listView.setAdapter(chatAdapter);
        }

        inputEditText = (EditText) view.findViewById(R.id.chat_input_text_editText);
        emojiBtn = (ImageButton) view.findViewById(R.id.chat_input_emoji_imageButton);
        sendBtn = (ImageButton) view.findViewById(R.id.chat_send_imageButton);
    }


    private void updateBtnEnabled(){
        boolean flag = true;

        if (this.chatAccessKey == null || "".equals(this.chatAccessKey)) {
            flag = false;
        }

        emojiBtn.setEnabled(flag);
        sendBtn.setEnabled(flag);
        inputEditText.setEnabled(flag);
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

    private void onSendBtn(){
        if (chatAccessKey == null) {
            Log.i(TAG, "Chat Access Key is null, you can not add Chat");
            return;
        }
        String inputText = inputEditText.getText().toString();
        if (inputText == null) {
            return;
        }
        Chat chat = new Chat(inputText, UserStatusPresenter.myUserAccessKey);

        FirebaseDao.insert(chat, chatAccessKey);
        inputEditText.setText("");
    }

    private void onEmojiBtn(){

    }

    @Override
    public void addChat(Chat chat) {
        chatAdapter.addChat(chat);
    }

    @Override
    public void notifySetChanged() {
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void setFocusLastItem() {
        listView.setSelection(listView.getAdapter().getCount()-1);
    }

    @Override
    public void updateProfileView() {

    }

    private class ChatAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Chat> chatList;

        public ChatAdapter(Context context) {
            chatList = new ArrayList<>();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return chatList.size();
        }

        @Override
        public Object getItem(int position) {
            return chatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = inflater.inflate(R.layout.item_chat, parent, false);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.chat_item_linearlayout);

            boolean isMyChat = (chatList.get(position).getOwnerKey() == UserStatusPresenter.myUserAccessKey) ? true : false;

            if(isMyChat){
                linearLayout.setGravity(Gravity.RIGHT);
            } else {
                linearLayout.setGravity(Gravity.LEFT);
            }


            boolean userChangedFlag = false;

            if (position == 0) {
                userChangedFlag = true;
            } else {
                String beforeOwnerKey = chatList.get(position - 1).getOwnerKey();
                String currentOwnerKey = chatList.get(position).getOwnerKey();
                Log.e("TAG", beforeOwnerKey + ", " + currentOwnerKey);
                if (!beforeOwnerKey.equals(currentOwnerKey)) {
                    userChangedFlag = true;
                }
            }

            /* ItemView Dependency Injection */
            ImageView profileImageView = (ImageView) view.findViewById(R.id.chat_profile_imageView);
            TextView textViewName = (TextView) view.findViewById(R.id.chat_name_textView);
            TextView textViewMsg = (TextView) view.findViewById(R.id.chat_message_textView);

            /* User Profile Visibility */
            if (userChangedFlag == true) {
                String name = getOwnerName(isMyChat);
                setTextViewName(textViewName, name);

                String imageUri = getImageUri(isMyChat);
                setProfileIamgeView(profileImageView, imageUri);

            } else {
                textViewName.setVisibility(View.GONE);
                profileImageView.setVisibility(View.GONE);
                view.findViewById(R.id.chat_wrapper_layout).setVisibility(View.GONE);
            }

            setTextViewMsg(textViewMsg, chatList.get(position).getMessage());

            return view;
        }

        public void addChat(Chat chat) {
            chatList.add(chat);
        }

        private String getOwnerName(boolean isMyChat) {
            return (isMyChat) ? UserStatusPresenter.myUserProfile.getName() : AppPresenter.getInstance().getChatOtherUserProfile().getName();
        }

        private String getImageUri(boolean isMyChat) {
            return (isMyChat) ? UserStatusPresenter.myUserProfile.getImageUri() : AppPresenter.getInstance().getChatOtherUserProfile().getImageUri();
        }

        private void setTextViewName(TextView textViewName, String name) {
            if(name == null) return;
            textViewName.setText(name);

        }

        private void setProfileIamgeView(ImageView imageView, String imageUri){
            if(imageUri == null) return;
            Glide.with(getActivity()).load(imageUri).bitmapTransform(new CropCircleTransformation(getContext())).into(imageView);
        }

        private void setTextViewMsg(TextView textViewMsg, String msg) {
            textViewMsg.setText(msg);
        }

    }





}

