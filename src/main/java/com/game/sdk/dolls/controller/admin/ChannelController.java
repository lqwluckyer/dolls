package com.game.sdk.dolls.controller.admin;

import com.game.sdk.dolls.request.channel.ChannelReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    public String showList(){
        return "channelList";
    }

    @RequestMapping(value = "/getChannelList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getChannelList(ChannelReq req){

        return channelService.getChannelList(req);
    }

    @RequestMapping(value = "/saveChannel", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp saveChannel(ChannelReq req){

        return channelService.saveChannel(req);
    }

    @RequestMapping(value = "/removeChannel", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp removeChannel(ChannelReq req){

        return channelService.removeChannel(req);
    }

    @RequestMapping(value = "/getSimpleChannel", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getSimpleChannel(){
        return channelService.getSimpleChannel();
    }

    @RequestMapping(value = "/getSimpleChannelByGame", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getSimpleChannelByGame(Integer gameId){
        return channelService.getChannelByGameId(gameId);
    }

}
