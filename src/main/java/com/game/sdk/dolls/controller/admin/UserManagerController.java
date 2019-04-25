package com.game.sdk.dolls.controller.admin;

import com.game.sdk.dolls.request.player.SearchPlayerReq;
import com.game.sdk.dolls.request.player.SearchPlayerRoleReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/userManager")
public class UserManagerController {

    @Autowired
    private PlayerService playerService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    public String showList(){
        return "playerList";
    }

    @RequestMapping(value = "/getPlayerList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getPlayerList(SearchPlayerReq req){

        return playerService.getPlayerList(req);
    }

    @RequestMapping(value = "/showPlayerList", method = RequestMethod.GET)
    public String showPlayerList(){
        return "playerRoleList";
    }

    @RequestMapping(value = "/getPlayerRoleList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getPlayerRoleList(SearchPlayerRoleReq req){

        return playerService.getPlayerRoleList(req);
    }
}
