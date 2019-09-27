package cn.cuilan.framework.utils.service;

import cn.cuilan.framework.utils.DateUtils;
import cn.cuilan.framework.utils.FileUpload;
import cn.cuilan.framework.utils.ImageUtils;
import cn.cuilan.framework.utils.TextUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2018/3/29 15:14
 */
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    private final static String QINIU_ACCESS_KEY = "W6HjfxWoDO24OOntKoPf75s5dq4CP96lcSmU9pxt";
    private final static String QINIU_SECRET_KEY = "rebWZM-EPm1qoATTatoSQU5yzaVMs3NeLG3t4fA2";
    private final static Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
    private static UploadManager uploadManager;

    static {
        uploadManager = new UploadManager();
    }

    @Value("${chouti.image.qiniu.space}")
    private String qnPicSpaceName;//七牛上传空间名称
    @Value("${chouti.image.domain}")
    private String picUploadPrefix;//图片上传后的域名前缀

    @Override
    public String uploadImgToSevenCows(File uploadFile, Boolean prefix) {
        if (uploadFile == null) {
            return "";
        }
        String nameKey = UploadUtil.genFileName(uploadFile);
        return upload(nameKey, prefix, uploadToken -> uploadManager.put(uploadFile, nameKey, uploadToken));
    }

    private int currentTimeMillis() {
        return (int) System.currentTimeMillis();
    }

    @Override
    public String uploadImgToSevenCows(byte[] bs, String fileName, Boolean prefix) {
        return upload(fileName, prefix, uploadToken -> uploadManager.put(bs, fileName, uploadToken));
    }

    @Override
    public String uploadImgToSevenCows(byte[] bs, Boolean prefix) {

        String type = ImageUtils.getImageType(bs);
        type = StringUtils.isBlank(type) ? "jpg" : type;

        String fileName = "CHOUTI_" + DateUtils.nowWithFormat("yyMMdd") + "_" + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + "." + type;
        return uploadImgToSevenCows(bs, fileName, prefix);
    }

    @Override
    public String uploadImgToSevenCows(String url, Boolean prefix) {
        url = TextUtils.URLDecode(url);
        byte[] bytes = FileUpload.translateRemoteImageToByte(url);
        if (bytes == null) {
            return null;
        }
        return uploadImgToSevenCows(bytes, prefix);
    }

    @Override
    public String uploadImgToSevenCows(String url, String referer) {
        url = TextUtils.URLDecode(url);
        byte[] bytes = FileUpload.translateRemoteImageToByte(url, referer);
        if (bytes == null) {
            return null;
        }
        return uploadImgToSevenCows(bytes, true);
    }

    private String upload(String fileName, boolean prefix, UploadFun doUpload) {
        String imgUrl = "";
        try {
            int startTime = currentTimeMillis();
            log.debug("upload to qiniu chat start....nameKey:" + fileName);
            String uploadToken = auth.uploadToken(qnPicSpaceName);
            Response res = doUpload.exec(uploadToken);
            int endTime = currentTimeMillis();
            log.debug("upload to qiniu chat time:" + (endTime - startTime) + " response:" + res.bodyString());
            imgUrl = getString(prefix, imgUrl, fileName, res);
        } catch (QiniuException e) {
            return getCatchString(imgUrl, e);
        }
        log.debug("upload to qiniu chat success....url: {}", imgUrl);
        return imgUrl;
    }

    private String getString(Boolean prefix, String imgUrl, String nameKey, Response res) {
        if (res.isOK()) {
            if (prefix) {
                imgUrl = picUploadPrefix + "/" + nameKey;
            } else {
                imgUrl = "/" + nameKey;
            }
        }
        return imgUrl;
    }

    private String getCatchString(String imgUrl, QiniuException e) {
        log.debug(e.getMessage());
        return imgUrl;
    }

    interface UploadFun {
        Response exec(String uploadToken) throws QiniuException;
    }

}
