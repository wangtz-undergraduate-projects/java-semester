package com.wudaokou.easylearn.constant;

public class Constant {
    //public static final String eduKGQuestionUrl = "http://open.edukg.cn/opedukg/api/typeOpen/open/inputQuestion";
    public static final String eduKGBaseUrl = "http://open.edukg.cn/opedukg/api/typeOpen/open/";
    public static final String eduKGLoginUrl = "http://open.edukg.cn/opedukg/api/typeAuth/";
    public static final String eduKGPhone = "18744798635";
    public static final String eduKGPassword = "12345678wtz";
    public static String eduKGId = "";

    public static final String backendBaseUrl = "http://tianze.site:6789";

    public static String backendToken = "";

    public static final int maxSearchRecordCount = 12; // 最多展示的历史记录数目

    public static final String[] subjectList = {"chinese", "math", "english",
            "physics", "chemistry", "biology", "history",
            "geo", "politics"};

    // 用于分割选择题选项
    public static final String[] choiceSplitChars = {
            ".", "、", "．", ""
    };

    // 设置ChannelManager频道管理中的每个item的间隔
    public static final int ITEM_SPACE = 5;

    // 0和1均表示ChannelManager频道管理中的tab不可可编辑
    // 其中tab的type为0时，字体会显示红色， 为1时会显示灰色
    public static final int ITEM_DEFAULT = 0;
    // 1均表示ChannelManager频道管理中的tab不可可编辑
    public static final int ITEM_UNEDIT = 1;
    // 表示ChannelManager频道管理中的tab可编辑
    public static final int ITEM_EDIT = 2;
}
