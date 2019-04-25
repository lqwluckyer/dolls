package com.game.sdk.dolls.service.impl;

import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.dao.GameDao;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.request.game.GameReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.GameService;
import com.game.sdk.dolls.utils.PageUtils;
import com.game.sdk.dolls.vo.GameVo;
import com.game.sdk.dolls.vo.SimpleGameVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameDao gameDao;

    @Autowired
    private DaoSuper daoSuper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public EasyUIResp getGameList(GameReq req) {
        EasyUIResp resp = new EasyUIResp();
        StringBuilder sqlBuilder = new StringBuilder("select * from t_game where state=1 ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_game where state=1 ");
        List params = new ArrayList();
        if(req.getId()!=null){
            sqlBuilder.append(" and id=?");
            countBuilder.append(" and id=?");
            params.add(req.getId());
        }
        if(StringUtils.isNotBlank(req.getGameName())){
            sqlBuilder.append(" and game_name like ?");
            countBuilder.append(" and game_name like ?");
            params.add("%"+req.getGameName()+"%");
        }

        long countNum = daoSuper.count(countBuilder.toString(), params);

        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }

        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<Game> list = daoSuper.findBySql(sqlBuilder.toString(), params, Game.class);
        if(CollectionUtils.isEmpty(list)){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }
        List<GameVo> data = new ArrayList<>();
        for (Game g : list) {
            GameVo vo = new GameVo();
            BeanUtils.copyProperties(g, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }

    @Override
    public EasyUIResp saveGame(GameReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (StringUtils.isBlank(req.getGameName())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏名为空");
            return resp;
        }
        if (StringUtils.isBlank(req.getOriginalName())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏原名为空");
            return resp;
        }
        Game game = null;
        if (req.getId() == null) {
            game = new Game();
            game.setCreateTime(new Date());
        } else {
            game = gameDao.getOne(req.getId());
            if (game == null) {
                resp.setCode(CommonCode.ARGS_ERROR);
                resp.setMsg("该游戏不存在");
                return resp;
            }
            game.setEditTime(new Date());
        }

        game.setId(req.getId());
        game.setGameName(req.getGameName());
        game.setOriginalName(req.getOriginalName());
        game.setGameKey(req.getGameKey());
        game.setGameSecret(req.getGameSecret());
        game.setGameRSAPubKey(req.getGameRSAPubKey());
        game.setGameRSAPriKey(req.getGameRSAPriKey());
        game.setOtherConfig(req.getOtherConfig());
        gameDao.saveAndFlush(game);
        cacheManager.saveGame(game);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp removeGame(GameReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (req.getId() == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏ID为空");
            return resp;
        }
        Game game = gameDao.getOne(req.getId());
        if (game == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("该游戏不存在");
            return resp;
        }
        game.setId(req.getId());
        game.setState(Boolean.FALSE);
        game.setEditTime(new Date());
        gameDao.saveAndFlush(game);
        cacheManager.removeGame(game.getId());
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp getSimpleGame() {
        EasyUIResp resp = new EasyUIResp();
        String sql = "select id,game_name,original_name from t_game where state=1 order by id desc";
        List<Object[]> list = daoSuper.findBySql(sql, null);
        List<SimpleGameVo> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(Object[] obj : list){
                SimpleGameVo vo = new SimpleGameVo();
                vo.setId(Integer.valueOf(obj[0].toString()));
                vo.setGameName(obj[1] == null ? StringUtils.EMPTY : obj[1].toString());
                vo.setOriginalName(obj[2] == null ? StringUtils.EMPTY : obj[2].toString());
                data.add(vo);
            }
            resp.setCode(CommonCode.SUCCESS);
            resp.setRows(data);
            return resp;
        }
        resp.setCode(CommonCode.CHANNEL_NONE);
        return resp;
    }
}
