package pub.chenhuang;

import pub.chenhuang.util.HttpUtil;

import java.util.Date;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String url = "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_=" + new Date().getTime();
        String result = HttpUtil.getByUTF8(url);
        result = result.split(";")[1];
        String uuid = result.substring(24, result.length() - 1);
        System.out.println("https://login.weixin.qq.com/qrcode/" + uuid);
        while (true) {
            long time = new Date().getTime();
            int time1 = (int) ~time;
            String loginUrl = "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login?loginicon=true&uuid=" + uuid + "&tip=0&r=" + time1 + "&_=" + time;
            String loginRes = HttpUtil.getByUTF8(loginUrl);
            loginRes = loginRes.substring(12, 15);
            if (loginRes.equals("200")) {
                break;
            } else {
                System.out.println("扫描失败");
            }
            System.out.println(loginRes);
            try {
                Thread.sleep(27000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
