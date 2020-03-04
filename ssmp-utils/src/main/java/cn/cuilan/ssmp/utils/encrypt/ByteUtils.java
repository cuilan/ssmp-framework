package cn.cuilan.ssmp.utils.encrypt;

/**
 * Byte二进制转换工具类
 *
 * @author zhang.yan
 * @date 2020/3/4
 */
public class ByteUtils {

    /**
     * byte数组转十六进制字符串
     *
     * @param bHex 二进制数组
     * @return 十六进制字符串
     */
    public static String bytesToHexString(byte[] bHex) {
        StringBuilder sb = new StringBuilder(bHex.length);
        for (byte hex : bHex) {
            String sTemp = Integer.toHexString(0xFF & hex);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转byte数组
     *
     * @param hex 十六进制字符串
     * @return 二进制数组
     */
    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)]));
        }
        return result;
    }

    /**
     * 字符转byte
     *
     * @param c 字符
     * @return byte二进制形式
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
