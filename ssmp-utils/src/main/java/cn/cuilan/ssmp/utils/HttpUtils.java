package cn.cuilan.ssmp.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 默认编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 默认连接池中最大连接数
     */
    public static final int DEFAULT_CONNECTION_MAX_TOTAL = 200;
    /**
     * 从连接池中获取请求连接的超时时间
     */
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;
    /**
     * 默认连接超时时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    /**
     * 默认socket读取数据超时时间,具体的长耗时请求中(如文件传送等)必须覆盖此设置
     */
    public static final int DEFAULT_SO_TIMEOUT = 15000;
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0";
    private static final PoolingHttpClientConnectionManager defaultConnectionManager;
    private static final ConnectionConfig defaultConnectionConfig;
    private static final RequestConfig defaultRequestConfig;
    private static final Registry<CookieSpecProvider> defaultCookieSpecRegistry;
    private static final CloseableHttpClient httpClient;
    private static boolean isWindow = Boolean.FALSE;


    static {
        isWindow = System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
        try {
            SSLContext sslContext = SSLContexts.custom().build();
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();
            // 消息约束
            /*MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200)
	                .setMaxLineLength(2000)
	                .build();*/
            // 默认连接配置
            defaultConnectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .build();
            // 默认请求配置
            defaultRequestConfig = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                    .setSocketTimeout(DEFAULT_SO_TIMEOUT)
                    .build();
            defaultConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            defaultConnectionManager.setMaxTotal(DEFAULT_CONNECTION_MAX_TOTAL);
            defaultConnectionManager.setDefaultMaxPerRoute(20);
            // 设置连接配置
            defaultConnectionManager.setDefaultConnectionConfig(defaultConnectionConfig);
            // 注册Cookie策略
            defaultCookieSpecRegistry = RegistryBuilder.<CookieSpecProvider>create()
                    .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider())
                    .build();
            httpClient = HttpClients.custom()
                    .setConnectionManager(defaultConnectionManager)
                    .setDefaultCookieSpecRegistry(defaultCookieSpecRegistry)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .setDefaultConnectionConfig(defaultConnectionConfig)
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setUserAgent(DEFAULT_USER_AGENT)
                    //                .setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    public static void applyDefaultHeaders(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/json,application/xml;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpRequestBase.setHeader("Connection", "keep-alive");
        httpRequestBase.setHeader("DNT", "1");
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url
     * @return - 默认返回text/html
     */
    public static String doGet(String url) {
        logger.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url
     * @param responseCharset - 指定响应html字符集
     * @return - 默认返回text/html
     */
    public static String doGet(String url, String responseCharset) {
        logger.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, new DefaultStringResponseHandler(responseCharset));
    }

    /**
     * <p>执行Get请求</p>
     *
     * @param url
     * @param responseHandler - 响应处理器
     * @return - 默认返回text/html
     */
    public static <T> T doGet(String url, ResponseHandler<T> responseHandler) {
        logger.debug(">>> doGet[url = {}]", url);
        HttpGet httpGet = new HttpGet(url);
        applyDefaultHeaders(httpGet);
        return doHttpRequest(httpGet, null, responseHandler);
    }

    /**
     * <p>执行Post请求</p>
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, Map<String, String> paramMap) {
        logger.debug(">>> doPost[url = {}, paramMap = {}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        applyDefaultHeaders(httpPost);
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler());
    }

    /**
     * <p>执行Post请求</p>
     *
     * @param url
     * @param paramMap
     * @param responseCharset - 指定响应html字符集
     * @return
     */
    public static String doPost(String url, Map<String, String> paramMap, String responseCharset) {
        logger.debug(">>> doPost[url = {}, paramMap = {}]", url, paramMap);
        HttpPost httpPost = createHttpPost(url, paramMap);
        applyDefaultHeaders(httpPost);
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler(responseCharset));
    }

    /**
     * 发送entity模式
     * @param url
     * @param entity
     * @param headers
     * @return
     */
    public static String doPost(String url, String entity, Map<String, String> headers) {
        HttpPost httpPost = createHttpPost(url, null);
        applyDefaultHeaders(httpPost);

        try {
            httpPost.setEntity(new StringEntity(entity));
            headers.entrySet().forEach(entry->{
               httpPost.setHeader(entry.getKey(), entry.getValue());
            });
        } catch (UnsupportedEncodingException e) {
            logger.error("send http by post", e);
            return null;
        }
        return doHttpRequest(httpPost, null, new DefaultStringResponseHandler(DEFAULT_CHARSET));
    }

    /**
     * <p>根据URL和参数创建HttpPost对象</p>
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static HttpPost createHttpPost(String url, Map<String, String> paramMap) {
        try {
            HttpPost httpPost = new HttpPost(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                List<NameValuePair> params = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, DEFAULT_CHARSET);
                httpPost.setEntity(formEntity);
            }
            return httpPost;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    /**
     * <p>执行http请求</p>
     *
     * @param httpMethod              - HTTP请求(HttpGet、HttpPost等等)
     * @param httpClientContextSetter - 可选参数,请求前的一些参数设置(如：cookie、鉴权认证等)
     * @param responseHandler         - 必选参数,响应处理类(如针对httpstatu的各种值做一些策略处理等等)
     * @return
     */
    public static <T> T doHttpRequest(HttpRequestBase httpMethod, HttpClientContextSetter httpClientContextSetter, ResponseHandler<T> responseHandler) {
        Args.notNull(responseHandler, "Parameter 'httpMethod' can not be null!");
        Args.notNull(responseHandler, "Parameter 'responseHandler' can not be null!");
        CloseableHttpResponse response = null;
        try {
            if (httpClientContextSetter != null) {
                HttpClientContext context = HttpClientContext.create();
                httpClientContextSetter.setHttpClientContext(context);
                response = getHttpClient().execute(httpMethod, context);
            } else {
                response = getHttpClient().execute(httpMethod);
            }
            return response == null ? null : responseHandler.handleResponse(response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new HttpClientException(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * <p>对于特殊请求(比如请求涉及到cookie的处理,鉴权认证等),默认的一些配置已经满足不了了,
     * 这时就可以使用一个独立于全局的配置来执行请求,这个独立于全局,又不会干扰其他线程的请求执行的机制就是使用HttpClientContext,
     * 该设置类用于对已经提供的一个基于全局配置的副本,来设置一些配置(见HttpClientContext.setXxx)</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月20日 下午4:27:50
     */
    public static interface HttpClientContextSetter {
        public void setHttpClientContext(HttpClientContext context);
    }

    /**
     * <p>默认keepAlive策略：如果响应中存在服务器端的keepAlive超时时间则返回该时间否则返回默认的</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月20日 下午3:26:39
     */
    public static class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            /**
             * 默认30秒
             */
            return 30 * 1000;
        }
    }

    /**
     * <p>默认的处理返回值为String的ResponseHandler</p>
     *
     * @author pengpeng
     * @version 1.0
     * @date 2014年7月21日 上午9:13:22
     */
    public static class DefaultStringResponseHandler implements ResponseHandler<String> {
        /**
         * 默认响应html字符集编码
         */
        private Charset defaultCharset;

        public DefaultStringResponseHandler() {
            super();
        }

        public DefaultStringResponseHandler(String defaultCharset) {
            super();
            this.defaultCharset = Charset.forName(defaultCharset);
        }

        public Charset getDefaultCharset() {
            return defaultCharset;
        }

        public void setDefaultCharset(Charset defaultCharset) {
            this.defaultCharset = defaultCharset;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, defaultCharset == null ? ContentType.getOrDefault(httpEntity).getCharset() : defaultCharset);
            }
            return null;
        }
    }

    public static class HttpClientException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public HttpClientException(String message) {
            super(message);
        }

        public HttpClientException(Throwable cause) {
            super(cause);
        }

        /**
         * Return the detail message, including the message from the nested exception
         * if there is one.
         */
        @Override
        public String getMessage() {
            return buildMessage(super.getMessage(), getCause());
        }

        /**
         * Retrieve the innermost cause of this exception, if any.
         *
         * @return the innermost exception, or {@code null} if none
         * @since 2.0
         */
        public Throwable getRootCause() {
            Throwable rootCause = null;
            Throwable cause = getCause();
            while (cause != null && cause != rootCause) {
                rootCause = cause;
                cause = cause.getCause();
            }
            return rootCause;
        }

        /**
         * Build a message for the given base message and root cause.
         *
         * @param message the base message
         * @param cause   the root cause
         * @return the full exception message
         */
        protected String buildMessage(String message, Throwable cause) {
            if (cause != null) {
                StringBuilder sb = new StringBuilder();
                if (message != null) {
                    sb.append(message).append("; ");
                }
                sb.append("nested exception is ").append(cause);
                return sb.toString();
            } else {
                return message;
            }
        }
    }

    public static File download(String url, Map<String, String> headers) {
        if (isWindow) {
            return download(url, "C:\\Windows\\Temp", UUID.randomUUID().toString(), headers);
        } else {
            return download(url, "/tmp", UUID.randomUUID().toString(), headers);
        }
    }

    public static File download(String url, String basePath, Map<String, String> headers) {
        return download(url, basePath, UUID.randomUUID().toString(), headers);
    }

    public static File download(String url, String basePath, String fileName, Map<String, String> headers) {
        // we're using GET but it could be via POST as well
        HttpGet get = new HttpGet(url);
        if (headers != null) {
            headers.forEach((k, v) -> get.setHeader(k, v));
        }

        File downloaded = null;
        try {
            downloaded = getHttpClient().execute(get, new FileDownloadResponseHandler(basePath, fileName));
        } catch (IOException e) {
            logger.error("download image exception, the url: {}", url, e);
        }

        return downloaded;
    }

    static class FileDownloadResponseHandler implements ResponseHandler<File> {
        private final String basePath;
        private final String fileName;

        public FileDownloadResponseHandler(String basePath, String fileName) {
            if (basePath.endsWith(File.separator)) {
                this.basePath = basePath;
            } else {
                this.basePath = basePath + File.separator;
            }


            if (fileName.startsWith(File.separator)) {
                fileName = fileName.substring(1);
            }

            if (fileName.endsWith(File.separator)) {
                fileName = fileName.substring(0, fileName.length() - 1);
            }

            if (fileName.endsWith(".")) {
                fileName = fileName.substring(0, fileName.length() - 1);
            }

            this.fileName = fileName + ".";
        }

        @Override
        public File handleResponse(HttpResponse response) throws IOException {
            String suffix = "jpg";
            Header[] headers = response.getHeaders("Content-Type");
            if (headers != null || headers.length > 0) {
                for (Header header : headers) {
                    String value = header.getValue();
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    value = ImageContentType.get(value);
                    suffix = value;
                }
            }

            File target = new File(this.basePath + this.fileName + suffix);
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }

            byte[] data = EntityUtils.toByteArray(response.getEntity());
            FileUtils.writeByteArrayToFile(target, data);
            return target;
        }
    }

    public enum ImageContentType {
        gif("image/gif"),
        //        net("image/pnetvue"),
//        tif("image/tiff"),
//        fax("image/fax"),
        ico("image/x-icon"),
        //        jfif("image/jpeg"),
//        jpe("image/jpeg"),
//        jpeg("image/jpeg"),
        jpg("image/jpeg"),
        png("image/png");
        private String value;

        ImageContentType(String value) {
            this.value = value;
        }

        public static String get(String type) {
            if (StringUtils.isBlank(type)) {
                return ImageContentType.jpg.name();
            }
            for (ImageContentType contentType : ImageContentType.values()) {
                if (type.toLowerCase().indexOf(contentType.value) != -1) {
                    return contentType.name();
                }
            }
            return ImageContentType.jpg.name();
        }

    }


    /**
     * 判断请求是否为Ajax
     *
     * @create_date 2016年2月2日
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public interface DownloadStatusHandler {
        void handle(long percent);
    }

}

