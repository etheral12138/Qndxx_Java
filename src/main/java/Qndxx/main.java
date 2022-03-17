package Qndxx;

import java.io.IOException;
import java.util.Map;

public class main {
    public static void main(String[] args) throws IOException {
        Qndxx qndxx=new Qndxx();
        String laravel_session="8rAucTd84mpMLxilmCjeWO08rbtC7opDnrwo9YvJ";//laravel_session需要自行抓包

        Map<String,Object> userinfo=qndxx.qndxx_action(laravel_session);//调用qndxx_action方法 返回map信息

        if (userinfo.size() > 2) {
            System.out.println(userinfo);
            System.out.println(qndxx.qndxx_confirm(userinfo));//调用qndxx_confirm方法 返回json信息
        }else {
            System.out.println("error");
        }

    }
}
