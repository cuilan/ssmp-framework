package cn.cuilan.ssmp.utils.service;

import cn.cuilan.ssmp.utils.HttpUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信公众号文章 工具类
 */
public class WeChatArticleUtil {


    /**
     * 获取文章标题
     *
     * @param url 链接地址
     */
    public static String getTitle(final String url) {
        try{
            String html = HttpUtils.doGet(url);

            Pattern pattern = Pattern.compile("var msg_title = \"(.*)\";");

            Matcher matcher = pattern.matcher(html);
            //通过 js的代码中标题 匹配标题
            if (matcher.find()) {
                return matcher.group(1);
            } else {
//            通过html标签中匹配标题

                pattern = Pattern.compile("<h2 class=\"rich_media_title\" id=\"activity-name\">([\\s\\S]*)</h2>");

                matcher = pattern.matcher(html);

                if (matcher.find())
                    return matcher.group(1).replaceAll("\n", "").trim();

            }
        }catch(Exception err){
            err.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String url = "https://mp.weixin.qq.com/s/IKyDUk02HDULXiUtuZfmZg";
        String title = getTitle(url);
        System.out.println(title);
    }
}
