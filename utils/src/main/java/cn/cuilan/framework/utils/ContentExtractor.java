package cn.cuilan.framework.utils;


import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Sets;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;
import cn.cuilan.framework.utils.service.WeChatArticleUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 * Copyright (c) 2005-2016 LongDai, Inc.
 * 简单正文抓取
 * Contributors:
 * DonQuixote  on 10/26/16 - 8:01 PM
 *******************************************************************************/
public class ContentExtractor {

    public static final Logger log = LoggerFactory.getLogger(ContentExtractor.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static OkHttpClient client = new OkHttpClient();


    private static List<String> excludeHost = Arrays.asList(
            "sports.163.com",
            "sports.cnr.cn",
            "sports.people.com.cn",
            "sports.sina.com.cn",
            "sports.eastday.com",
            "sports.qianlong.com",
            "sohu.com");

    protected Document doc;


    ContentExtractor(Document doc) {
        this.doc = doc;
    }

    protected HashMap<Element, CountInfo> infoMap = new HashMap<Element, CountInfo>();

    private static final ArrayList<String> imageType = new ArrayList<String>() {{
        add("png");
        add("jpg");
        add("jpeg");
        add("gif");
    }};

    class CountInfo {

        int textCount = 0;
        int linkTextCount = 0;
        int tagCount = 0;
        int linkTagCount = 0;
        double density = 0;
        double densitySum = 0;
        double score = 0;
        int pCount = 0;
        ArrayList<Integer> leafList = new ArrayList<Integer>();

    }

    protected void clean() {
        doc.select("script,noscript,style,iframe,br").remove();
    }

    protected CountInfo computeInfo(Node node) {

        if (node instanceof Element) {
            Element tag = (Element) node;

            CountInfo countInfo = new CountInfo();
            for (Node childNode : tag.childNodes()) {
                CountInfo childCountInfo = computeInfo(childNode);
                countInfo.textCount += childCountInfo.textCount;
                countInfo.linkTextCount += childCountInfo.linkTextCount;
                countInfo.tagCount += childCountInfo.tagCount;
                countInfo.linkTagCount += childCountInfo.linkTagCount;
                countInfo.leafList.addAll(childCountInfo.leafList);
                countInfo.densitySum += childCountInfo.density;
                countInfo.pCount += childCountInfo.pCount;
            }
            countInfo.tagCount++;
            String tagName = tag.tagName();
            if (tagName.equals("a")) {
                countInfo.linkTextCount = countInfo.textCount;
                countInfo.linkTagCount++;
            } else if (tagName.equals("p")) {
                countInfo.pCount++;
            }

            int pureLen = countInfo.textCount - countInfo.linkTextCount;
            int len = countInfo.tagCount - countInfo.linkTagCount;
            if (pureLen == 0 || len == 0) {
                countInfo.density = 0;
            } else {
                countInfo.density = (pureLen + 0.0) / len;
            }

            infoMap.put(tag, countInfo);

            return countInfo;
        } else if (node instanceof TextNode) {
            TextNode tn = (TextNode) node;
            CountInfo countInfo = new CountInfo();
            String text = tn.text();
            int len = text.length();
            countInfo.textCount = len;
            countInfo.leafList.add(len);
            return countInfo;
        } else {
            return new CountInfo();
        }
    }

    protected double computeScore(Element tag) {
        CountInfo countInfo = infoMap.get(tag);
        double var = Math.sqrt(computeVar(countInfo.leafList) + 1);
        double score = Math.log(var) * countInfo.densitySum * Math.log(countInfo.textCount - countInfo.linkTextCount + 1) * Math.log10(countInfo.pCount + 2);
        return score;
    }

    protected double computeVar(ArrayList<Integer> data) {
        if (data.size() == 0) {
            return 0;
        }
        if (data.size() == 1) {
            return data.get(0) / 2;
        }
        double sum = 0;
        for (Integer i : data) {
            sum += i;
        }
        double ave = sum / data.size();
        sum = 0;
        for (Integer i : data) {
            sum += (i - ave) * (i - ave);
        }
        sum = sum / data.size();
        return sum;
    }

    public Element getContentElement() throws Exception {
        clean();
        computeInfo(doc.body());
        double maxScore = 0;
        Element content = null;
        for (Map.Entry<Element, CountInfo> entry : infoMap.entrySet()) {
            Element tag = entry.getKey();
            if (tag.tagName().equals("a") || tag == doc.body()) {
                continue;
            }
            double score = computeScore(tag);
            if (score > maxScore) {
                maxScore = score;
                content = tag;
            }
        }
        if (content == null) {
            throw new Exception("extraction failed");
        }
        return content;
    }

    public News getNews() throws Exception {
        News news = new News();
        Element contentElement;
        try {
            contentElement = getContentElement();
            news.setContentElement(contentElement);
        } catch (Exception ex) {
            log.info("news content extraction failed,extraction abort", ex);
            throw new Exception(ex);
        }

        if (StringUtils.isNotBlank(doc.baseUri())) {
            news.setUrl(doc.baseUri());
        }

        try {
            news.setTime(getTime(contentElement));
        } catch (Exception ex) {
            log.info("news title extraction failed", ex);
        }

        try {
            news.setTitle(getTitle(contentElement));
        } catch (Exception ex) {
            log.info("title extraction failed", ex);
        }
        return news;
    }

    protected String getTime(Element contentElement) throws Exception {
        String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-2]?[1-9])[^0-9]{1,5}?([0-9]{1,2})[^0-9]{1,5}?([0-9]{1,2})";
        Pattern pattern = Pattern.compile(regex);
        Element current = contentElement;
        for (int i = 0; i < 2; i++) {
            if (current != null && current != doc.body()) {
                Element parent = current.parent();
                if (parent != null) {
                    current = parent;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (current == null) {
                break;
            }
            String currentHtml = current.outerHtml();
            Matcher matcher = pattern.matcher(currentHtml);
            if (matcher.find()) {
                return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " " + matcher.group(4) + ":" + matcher.group(5) + ":" + matcher.group(6);
            }
            if (current != doc.body()) {
                current = current.parent();
            }
        }

        try {
            return getDate(contentElement);
        } catch (Exception ex) {
            throw new Exception("time not found");
        }

    }

    protected String getDate(Element contentElement) throws Exception {
        String regex = "([1-2][0-9]{3})[^0-9]{1,5}?([0-1]?[0-9])[^0-9]{1,5}?([0-9]{1,2})";
        Pattern pattern = Pattern.compile(regex);
        Element current = contentElement;
        for (int i = 0; i < 2; i++) {
            if (current != null && current != doc.body()) {
                Element parent = current.parent();
                if (parent != null) {
                    current = parent;
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (current == null) {
                break;
            }
            String currentHtml = current.outerHtml();
            Matcher matcher = pattern.matcher(currentHtml);
            if (matcher.find()) {
                return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
            }
            if (current != doc.body()) {
                current = current.parent();
            }
        }
        throw new Exception("date not found");
    }

    protected double strSim(String a, String b) {
        int len1 = a.length();
        int len2 = b.length();
        if (len1 == 0 || len2 == 0) {
            return 0;
        }
        double ratio;
        if (len1 > len2) {
            ratio = (len1 + 0.0) / len2;
        } else {
            ratio = (len2 + 0.0) / len1;
        }
        if (ratio >= 3) {
            return 0;
        }
        return (lcs(a, b) + 0.0) / Math.max(len1, len2);
    }

    protected String getTitle(final Element contentElement) throws Exception {
        final ArrayList<Element> titleList = new ArrayList<Element>();
        final ArrayList<Double> titleSim = new ArrayList<Double>();
        final AtomicInteger contentIndex = new AtomicInteger();
        final String metaTitle = doc.title().trim();

        if (!StringUtil.isBlank(metaTitle)) {
            doc.body().traverse(new NodeVisitor() {
                public void head(Node node, int i) {
                    if (node instanceof Element) {
                        Element tag = (Element) node;
                        if (tag == contentElement) {
                            contentIndex.set(titleList.size());
                            return;
                        }
                        String tagName = tag.tagName();
                        if (Pattern.matches("h[1-6]", tagName)) {
                            String title = tag.text().trim();
                            double sim = strSim(title, metaTitle);
                            titleSim.add(sim);
                            titleList.add(tag);
                        }
                    }
                }

                public void tail(Node node, int i) {
                }
            });
            int index = contentIndex.get();
            if (index > 0) {
                double maxScore = 0;
                int maxIndex = -1;
                for (int i = 0; i < index; i++) {
                    double score = (i + 1) * titleSim.get(i);
                    if (score > maxScore) {
                        maxScore = score;
                        maxIndex = i;
                    }
                }
                if (maxIndex != -1) {
                    return titleList.get(maxIndex).text();
                }
            }
        }

        Elements titles = doc.body().select("*[id^=title],*[id$=title],*[class^=title],*[class$=title]");
        if (titles.size() > 0) {
            String title = titles.first().text();
            if (title.length() > 5 && title.length() < 40) {
                return titles.first().text();
            }
        }
        try {
            return getTitleByEditDistance(contentElement);
        } catch (Exception ex) {
            throw new Exception("title not found");
        }

    }

    protected String getTitleByEditDistance(Element contentElement) throws Exception {
        final String metaTitle = doc.title();

        final ArrayList<Double> max = new ArrayList<Double>();
        max.add(0.0);
        final StringBuilder sb = new StringBuilder();
        doc.body().traverse(new NodeVisitor() {

            public void head(Node node, int i) {

                if (node instanceof TextNode) {
                    TextNode tn = (TextNode) node;
                    String text = tn.text().trim();
                    double sim = strSim(text, metaTitle);
                    if (sim > 0) {
                        if (sim > max.get(0)) {
                            max.set(0, sim);
                            sb.setLength(0);
                            sb.append(text);
                        }
                    }

                }
            }

            public void tail(Node node, int i) {
            }
        });
        if (sb.length() > 0) {
            return sb.toString();
        }
        throw new Exception();

    }

    protected int lcs(String x, String y) {

        int M = x.length();
        int N = y.length();
        if (M == 0 || N == 0) {
            return 0;
        }
        int[][] opt = new int[M + 1][N + 1];

        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        return opt[0][0];

    }

    protected int editDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                if (c1 == c2) {
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }

    /*输入Jsoup的Document，获取正文所在Element*/
    public static Element getContentElementByDoc(Document doc) throws Exception {
        ContentExtractor ce = new ContentExtractor(doc);
        return ce.getContentElement();
    }

    /*输入HTML，获取正文所在Element*/
    public static Element getContentElementByHtml(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        return getContentElementByDoc(doc);
    }

    /*输入HTML和URL，获取正文所在Element*/
    public static Element getContentElementByHtml(String html, String url) throws Exception {
        Document doc = Jsoup.parse(html, url);
        return getContentElementByDoc(doc);
    }

    /*输入URL，获取正文所在Element*/
    public static Element getContentElementByUrl(String url) throws Exception {
        HttpRequest request = new HttpRequest(url);
        String html = request.getResponse().getHtmlByCharsetDetect();
        return getContentElementByHtml(html, url);
    }

    /*输入Jsoup的Document，获取正文文本*/
    public static String getContentByDoc(Document doc) throws Exception {
        ContentExtractor ce = new ContentExtractor(doc);
        return ce.getContentElement().text();
    }

    /*输入HTML，获取正文文本*/
    public static String getContentByHtml(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        return getContentElementByDoc(doc).text();
    }

    /*输入HTML和URL，获取正文文本*/
    public static String getContentByHtml(String html, String url) throws Exception {
        Document doc = Jsoup.parse(html, url);
        return getContentElementByDoc(doc).text();
    }

    /*输入URL，获取正文文本*/
    public static String getContentByUrl(String url) throws Exception {
        HttpRequest request = new HttpRequest(url);
        String html = request.getResponse().getHtmlByCharsetDetect();
        return getContentByHtml(html, url);
    }

    /*输入Jsoup的Document，获取结构化新闻信息*/
    public static News getNewsByDoc(Document doc) throws Exception {
        ContentExtractor ce = new ContentExtractor(doc);
        return ce.getNews();
    }

    /*输入HTML，获取结构化新闻信息*/
    public static News getNewsByHtml(String html) throws Exception {
        Document doc = Jsoup.parse(html);
        return getNewsByDoc(doc);
    }

    /*输入HTML和URL，获取结构化新闻信息*/
    public static News getNewsByHtml(String html, String url) throws Exception {
        Document doc = Jsoup.parse(html, url);
        return getNewsByDoc(doc);
    }

    /*输入URL，获取结构化新闻信息*/
    public static News getNewsByUrl(String url) throws Exception {
        HttpRequest request = new HttpRequest(url);

        String html = request.getResponse().getHtmlByCharsetDetect();

        return getNewsByHtml(html);
    }

    /**
     * 获取url对应的title和摘要
     */
    public static Map<String,String> getTitleAndSummary(String url) throws IOException {
        Map<String,String> rs  = new HashMap<>();
        if(url.startsWith("https://mp.weixin.qq.com")){
            String title = WeChatArticleUtil.getTitle(url);
            rs.put("title",title);
            rs.put("summary", "");
            return rs;
        }

        Document doc = Jsoup.parse(new URL(url), 50000);

        String title = doc.title();
        String summary = "";
        if (doc.select("meta[name=description]").first() != null) {
            summary = doc.select("meta[name=description]").first().attr("content");
        }
        if (ChoutiWeb.getLength(summary, false) > 240) {
            summary = ChoutiWeb.getSplitLength(summary,240, false);
        }

        rs.put("title",title);
        rs.put("summary",summary);

        return rs;
    }

    //such as http://maoqiuapp.com/topic/aa1000cf49a34fbead6c201984591999
    private static String extractorMaoQiu(String url) {
        try {

            String topic = url.substring(url.lastIndexOf("/topic/") + 7);

            String data = String.format("{\"contentId\": \"%s\", \"operationToken\": \"49c88379e2024bebb108e9381b2b06fd\"}", topic);

            OkHttpClient.Builder builder=new OkHttpClient.Builder();
            OkHttpClient client =
                    builder.connectTimeout(2, TimeUnit.SECONDS)
                            .readTimeout(2, TimeUnit.SECONDS)
                            .writeTimeout(2, TimeUnit.SECONDS).build();
            RequestBody body = RequestBody.create(JSON, data);
            Request request = new Request.Builder()
                    .url("http://www.maoqiuapp.com/v1/album/detail")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();

            JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(response.body().string());

            JSONArray imgs = jsonObject.getJSONObject("data").getJSONArray("imgs");

            if (imgs.size() == 0) {
                return null;
            }

            Object printScreen = imgs.getJSONObject(0).get("printScreen");

            if (printScreen != null && StringUtils.isNotEmpty(printScreen.toString())) {
                return printScreen.toString();
            }

            return jsonObject.getJSONObject("data").getJSONArray("imgs").getJSONObject(0).get("imgUrl").toString();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    static Set<String> blackKeywords= Sets.newHashSet("loading","copyright");
    /**
     * 输入 URL 获取新闻链接配图
     * 如果抓取成功则返回图片地址否则返回 null
     *
     * @param newUrl 新闻地址
     */
    public static List<String> extractorImage(String newUrl) {
        List<String> result=new ArrayList<>();
        try {
            URL link = new URL(newUrl);
            if (link.getHost().contains("maoqiuapp.com")) {
                return Arrays.asList(extractorMaoQiu(newUrl));
            }
            if (link.getHost().contains("sputniknews.cn")) {
                return Arrays.asList(CatchImage.getImageSrc(newUrl));
            }

            String host = link.getHost();

            if (excludeHost(host)) {
                return null;
            }


            News news = ContentExtractor.getNewsByUrl(newUrl);
            if(StringUtils.isBlank(news.getUrl())){
                news.setUrl(newUrl);
            }

            Elements images = news.getContentElement().getElementsByTag("img");

            //遍历有线匹配属性为src或者包含 src 的 否则遍历所有 img attr 取里面有 图片地址的 url 判断
            for (Element e : images) {
                //直接根据 src 名称来获取
                if (e.attr("src") != null && !e.attr("src").isEmpty()) {
                    String url = parseUrl(news.getUrl(), e.attr("src"));
                    if (isImgUrl(url)) {
                        result.add(url);
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(result)){
                return result;
            }

            eachImg:
            for (Element e : images) {
                for (Attribute a : e.attributes()) {
                    String url = parseUrl(news.getUrl(), a.getValue());
                    if (isImgUrl(url)) {
                        result.add(url);
                        continue eachImg;
                    }
                }
            }
        } catch (Exception e) {
            log.error("图片抓取异常", e);
        } finally {
            log.info("抓取配图结果[url={},pics={}]", newUrl, result);
        }
        return result;
    }

    static boolean isImgUrl(String url){
        try {
            if (StringUtils.isBlank(url)) {
                return false;
            }
            for (String keyword : blackKeywords) {
                if (url.contains(keyword)) {
                    return false;
                }
            }

            //如果这个地址是图片地址判断是不是二维码
            if (isAdvertisingUrl(url)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    public static boolean excludeHost(String host) {
        //检测是否需要跳过配图抓取
        //        return excludeHost.stream().filter((host::contains)).count() > 0;
        if (StringUtils.isEmpty(host)) {
            return false;
        }
        for (String exclude : excludeHost) {
            if (host.contains(exclude)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是广告地址
     *
     * @return 如果是则返回真
     */
    public static boolean isAdvertisingUrl(String url) {
        try {
            Request request = new Request.Builder().url(url).build();
            //如果是二维码图片则判定是广告
            BufferedImage image = ImageIO.read(client.newCall(request).execute().body().byteStream());

            //太少了的图片不要
            if (image.getHeight() <= 100 || image.getWidth() <= 100) {
                return true;
            }

            //如果宽度长度比小余这个阈值也判定是广告图
            //0.17的话就是一个宽度很小 长度很长的条形图了
            if ((image.getHeight() / (image.getWidth() * 1.0)) <= 0.17) {
                return true;
            }

            if (isQRImage(image)) {
                return true;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            //如果出异常了,则直接丢弃
            return true;
        }
        return false;
    }


    /**
     * 解析图片地址
     * 如果 path 路径不包含主机区域则填充 否则返回 path
     *
     * @param host 主机域名
     * @param path 路径
     */
    private static String parseUrl(String host, String path) {

        URL url;
        try {
            url = new URL(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        //匹配 //img.xxx.com/xxx.jpg 这种格式
        if (path.indexOf("//") == 0) {
            return url.getProtocol() + ":" + path;
        }

        //匹配 /xxx.jpg 这种格式
        if (!path.contains("http")) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return url.getProtocol() + "://" + host + path;
        }

        return path;
    }

//    /**
//     * 判断是否是图片地址
//     *
//     * @param url url
//     */
//    private static boolean isImageUrl(String url) {
//        if (url == null || url.isEmpty()) {
//            return false;
//        }
//
//        String prefix = url.substring(url.lastIndexOf(".") + 1);
//        for (String type : imageType) {
//            if (prefix.contains(type)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 判断是否是二维码地址
     *
     * @return 如果是则返回真
     */
    private static boolean isQRImage(BufferedImage image) {

        if (image == null) {
            return false;
        }
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        //尝试解码
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        try {
            new QRCodeReader().decode(bitmap, hints);
            return true;
        } catch (NotFoundException notFound) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new QRCodeMultiReader().decodeMultiple(bitmap, hints);
            return true;
        } catch (NotFoundException notFound) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
