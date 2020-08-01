package cn.qulei.oa.util;

import org.springframework.stereotype.Component;

@Component
public class FilterUtil {
    //不需要登录的uri
    private static final String [] IGNORE_URI = {"/index","/css/","/account/login","/js/","/images"
            ,"/account/validataAccount","/account/register"};
    /**
    @Description:  判断访问的uri是否是可以被放行的
    * @author      QuLei
    * @return      如果有权访问返回true  否则返回false
    * @exception
    * @date        2019-07-13 15:58
    */
    public static boolean canPassIgnore(String uri){
        for (String val: IGNORE_URI) {

            if(uri.startsWith(val)){
                return true;
            }
        }
        return  false;
    }
}
