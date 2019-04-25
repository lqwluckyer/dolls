package com.game.sdk.dolls.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 对于 https 请求工具类，应该使用连接池，减少创建http 的次数，
 * 这样对性能有一定提升，
 * 强烈不推荐使用EntityUtils这个类，除非目标服务器发出的响应是可信任的，并且http响应实体的长度不会过大
 */
public final class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final String EQ = "=";
    private static final String AND = "&";
    private static final String OR = "?";
    /**
     * 连接池
     */
    private static final PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager();

    /**
     * 默认请求配置
     */
    private static final RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(30000)
            .setSocketTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .build();

    /**
     * 默认连接配置
     */
    private static final ConnectionConfig DEFAULT_CONN_CONFIG = ConnectionConfig.custom()
            .setCharset(Consts.UTF_8)
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .build();

    static {
        poolConnManager.setMaxTotal(200);
        poolConnManager.setDefaultMaxPerRoute(30);
        poolConnManager.setDefaultConnectionConfig(DEFAULT_CONN_CONFIG);
    }

    private HttpUtil() {
    }

    /**
     * 发送GET请求，若没有参数时，则传入 null
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static final String sendGet(String url, Map<String, Object> paramMap) {
        try {
            String requestUrl = url;
            if (MapUtils.isNotEmpty(paramMap)) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    sb.append(entry.getKey()).append(EQ).append(entry.getValue()).append(AND);
                }
                if (url.contains(OR)) {
                    requestUrl = url + sb.insert(0, AND).toString();
                } else {
                    requestUrl = url + sb.insert(0, OR).toString();
                }
            }
            HttpGet get = new HttpGet(requestUrl);
            get.setConfig(DEFAULT_REQUEST_CONFIG);
            setDefaultGetHeader(get);
            String result =  execute(get);
            return result;
        } catch (Exception e) {
            logger.error("请求失败！请求地址:" + url + " 请求参数:" + JSON.toJSONString(paramMap), e);
            return null;
        }
    }

    /**
     * 发送请求，表单形式发送
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static final String sendPost(String url, Map<String, Object> paramMap) {
        try {
            HttpPost post = new HttpPost(url);
            post.setConfig(DEFAULT_REQUEST_CONFIG);
            post.setEntity(new UrlEncodedFormEntity(buildParamMap(paramMap), Consts.UTF_8.name()));
            setDefaultPostHeader(post);
            String result =  execute(post);
            return result;
        } catch (Exception e) {
            logger.error("请求失败！请求地址:" + url + " 请求参数:" + JSON.toJSONString(paramMap), e);
            return null;
        }
    }

    /**
     * 发送请求，JSON格式
     *
     * @param url
     * @param jsonStr
     * @return
     */
    public static final String sendPostJson(String url, String jsonStr) {
        try {
            HttpPost post = new HttpPost(url);
            post.setConfig(DEFAULT_REQUEST_CONFIG);
            post.setEntity(new StringEntity(jsonStr, Consts.UTF_8.name()));
            setDefaultJsonHeader(post);
            String result = execute(post);
            return result;
        } catch (Exception e) {
            logger.error("请求失败！请求地址:" + url + " 请求参数:" + jsonStr, e);
            return null;
        }
    }

    /**
     * 发送请求
     *
     * @param request
     * @return
     */
    public static final String execute(HttpRequestBase request) throws Exception {
        CloseableHttpClient client = httpClientInstance();
        CloseableHttpResponse response = client.execute(request);
        String result =  EntityUtils.toString(response.getEntity(), Consts.UTF_8.name());
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            closeHttpRequest(request, response);
            return result;
        }
        logger.info("http query fail , url:{}, status:{}, result:{}", request.getURI(),response.getStatusLine().getStatusCode(), result);
        closeHttpRequest(request, response);
        return null;
    }

    private static void closeHttpRequest(HttpRequestBase request, CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
            if (request != null) {
                request.releaseConnection();
            }

        } catch (Exception e) {
            logger.error("关闭连接失败，异常信息:", e);
            throw new RuntimeException("关闭连接失败，异常信息! 请求地址:" + request.getURI());
        }
    }

    /**
     * 设置默认请求头，非JSON格式
     *
     * @param request
     */
    private static void setDefaultPostHeader(HttpRequestBase request) {
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.withCharset(Consts.UTF_8.name()).getMimeType());
        request.setHeader(HttpHeaders.CONNECTION, HTTP.CONN_KEEP_ALIVE);
        request.setHeader(HttpHeaders.ACCEPT_CHARSET, Consts.UTF_8.name());
    }

    /**
     * 设置默认请求头，JSON格式
     *
     * @param request
     */
    private static void setDefaultJsonHeader(HttpRequestBase request) {
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.withCharset(Consts.UTF_8.name()).getMimeType());
        request.setHeader(HttpHeaders.CONNECTION, HTTP.CONN_KEEP_ALIVE);
        request.setHeader(HttpHeaders.ACCEPT_CHARSET, Consts.UTF_8.name());
    }

    private static void setDefaultGetHeader(HttpRequestBase request) {
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8.name()).getMimeType());
        request.setHeader(HttpHeaders.CONNECTION, HTTP.CONN_KEEP_ALIVE);
        request.setHeader(HttpHeaders.ACCEPT_CHARSET, Consts.UTF_8.name());
    }

    /**
     * 从连接池中获得一个连接
     *
     * @return
     */
    private static CloseableHttpClient httpClientInstance() {
        return HttpClients.custom()
                .setConnectionManager(poolConnManager)
                .build();
    }

    /**
     * 把Map 转换成请求参数，非 Json 格式
     *
     * @param argMap
     * @return
     */
    private static List<NameValuePair> buildParamMap(Map<String, Object> argMap) {

        if (MapUtils.isEmpty(argMap)) {
            return Collections.emptyList();
        }

        List<NameValuePair> argList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            argList.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
        }

        return argList;
    }

    /**
     * 在遇到异常时尝试重试
     *
     * @param retryLimit    重试次数
     * @param retryCallable 重试回调
     * @param <V>           泛型
     * @return V 结果
     */
    public static <V> V retryOnException(int retryLimit, java.util.concurrent.Callable<V> retryCallable, String bin) {

        V v = null;
        for (int i = 0; i < retryLimit; i++) {
            try {
                v = retryCallable.call();
            } catch (Exception e) {
                logger.error("retry on {} times , 事件：【{}】 ", (i + 1), bin, e);
            }
        }
        return v;
    }
}

