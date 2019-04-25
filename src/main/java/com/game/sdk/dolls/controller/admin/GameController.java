package com.game.sdk.dolls.controller.admin;

import com.game.sdk.dolls.request.game.GameReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    public String showList(){
        return "gameList";
    }

    @RequestMapping(value = "/getGameList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getGameList(GameReq req){
        return gameService.getGameList(req);
    }

    @RequestMapping(value = "/saveGame", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp saveGame(GameReq req){

        return gameService.saveGame(req);
    }

    @RequestMapping(value = "/removeGame", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp removeGame(GameReq req){

        return gameService.removeGame(req);
    }

    @RequestMapping(value = "/getSimpleGame", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getSimpleGame(){
        return gameService.getSimpleGame();
    }

}
