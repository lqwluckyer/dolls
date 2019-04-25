package com.game.sdk.dolls.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.service.PlayerRoleService;
import com.game.sdk.dolls.service.PlayerService;
import com.game.sdk.dolls.service.ServerAreaService;
import com.game.sdk.dolls.utils.IpUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2019-01-21.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private ServerAreaService serverAreaService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRoleService playerRoleService;

    @RequestMapping(value = "/getLoginToken", method = {RequestMethod.POST})
    public BaseResp getLoginToken(HttpServletRequest request, HttpServletResponse response, LoginReq req) {
        try {
            req.setLoginIp(IpUtils.getRealIpAddress(request));
            BaseResp resp = playerService.getToken(req);
            return resp;
        } catch (Exception e) {
            logger.info("player login Exception, extension:" + req.getExtension() + " app:" + req.getAppId() + " channelId:" + req.getChannelId(), e);
            BaseResp resp = new BaseResp();
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return resp;
        }
    }

    /***
     *
     */
    @RequestMapping(value = "/verifyAccount", method = {RequestMethod.POST})
    public BaseResp verifyAccount(LoginReq req) {
        try {
            BaseResp resp = playerService.loginVerify(req);
            return resp;
        } catch (Exception e) {
            logger.info("loginVerify Exception req:" + JSON.toJSONString(req), e);
            BaseResp resp = new BaseResp();
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return resp;
        }
    }

    @RequestMapping(value = "/upServerArea", method = {RequestMethod.POST})
    public BaseResp upServerArea(LoginReq req) {
        try {
            BaseResp resp = serverAreaService.upServerArea(req);
            return resp;
        } catch (Exception e) {
            logger.info("upServerArea Exception req:" + JSON.toJSONString(req), e);
            BaseResp resp = new BaseResp();
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return resp;
        }
    }

    @RequestMapping(value = "/upPlayerRole", method = {RequestMethod.POST})
    public BaseResp upPlayerRole(HttpServletRequest request,LoginReq req) {
        try {
            req.setLoginIp(IpUtils.getRealIpAddress(request));
            BaseResp resp = playerRoleService.upPlayerRole(req);
            return resp;
        } catch (Exception e) {
            logger.info("upPlayerRole Exception req:" + JSON.toJSONString(req), e);
            BaseResp resp = new BaseResp();
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return resp;
        }
    }

    @RequestMapping(value = "/test1", method = {RequestMethod.POST, RequestMethod.GET})
    public String test1(){
        return "S";
    }

    @RequestMapping(value = "/test2", method = {RequestMethod.POST, RequestMethod.GET})
    public BaseResp test2(){
        BaseResp resp = new BaseResp();
        resp.setCode(0);
        return resp;
    }

    @RequestMapping(value = "/test3", method = {RequestMethod.POST, RequestMethod.GET})
    public BaseResp test3(){
        BaseResp resp = new BaseResp();
        JSONObject data = new JSONObject();
        data.put("userId",12233);
        resp.setCode(0);
        resp.setData(data);
        return resp;
    }
}
