package com.wudaokou.easylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wudaokou.easylearn.constant.SubjectMapChineseToEnglish;

public class OrganizeActivity extends AppCompatActivity {

    TextInputLayout chooseSubjectLayout;
    AutoCompleteTextView chooseSubject;
    TextInputLayout nameLayout;
    TextInputEditText nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize);

        chooseSubject = findViewById(R.id.chooseSubject);
        chooseSubjectLayout = findViewById(R.id.chooseSubjectLayout);
        nameLayout = findViewById(R.id.nameLayout);
        nameText = findViewById(R.id.nameText);

        // 选择学科
        String[] subjects = getResources().getStringArray(R.array.subjects);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_subject_item, subjects);
        chooseSubject.setAdapter(arrayAdapter);

        chooseSubject.setOnItemClickListener((parent, view, position, id) -> chooseSubjectLayout.setError(null));
        nameText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                nameLayout.setError(null);
            }
        });
        nameText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                generateOrganize(nameText);
                return true;
            }
            return false;
        });
    }

    public void getBack(View view) {
        finish();
    }

    public void generateOrganize(View view) {
        boolean valid = true;
        String subject = chooseSubject.getText().toString();
        String subjectInEnglish = SubjectMapChineseToEnglish.getMap().get(subject);
        if(subject.equals("")){
            chooseSubjectLayout.setError("请选择学科");
            valid = false;
        }
        Editable nameEditable = nameText.getText();
        assert nameEditable != null;
        String name = nameEditable.toString();
        if(name.equals("")){
            nameLayout.setError("请输入知识点");
            valid = false;
        }

        if(!valid) return;

        Intent intent = new Intent(this, OrganizeResultActivity.class);
        intent.putExtra("course", subjectInEnglish);
        intent.putExtra("name", name);
        startActivity(intent);

    }
}