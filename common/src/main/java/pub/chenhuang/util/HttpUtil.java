package pub.chenhuang.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cjw on 2016/10/26.
 */
public class HttpUtil {
    private static Logger LOG = Logger.getLogger(HttpUtil.class);

    public static String postJson(String url, String param, Map<String, String> headers) {
        String string = "";
        try {
            DefaultHttpClient httpclient = null;
            if (url.startsWith("https")) {
                httpclient = new SSLClient();
            } else {
                httpclient = new DefaultHttpClient();
            }
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (String s : headers.keySet()) {
                    httpPost.setHeader(s, headers.get(s));
                }
            }
            StringEntity entity = new StringEntity(param, "utf-8");
            httpPost.setEntity(entity);
            HttpResponse response2 = httpclient.execute(httpPost);
            try {
                HttpEntity entity2 = response2.getEntity();
                string = EntityUtils.toString(entity2, "utf-8");
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        String string = "";
        try {
            DefaultHttpClient httpclient = null;
            if (url.startsWith("https")) {
                httpclient = new SSLClient();
            } else {
                httpclient = new DefaultHttpClient();
            }
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            HttpResponse response2 = httpclient.execute(httpPost);
            try {
                HttpEntity entity2 = response2.getEntity();
                string = EntityUtils.toString(entity2, "utf-8");
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static String getByCharSet(String url, String charSet, CookieStore cookieStore) {
        return getWithCookie(url, charSet, cookieStore);
    }


    public static String getByUTF8(String url, CookieStore cookieStore) {
        return getByCharSet(url, "utf-8", cookieStore);
    }

    public static Map<String, Object> getByUTF8AndStoreCookie(String url) {
        return getAndStoreCookie(url, "utf-8");
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @return String类型的返回信息
     */
    private static String getWithCookie(String url, String charSet, CookieStore cookieStore) {
        String string = "";
        try {
            DefaultHttpClient httpclient = null;
            if (url.startsWith("https")) {
                httpclient = new SSLClient();
            } else {
                httpclient = new DefaultHttpClient();
            }
            httpclient.setCookieStore(cookieStore);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();
                string = EntityUtils.toString(entity, charSet);
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    private static Map<String, Object> getAndStoreCookie(String url, String charSet) {
        String string = "";
        try {
            DefaultHttpClient httpclient = null;
            if (url.startsWith("https")) {
                httpclient = new SSLClient();
            } else {
                httpclient = new DefaultHttpClient();
            }
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();

                string = EntityUtils.toString(entity, charSet);
                CookieStore cookieStore = httpclient.getCookieStore();
                Map<String, Object> map = Maps.newHashMap();
                map.put("cookieStore", cookieStore);
                map.put("result", string);
                return map;
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static InputStream getImgByUrl(String url) {
        InputStream in = null;
        try {
            DefaultHttpClient httpclient = null;
            if (url.startsWith("https")) {
                httpclient = new SSLClient();
            } else {
                httpclient = new DefaultHttpClient();
            }
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response1 = httpclient.execute(httpGet);

            try {
                HttpEntity entity = response1.getEntity();
                in = entity.getContent();
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;

    }

    public final static String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  

        String ip = request.getHeader("X-Forwarded-For");
        if (LOG.isInfoEnabled()) {
            LOG.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (LOG.isInfoEnabled()) {
                    LOG.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (LOG.isInfoEnabled()) {
                    LOG.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (LOG.isInfoEnabled()) {
                    LOG.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (LOG.isInfoEnabled()) {
                    LOG.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (LOG.isInfoEnabled()) {
                    LOG.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static void main(String[] args) {
        List<String> a = Lists.newArrayList();
        a.add("1");
        test(a);
        System.out.println(a);
    }

    public static void test(List<String> a) {

        a.add("1234");
    }
}
