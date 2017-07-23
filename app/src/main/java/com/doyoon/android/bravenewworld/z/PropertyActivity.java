package com.doyoon.android.bravenewworld.z;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/* Java의 Property */
public class PropertyActivity extends FragmentActivity {

    Preference preference;
    SharedPreferences sharedPreferences;

    private EditText editName, editEmail, editPassword;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // setContentView(R.layout.activity_property);

        /* Transaction */
        // Data를 처리하는 하나의 단위를 트랜잭션이라고 한다.
        // ABCDEF를 처리해서 데이터를 저장한다고 하면 중간에 오류가 난다면 (데이터 처리 태스크)
        // 다시 원복을 시키는것
        // 읽기에는 트랜잭션이라는 개념이 없고, 수정 삭제 삽입에만 있다.
        // 그래서 Read는 Editor를 꺼내지 않는다.

        /* XML HTML Txt는 매직넘버가 없다. 그냥 구조만 정의해둔것.. */
        sharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE); // 파일명으로 Properties 생성
        preference = new Preference(this); // Activity 이름으로 생성(지금은 거의 사용하지 않는다.)
        /*
        this.editName = (EditText) findViewById(R.id.property_inputText_name);
        this.editEmail = (EditText) findViewById(R.id.property_inputText_email);
        this.editPassword = (EditText) findViewById(R.id.property_inputText_pw);

        this.btnSave = (Button) findViewById(R.id.property_btn_send);
        */

        loadPref();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePref("name", editName.getText().toString());
                savePref("email", editEmail.getText().toString());
                savePref("password", editPassword.getText().toString());
            }
        });
    }

    public void loadPref() {
        String name = sharedPreferences.getString("name", "[none]");
        String email = sharedPreferences.getString("email", "[none]");
        String password = sharedPreferences.getString("password", "");

        this.editName.setText(name);
        this.editEmail.setText(email);
        this.editPassword.setText(password);

    }

    // Save Preference
    public void savePref(String key, String value) {
        // 1. Get Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 2. editor를 통해서 키 값을 저장
        editor.putString(key, value);
        // 3. editor 커밋
        editor.commit();
    }

    // 삭제하기
    private void removePreferences(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    // 전체 삭제하기
    private void removeAllPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
