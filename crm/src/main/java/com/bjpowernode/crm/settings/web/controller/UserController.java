package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.MD5Util;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 2021/6/4
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(HttpServletRequest request){
         Cookie[] cookies=request.getCookies();
         String loginAct=null;
         String loginPwd=null;
         for(Cookie cookie:cookies){
             String name=cookie.getName();
             if("loginAct".equals(name)){
                 loginAct=cookie.getValue();
                 continue;
             }
             if("loginPwd".equals(name)){
                 loginPwd=cookie.getValue();
             }
         }
         if(loginAct!=null&&loginPwd!=null){
             Map<String,Object> map=new HashMap<>();
             map.put("loginAct", loginAct);
             map.put("loginPwd", MD5Util.getMD5(loginPwd));

             User user=userService.queryUserByLoginAndPwd(map);
             //user存入session
             request.getSession().setAttribute("sessionUser",user);
             //提到到后台
             return "redirect:/workbench/index.do";
         }else{
             return "settings/qx/user/login";
         }

    }

    //处理登录请求
    @RequestMapping("/settings/qx/user/login.do")
    public @ResponseBody Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpServletResponse response, HttpSession session){
        Map<String,Object> map=new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd", MD5Util.getMD5(loginPwd));

        User user=userService.queryUserByLoginAndPwd(map);
        System.out.println(request.getRemoteAddr());

        //返回对象
        ReturnObject returnObject=new ReturnObject();
        //用户名不可以为空
        if(user==null){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL); //0失败 404 500 401
            returnObject.setMessage("用户名或密码错误");
        }else {
            //用户是否过期
            if(DateUtils.formatDateTime(new Date()).compareTo(user.getExpireTime())>0){
                //账号已经过期，登录失败
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL); //0失败 404 500 401
                returnObject.setMessage("账号已经过期");
            }else if("0".equals(user.getLockState())){ //null
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL); //0失败 404 500 401
                returnObject.setMessage("账号已经锁定");
                //验证ip地址是否在允许的ip,192.168.1.1,127.0.0.1,192.168.137.128->192.168.137.129
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){//0.0.0.0.0:1
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL); //0失败 404 500 401
                returnObject.setMessage("此ip不可能访问");
            }else{
                //登录成功
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS); //1成功 http 200成功
                //保存user到sessiion中去，验证是否登录做准备
                session.setAttribute(Contants.SESSION_USER, user);

                //免登录功能
                if("true".equals(isRemPwd)){
                    //生成cookie
                    Cookie c1=new Cookie("loginAct",loginAct);
                    c1.setMaxAge(10*24*60*60);//保存的时间
                    response.addCookie(c1);

                    Cookie c2=new Cookie("loginPwd",loginPwd);
                    c2.setMaxAge(10*24*60*60);//保存的时间
                    response.addCookie(c2);
                }else{
                    //不保存cookie,清cookie
                   // response.delete(c1);
                    //生成cookie
                    Cookie c1=new Cookie("loginAct",null);
                    c1.setMaxAge(0);//保存的时间
                    response.addCookie(c1);

                    Cookie c2=new Cookie("loginPwd",null);
                    c2.setMaxAge(0);//保存的时间
                    response.addCookie(c2);
                }


            }
        }

        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){
        //清空cookie
        Cookie c1=new Cookie("loginAct",null);
        c1.setMaxAge(0);//保存的时间
        response.addCookie(c1);

        Cookie c2=new Cookie("loginPwd",null);
        c2.setMaxAge(0);//保存的时间
        response.addCookie(c2);

        //销毁session
        session.invalidate();

        return "redirect:/";
    }

}
