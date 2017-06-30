package pub.chenhuang.util.http;

import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cjw on 2016/10/26.
 */
public class HttpUtil {
    private static org.slf4j.Logger LOG= LoggerFactory.getLogger(HttpUtil.class);

    public static CloseableHttpClient createHttpClient(String url, CookieStore cookieStore){
        System.setProperty ("jsse.enableSNIExtension", "false");
        if(url.startsWith("https")){
            return createHttpsClient(cookieStore);
        }else{
            return createHttpClient(cookieStore);
        }
    }
    public static CloseableHttpClient createHttpsClient(CookieStore cookieStore){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultCookieStore(cookieStore).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return  HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }
    public static CloseableHttpClient createHttpClient(CookieStore cookieStore){
        return  HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }


    public static String postJson(String url, String param, Map<String, String> headers) {
        String string = "";
        try {
            CookieStore cookieStore=new BasicCookieStore();
            CloseableHttpClient httpclient = createHttpClient(url,cookieStore);
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (String s : headers.keySet()) {
                    httpPost.setHeader(s, headers.get(s));
                }
            }
            StringEntity postEntity = new StringEntity(param, "utf-8");
            httpPost.setEntity(postEntity);
            HttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity respEntity = resp.getEntity();
                string = EntityUtils.toString(respEntity, "utf-8");
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }


    public static String postJsonWithCookies(String url, String param, Map<String, String> headers, CookieStore cookieStore) {
        String string = "";
        try {
            CloseableHttpClient httpclient =createHttpClient(url,cookieStore);
            HttpPost httpPost = new HttpPost(url);
            if (headers != null) {
                for (String s : headers.keySet()) {
                    httpPost.setHeader(s, headers.get(s));
                }
            }
            StringEntity postEntity = new StringEntity(param, "utf-8");
            httpPost.setEntity(postEntity);
            HttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity respEntity = resp.getEntity();
                string = EntityUtils.toString(respEntity, "utf-8");
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }


    public static String post(String url, Map<String, String> params) {
        String string = "";
        try {
            CookieStore cookieStore=new BasicCookieStore();
            CloseableHttpClient httpclient = createHttpClient(url,cookieStore);
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            HttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity responseEntity = response.getEntity();
                string = EntityUtils.toString(responseEntity, "utf-8");
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }


    private static String getByCharSet(String url, String charSet, CookieStore cookieStore) {
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
            CloseableHttpClient httpclient
                    = createHttpClient(url,cookieStore);
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
            CookieStore cookieStore=new BasicCookieStore();
            CloseableHttpClient httpclient =createHttpClient(url,cookieStore);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();

                string = EntityUtils.toString(entity, charSet);
                Map<String, Object> map = Maps.newHashMap();
                map.put("cookieStore", cookieStore);
                map.put("result", string);
                return map;
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static InputStream getImgByUrl(String url) {
        InputStream in = null;
        try {
            CookieStore cookieStore=new BasicCookieStore();
            CloseableHttpClient httpclient = createHttpClient(url,cookieStore);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response1 = httpclient.execute(httpGet);

            try {
                HttpEntity entity = response1.getEntity();
                in = entity.getContent();
            } finally {
                httpclient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;

    }


}
