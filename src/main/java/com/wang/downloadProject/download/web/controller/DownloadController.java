package com.wang.downloadProject.download.web.controller;

import com.google.code.kaptcha.Producer;
import com.wang.downloadProject.Exception.ValidateCodeException;
import com.wang.downloadProject.download.constant.Constant;
import com.wang.downloadProject.download.service.DownloadService;
import com.wang.downloadProject.utils.EncodeBase64;
import com.wang.downloadProject.utils.WriteJsonUntil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping(value = "/download")
@Controller
public class DownloadController {
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private Producer captchaProducer;

    private final String VALIDATE_CODE = "VALIDATE_CODE";

    private final String EXPIRE_TIME = "EXPIRE_TIME";

    public DownloadController() {
    }

    @RequestMapping(value = "/getValidateCode.do",method = GET)
    public void getJpg(HttpServletRequest request, HttpServletResponse response){
        try {
            HttpSession session = request.getSession();

            // 设置清除浏览器缓存
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/png");

            // 验证码一分钟内有效
            long expireTime = System.currentTimeMillis() + Constant.TIME;

            // 将验证码放到session中
            String validateCode = captchaProducer.createText();
            session.setAttribute(VALIDATE_CODE, EncodeBase64.encodeBase64(validateCode));//将加密后的验证码放到session中，确保安全
            session.setAttribute(EXPIRE_TIME, expireTime);

            // 输出验证码图片
            BufferedImage bufferedImage = captchaProducer.createImage(validateCode);
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(bufferedImage, "png", out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getPath.do",method = GET ,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object getPath(){
        //创建map
        Map<String,Object> map = new HashMap();
        //创建json
        String json =null;
        try {
            //调用service层
            String path = downloadService.getPath();
            //创建成功消息
            map.put("success",true);
            map.put("path",path);
            json = WriteJsonUntil.printJsonObj(map);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            //创建失败消息
            map.put("success",false);
            map.put("msg",msg);
            json = WriteJsonUntil.printJsonObj(map);
        }
        return json;
    }


    @RequestMapping(value = "/getURL.do",method = POST ,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object startDownload(String webPath,String locPath ,String validateCode,HttpServletRequest request){
        //获取系统时间
        long time = (long) request.getSession().getAttribute(EXPIRE_TIME);
        //创建map
        Map<String,Object> map = new HashMap();
        //创建json
        String json =null;
        try {
            //判断验证码是否输入错误
            if(null == validateCode||"".equals(validateCode)){
                throw new ValidateCodeException("验证码错误,请重新输入");
            }
            //判断验证码是否超时
            if(System.currentTimeMillis()>time){
                throw new ValidateCodeException("验证码超时，请重新输入");
            }
            //调用service层
            downloadService.createDownload(webPath,locPath,request);
            //创建成功消息
            json = WriteJsonUntil.printJsonFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            //创建失败消息
            map.put("success",false);
            map.put("msg",msg);
            json = WriteJsonUntil.printJsonObj(map);
        }
        return json;
    }

    @RequestMapping(value = "/startDownload.do",method = GET ,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object startDownload1(HttpServletRequest request){
        //get url Path length
        String webPath = (String) request.getSession().getAttribute("webPath");
        String locPath = (String) request.getSession().getAttribute("locPath");
        long length =(long) request.getSession().getAttribute("length");
        //将下载字节长度改为MB
        float length_MB = (float) length/ Constant.MB;
        //创建map
        Map<String,Object> map = new HashMap();
        //创建json
        String json =null;
        String msg = null;
        try {
            //调用service层
            long pre = System.currentTimeMillis();
            boolean res = downloadService.startDownload(webPath,locPath,length);
            long end =System.currentTimeMillis();
            //下载大小
            String FileLength = String.format("%.2f",length_MB);
            //下载时间（s）
            int time = (int) (end - pre)/1000;
            //平均下载速度
            String speed = String.format("%.2f", length_MB / time);
            //创建成功消息
            map.put("success",true);
            msg = "下载成功,本次下载大小："+FileLength+"Mb，下载时间："+time+"(s)，平均下载速度："+speed+"Mb/s";
            map.put("msg",msg);
            json = WriteJsonUntil.printJsonObj(map);
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getMessage();
            //创建失败消息
            map.put("success",false);
            map.put("msg",msg);
            json = WriteJsonUntil.printJsonObj(map);
        }
        return json;
    }

}
