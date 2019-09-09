package cn.cuilan.framework.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2018/11/20 16:36
 */
public class UserUtils {

    private final static String cryptPwdKey = "abcdef123456";

    public static String crypt_pwd(String src) {
        char item;
        char keyItem;
        int i = 0;
        int j = 0;
        int len;
        char[] destChars = new char[src.length()];
        char[] srcChars = src.toCharArray();
        char[] keyChars = cryptPwdKey.toCharArray();
        for (len = src.length(); i < len; i++) {
            item = srcChars[i];
            keyItem = keyChars[j];
            destChars[i] = (char) ((item & 0xF0) + ((item & 0x0F) ^ (keyItem & 0x0F)));
            j++;
            if (j >= cryptPwdKey.length()) j = 0;
        }
        return String.valueOf(destChars);
    }
}
