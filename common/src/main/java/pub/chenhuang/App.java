package pub.chenhuang;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;


import org.apache.http.client.CookieStore;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pub.chenhuang.model.WXUserModel;
import pub.chenhuang.util.HttpUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static Map<String, String> baseRequest;

    public static void main(String[] args) {
        String qrCodeUrl = getQrCodeUrl();
        System.out.println(qrCodeUrl);
        String redirectUri = scanThenLogin(qrCodeUrl);
        getLoginParamByRedirectUri(redirectUri);

    }

    public static String getQrCodeUrl() {
        String qrcodeUrl = "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_=" + new Date().getTime();
        String qrcodeResult = HttpUtil.getByUTF8(qrcodeUrl, null);
        qrcodeResult = qrcodeResult.split(";")[1];
        String uuid = qrcodeResult.substring(24, qrcodeResult.length() - 1);
        String qrcode = "https://login.weixin.qq.com/qrcode/" + uuid;
        return qrcode;
    }

    public static String scanThenLogin(String qrcodeUrl) {
        while (true) {
            long timeStramp = new Date().getTime();
            int timeStrampReverse = (int) ~timeStramp;
            String uuid = qrcodeUrl.substring(35, qrcodeUrl.length());
            String loginUrl = "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login?loginicon=true&uuid="
                    + uuid + "&tip=0&r=" + timeStrampReverse + "&_=" + timeStramp;
            String loginResult = HttpUtil.getByUTF8(loginUrl, null);
            String loginCode = loginResult.substring(12, 15);
            if (loginCode.equals("200")) {
                System.out.println(loginResult);
                return loginResult.substring(38, loginResult.length() - 2);
            } else {
                System.out.println("扫描失败");
            }
        }
    }

    public static void getLoginParamByRedirectUri(String redirect_uri) {

        Map<String, Object> resultAndCookieStore = HttpUtil.getByUTF8AndStoreCookie(redirect_uri + "&fun=new&version=v2");
        String result = (String) resultAndCookieStore.get("result");
        CookieStore cookieStore = (CookieStore) resultAndCookieStore.get("cookieStore");
        try {
            Document document = DocumentHelper.parseText(result);
            Element xml = document.getRootElement();
            Iterator<Element> iter = xml.elementIterator();
            Map<String, String> loginParam = Maps.newHashMap();
            while (iter.hasNext()) {
                try {
                    Element ele = iter.next();
                    loginParam.put(ele.getName(), ele.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String initResult=getWeChatInitInformationByLoginParam(loginParam);
            String wxContactStr = listWxContact(loginParam, cookieStore);
            Gson gson = new Gson();
            Map<String, Object> contactMap = gson.fromJson(wxContactStr, Map.class);
            List<Map<String, Object>> contactList = (List<Map<String, Object>>) contactMap.get("MemberList");
            List<WXUserModel> wxUserModelList = Lists.newArrayList();
            for (Map<String, Object> userMap : contactList) {
                WXUserModel wxUserModel = getWCUserModelByContactMap(userMap);
                wxUserModelList.add(wxUserModel);
            }
            contactList = null;
            WXUserModel wxUserModel = getByUserName(wxUserModelList, "老婆");
            WXUserModel selfUserModel = getByUserName(wxUserModelList, "gold great");
            while (true) {
                System.out.println(sendWXMsg(selfUserModel,wxUserModel, "我爱你", cookieStore, loginParam));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public static String sendWXMsg(WXUserModel selfUserModel,WXUserModel wxUserModel, String content, CookieStore cookieStore, Map<String, String> loginParam) {
        String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg?lang=zh_CN&pass_ticket=" +
                loginParam.get("pass_ticket");
        Map<String, Object> requestParam = Maps.newHashMap();
        Map<String, Object> param =null;
        setBaseRequest(loginParam);
        requestParam.put("BaseRequest", baseRequest);
        param = Maps.newHashMap();
        String clientMsgId = ((Long) ((new Date().getTime()) << 4)).toString() + (((Double) (Math.random() * 10000)).longValue());
        param.put("ClientMsgId", clientMsgId);
        param.put("Content", content);
        param.put("FromUserName", selfUserModel.getUserName());
        param.put("LocalID", clientMsgId);
        param.put("ToUserName", wxUserModel.getUserName());
        param.put("Type", 1);

        requestParam.put("Msg", param);
        requestParam.put("Scene", 0);

        String result = HttpUtil.postJsonWithCookies(url, new Gson().toJson(requestParam),
                null, cookieStore);
        return result;
    }

    public static void setBaseRequest(Map<String, String> loginParam) {
        baseRequest = Maps.newHashMap();
        String DeviceID = "e" + ((Double) (Math.random() * (1000000000000000L))).longValue();

        baseRequest.put("DeviceID", DeviceID);
        baseRequest.put("Sid", loginParam.get("wxsid"));
        baseRequest.put("Skey", loginParam.get("skey"));
        baseRequest.put("Uin", loginParam.get("wxuin"));
    }

    public static WXUserModel getByUserName(List<WXUserModel> wxUserModelList, String nickName) {
        try {
            for (WXUserModel wxUserModel : wxUserModelList) {
                if (wxUserModel.getNickName().equals(nickName) || wxUserModel.getRemarkName().equals(nickName)) {
                    return wxUserModel;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WXUserModel getWCUserModelByContactMap(Map<String, Object> contactMap) {
        Set<String> keys = contactMap.keySet();
        WXUserModel wxUserModel = new WXUserModel();
        for (String key : keys) {
            Class clazz = wxUserModel.getClass();
            try {
                Method method = clazz.getMethod("set" + key, String.class);
                String val = null;
                try {
                    Double douVal = (Double) contactMap.get(key);
                    Long longVal = douVal.longValue();
                    val = longVal.toString();
                } catch (Exception e) {
                    val = (String) contactMap.get(key);
                }
                method.invoke(wxUserModel, val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wxUserModel;
    }

    public static String getWeChatInitInformationByLoginParam(Map<String, String> loginParam) {
        Map<String, String> param = Maps.newHashMap();
        Map<String, Map<String, String>> requestParam = Maps.newHashMap();
        setBaseRequest(loginParam);
        requestParam.put("BaseRequest", baseRequest);

        String initUrl = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=" + ((int) (~(new Date().getTime()))) +
                "&pass_ticket=" + loginParam.get("pass_ticket");
        return HttpUtil.postJson(initUrl, new Gson().toJson(requestParam), null);

    }

    public static String listWxContact(Map<String, String> loginParam, CookieStore cookieStore) {
        String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact?lang=zh_CN&pass_ticket=" +
                loginParam.get("pass_ticket") +
                "&r=" + new Date().getTime() +
                "&seq=0&skey=" + loginParam.get("skey");
        System.out.println(url);
        return HttpUtil.getByUTF8(url, cookieStore);
    }
}
