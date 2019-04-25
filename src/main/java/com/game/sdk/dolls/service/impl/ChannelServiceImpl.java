package com.game.sdk.dolls.service.impl;

import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.ChannelDao;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.request.channel.ChannelReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.ChannelService;
import com.game.sdk.dolls.utils.PageUtils;
import com.game.sdk.dolls.vo.ChannelVo;
import com.game.sdk.dolls.vo.SimpleChannelVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019-01-22.
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    private static Logger logger = LoggerFactory.getLogger(ChannelServiceImpl.class);

    @Autowired
    private DaoSuper daoSuper;

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public EasyUIResp getChannelByGameId(Integer gameId) {
        EasyUIResp resp = new EasyUIResp();
        if (gameId == null) {
            logger.info("getChannelByGameId id or game id is null");
            return null;
        }
        String sql = "select id,channel_name from t_channel where game_id=? order by id desc";
        List params = new ArrayList();
        params.add(gameId);
        List<Object[]> list = daoSuper.findBySql(sql, params);
        if(CollectionUtils.isNotEmpty(list)){
            List<SimpleChannelVo> data = new ArrayList<>();
            for(Object[] obj : list){
                SimpleChannelVo vo = new SimpleChannelVo();
                vo.setId(Integer.valueOf(obj[0].toString()));
                vo.setChannelName(obj[1].toString());
                data.add(vo);
            }
            resp.setCode(CommonCode.SUCCESS);
            resp.setRows(data);
            return resp;
        }
        resp.setCode(CommonCode.CHANNEL_NONE);
        return resp;
    }

    public Channel getChannelByPrimaryKey(Integer id){
        if(id==null){
            logger.info("getChannelByPrimaryKey id is null");
            return null;
        }
        Channel channel = channelDao.getOne(id);
        return channel;
    }

    @Override
    public EasyUIResp getChannelList(ChannelReq req) {
        EasyUIResp resp = new EasyUIResp();
        StringBuilder sqlBuilder = new StringBuilder("select * from t_channel where state=1 ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_channel where state=1 ");
        List params = new ArrayList();
        if(req.getId()!=null){
            sqlBuilder.append(" and id=?");
            countBuilder.append(" and id=?");
            params.add(req.getId());
        }
        if(req.getGameId()!=null){
            sqlBuilder.append(" and game_id=?");
            countBuilder.append(" and game_id=?");
            params.add(req.getGameId());
        }
        if(StringUtils.isNotBlank(req.getChannelName())){
            sqlBuilder.append(" and channel_name like ?");
            countBuilder.append(" and channel_name like ?");
            params.add("%"+req.getChannelName()+"%");
        }
        long countNum = daoSuper.count(countBuilder.toString(), params);
        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }

        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<Channel> list = daoSuper.findBySql(sqlBuilder.toString(), params, Channel.class);

        List<ChannelVo> data = new ArrayList<>();
        for (Channel c : list) {
            ChannelVo vo = new ChannelVo();
            BeanUtils.copyProperties(c, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }

    @Override
    public EasyUIResp saveChannel(ChannelReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (StringUtils.isBlank(req.getChannelName())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("渠道名为空");
            return resp;
        }
        if (StringUtils.isBlank(req.getParametersConfig())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("渠道参数配置为空");
            return resp;
        }
        if(req.getGameId()==null){
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏ID为空");
            return resp;
        }
        if(req.getSdkId()==null){
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("SDK ID为空");
            return resp;
        }
        Channel channel = null;
        if (req.getId() == null) {
            channel = new Channel();
            channel.setCreateTime(new Date());
        } else {
            channel = channelDao.getOne(req.getId());
            if (channel == null) {
                resp.setCode(CommonCode.ARGS_ERROR);
                resp.setMsg("该渠道不存在");
                return resp;
            }
            channel.setEditTime(new Date());
        }

        channel.setId(req.getId());
        channel.setChannelName(req.getChannelName());
        channel.setGameId(req.getGameId());
        channel.setSdkId(req.getSdkId());
        channel.setParametersConfig(req.getParametersConfig());
        channel.setLoginReqConfig(req.getLoginReqConfig());
        channel.setLoginSignConfig(req.getLoginSignConfig());
        channel.setLoginRespConfig(req.getLoginRespConfig());
        channel.setOrderReqConfig(req.getOrderReqConfig());
        channel.setOrderSignConfig(req.getOrderSignConfig());
        channel.setOrderRespConfig(req.getOrderRespConfig());
        channel.setCallbackConfig(req.getCallbackConfig());
        channel.setCallbackSignConfig(req.getCallbackSignConfig());
        channel.setLoginHandingBean(req.getLoginHandingBean());
        channel.setOrderHandingBean(req.getOrderHandingBean());
        channel.setOpenPay(req.getOpenPay());
        channel.setOpenLogin(req.getOpenLogin());
        channel.setOpenRegister(req.getOpenRegister());
        channelDao.saveAndFlush(channel);
        cacheManager.saveChannel(channel);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp removeChannel(ChannelReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (req.getId() == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("渠道ID为空");
            return resp;
        }
        Channel channel = channelDao.getOne(req.getId());
        if (channel == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("该渠道不存在");
            return resp;
        }
        channel.setId(req.getId());
        channel.setState(Boolean.FALSE);
        channel.setEditTime(new Date());
        channelDao.saveAndFlush(channel);
        cacheManager.removeChannel(channel.getId());
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp getSimpleChannel() {
        EasyUIResp resp = new EasyUIResp();
        String sql = "select id,channel_name from t_channel where state=1 order by id desc";
        List<Object[]> list = daoSuper.findBySql(sql, null);
        List<SimpleChannelVo> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(Object[] obj : list){
                SimpleChannelVo vo = new SimpleChannelVo();
                vo.setId(Integer.valueOf(obj[0].toString()));
                vo.setChannelName(obj[1] == null ? StringUtils.EMPTY : obj[1].toString());
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
