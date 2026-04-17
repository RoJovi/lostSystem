package com.jovi.filter;

import com.jovi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter
@Slf4j
public class filter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("=== AuthFilter 初始化成功 ===");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;	//强转回去
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();	//获取请求路径
        String token = request.getHeader("token");	//获取令牌

        log.info("请求路径: {}", requestURI);
        log.info("请求方法: {}", request.getMethod());

        // CORS 响应头
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, token");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 放行 OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("放行 OPTIONS 请求");
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        if(requestURI.contains("login") || requestURI.contains("register")|| requestURI.contains("upload") ){
            log.info("放行登录/注册请求: {}", requestURI);
            filterChain.doFilter(request,response);	//放行
            return;
        }

        if(token == null || token.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.info("token: {}", token);

        try {
            // 解析 token，获取用户信息
            Claims claims = JwtUtils.parseToken(token);
            Integer userId = (Integer) claims.get("userId");
            Integer userType = (Integer) claims.get("userType");  // 0为普通用户，1为管理员
            log.info("用户 {} 以 {} 身份访问 {}", userId, userType, requestURI);
            // 权限校验
            if (requestURI.contains("/admin")) {
                // 访问管理员接口，必须是 admin
                if (userType != 1) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 无权限
                    return;
                }
            }

            /* 访问普通用户接口，必须是 user
            if (userType != 0) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }*/

            // 发布帖子接口
            if (requestURI.contains("/lost/create") || requestURI.contains("/found/create") || requestURI.contains("my-posts") || requestURI.contains("/messages")) {
                if (userType != 0) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }


            // 存入 request，供 Controller 使用
            request.setAttribute("userId", userId);
            request.setAttribute("userType", userType);

        } catch (Exception e) {
            log.error("token 解析失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request,response);
    }
}
