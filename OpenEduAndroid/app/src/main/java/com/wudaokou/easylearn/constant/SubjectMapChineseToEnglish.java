package com.wudaokou.easylearn.constant;

import java.util.HashMap;

public class SubjectMapChineseToEnglish {
    public static HashMap<String, String> map;
    public static HashMap<String, String> getMap () {
        if (map == null) {
            map = new HashMap<>();
            map.put("语文", "chinese");
            map.put("数学", "math");
            map.put("英语", "english");
            map.put("物理", "physics");
            map.put("化学", "chemistry");
            map.put("生物", "biology");
            map.put("历史", "history");
            map.put("地理", "geo");
            map.put("政治", "politics");
        }
        return map;
    }
}
