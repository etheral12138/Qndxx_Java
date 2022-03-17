package Qndxx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Qndxx {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Map<String, Object> qndxx_action(String laravel_session) throws IOException {
//        JSONObject res=new JSONObject();
        Map<String, Object> res = new HashMap<>();

        logger.info(laravel_session);
        String login_url = "https://service.jiangsugqt.org/youth/lesson";
        Connection relogin_con = Jsoup.connect(login_url);//江苏省青年大学习接口
        Map<String, String> header = new HashMap<>(); //创建请求头
        String UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.18(0x18001234) NetType/WIFI Language/zh_CN";
        header.put("User-Agent", UA);
        String cookie = "laravel_session=" + laravel_session; //组合laravel_session
        header.put("Cookie", cookie);
        Connection.Response relogin = relogin_con.ignoreContentType(true).followRedirects(true).headers(header).method(Connection.Method.GET).execute(); //登录

        res.put("Cookie",relogin.cookies());
//        System.out.println(relogin.body());
        Document loginhtml = relogin.parse();// 解析信息确认页面
//        System.out.println(loginhtml);
        Elements scripts = loginhtml.getElementsByTag("script");
//        System.out.println(scripts);
        for (Element script : scripts) { //解析js

            if (script.data().contains("var token")) {  //获取token所在js
//                System.out.println(script.data());
                String pattern = "\"\\w{40}\"";   //匹配40位的token
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(script.data());
                if (m.find()) {
                    String group = m.group();
//                    System.out.println(group);
                    String token = group.substring(1, group.length() - 1);
//                    System.out.println(token);
                    res.put("token",token);

                }
                String pattern1 = "'lesson_id':[0-9]{3}"; //匹配lesson_id
                Pattern p1 = Pattern.compile(pattern1);
                Matcher m1 = p1.matcher(script.data());
                if (m1.find()) {
                    String lesson_id = m1.group();
//                    System.out.println(group);
                    res.put("lesson_id",lesson_id.substring(lesson_id.length()-3));
//                    System.out.println(lesson_id.substring(lesson_id.length()-3));


                }
            }
//            System.out.println(script);
//            System.out.println(1);
//            System.out.println(Arrays.toString(script.data().toString().split("var token ?= ?\"(.*?)\"")));
//            String[] data = script.data().toString().split("var");
//            for(String variable : data){
//                System.out.println(variable);
//            }

        }

        Elements userinfos = loginhtml.select(".confirm-user-info > p");//找到用户信息div 课程姓名编号单位
//        System.out.println(userinfos);
        for (Element userinfo : userinfos) { //分布解析课程姓名编号单位信息
            String title = userinfo.text().substring(0, 4);
            String data = userinfo.text().substring(5);
//            System.out.println(userinfo.text());
//            System.out.println(title+":"+data);
            res.put(title, data);
//            System.out.println(data);
        }

        //再次发送请求 放在下面的方法中
//
//        Document user_info = Jsoup.parseBodyFragment(String.valueOf(userinfo));
//        System.out.println(user_info);
//        Elements info = user_info.select("p");
//        System.out.println(info);

//        res.put("code",1);
//        res.put("laravel_session",laravel_session);
        logger.info(String.valueOf(res));
        return res;
    }

    public JSONObject qndxx_confirm(Map<String, Object> userinfo) throws IOException {
//        JSONObject res = new JSONObject();
//        System.out.println(userinfo);
//        System.out.println(userinfo.get("Cookie"));
        String confirm_url = "https://service.jiangsugqt.org/youth/lesson/confirm";
        Connection confirm_con = Jsoup.connect(confirm_url);//江苏省青年大学习确认接口
        Map<String,String> params=new HashMap<>(); //构造参数
        params.put("_token", (String) userinfo.get("token"));

        params.put("lesson_id", (String) userinfo.get("lesson_id"));
//        System.out.println(params);
//        logger.info(String.valueOf(params));
        Connection.Response confirm = confirm_con.ignoreContentType(true).followRedirects(true).data(params).cookies((Map<String, String>) userinfo.get("Cookie")).method(Connection.Method.POST).execute(); //登录
//        System.out.println(confirm.body());
//        JSONObject jsonObject=new JSONObject();
//        System.out.println(JSON.parse(confirm.body()));
        JSONObject result= (JSONObject) JSON.parse(confirm.body());
//        System.out.println(result);
        if (result.get("message").equals("操作成功") && result.get("status").equals(1)){
//            System.out.println("ok");


        }else {
            result.put("message","error");
        }

//        System.out.println(jsonObject.parse(confirm.body()));
        return result;
    }
}
