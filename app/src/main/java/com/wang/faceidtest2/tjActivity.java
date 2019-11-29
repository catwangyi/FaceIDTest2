package com.wang.faceidtest2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wang.faceidtest2.Services.RunOnUI;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class tjActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tj_layout);
        final EditText editText = findViewById(R.id.search_id);
        Button button = findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText.getText().toString().trim();
                if (id.isEmpty()){
                    RunOnUI.Run(getApplicationContext(),"请输入正确的账号" );
                }else{
                    Intent intent = new Intent(getApplicationContext(),CheckInfoActivity.class);
                    intent.putExtra("id",id );
                    startActivity(intent);
                }
            }
        });
    }
}
