package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class EntityLinkSearchActivity extends AppCompatActivity {

    TextInputLayout chooseSubjectLayout;
    AutoCompleteTextView chooseSubject;
    TextInputLayout searchTextLayout;
    TextInputEditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_link_search);

        searchText = findViewById(R.id.text);
        chooseSubject = findViewById(R.id.chooseSubject);
        chooseSubjectLayout = findViewById(R.id.chooseSubjectLayout);
        searchTextLayout = findViewById(R.id.textInputLayout);

        // 选择学科
        String[] subjects = getResources().getStringArray(R.array.subjects);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, subjects);
        chooseSubject.setAdapter(arrayAdapter);

        chooseSubject.setOnItemClickListener((parent, view, position, id) -> chooseSubjectLayout.setError(null));
        searchText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                searchTextLayout.setError(null);
            }
        });

    }

    public void getBack(View view) {
        EntityLinkSearchActivity.this.finish();
    }


    public void search(View view) {
        String subject = chooseSubject.getText().toString();
        boolean valid = true;
        if(subject.equals("")){
            chooseSubjectLayout.setError("请选择学科");
            valid = false;
        }
        String text = String.valueOf(searchText.getText());
        if(text.equals("")) {
            searchTextLayout.setError("请输入搜索文本");
            valid = false;
        }
        if(!valid) return;
        Intent intent = new Intent(this, EntityLinkResultActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    public void clearText(View view) {
        searchText.setText("");
    }
}