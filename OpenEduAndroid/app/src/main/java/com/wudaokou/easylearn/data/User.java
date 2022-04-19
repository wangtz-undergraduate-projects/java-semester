package com.wudaokou.easylearn.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    public String name;

    public User(@NonNull String name) {
        this.name = name;
        // 默认只勾选了语数英三科
        this.chineseChosen = true;
        this.mathChosen = true;
        this.englishChosen = true;
        this.physicsChosen = false;
        this.chemistryChosen = false;
        this.biologyChosen = false;
        this.historyChosen = false;
        this.geographyChosen = false;
        this.politicsChosen = false;
    }

    public boolean chineseChosen;

    public boolean mathChosen;

    public boolean englishChosen;

    public boolean physicsChosen;

    public boolean chemistryChosen;

    public boolean biologyChosen;

    public boolean historyChosen;

    public boolean geographyChosen;

    public boolean politicsChosen;
}
