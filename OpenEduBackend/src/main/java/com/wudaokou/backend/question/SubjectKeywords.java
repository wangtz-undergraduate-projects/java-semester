package com.wudaokou.backend.question;

import com.wudaokou.backend.history.Course;

import java.util.HashMap;
import java.util.Map;

public class SubjectKeywords {
    public static String[] chinese = {
            "语文", "诗", "李白", "对联", "成语", "方言", "比喻", "拟人", "名言", "作文", "修辞", "小说", "散文",
            "文言文", "人称", "思想", "主题", "情感", "杜甫", "苏轼", "虚词", "介词", "主语", "阅读", "写作"
    };
    public static String[] math = {
            "概率", "方程", "解", "实数", "小数", "几何", "圆", "向量", "空间", "平面", "集合",
            "数论", "函数", "坐标", "微分", "积分", "统计", "质数", "常数", "公式",
            "高斯", "欧拉"
    };
    public static String[] english = {
            "english", "actor", "home", "search", "train", "me", "this", "be", "there", "human", "war",
            "university", "work", "money", "hard", "tear", "mouth", "ear", "movie", "song", "ball"
    };
    public static String[] physics = {
            "物理", "力", "能量", "速度", "电", "磁", "摩擦", "光", "功", "热", "声", "波", "辐射", "振动",
            "折射", "反射", "牛顿", "爱因斯坦", "霍金", "库仑", "赫兹", "电荷"
    };
    public static String[] chemistry = {
            "化学","原子", "分子", "化学键", "摩尔", "气压", "氧化", "酸", "碱", "盐", "金属", "非金属",
            "有机", "无机", "结构", "键能", "笨", "元素", "门捷列夫", "电子", "催化剂", "道尔顿",
            "居里", "离子", "化合物", "化学反应", "氧化还原", "物质", "醇", "电离", "反应热"
    };
    public static String[] biology = {
            "生物", "动物", "植物", "真菌", "病毒", "细菌", "繁殖", "分裂", "细胞", "基因", "染色体", "DNA",
            "RNA", "蛋白质", "糖原", "呼吸", "宿主", "寄生", "多样性", "糖", "遗传", "孟德尔", "原核",
            "真核", "叶绿体", "酶", "进化", "变异", "形状", "种群", "食物链", "赤潮"
    };
    public static String[] history = {
            "历史", "秦始皇", "唐", "宋", "元", "明", "清", "春秋", "战国", "夏", "商", "周", "秦", "汉",
            "帝王", "宗族", "权力", "战争", "史书", "考古", "世界史", "中国史", "近代史", "鸦片战争", "抗日",
            "新中国", "党", "原始", "文字", "语言", "风俗", "传统", "文化", "制度", "经济", "服饰", "饮食",
            "住宅", "贸易"
    };
    public static String[] geo = {
            "地理", "气候", "草原", "河流", "山地", "平原", "洋流", "纬度", "经度", "地球", "太阳", "回归",
            "春分", "赤道", "南极", "北极", "冰川", "热带", "温带", "寒带", "风", "水", "土", "光", "南半球",
            "北半球", "时区", "江", "湖", "海", "云"
    };
    public static String[] politics = {
            "政治", "制度", "国家", "民族", "权力", "党", "人民", "爱国", "经济", "贸易", "哲学", "思想",
            "民主", "人权", "自由", "平等", "法律", "义务", "责任", "政府", "阶级", "革命", "主权", "战争",
            "暴力", "马克思", "西方", "世纪", "外交"
    };

    public static Map<Course, String[]> map;
    public static Map<Course, String[]> getMap() {
        if (map == null) {
            map = new HashMap<>();
            map.put(Course.CHINESE, chinese);
            map.put(Course.MATH, math);
            map.put(Course.ENGLISH, english);
            map.put(Course.PHYSICS, physics);
            map.put(Course.CHEMISTRY, chemistry);
            map.put(Course.BIOLOGY, biology);
            map.put(Course.HISTORY, history);
            map.put(Course.GEO, geo);
            map.put(Course.POLITICS, politics);
        }
        return map;
    }
}
