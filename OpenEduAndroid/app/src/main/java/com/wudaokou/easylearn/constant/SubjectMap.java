package com.wudaokou.easylearn.constant;

import androidx.room.Room;

import com.wudaokou.easylearn.SubjectManageActivity;
import com.wudaokou.easylearn.data.MyDatabase;

import java.util.HashMap;
import java.util.Map;

public class SubjectMap {
    public static HashMap<String, String> map;
    public static HashMap<String, String> getMap () {
        if (map == null) {
            map = new HashMap<>();
            map.put("chinese", "语文");
            map.put("math", "数学");
            map.put("english", "英语");
            map.put("physics", "物理");
            map.put("chemistry", "化学");
            map.put("biology", "生物");
            map.put("history", "历史");
            map.put("geo", "地理");
            map.put("politics", "政治");
        }
        return map;
    }
}
