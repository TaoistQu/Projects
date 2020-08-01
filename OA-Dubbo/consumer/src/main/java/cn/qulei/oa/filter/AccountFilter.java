package cn.qulei.oa.filter;

import cn.qulei.oa.util.FilterUtil;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Description:    用户权限处理
 ** @Author:      QuLei
 * @CreateDate:   2019-07-13 15:43
 * @Version:      1.0
 */
@Component
@WebFilter(urlPatterns = "/*")
public class AccountFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String uri = req.getRequestURI();

        //判断当前访问的uri是否在列表中
        if(FilterUtil.canPassIgnore(uri)){
            chain.doFilter(req,resp);
            return ;
        }
        //判断是否登录
        Object account = session.getAttribute("account");
        if(null == account){
            resp.sendRedirect("/account/login");
            return;
        }

        chain.doFilter(req,resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //加载Filter启动之前需要的资源
        System.out.println("----------AccountFilter init-----------");
    }
}
