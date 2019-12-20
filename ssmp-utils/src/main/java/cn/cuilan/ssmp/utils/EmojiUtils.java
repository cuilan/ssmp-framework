package cn.cuilan.ssmp.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtils {
    public static boolean containsEmoji(String source) {
        if(StringUtils.isEmpty(source)){
            return false;
        }
        int len = source.length();
        boolean isEmoji = false;

        for (int i = 0; i < len; ++i) {
            char hs = source.charAt(i);
            char ls;
            if ('\ud800' <= hs && hs <= '\udbff') {
                if (source.length() > 1) {
                    ls = source.charAt(i + 1);
                    int uc = (hs - '\ud800') * 1024 + (ls - '\udc00') + 65536;
                    if (118784 <= uc && uc <= 128895) {
                        return true;
                    }
                }
            } else {
                if (8448 <= hs && hs <= 10239 && hs != 9787) {
                    return true;
                }

                if (11013 <= hs && hs <= 11015) {
                    return true;
                }

                if (10548 <= hs && hs <= 10549) {
                    return true;
                }

                if (12951 <= hs && hs <= 12953) {
                    return true;
                }

                if (hs == 169 || hs == 174 || hs == 12349 || hs == 12336 || hs == 11093 || hs == 11036 || hs == 11035 || hs == 11088 || hs == 8986) {
                    return true;
                }

                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    ls = source.charAt(i + 1);
                    if (ls == 8419) {
                        return true;
                    }
                }
            }
        }

        return isEmoji;
    }

//    public static void main(String[] a) {
//        String str = "今天下雨☔️，市政还在洒水，这是为了完成任务吗？";
//        boolean b = containsEmoji(str);
//        System.out.println(b);
//        System.out.println(str);
//        String s = filterEmoji(str);
//        System.out.println("=========");
//        System.out.println(s);
//    }

    public static String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[☀-⟿]", 66);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("");
                return source;
            } else {
                return source;
            }
        } else {
            return source;
        }
    }
}

