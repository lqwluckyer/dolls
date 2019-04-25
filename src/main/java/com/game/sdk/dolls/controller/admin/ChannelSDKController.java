package com.game.sdk.dolls.controller.admin;

import com.game.sdk.dolls.request.channelsdk.ChannelSDKReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.ChannelSDKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/channelsdk")
public class ChannelSDKController {

    @Autowired
    private ChannelSDKService channelSDKService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    public String showList(){
        return "channesdklList";
    }

    @RequestMapping(value = "/getChannelSDKList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getChannelSDKList(ChannelSDKReq req){
        return channelSDKService.getChannelSDKList(req);
    }

    @RequestMapping(value = "/saveChannelSDK", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp saveChannelSDK(ChannelSDKReq req){

        return channelSDKService.saveChannelSDK(req);
    }

    @RequestMapping(value = "/removeChannelSDK", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp removeChannelSDK(ChannelSDKReq req){

        return channelSDKService.removeChannelSDK(req);
    }

    @RequestMapping(value = "/getSimpleSDKList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getSimpleSDKList(){
        return channelSDKService.getSimpleSDKList();
    }
}
