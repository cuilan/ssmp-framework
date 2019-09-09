package cn.cuilan.framework.utils;

import com.google.common.base.Joiner;

/**
 * Created by ws on 2016/11/15.
 */
public class PushFormatUtils {

    //信息体的前缀
    static String URL_NEWS = "chouti://news?";

    static String URL_NOTICE = "chouti://notice?";

    static String URL_MSG = "chouti://msg?";

    //data信息类型
    static String TYPE_TEXT = "type=text&";

    static String TYPE_BYTE = "type=byte&";

    static String TYPE_NEWS = "type=1&";

    static String TYPE_NOTICE = "type=2&";

    static String TYPE_MSG = "type=3&";

    //date解析方式
    static String PARSE_JSON = "parse=json&";

    static String PARSE_XML = "parse=xml&";

    static String DATA = "data=";

    //创建新闻信息规则
    public static String createNewsRule(String data){
        Joiner joiner = Joiner.on("").skipNulls();
        String content = joiner.join(URL_NEWS,TYPE_NEWS,PARSE_JSON,DATA,data);
        return content;
    }

    //创建系统通知规则
    public static String createNoticesRule(String data){
        Joiner joiner = Joiner.on("").skipNulls();
        String content = joiner.join(URL_NOTICE,TYPE_NOTICE,PARSE_JSON,DATA,data);
        return content;
    }

    //创建聊天消息规则
    public static String createMsgRule(String data){
        Joiner joiner = Joiner.on("").skipNulls();
        String content = joiner.join(URL_MSG,TYPE_MSG,PARSE_JSON,DATA,data);
        return content;
    }

}
