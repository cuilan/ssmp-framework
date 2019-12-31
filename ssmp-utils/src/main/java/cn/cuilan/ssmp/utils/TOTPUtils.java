package cn.cuilan.ssmp.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

import java.util.concurrent.TimeUnit;

/**
 * 基于时间的一次性密码算法
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
public class TOTPUtils {

    /**
     * 认证时间60秒
     */
    private static final long AUTH_TIME = TimeUnit.SECONDS.toMillis(45L);

    private static GoogleAuthenticatorConfig googleAuthenticatorConfig;

    static {
        googleAuthenticatorConfig = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(AUTH_TIME)
                // 容错时间5秒
                .setWindowSize(5).build();
    }

    /**
     * 生成安全码
     */
    public static String generatorKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(googleAuthenticatorConfig);
        return gAuth.createCredentials().getKey();
    }

    /**
     * 生成TOTP码
     *
     * @param secretKey 安全码
     */
    public static int generatorTOTPCode(String secretKey) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(googleAuthenticatorConfig);
        return gAuth.getTotpPassword(secretKey);
    }

    /**
     * 认证TOTP码是否有效
     *
     * @param secretKey 安全码
     * @param totpCode  TOTP码
     */
    public static boolean authSecretKey(String secretKey, int totpCode) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(googleAuthenticatorConfig);
        return gAuth.authorize(secretKey, totpCode);
    }
}
