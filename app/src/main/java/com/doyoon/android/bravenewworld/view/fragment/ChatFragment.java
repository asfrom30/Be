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
import com.doyoon.android.bravenewworld.z.util.Const;
import com.doyoon.android.bravenewworld.z.util.ConvString;
import com.doyoon.android.bravenewworld.z.util.LogUtil;

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
        LogUtil.logLifeCycle(TAG, "On Create");
    }

    private String chatAccessKey;
    private ChatAdapter chatAdapter;

    private TextView titleTextView;
    private ListView listView;
    private EditText inputEditText;
    private ImageButton emojiBtn, sendBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.logLifeCycle(TAG, "On Create View");

        /* Set AppPresenter */
        AppPresenter.getInstance().setChatUIController(this);
        this.chatAccessKey = AppPresenter.getInstance().getCurrentChatAccessKey();

        /* UserProfileView setting */
        View view = inflater.inflate(R.layout.fragment_user_chat, container, false);
        this.dependencyInjection(view);
        this.addWidgetsListener();

        /* Update View */
        updateBtnEnabled();
        updateTitle();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.logLifeCycle(TAG, "On Resume View");

    }

    private void dependencyInjection(View view) {
        if (listView == null) {
            listView = (ListView) view.findViewById(R.id.chat_list_view);
        }


        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(getContext());
            listView.setAdapter(chatAdapter);
        }

        titleTextView = (TextView) view.findViewById(R.id.chat_title);
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

    @Override
    public void updateTitle() {
        String myName = "-";
        String otherName = "-";

        if (UserStatusPresenter.getInstance().myUserAccessKey != null) {
            String myAccessKey = UserStatusPresenter.getInstance().myUserAccessKey;
            myName = ConvString.commaStringToSign(myAccessKey);
            if (UserStatusPresenter.getInstance().myUserProfile != null) {
                if(UserStatusPresenter.getInstance().myUserProfile.getName() != null){
                    myName = UserStatusPresenter.getInstance().myUserProfile.getName();
                }
            }
        }

        if (UserStatusPresenter.getInstance().otherUserAccessKey != null) {

            String otherAccessKey = UserStatusPresenter.getInstance().otherUserAccessKey;
            otherName = ConvString.commaStringToSign(otherAccessKey);

            if(UserStatusPresenter.getInstance().otherUserProfile != null){
                if(UserStatusPresenter.getInstance().otherUserProfile.getName() != null) {
                    otherName = UserStatusPresenter.getInstance().otherUserProfile.getName();
                }
            }
        }


        if (UserStatusPresenter.getInstance().activeUserType == Const.ActiveUserType.Giver) {
            titleTextView.setText(myName + "님이 " + otherName +"에게 우산이 되어 주었습니다.");
        } else if (UserStatusPresenter.getInstance().activeUserType == Const.ActiveUserType.Taker) {
            titleTextView.setText(otherName + "님이 " + myName +"에게 우산이 되어 주었습니다.");
        }
    }

    private class ChatAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Chat> chatList;

        public ChatAdapter(Context context) {
            chatList = new ArrayList<>();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
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

            //todo need to recycle view(not create every time)
            View view = inflater.inflate(R.layout.item_chat, parent, false);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.chat_item_linearlayout);

            boolean isMyChat = (chatList.get(position).getOwnerKey().equals(UserStatusPresenter.myUserAccessKey)) ? true : false;
            Log.i(TAG, "owner key is " + chatList.get(position).getOwnerKey());
            Log.i(TAG, "user key is " + UserStatusPresenter.myUserAccessKey);
            Log.i(TAG, "=====" + isMyChat);

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
            ImageView profileImageViewLeft = (ImageView) view.findViewById(R.id.chat_profile_imageView_left);
            ImageView profileImageViewRight = (ImageView) view.findViewById(R.id.chat_profile_imageView_right);
            TextView textViewName = (TextView) view.findViewById(R.id.chat_name_textView);
            TextView textViewMsg = (TextView) view.findViewById(R.id.chat_message_textView);

            /* View Setting */
            ImageView profileImageView = null;

            if(isMyChat){
                linearLayout.setGravity(Gravity.RIGHT);
                profileImageViewLeft.setVisibility(View.GONE);
                profileImageView = profileImageViewRight;
            } else {
                linearLayout.setGravity(Gravity.LEFT);
                profileImageViewRight.setVisibility(View.GONE);
                profileImageView = profileImageViewLeft;
            }

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
            return (isMyChat) ? UserStatusPresenter.myUserProfile.getName() : UserStatusPresenter.otherUserProfile.getName();
        }

        private String getImageUri(boolean isMyChat) {
            return (isMyChat) ? UserStatusPresenter.myUserProfile.getImageUri() : UserStatusPresenter.otherUserProfile.getImageUri();
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

