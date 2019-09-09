package cn.cuilan.framework.utils.service;

import java.io.File;

public interface UploadService {
    /**
     * 上传图片到七牛(文件)
     *
     * @param uploadFile 要上传的文件
     * @param prefix     返回值是否带前缀
     */
    String uploadImgToSevenCows(File uploadFile, Boolean prefix);

    /**
     * 上传图片到七牛(字节)
     * 可自定义文件名
     *
     * @param bs       要上传的文件的字节数组
     * @param fileName 文件名
     * @param prefix   返回值是否带前缀
     */
    String uploadImgToSevenCows(byte[] bs, String fileName, Boolean prefix);
    /**
     * 上传图片到七牛(字节)
     *
     * @param bs       要上传的文件的字节数组
     * @param prefix   返回值是否带前缀
     */
    String uploadImgToSevenCows(byte[] bs, Boolean prefix);

    /**
     * 将图片URL上传到七牛
     * @param url
     * @param prefix
     * @return
     */
    String uploadImgToSevenCows(String url, Boolean prefix);

    /**
     * 将图片URL上传到七牛
     */
    String uploadImgToSevenCows(String url, String referer);

}