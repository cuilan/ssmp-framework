package cn.cuilan.framework.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoParser {

    /**
     * 匹配视频地址的正则表达式
     */
    public static String videoSiteRegex = "^(http://)(\\S){0,}("
            +
            // 优酷
            "(v.youku.com)|(player.youku.com)|(static.youku.com)"
            +
            // 土豆
            "|(tudou.com)|(js.tudouui.com)"
            +
            // 爱奇艺
            "|(iqiyi.com)|(video.qiyi.com)"
            +
            // 17173游戏视频
            "|(17173.tv.sohu.com)"
            +
            // 搜狐
            "|(tv.sohu.com)|(vrs.sohu.com)"
            +
            // 腾讯视频
            "|(v.qq.com)|(video.qq.com)|(imgcache.qq.com)"
            +
            // 酷6
            "|(v.ku6.com)|(player.ku6.com)|(player.ku6cdn.com)"
            +
            // pptv
            "|(v.pptv.com)|(player.pptv.com)|(player.pplive.cn)"
            +
            // 新浪视频
            "|(video.sina.com.cn)"
            +
            // 56网
            "|(56.com)"
            +
            // 网易视频
            "|(v.163.com/[^(photoview)])|(swf.ws.126.net)|(v.ent.163.com)"
            +
            // 激动网
            "|(joy.cn)"
            +
            // 乐视网
            "|(letv.com)"
            +
            // 音悦台
            "|(yinyuetai.com)"
            +
            // 迅雷看看
            "|(vod.kankan.com)|(video.kankan.com)|(kankan.com/vod)"
            +
            // 百度视频
            "|(mv.baidu.com)|(tieba.baidu.com/shipin/bw/video)"
            +
            // PPS
            "|(v.pps.tv)|(player.pps.tv)"
            +
            // 凤凰视频
            "|(v.ifeng.com)|(img.ifeng.com/swf)"
            +
            // cntv
            "|(player.cntv.cn)"
            +
            // 爱西柚
            "|(xiyou.cntv.cn)"
            +
            // 电影网
            "|(m1905.com/vod/play)|(m1905.com/video/play)"
            +
            // 江苏网络电视台
            "|(jstv.com)"
            +
            // 北京电视台
            "|(btv.com.cn/video/VID)"
            +
            // 齐鲁网
            "|(v.iqilu.com)"
            +
            // 新华网
            "|(xinhuanet.com[\\S]{0,}video)"
            +
            // 时光网
            "|(movie.mtime.com)"
            +
            // 第一视频
            "|(v1.cn)"
            +
            // 中关村在线
            "|(v.zol.com)"
            +
            // Tom宽频
            "|(tv.tom.com[\\S]{1,}video_id=[\\d]{1,})|(tv.tom.com[\\S]{1,}\\.swf[\\S]{1,}video=)"
            +
            // 播视网
            "|(boosj.com\\/[\\d]{4,})|(static.boosj.com)"
            +
            // 爆米花
            "|(video.baomihua.com)"
            +
            // acfun
            "|(acfun.tv/v/ac[\\d]{4,})|(ssl.acfun.tv/player)|(w5cdn.ranktv.cn/player)"
            +
            // 哔哩哔哩
            "|(bilibili.smgbb.cn/video/av[\\d]{4,})|(bilibili.tv/video/av[\\d]{4,})|(static.hdslb.com)|(video6.smgbb.cn)"
            +
            // 酷狗MV
            "|(kugou.com[\\S]{1,}mv_[\\d]{3,})" +
            // 酷狗MV
            "|(weiphone.com[\\S]{1,}weplayer.swf)" +
            // 秒拍
            "|(miaopai.com)" +
            // 快手
            "|(kuaishou.com)" +
            // 艺术中国
            "|(art.china)" + ")(\\S){0,}";
    /**
     * 匹配视频地址的正则表达式
     */
    public static String videoSiteRegexV2 = "^((http://)|(https://))(\\S){0,}("
            +
            // 秒拍
            "(miaopai.com)" +

            // 哔哩哔哩
            "|(bilibili.com)" +

            // acfun
            "|(acfun.tv)" +

            // 本站文件
            "|(chouti.com)" +

            //微博视频
            "|(weibo.com/tv/v)" +

            "|(video.weibo.com)" +

            "|(m.weibo.cn)" +

            "|(kuaishou.com)" +

            //t.cn短连接
            "|(t.cn)" +

            //转gif
            "|(.gif)" +

            "|(.GIF)" +

            ")(\\S){0,}";
    public static String videoSiteRegexGif = "^(http://)(\\S){0,}(" +

            //转gif
            "(.gif)" +

            "|(.GIF)" +

            ")(\\S){0,}";
    public static String videoSiteRegexJPG = "^(http://)(\\S){0,}(" +

            //转gif
            "(.jpg)" +

            "|(.JPG)" +

            ")(\\S){0,}";

    public static boolean isVideoUrl(String url) {
        if (isEmptyString(url)) {
            return false;
        }
        return url.matches(videoSiteRegex);
    }

    public static boolean isVideoUrlV2(String url) {
        if (isEmptyString(url)) {
            return false;
        }
        return url.matches(videoSiteRegexV2);
    }

    public static ArrayList<String> checkInnerVideo(String url) {
        ArrayList<String> videos = new ArrayList<String>();
        Video video = parserVideo(url, null);
        if (video == null || !video.hasVideo()) {
            LinkedHashSet<String> flashUrls = new LinkedHashSet<String>();
            try {
                Document document = Jsoup.connect(url).timeout(10000).get();
                parserObjectTagFlashUrls(flashUrls, document);
                parserEmbedTagFlashUrls(flashUrls, document);
                parserOtherSpecial(url, flashUrls, document);
                if (flashUrls != null && flashUrls.size() > 0) {
                    for (String flash : flashUrls) {
                        Video innerVideo = parserVideo(flash, null);
                        if (innerVideo != null && innerVideo.hasVideo()) {
                            videos.add(flash);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            videos.add(url);
        }
        return videos;
    }

    private static void parserOtherSpecial(String url, LinkedHashSet<String> flashUrls, Document document) {
        if (url.contains("cnbeta.com") && document.title().contains("视频")) {
            parserWeiphonePlayer(flashUrls, document);
        }
        parserYoukuIframe(flashUrls, document);
    }

    private static void parserYoukuIframe(LinkedHashSet<String> flashUrls, Document document) {
        Elements iframes = document.select("iframe[src^=http://player.youku]");
        if (iframes != null && iframes.size() > 0) {
            for (Element iframe : iframes) {
                String src = iframe.attr("src");
                String sid = regexCutString(src, new RegexCutRule[]{new RegexCutRule("embed/[\\w]{1,}\\W", 6, 1),
                        new RegexCutRule("embed/[\\w]{1,}$", 6, 0)});
                if (!isEmptyString(sid)) {
                    flashUrls.add("http://player.youku.com/player.php/sid/" + sid + "/v.swf");
                }
            }
        }
    }

    private static void parserWeiphonePlayer(LinkedHashSet<String> flashUrls, Document document) {
        Elements elements = document.getElementsByTag("script");
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                String weiPhonePlayer = regexCutString(element.toString(), new RegexCutRule[]{new RegexCutRule(
                        "weiphoneplayer[\\S]{1,}flashplayer[\\S]{1,}file[\\S]{1,}[\",;\\s]", 0, 0),});
                if (!isEmptyString(weiPhonePlayer)) {
                    String swf = regexCutString(weiPhonePlayer, new RegexCutRule[]{new RegexCutRule(
                            "flashplayer:\"[\\S&&[^,\";]]{1,}\\.swf\"", 13, 1),});
                    String file = regexCutString(weiPhonePlayer, new RegexCutRule[]{new RegexCutRule(
                            "file:\"[\\S&&[^,\";]]{1,}\\.mp4\"", 6, 1),});
                    if (!isEmptyString("swf") && !isEmptyString(file)) {
                        flashUrls.add(swf + "?file=" + file);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void parserObjectTagFlashUrls(LinkedHashSet<String> flashUrls, Document document) {
        Elements objects = document.select("object");
        if (objects != null) {
            for (Element element : objects) {
                Elements srcParams = element.select("param[name=src][value^=http]");
                Elements movieParams = element.select("param[name=movie][value^=http]");
                String data = element.attr("data");
                Elements flashvarsParams = element.select("param[name=flashvars]");
                String flashvars = "";
                if (flashvarsParams != null && flashvarsParams.size() > 0) {
                    flashvars = flashvarsParams.first().text();
                }
                if (srcParams != null && srcParams.size() > 0) {
                    String src = srcParams.first().attr("value");
                    if (!isEmptyString(src)) {
                        if (!src.contains("?") && !isEmptyString(flashvars)) {
                            src = src + "?" + flashvars;
                        }
                        flashUrls.add(URLDecoder.decode(src));
                    }
                }
                if (movieParams != null && movieParams.size() > 0) {
                    String movie = movieParams.first().attr("value");
                    if (!isEmptyString(movie)) {
                        if (!movie.contains("?") && !isEmptyString(flashvars)) {
                            movie = movie + "?" + flashvars;
                        }
                        flashUrls.add(URLDecoder.decode(movie));
                    }
                }
                if (!isEmptyString(data)) {
                    if (!data.contains("?") && !isEmptyString(flashvars)) {
                        data = data + "?" + flashvars;
                    }
                    flashUrls.add(URLDecoder.decode(data));
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void parserEmbedTagFlashUrls(LinkedHashSet<String> flashUrls, Document document) {
        Elements embeds = document.select("embed[type=application/x-shockwave-flash][src^=http]");
        if (embeds != null) {
            for (Element element : embeds) {
                String url = element.attr("src");
                String flashvars = element.attr("flashvars");
                if (!isEmptyString(url)) {
                    if (!isEmptyString(flashvars)) {
                        url = url + "?" + flashvars;
                    }
                    flashUrls.add(URLDecoder.decode(url));
                }
            }
        }
    }

    /**
     * 解析非内嵌视频
     *
     * @param url
     * @param platform
     * @return
     */
    public static Video parserVideo(String url, Platform platform) {
        try {
            if (platform == null) {
                platform = Platform.ANDROID;
            }
            if (url.contains("youku")) {
                return parserYoukuVideo(url, platform);
            } else if (url.contains("tudou")) {
                return parserTudouVideo(url, platform);
            } else if (url.contains("qiyi")) {
                return parserQiyiVideo(url, platform);
            } else if (url.contains("17173.tv.sohu")) {
                return parser17173Video(url, platform);
            } else if (url.contains("my.tv.sohu")) {
                return parserMySohuVideo(url, platform);
            } else if (url.contains("sohu")) {
                return parserSohuVideo(url, platform);
            } else if (url.contains("qq.com")) {
                return parserQQVideo(url, platform);
            } else if (url.contains("ku6")) {
                return parserKu6Video(url, platform);
            } else if (url.contains("pptv")) {
                return parserPPTVVideo(url, platform);
            } else if (url.contains("sina")) {
                return parserSinaVideo(url, platform);
            } else if (url.contains("56.com")) {
                return parser56ComVideo(url, platform);
            } else if (url.contains("163.com") || url.contains("126.net")) {
                return parser163Video(url, platform);
            } else if (url.contains("joy")) {
                return parserJoyVideo(url, platform);
            } else if (url.contains("letv")) {
                return parserLetvVideo(url, platform);
            } else if (url.contains("yinyuetai")) {
                return parserYinyuetaiVideo(url, platform);
            } else if (url.contains("kankan.com")) {
                return parserXunleiKankanVideo(url, platform);
            } else if (url.contains("baidu")) {
                return parserBaiduVideo(url, platform);
            } else if (url.contains("pps")) {
                return parserPPSVideo(url, platform);
            } else if (url.contains("ifeng")) {
                return parserIfengVideo(url, platform);
            } else if (url.contains("cntv") && !url.contains("xiyou")) {
                return parserCntvVideo(url, platform);
            } else if (url.contains("xiyou.cntv")) {
                return parserXiyouVideo(url, platform);
            } else if (url.contains("jstv.com")) {
                return parserJstvVideo(url, platform);
            } else if (url.contains("btv.com")) {
                return parserBtvVideo(url, platform);
            } else if (url.contains("v.iqilu.com")) {
                return parserIqiluVideo(url, platform);
            } else if (url.contains("tv.tom")) {
                return parserTvTomVideo(url, platform);
            } else if (url.contains("boosj")) {
                return parserBoosjVideo(url, platform);
            } else if (url.contains("acfun")) {
                return parserAcfunVideo(url, platform);
            } else if (url.contains("bilibili") || url.contains("static.hdslb.com")) {
                return parserBilibiliVideo(url, platform);
            } else if (url.contains("kankanews.com") || (url.contains("smgbb.cn") && url.contains(".swf"))) {
                return parserKankannewsVideo(url, platform);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Video parserKankannewsVideo(String url, Platform platform) {
        String xmlid = "";
        if (url.contains(".swf")) {
            xmlid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("xmlid=vxml/[\\S&&[^&,\"]]{4,}&", 11, 1),
                    new RegexCutRule("xmlid=vxml/[\\S&&[^&,\"]]{4,}$", 11, 0)});
        } else {
            try {
                int index = url.substring(0, url.lastIndexOf("/")).lastIndexOf("/");
                xmlid = url.substring(index + 1, url.lastIndexOf("."));
            } catch (Exception e) {
            }
        }
        if (!isEmptyString(xmlid)) {
            Video video = new Video();
            video.site = "kankannews";
            // swf
            video.setSwf("http://video1.kksmg.com/experience/bootstrap.swf?publisherId=88000&playerId=76071766572662785&apiDomain=apivideo.kankanews.com&xmlid=vxml/"
                    + xmlid);
            // sef end
            return video;
        }
        return null;
    }

    private static Video parserBilibiliVideo(String url, Platform platform) {
        String aid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("video/av[\\d]{4,}\\D", 8, 1),
                new RegexCutRule("video/av[\\d]{4,}$", 8, 0), new RegexCutRule("aid=[\\d]{4,}$", 4, 0),
                new RegexCutRule("aid=[\\d]{4,}\\D", 4, 1)});
        Video video = new Video();
        video.site = "bilibili";
        if (!isEmptyString(aid)) {
            // swf
            video.setSwf("http://static.hdslb.com/miniloader.swf?aid=" + aid);
            // swf end
        }
        return video;
    }

    private static Video parserAcfunVideo(String url, Platform platform) {
        String swfId = url.substring(url.lastIndexOf("ac") + 2, url.length());
        String videoid = "";
        try {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("videoid", new RegexCutRule[]{new RegexCutRule("\\[video\\][\\d]{4,}\\[/video\\]", 7, 8),
                    new RegexCutRule("\\[Video\\][\\d]{4,}\\[/Video\\]", 7, 8)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                videoid = returnMap.get("videoid");
            }
        } catch (Exception e) {
        }
        if (isEmptyString(videoid)) {
            videoid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\d]{4,}$", 4, 0),
                    new RegexCutRule("vid=[\\d]{4,}\\D", 4, 1)});
        }
        if (!isEmptyString(videoid)) {
            HashMap<String, RegexCutRule[]> vRegexMap = new HashMap<String, RegexCutRule[]>();
            vRegexMap.put("vtype", new RegexCutRule[]{new RegexCutRule("\"vtype\":\"[\\w]{2,}\"", 9, 1)});
            vRegexMap.put("cid", new RegexCutRule[]{new RegexCutRule("\"cid\":\"[\\w]{2,}\"", 7, 1)});
            HashMap<String, String> vReturnMap = regexCutStringFromUrlContent(
                    "http://www.acfun.tv/api/getVideoByID.aspx?vid=" + videoid, vRegexMap);
            if (!isEmptyMap(vReturnMap)) {
                String vtype = vReturnMap.get("vtype");
                String cid = vReturnMap.get("cid");
                if (!isEmptyString(vtype) && !isEmptyString(cid)) {
                    Video video = new Video();
                    if (vtype.contains("youku")) {
                        video = paserYoukuVideoById(cid, platform);
                    } else if (vtype.contains("tudou")) {
                        video = paserTudouVideoById(cid, url, platform);
                    } else if (vtype.contains("qiyi")) {
                        video = paserQiyiVideoById(cid, platform);
                    } else if (vtype.contains("17173")) {
                        video = parser17173Video("Flvid=" + cid, platform);
                    } else if (vtype.contains("sohu") && vtype.contains("my")) {
                        video = parserMySohuVideo("vid=" + cid, platform);
                    } else if (vtype.contains("sohu")) {
                        video = parserSohuVideo("vid=" + cid, platform);
                    } else if (vtype.contains("qq")) {
                        video = parserQQVideo("vid=" + cid, platform);
                    } else if (vtype.contains("ku6")) {
                        video = parserKu6Video("vid=" + cid, platform);
                    } else if (vtype.contains("sina")) {
                        video = parserSinaVideo("vid=" + cid, platform);
                    } else if (vtype.contains("56")) {
                        video = parser56ComVideo("vid=" + cid, platform);
                    } else if (vtype.contains("163") || url.contains("126")) {
                        video = parser163Video("vid=" + cid, platform);
                    } else if (vtype.contains("joy")) {
                        video = parserJoyVideo("id=" + cid, platform);
                    } else if (vtype.contains("letv")) {
                        video = parserLetvVideo("id=" + cid, platform);
                    }
                    if (video != null) {
                        // swf
                        if (!isEmptyString(swfId)) {
                            video.setSwf("http://www.acfun.tv/player/ac" + swfId);
                        }
                        // swf end
                        video.site = "acfun";
                        return video;
                    }
                }
            }
        }
        return null;
    }

    private static Video parserBoosjVideo(String url, Platform platform) {
        if (url.contains(".swf")) {
            try {
                HttpURLConnection openConnection = (HttpURLConnection) new URL(url).openConnection();
                openConnection.getContentLength();
                url = openConnection.getURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String id = regexCutString(url, new RegexCutRule[]{new RegexCutRule("\\.com/[\\d]{4,}\\.htm", 5, 4),
                new RegexCutRule("vid=[\\d]{4,}\\D", 4, 1), new RegexCutRule("vid=[\\d]{4,}$", 4, 0)});
        if (!isEmptyString(id)) {
            try {
                Document document = Jsoup.connect("http://www.boosj.com/" + id + ".xml").timeout(5000).get();
                String file = document.getElementsByTag("file").first().text();
                if (!isEmptyString(file)) {
                    String pic = "";
                    try {
                        pic = document.getElementsByTag("pic").first().text();
                    } catch (Exception e) {
                    }
                    Video video = new Video();
                    video.site = "boosj";
                    video.img = pic;
                    if (file.endsWith(".mp4")) {
                        video.addVideoUrl(file);
                    } else if (platform == Platform.ANDROID) {
                        video.addVideoUrl(file);
                    }
                    return video;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private static Video parserTvTomVideo(String url, Platform platform) {
        String mp4 = "";
        if (url.contains(".swf")) {
            mp4 = regexCutString(url, new RegexCutRule[]{new RegexCutRule("video=[\\S&&[^&,]]{4,}\\.flv", 6, 0)});
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("mp4", new RegexCutRule[]{new RegexCutRule("mp4 =\"http[\\S&&[^\'\",]]{4,}\\.mp4\"", 6, 1)});
            regexMap.put("video_url", new RegexCutRule[]{new RegexCutRule(
                    "video_url=\"http[\\S&&[^\'\",]]{4,}\\.flv\"", 11, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                mp4 = returnMap.get("mp4");
                if (isEmptyString(mp4)) {
                    mp4 = returnMap.get("video_url");
                }
            }
        }
        if (!isEmptyString(mp4)) {
            mp4 = URLDecoder.decode(mp4);
            if (mp4.contains("clip.uhoop") && mp4.contains("hd")) {
                mp4 = mp4.replace(".flv", ".mp4");
            }
            Video video = new Video();
            video.site = "tvtom";
            video.addVideoUrl(mp4);
            return video;
        }
        return null;
    }

    private static Video parserIqiluVideo(String url, Platform platform) {
        String id = regexCutString(url, new RegexCutRule[]{new RegexCutRule("\\/[\\d]{4,}\\.shtm", 1, 5),
                new RegexCutRule("\\/[\\d]{4,}\\.htm", 1, 4), new RegexCutRule("\\/[\\d]{4,}\\.swf", 1, 4)});
        if (!isEmptyString(id)) {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("mp4", new RegexCutRule[]{new RegexCutRule(
                    "\"value\":\\{\"url\":\"http[\\S&&[^\"]]{4,}\"\\}", 16, 2)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(
                    "http://asi.iqilu.com/video/getMediaUrl?id=" + id, regexMap);
            if (!isEmptyMap(returnMap)) {
                Video video = new Video();
                String mp4 = returnMap.get("mp4");
                if (!isEmptyString(mp4)) {
                    video.site = "iqilu";
                    mp4 = mp4.replace("\\", "");
                    mp4 = "http://stream.iqilu.com/" + mp4.substring(mp4.indexOf("vod"));
                    video.addVideoUrl(mp4.replace("\\", ""));
                }
                return video;
            }
        }
        return null;
    }

    private static Video parserBtvVideo(String url, Platform platform) {
        if (url.contains("btv.com.cn/video")) {
            String vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("video\\/VIDE[\\d]{4,}\\D", 6, 1),
                    new RegexCutRule("video\\/VIDE[\\d]{4,}$", 6, 0)});
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("mp4", new RegexCutRule[]{new RegexCutRule("http:[\\w\\/\\.\\_[^\\S]]{4,}\\.mp4", 0, 0)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(
                    "http://space.btv.com.cn/flvPlayer/playcfg/flv_infoforbtv.jsp?id=" + vid, regexMap);
            if (!isEmptyMap(returnMap)) {
                Video video = new Video();
                video.site = "btv";
                video.img = "http://img.space.btv.com.cn/flv_images/image/" + vid + ".jpg";
                video.addVideoUrl(returnMap.get("mp4"));
                return video;
            }
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("mp4", new RegexCutRule[]{new RegexCutRule(
                    "var myPlay = \" http:[\\w\\/\\.\\_[^\\S]]{4,}\\.mp4\"", 15, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                Video video = new Video();
                video.site = "btv";
                video.addVideoUrl(returnMap.get("mp4"));
                return video;
            }
        }
        return null;
    }

    private static Video parserJstvVideo(String url, Platform platform) {
        HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
        regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("fo\\.addVariable\\(\"vid\",\"[\\d]{4,}\\D", 22, 1)});
        HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
        if (!isEmptyMap(returnMap)) {
            String vid = returnMap.get("vid");
            if (!isEmptyString(vid)) {
                try {
                    Document document = Jsoup
                            .connect("http://playerwebservice.jstv.com/video.asmx/GetVideo?ip=&url=&vid=" + vid)
                            .timeout(5000).get();
                    Video video = new Video();
                    video.site = "jstv";
                    if (platform == Platform.ANDROID) {
                        video.addVideoUrl(document.getElementsByTag("videoplayurl").first().text());
                    }
                    video.img = document.getElementsByTag("smallposterurl").first().text();
                    return video;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Video parser17173Video(String url, Platform platform) {
        String Flvid = "";
        if (url.contains("Flvid=")) {
            Flvid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("Flvid=[\\d]{4,}\\D", 6, 1),
                    new RegexCutRule("Flvid=[\\d]{6,}$", 4, 0)});
        } else {
            // id:'1609714'
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("Flvid", new RegexCutRule[]{new RegexCutRule("Flvid=[\\d]{4,}\\D", 6, 1),
                    new RegexCutRule("id\\:\\'[\\d]{4,}\\'", 4, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                Flvid = returnMap.get("Flvid");
            }
        }
        if (!isEmptyString(Flvid)) {
            try {
                Document document = Jsoup.connect("http://17173.tv.sohu.com/port/pconfig_r.php?id=" + Flvid)
                        .timeout(5000).get();
                String MD5 = document.getElementsByTag("MD5").first().text();
                if (!isEmptyString(MD5)) {
                    String[] md5s = MD5.split("\\|\\|");
                    Video video = new Video();
                    // swf
                    video.setSwf("http://17173.tv.sohu.com/playercs2008.swf?Flvid=" + Flvid);
                    // swf end
                    video.site = "17173";
                    for (String mp4 : md5s) {
                        if (mp4.matches("^http[\\S]{1,}mp4$")) {
                            video.addVideoUrl(mp4);
                        }
                    }
                    String link = document.getElementsByTag("Link").first().text();
                    if (isEmptyString(link)) {
                        link = document.getElementsByTag("Link").first().nextSibling().toString();
                    }
                    if (!isEmptyString(link)) {
                        document = Jsoup.connect("http://share.v.t.qq.com/index.php?c=share&a=index&url=" + link)
                                .timeout(5000).get();
                        video.img = document.getElementById("video_minipic").attr("src");
                    }
                    return video;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static Video parserXiyouVideo(String url, Platform platform) {
        String videoId = regexCutString(url, new RegexCutRule[]{new RegexCutRule(
                "v-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}\\W", 2, 1)});
        if (!isEmptyString(videoId)) {
            Video video = new Video();
            video.site = "cntv";
            // swf
            video.setSwf("http://player.xiyou.cntv.cn/" + videoId + ".swf");
            // swf end
            return video;
        }
        return null;
    }

    private static Video parserCntvVideo(String url, Platform platform) {
        if (url.contains(".swf")) {
            Video video = new Video();
            video.site = "cntv";
            video.swf = url;
        }
        return null;
    }

    private static Video parserIfengVideo(String url, Platform platform) {
        String guid = regexCutString(url, new RegexCutRule[]{
                new RegexCutRule("/[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}\\.shtm", 1, 5),
                new RegexCutRule("/[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}\\.htm", 1, 4),
                new RegexCutRule("#[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}\\W", 1, 1),
                new RegexCutRule("#[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}$", 1, 0),
                new RegexCutRule("guid=[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}\\W", 5, 1),
                new RegexCutRule("guid=[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}-[\\w]{4,}$", 5, 0)});
        if (!isEmptyString(guid)) {
            Video video = new Video();
            // swf
            if (!isEmptyString(guid)) {
                video.setSwf("http://v.ifeng.com/include/exterior.swf?guid=" + guid);
            }
            // swf end
            video.site = "ifeng";
            return video;
        }
        return null;
    }

    private static Video parserPPSVideo(String url, Platform platform) {
        String sid = "";
        if (url.contains(".swf")) {
            sid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("sid/[\\w]{3,}\\W", 4, 1),
                    new RegexCutRule("sid=[\\w]{3,}\\W", 4, 1), new RegexCutRule("sid=[\\w]{3,}$", 4, 0)});
            if (!isEmptyString(sid)) {
                url = "http://v.pps.tv/play_" + sid + ".html";
            }
        }
        if (isEmptyString(sid)) {
            sid = regexCutString(url, new RegexCutRule("play_[\\w]{3,}\\W", 5, 1));
        }
        Video video = new Video();
        // swf
        if (!isEmptyString(sid)) {
            video.setSwf("http://player.pps.tv/player/sid/" + sid + "/v.swf");
        }
        // swf end
        video.site = "pps";
        return video;
    }

    private static Video parserBaiduVideo(String url, Platform platform) {
        String vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("v_id=[\\w]{3,}\\W", 5, 1),
                new RegexCutRule("v_id=[\\w]{3,}$", 5, 0), new RegexCutRule("vid=[\\w]{3,}\\W", 4, 1),
                new RegexCutRule("vid=[\\w]{3,}$", 4, 0)});
        if (!isEmptyString(vid)) {
            try {
                Document document = Jsoup.connect(url).timeout(5000).get();
                String src = document.getElementsByTag("embed").first().attr("src");
                if (!isEmptyString(src) && !src.contains("mv.baidu.com")) {
                    return parserVideo(src, platform);
                }
            } catch (Exception e) {
            }
            Video video = new Video();
            video.site = "tieba";
            video.img = "http://mvimg.baidu.com/snap/" + vid;
            if (platform == Platform.ANDROID) {
                video.addVideoUrl("http://v98.mvideo.baidu.com/video/" + vid);
            }
            return video;
        }
        return null;
    }

    private static Video parserXunleiKankanVideo(String url, Platform platform) {
        String gcid = "";
        HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
        regexMap.put("gcid", new RegexCutRule[]{new RegexCutRule(
                "http://pubnet\\.sandai\\.net[\\S&&[^,]&&[^;]&&[^\"]]{2,}\\.flv", 24, 0)});
        HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
        if (!isEmptyMap(returnMap)) {
            gcid = returnMap.get("gcid");
            gcid = gcid.substring(gcid.indexOf("/") + 1);
            gcid = gcid.substring(gcid.indexOf("/") + 1);
            gcid = gcid.substring(0, gcid.indexOf("/"));
        }
        if (!isEmptyString(gcid)) {
            Video video = new Video();
            // swf
            video.setSwf("http://video.xunlei.com/dt/swf/v.swf?gcid=" + gcid);
            // swf end
            video.site = "kankan";
        }
        return null;
    }

    private static Video parserYinyuetaiVideo(String url, Platform platform) {
        if (url.contains(".swf")) {
            if (url.contains("playlist")) {
                String playlistId = regexCutString(url, new RegexCutRule[]{
                        new RegexCutRule("playlistId=[\\d]{4,}\\D", 11, 1),
                        new RegexCutRule("playlistId=[\\d]{4,}$", 11, 0),
                        new RegexCutRule("playlist\\/player\\/[\\d]{4,}\\/", 16, 1)});
                if (!isEmptyString(playlistId)) {
                    url = "http://www.yinyuetai.com/playlist/" + playlistId;
                }
            } else {
                String videoId = regexCutString(url, new RegexCutRule[]{
                        new RegexCutRule("videoId=[\\d]{4,}\\D", 8, 1), new RegexCutRule("videoId=[\\d]{4,}$", 8, 0),
                        new RegexCutRule("video\\/player\\/[\\d]{4,}\\/", 13, 1)});
                if (!isEmptyString(videoId)) {
                    url = "http://www.yinyuetai.com/video/" + videoId;
                }
            }
        }
        if (url.contains("playlist")) {
            Video video = new Video();
            video.site = "yinyuetai";
            // swf
            String id = regexCutString(url, new RegexCutRule[]{new RegexCutRule("playlist\\/[\\d]{4,}\\D", 9, 1),
                    new RegexCutRule("playlist\\/[\\d]{4,}$", 9, 0),
                    new RegexCutRule("playlist\\/player\\/[\\d]{4,}\\/", 16, 1)});
            video.setSwf("http://player.yinyuetai.com/playlist/player/" + id + "/v_0.swf");
            // swf end
            return video;
        } else {
            Video video = new Video();
            video.site = "yinyuetai";
            // swf
            String id = regexCutString(url, new RegexCutRule[]{new RegexCutRule("video\\/[\\d]{4,}\\D", 6, 1),
                    new RegexCutRule("video\\/[\\d]{4,}$", 6, 0),
                    new RegexCutRule("video\\/player\\/[\\d]{4,}\\/", 13, 1)});
            video.setSwf("http://player.yinyuetai.com/video/player/" + id + "/v_0.swf");
            // swf end
            return video;
        }
    }

    private static Video parserLetvVideo(String url, Platform platform) {
        Video video = new Video();
        video.site = "letv";
        String vid = "";
        if (url.contains("id=")) {
            vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("id=[\\d]{4,}\\D", 3, 1),
                    new RegexCutRule("id=[\\d]{4,}$", 3, 0)});
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid:[\\d]{4,}\\D", 4, 1),
                    new RegexCutRule("vid:[\\d]{4,}$", 4, 0)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                vid = returnMap.get("vid");
            }
        }
        // swf
        if (!isEmptyString(vid)) {
            video.setSwf("http://i7.imgs.letv.com/player/swfPlayer.swf?id=" + vid);
        }
        // swf end
        return video;
    }

    private static Video parserJoyVideo(String url, Platform platform) {
        String vid = "";
        Video video = new Video();
        video.site = "joy";
        if (url.contains(".swf")) {
            if (!url.contains("id=")) {
                try {
                    URL url2 = new URL(url);
                    HttpURLConnection openConnection = (HttpURLConnection) url2.openConnection();
                    openConnection.getContentLength();
                    url = openConnection.getURL().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("strvid=[\\d]{4,}\\D", 7, 1),
                    new RegexCutRule("strvid=[\\d]{4,}$", 7, 0)});
            // swf
            if (!isEmptyString(vid)) {
                video.setSwf("http://client.joy.cn/flvplayer/" + vid + "_1_0_1.swf");
            }
            // swf end
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("videoId:\"[\\d]{4,}\\D", 9, 1),
                    new RegexCutRule("videoId:\"[\\d]{4,}$", 9, 0)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                vid = returnMap.get("vid");
                // swf
                if (!isEmptyString(vid)) {
                    video.setSwf("http://client.joy.cn/flvplayer/" + vid + "_1_0_1.swf");
                }
                // swf end
            }
        }
        return video;
    }

    /**
     * 网易视频
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parser163Video(String url, Platform platform) {

        Video video = new Video();
        video.site = "163";

        if (!url.contains(".swf")) {
            String vid = "", sid = "";
            if (url.matches("\\S{1,}\\/[\\w]{8,}[\\/||\\_][\\w]{8,}\\.html")) {
                String vid_sid = regexCutString(url, new RegexCutRule("\\/[\\w]{8,}[\\/||\\_][\\w]{8,}\\.html", 1, 5));
                if (!isEmptyString(vid_sid)) {
                    int i = vid_sid.indexOf("/");
                    if (i <= 0) {
                        i = vid_sid.indexOf("_");
                    }
                    if (i > 0) {
                        sid = vid_sid.substring(0, i);
                        vid = vid_sid.substring(i + 1, vid_sid.length());
                    }
                }
            }
            HashMap<String, RegexCutRule[]> iRegexMap = new HashMap<String, RegexCutRule[]>();
            iRegexMap.put("topicid", new RegexCutRule[]{new RegexCutRule("topicid=[\\d]{2,}\\D", 8, 1),
                    new RegexCutRule("topicid=[\\d]{2,}$", 8, 0), new RegexCutRule("topicid : \"[\\d]{2,}\"", 11, 1)});
            iRegexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid=[\\w]{6,}\\W", 4, 1),
                    new RegexCutRule("vid=[\\w]{6,}$", 4, 0),
                    new RegexCutRule("thisMovieInfo\\.id = \'[\\w]{6,}\'", 20, 1)});

            iRegexMap.put("sid", new RegexCutRule[]{new RegexCutRule("sid=[\\w]{6,}\\W", 4, 1),
                    new RegexCutRule("sid=[\\w]{6,}$", 4, 0), new RegexCutRule("thisPlay\\.id = \'[\\w]{6,}\'", 15, 1),
                    new RegexCutRule("sid : \"[\\w]{6,}\"", 7, 1)});
            HashMap<String, String> iReturnMap = regexCutStringFromUrlContent(url, iRegexMap);
            if (!isEmptyMap(iReturnMap) || (!isEmptyString(vid) && !isEmptyString(sid))) {
                if (!isEmptyString(iReturnMap.get("vid"))) {
                    vid = iReturnMap.get("vid");
                }
                String topicid = iReturnMap.get("topicid");
                if (!isEmptyString(iReturnMap.get("sid"))) {
                    sid = iReturnMap.get("sid");
                }
                if (!isEmptyString(vid)) {
                    if (!isEmptyString(sid) && !isEmptyString(topicid)) {
                        // swf
                        video.setSwf("http://v.163.com/swf/video/NetEaseFlvPlayerV3.swf?topicid=" + topicid + "&vid="
                                + vid + "&sid=" + sid);
                        // end swf
                    }
                }
            }
        }

        return video;
    }

    /**
     * 56网
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parser56ComVideo(String url, Platform platform) {
        String vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("\\/v_[\\w]{6,}\\.htm", 3, 4),
                new RegexCutRule("vid-[\\w]{6,}\\.htm", 4, 4), new RegexCutRule("id-[\\w]{6,}\\.htm", 3, 4),
                new RegexCutRule("\\/v_[\\w]{6,}\\.swf", 3, 4), new RegexCutRule("\\/cpm_[\\w]{6,}\\.swf", 5, 4),
                new RegexCutRule("vid=[\\w]{6,}\\W", 4, 1), new RegexCutRule("vid=[\\w]{6,}$", 4, 0)});
        if (!isEmptyString(vid)) {
            Video video = new Video();
            video.site = "56";
            // swf
            video.setSwf("http://player.56.com/v_" + vid + ".swf");
            // swf end
            return video;
        }
        return null;
    }

    /**
     * 新浪视频
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserSinaVideo(String url, Platform platform) {
        String vid = "";
        if (url.contains("vid=") || url.contains(".swf")) {
            if (!url.contains("vid=")) {
                try {
                    URL url2 = new URL(url);
                    HttpURLConnection openConnection = (HttpURLConnection) url2.openConnection();
                    openConnection.getContentLength();
                    url = openConnection.getURL().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\d]{6,}\\D", 4, 1),
                    new RegexCutRule("vid=[\\d]{6,}$", 4, 0)});
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid=[\\d]{6,}\\D", 4, 1),
                    new RegexCutRule("vid=[\\d]{6,}$", 4, 0), new RegexCutRule("\"vid\",\\s[\\d]{6,}\\D", 7, 1),
                    new RegexCutRule("\"vid\",\\s[\\d]{6,}$", 7, 0), new RegexCutRule("\"vid\",[\\d]{6,}\\D", 6, 1),
                    new RegexCutRule("\"vid\",[\\d]{6,}$", 6, 0), new RegexCutRule("vid:\'[\\d]{6,}\\D", 5, 1),
                    new RegexCutRule("vid:\'[\\d]{6,}$", 5, 0), new RegexCutRule("play-data=\"[\\d]{6,}\\D", 11, 1),
                    new RegexCutRule("playVideo\\(\"[\\d]{6,}\"", 11, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                vid = returnMap.get("vid");
            }
        }
        if (!isEmptyString(vid)) {
            Video video = new Video();
            video.site = "sina";
            // swf
            video.setSwf("http://you.video.sina.com.cn/api/sinawebApi/outplayrefer.php/s.swf&vid=" + vid);
            // swf end
            return video;
        }
        return null;
    }

    /**
     * PPTV
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserPPTVVideo(String url, Platform platform) {
        String swfId = "";
        if (url.contains(".swf")) {
            swfId = regexCutString(url, new RegexCutRule("\\/[\\w]{2,}\\.swf", 1, 4));
            url = "http://v.pptv.com/show/" + swfId + ".html";
        } else {
            swfId = regexCutString(url, new RegexCutRule[]{new RegexCutRule("\\/[\\w]{2,}\\.html", 1, 5),
                    new RegexCutRule("%2F[\\w]{2,}\\.html", 3, 5), new RegexCutRule("%2f[\\w]{2,}\\.html", 3, 5)});
            url = "http://v.pptv.com/show/" + swfId + ".html";
        }
        Video video = new Video();
        video.site = "pptv";
        // swf
        if (!isEmptyString(swfId)) {
            video.setSwf("http://player.pptv.com/v/" + swfId + ".swf");
        }
        // end swf
        return video;
    }

    /**
     * KU6网
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserKu6Video(String url, Platform platform) {
        String vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\w\\.]{2,}&", 4, 1),
                new RegexCutRule("vid=[\\w\\.]{2,}$", 4, 0), new RegexCutRule("\\/[^\\/&&[^\\s]]{2,}\\.htm", 1, 4),
                new RegexCutRule("\\/[^\\/&&[^\\s]]{2,}\\.\\/v\\.swf", 1, 6)});
        if (!isEmptyString(vid)) {
            Video video = new Video();
            video.site = "ku6";
            // swf
            video.setSwf("http://player.ku6.com/refer/" + vid + "/v.swf");
            // swf end
            return video;
        }
        return null;
    }

    /**
     * 腾讯视频
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserQQVideo(String url, Platform platform) {
        String vid = "";
        if (url.contains(".swf") || url.contains("vid=")) {
            vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\w]{2,}\\W", 4, 1),
                    new RegexCutRule("vid=[\\w]{2,}$", 4, 0)});
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid:\"[\\w]{2,}\"", 5, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                vid = returnMap.get("vid");
            }
        }
        if (!isEmptyString(vid)) {
            Video video = new Video();
            video.img = "http://shp.qpic.cn/qqvideo/0/" + vid + "/400";
            video.site = "QQ";
            // swf
            video.setSwf("http://static.video.qq.com/TPout.swf?vid=" + vid);
            // sef end
            return video;
        }
        return null;
    }

    /**
     * 搜狐视频空间
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserMySohuVideo(String url, Platform platform) {
        String id = "";
        if (url.contains("user/detail")) {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid=\"[\\d]{7,}\"", 5, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                id = returnMap.get("vid");
            }
        } else {
            id = regexCutString(url, new RegexCutRule[]{new RegexCutRule("/[\\d]{8,}\\.", 1, 1),
                    new RegexCutRule("/[\\d]{8,}$", 1, 0), new RegexCutRule("id=[\\d]{8,}", 3, 0)});
        }
        if (!isEmptyString(id)) {
            Video video = new Video();
            video.site = "sohu";
            // swf
            if (!isEmptyString(id)) {
                video.setSwf("http://share.vrs.sohu.com/my/v.swf&autoplay=false&id=" + id + "&skinNum=1&topBar=1&xuid=");
            }
            // swf end
            return video;
        }
        return null;
    }

    /**
     * 搜狐视频
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserSohuVideo(String url, Platform platform) {
        String vid = null;
        if (url.contains(".swf") || url.contains("vid")) {
            vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\d]{3,}\\D", 4, 1),
                    new RegexCutRule("vid=[\\d]{3,}$", 4, 0), new RegexCutRule("sohu\\.com/[\\d]{3,}/", 9, 1)});
            if ((isEmptyString(vid)) && url.contains(".swf") && url.contains("id")) {
                return parserMySohuVideo(url, platform);
            }
        } else if (url.contains("m.tv.sohu")) {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("vid", new RegexCutRule[]{new RegexCutRule("vid = \"[\\d]{4,}\"", 7, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                vid = returnMap.get("vid");
            }
        }
        Video video = new Video();
        video.site = "sohu";
        // swf
        if (!isEmptyString(vid)) {
            video.setSwf("http://share.vrs.sohu.com/" + vid + "/v.swf");
        }
        // swf end
        return video;
    }

    /**
     * 爱奇艺
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserQiyiVideo(String url, Platform platform) {
        String tvId = "";
        String videoId = "";
        if (url.contains("tvId")) {
            tvId = regexCutString(url, new RegexCutRule[]{new RegexCutRule("(tvId=)(\\d){2,}$", 5, 0),
                    new RegexCutRule("(tvId=)(\\d){2,}\\D", 5, 1), new RegexCutRule("(tvid=)(\\d){2,}$", 5, 0),
                    new RegexCutRule("(tvid=)(\\d){2,}\\D", 5, 1)});
            videoId = regexCutString(url, new RegexCutRule[]{new RegexCutRule("vid=[\\w]{2,}&", 4, 1),
                    new RegexCutRule("vid=[\\w]{2,}$", 4, 0), new RegexCutRule("qiyi\\.com/(\\w){2,}/", 9, 1)});
        } else {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("tvId", new RegexCutRule[]{new RegexCutRule("(\"tvId\":\"[\\d]{2,}\")", 8, 1),
                    new RegexCutRule("(tvid=\"[\\d]{2,}\")", 6, 1)});
            regexMap.put("videoId", new RegexCutRule[]{new RegexCutRule("\"videoId\":\"[\\w]{2,}\"", 11, 1),
                    new RegexCutRule("videoid=\"[\\w]{2,}\"", 9, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(url, regexMap);
            if (!isEmptyMap(returnMap)) {
                tvId = returnMap.get("tvId");
                videoId = returnMap.get("videoId");
            }
        }
        Video video = paserQiyiVideoById(tvId, platform);
        if (!isEmptyString(videoId)) {
            // swf
            video.setSwf("http://player.video.qiyi.com/" + videoId + ".swf");
            // swf end
        } else if ((url.startsWith("http://player.video.qiyi.com") || url.contains("vid=")) && url.contains(".swf")) {
            // swf
            video.setSwf(url);
            // swf end
        }
        return video;
    }

    private static Video paserQiyiVideoById(String tvId, Platform platform) {
        if (isEmptyString(tvId)) {
            return null;
        }
        Video video = new Video();
        video.site = "iqiyi";
        String m4u = "";
        HashMap<String, RegexCutRule[]> protocolRegexMap = new HashMap<String, RegexCutRule[]>();
        protocolRegexMap.put("m4u",
                new RegexCutRule[]{new RegexCutRule("\"m4u\":\"http:[\\w\\/\\.]{1,}.mp4\"", 7, 1)});
        HashMap<String, String> protocolReturnMap = regexCutStringFromUrlContent("http://cache.m.iqiyi.com/qmt/" + tvId
                + "/", protocolRegexMap);
        if (!isEmptyMap(protocolReturnMap)) {
            m4u = protocolReturnMap.get("m4u");
        }
        String videoUrl = "";
        if (!isEmptyString(m4u)) {
            HashMap<String, RegexCutRule[]> regexMap = new HashMap<String, RegexCutRule[]>();
            regexMap.put("videoUrl", new RegexCutRule[]{new RegexCutRule(
                    "data:\\{\"l\":\"http:[\\w\\/\\.]{1,}.mp4\\?", 11, 1)});
            HashMap<String, String> returnMap = regexCutStringFromUrlContent(m4u + "?v=875351784", regexMap);
            if (!isEmptyMap(returnMap)) {
                videoUrl = returnMap.get("videoUrl");
            }
        }
        if (!isEmptyString(videoUrl)) {
            video.addVideoUrl(videoUrl);
        }
        return video;
    }

    /**
     * 土豆网
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserTudouVideo(String url, Platform platform) {
        Video video = null;
        String pic = "";
        if (url.contains(".swf")) {
            try {
                URL url2 = new URL(url);
                HttpURLConnection openConnection = (HttpURLConnection) url2.openConnection();
                openConnection.getContentLength();
                url = openConnection.getURL().toString();
                String iid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("(iid=)(\\d){1,}\\D", 4, 1),
                        new RegexCutRule("(iid=)(\\d){1,}$", 4, 0)});
                String vcode = regexCutString(url, new RegexCutRule[]{new RegexCutRule("(youkuId=)(\\w){1,}&", 8, 1),
                        new RegexCutRule("(vcode=)(\\w){1,}&", 6, 1), new RegexCutRule("(youkuId=)(\\w){1,}&", 8, 0),
                        new RegexCutRule("(vcode=)(\\w){1,}&", 6, 0)});
                video = paserTudouVideoById(iid, url, platform);
                if (!isEmptyString(vcode)) {
                    video = paserYoukuVideoById(vcode, platform);
                } else if (!isEmptyString(iid)) {
                    video = paserTudouVideoById(iid, url, platform);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Document document = Jsoup
                        .connect(url)
                        .userAgent(
                                "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
                        .get();
                String data = document.toString();
                String vcode = regexCutString(data, new RegexCutRule[]{
                        new RegexCutRule("(vcode: \")(\\w){1,}\"", 8, 1),
                        new RegexCutRule("(vcode:\")(\\w){1,}\"", 7, 1)});
                String iid = regexCutString(data, new RegexCutRule[]{new RegexCutRule("(iid: )(\\d){1,}\\D", 5, 1),
                        new RegexCutRule("(iid:)(\\d){1,}\\D", 4, 1)});
                pic = regexCutString(data, new RegexCutRule[]{new RegexCutRule("(pic: \")(\\w){1,}\"", 6, 1),
                        new RegexCutRule("(pic:\")(\\w){1,}\"", 5, 1)});
                if (!isEmptyString(vcode)) {
                    video = paserYoukuVideoById(vcode, platform);
                } else if (!isEmptyString(iid)) {
                    video = paserTudouVideoById(iid, url, platform);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (video.hasVideo()) {
            if (isEmptyString(video.img) && !isEmptyString(pic)) {
                video.img = pic;
            }
            return video;
        } else {
            return null;
        }
    }

    private static Video paserTudouVideoById(String iid, String url, Platform platform) {
        if (isEmptyString(iid)) {
            return null;
        }
        Video video = new Video();
        video.site = "tudou";
        // swf
        if (url.startsWith("http://www.tudou.com")) {
            try {
                String sub = "";
                if (url.startsWith("http://www.tudou.com/programs/view")) {
                    sub = url.substring(url.indexOf("programs/") + 9, url.length());
                } else {
                    sub = url.substring(url.indexOf(".com/") + 5, url.length());
                }
                String lid = sub.substring(0, 1);
                String subSub = sub.substring(sub.indexOf("/") + 1, sub.length());
                String resid = subSub.substring(0, subSub.indexOf("/"));
                video.setSwf("http://www.tudou.com/" + lid + "/" + resid + "/&iid=" + iid + "/v.swf");
            } catch (Exception e) {
            }
        } else if (url.contains(".swf") && url.contains("rpid=") && url.contains("resourceId=")) {
            video.setSwf(url);
        }
        // swf end
        return video;
    }

    /**
     * 优酷网
     *
     * @param url
     * @param platform
     * @return
     */
    private static Video parserYoukuVideo(String url, Platform platform) {
        String vid = regexCutString(url, new RegexCutRule[]{new RegexCutRule("VideoIDS=[\\w&&[^_]]{3,}&", 9, 1),
                new RegexCutRule("VideoIDS=[\\w&&[^_]]{3,}$", 9, 0), new RegexCutRule("id_[\\w&&[^_]]{3,}.htm", 3, 4),
                new RegexCutRule("sid/[\\w&&[^_]]{3,}/", 4, 1), new RegexCutRule("sid/[\\w&&[^_]]{3,}$", 4, 0),
                new RegexCutRule("embed/[\\w]{1,}\\W", 6, 1), new RegexCutRule("embed/[\\w]{1,}$", 6, 0)});
        return paserYoukuVideoById(vid, platform);
    }

    private static Video paserYoukuVideoById(String vid, Platform platform) {
        if (isEmptyString(vid)) {
            return null;
        }
        Video video = new Video();
        video.site = "youku";
        // swf
        video.setSwf("http://player.youku.com/player.php/sid/" + vid + "/v.swf");
        // swf end
        return video;
    }

    /**
     * 从url获取流,并根据正则从中截取多个字符串
     *
     * @param url
     * @param regexMap
     * @return
     */
    private static HashMap<String, String> regexCutStringFromUrlContent(String url,
                                                                        HashMap<String, RegexCutRule[]> regexMap) {
        if (isEmptyString(url) || isEmptyMap(regexMap)) {
            return null;
        }
        HashMap<String, String> returnMap = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            String s = "";
            while ((s = reader.readLine()) != null) {
                if (returnMap.size() == regexMap.size()) {
                    break;
                }
                for (String key : regexMap.keySet()) {
                    if (returnMap.get(key) == null) {
                        String a = regexCutString(s, regexMap.get(key));
                        if (!isEmptyString(a)) {
                            returnMap.put(key, a);
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 利用多个正则从一个字符串中截取一个字符串 只要匹配到一个就返回
     *
     * @return 第一个匹配的子串
     */
    private static String regexCutString(String souce, RegexCutRule[] regexCutRules) {
        String returnStr = "";
        if (regexCutRules != null && regexCutRules.length > 0) {
            for (RegexCutRule rule : regexCutRules) {
                returnStr = regexCutString(souce, rule);
                if (!isEmptyString(returnStr)) {
                    break;
                }
            }
        }
        return returnStr;
    }

    /**
     * 举例: 将字符串中的"\u003a"转换为":"
     */
    public static String unicodeToString(String unicodeStr) {
        while (unicodeStr.matches(".{0,}\\\\u[0-9a-f]{4}.{0,}")) {
            String unicode = regexCutString(unicodeStr, new RegexCutRule("\\\\u[0-9a-f]{4}", 0, 0));
            unicode = unicode.replace("\\u", "");
            int hex = Integer.parseInt(unicode, 16);
            char c = (char) hex;
            unicodeStr = unicodeStr.replaceAll("\\\\u" + unicode, c + "");
        }
        return unicodeStr;
    }

    /**
     * 利用正则从一个字符串中截取一个字符串
     *
     * @return 第一个匹配的子串
     */
    private static String regexCutString(String souce, RegexCutRule regexCutRule) {
        if (regexCutRule == null) {
            return "";
        }
        Pattern p = Pattern.compile(regexCutRule.regex);
        Matcher m = p.matcher(souce);
        while (m.find()) {
            String str = souce.substring(m.start(), m.end());
            if (!isEmptyString(str) && str.length() >= regexCutRule.start + regexCutRule.end) {
                return str.substring(regexCutRule.start, str.length() - regexCutRule.end);
            }
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    private static boolean isEmptyMap(Map map) {
        return map == null || map.size() <= 0;
    }

    private static boolean isEmptyString(String s) {
        return s == null || s.trim().length() <= 0;
    }

    public static void main(String args[]) {
        String url = "https://www.miaopai.com/show/jSLdpnWsCBLkmtl~hxkJcdmt5O1Z2aZhElfFkQ__.htm";
        System.out.println(isVideoUrlV2(url));
    }

    /**
     * 平台类型 ios,android,win
     */
    public enum Platform {
        IOS, ANDROID, WIN
    }

    /**
     * 正则剪切规则 根据regex表达式从一个字符串中剪切出一个匹配的字符中
     * 然后根据start和end,从匹配的字符串中再次截取,从开始去除start个字符, 从结尾去除end个字符
     */
    static class RegexCutRule {
        public String regex;
        public int start;
        public int end;

        public RegexCutRule(String regex, int start, int end) {
            super();
            this.regex = regex;
            this.start = start;
            this.end = end;
        }
    }

    /**
     * 视频解析后返回的视频对象 有可能一个视频,会分为多段,所以视频播放地址用arraylist存放
     */
    public static class Video {
        private String site;// 视频所属站点 比如 youku
        private ArrayList<String> videoUrls;// 视频的真实播放地址
        private String swf;
        private String img;// 视频的截图 比如

        public String getSite() {
            return site;
        }

        public ArrayList<String> getVideoUrls() {
            return videoUrls;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        private void addVideoUrl(String url) {
            if (videoUrls == null) {
                videoUrls = new ArrayList<>();
            }
            videoUrls.add(url);
        }

        boolean hasVideo() {
            return videoUrls != null && videoUrls.size() > 0;
        }

        public String getImg() {
            return img;
        }
    }
}
