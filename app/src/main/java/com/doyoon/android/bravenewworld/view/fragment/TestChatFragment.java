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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.doyoon.android.bravenewworld.R;
import com.doyoon.android.bravenewworld.domain.DummyDao;
import com.doyoon.android.bravenewworld.domain.firebase.value.Chat;
import com.doyoon.android.bravenewworld.presenter.Presenter;
import com.doyoon.android.bravenewworld.presenter.interfaces.ChatUIController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/18/2017.
 */

/* 좌우로 정렬하기 */
/* 말풍선*/

/* 이거 리팩토링해서 채팅방으로 재사용하기... 인터페이스로 분리해서 oop에 넣어 놓을 것.. */

public class TestChatFragment extends Fragment implements ChatUIController {

    public static TestChatFragment newInstance() {

        Bundle args = new Bundle();

        TestChatFragment fragment = new TestChatFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Presenter.getInstance().setChatUIController(this);
    }

    private ChatAdapter chatAdapter;
    private ListView listView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_fragment_chat, container, false);


        if (listView == null) {
            listView = (ListView) view.findViewById(R.id.chat_list_view);
        }



        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(getContext());
        }

        listView.setAdapter(chatAdapter);


        view.findViewById(R.id.leftBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftClick();
            }
        });

        view.findViewById(R.id.rightBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightClick();
            }
        });

        return view;
    }


    private void leftClick(){
        String randomMsg = DummyDao.getRandomMsg();
        Chat chat = new Chat(randomMsg, "heydude");
        chatAdapter.addChat(chat);
        chatAdapter.notifyDataSetChanged();
        listView.setSelection(listView.getAdapter().getCount()-1);
    }

    private void rightClick(){
        String randomMsg = DummyDao.getRandomMsg();
        Chat chat = new Chat(randomMsg, "kimdoyoon");
        chatAdapter.addChat(chat);
        chatAdapter.notifyDataSetChanged();
        listView.setSelection(listView.getAdapter().getCount()-1);
    }

    @Override
    public void addChat(Chat chat) {

    }

    @Override
    public void notifySetChanged() {

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

            Log.e("TAG", "position is " + position);

            View view = inflater.inflate(R.layout.item_chat, parent, false);

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.test_linear);
            linearLayout.setGravity(Gravity.RIGHT);

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

            Chat chat = chatList.get(position);
            TextView textViewName = (TextView) view.findViewById(R.id.chat_name_textView);
            TextView textViewMsg = (TextView) view.findViewById(R.id.chat_message_textView);

            if (userChangedFlag == true) {
                textViewName.setText(chat.getOwnerKey());
                textViewName.setVisibility(View.VISIBLE);
            } else {
                Log.e("TAG", "Visible Gone");
                textViewName.setVisibility(View.GONE);
            }

            textViewMsg.setText(chat.getMessage());


            // textViewName.setGravity(Gravity.RIGHT);

            return view;
        }

        public void addChat(Chat chat) {
            chatList.add(chat);
        }

    }



}

