package com.game.sdk.dolls.service.impl;

import com.alibaba.fastjson.JSON;
import com.game.sdk.dolls.bean.SnowflakeWorkerId;
import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.constants.PayState;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.dao.PayOrderDao;
import com.game.sdk.dolls.entity.*;
import com.game.sdk.dolls.request.payorder.PayOrderReq;
import com.game.sdk.dolls.request.payorder.SearchPayOrderReq;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.sdk.SDKOrderHandle;
import com.game.sdk.dolls.service.PayOrderService;
import com.game.sdk.dolls.service.PlayerRoleService;
import com.game.sdk.dolls.service.PlayerService;
import com.game.sdk.dolls.utils.CommonUtils;
import com.game.sdk.dolls.utils.PageUtils;
import com.game.sdk.dolls.vo.PayOrderVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PayOrderServiceImpl implements PayOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderServiceImpl.class);

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PlayerRoleService playerRoleService;

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private SnowflakeWorkerId workerId;

    @Autowired(required = false)
    @Qualifier("DefaultSDKOrderHandle")
    private SDKOrderHandle orderHandle;

    @Autowired
    private DaoSuper daoSuper;

    @Override
    public BaseResp getOrderInfo(PayOrderReq req) {
        BaseResp resp = new BaseResp();
        if (req.getUserId() == null) {
            logger.info("userId is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }
        Player player = playerService.getPlayerByPrimaryKey(req.getUserId());
        if (player == null) {
            logger.info("player is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }
        Game game = cacheManager.getGame(player.getGameId());
        if (game == null) {
            logger.info("game is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.GAME_NONE);
            return resp;
        }
        if(!sign(req, game)){
            logger.info("sign fail req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SIGN_ERROR);
            return resp;
        }
        Channel channel = cacheManager.getChannel(player.getChannelId());
        if(channel == null){
            logger.info("channel is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.CHANNEL_NONE);
            return resp;
        }
        if(!channel.getOpenPay()){
            logger.info("channel close pay req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.PAY_CLOSED);
            return resp;
        }
        if(!Objects.equals(game.getId(), channel.getGameId())){
            logger.info("channelId not match gameId req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }
        ChannelSDK sdk = cacheManager.getChannelSDK(channel.getSdkId());
        if(sdk == null){
            logger.info("sdk is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SDK_NONE);
            return resp;
        }
        LoginReq loginReq = new LoginReq();
        loginReq.setServerId(req.getServerId());
        loginReq.setRoleId(req.getRoleId());
        PlayerRole playerRole = playerRoleService.getPlayerRole(player, loginReq);
        if(playerRole == null){
            logger.info("role is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }

        PayOrder payOrder = save(req, player, playerRole);
        logger.info("player create order success , orderId:{}, playerId:{}", payOrder.getId(), player.getId());
        resp = orderHandle.handleChannelOrder(payOrder, player, channel,sdk);
        logger.info("getOrderInfo success, resp data:{}, playerId:{}", JSON.toJSONString(resp), player.getId());
        return resp;
    }

    @Override
    public PayOrder getPayOrderWithLock(Long id) {
        if(id==null){
            return null;
        }
        return payOrderDao.getPayOrderWithLock(id);
    }

    @Override
    public void savePayOrder(PayOrder payOrder) {
        if(payOrder!=null){
            payOrderDao.saveAndFlush(payOrder);
        }
    }

    @Override
    public EasyUIResp getPayOrderList(SearchPayOrderReq req) {
        EasyUIResp resp = new EasyUIResp();
        if(req.getId() ==null && (req.getGameId()==null || req.getChannelId()==null)){
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏或订单号为空");
            return resp;
        }

        StringBuilder sqlBuilder = new StringBuilder("select * from t_pay_order where ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_pay_order where ");
        List params = new ArrayList();
        boolean hasParams = Boolean.FALSE;
        if(req.getId()!=null){
            sqlBuilder.append(" id=?");
            countBuilder.append(" id=?");
            params.add(req.getId());
            hasParams = Boolean.TRUE;
        }
        if(req.getGameId()!=null){
            if(hasParams){
                sqlBuilder.append(" and game_id=?");
                countBuilder.append(" and game_id=?");
            }else{
                sqlBuilder.append(" game_id=?");
                countBuilder.append(" game_id=?");
            }
            params.add(req.getGameId());
            hasParams = Boolean.TRUE;
        }
        if(req.getChannelId()!=null){
            if(hasParams){
                sqlBuilder.append(" and channel_id=?");
                countBuilder.append(" and channel_id=?");
            }else{
                sqlBuilder.append(" channel_id=?");
                countBuilder.append(" channel_id=?");
            }
            params.add(req.getChannelId());
            hasParams = Boolean.TRUE;
        }
        if(req.getPlayerId()!=null){
            if(hasParams){
                sqlBuilder.append(" and player_id=?");
                countBuilder.append(" and player_id=?");
            }else{
                sqlBuilder.append(" player_id=?");
                countBuilder.append(" player_id=?");
            }
            params.add(req.getPlayerId());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getChannelOrderId())){
            if(hasParams){
                sqlBuilder.append(" and channel_order_id=?");
                countBuilder.append(" and channel_order_id=?");
            }else{
                sqlBuilder.append(" channel_order_id=?");
                countBuilder.append(" channel_order_id=?");
            }
            params.add(req.getChannelOrderId());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getRoleId())){
            if(hasParams){
                sqlBuilder.append(" and role_id=?");
                countBuilder.append(" and role_id=?");
            }else{
                sqlBuilder.append(" role_id=?");
                countBuilder.append(" role_id=?");
            }
            params.add(req.getRoleId());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getStartTime())){
            if(hasParams){
                sqlBuilder.append(" and create_time>=?");
                countBuilder.append(" and create_time>=?");
            }else{
                sqlBuilder.append(" create_time>=?");
                countBuilder.append(" create_time>=?");
            }
            params.add(req.getStartTime());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getEndTime())){
            if(hasParams){
                sqlBuilder.append(" and create_time<=?");
                countBuilder.append(" and create_time<=?");
            }else{
                sqlBuilder.append(" create_time<=?");
                countBuilder.append(" create_time<=?");
            }
            params.add(req.getEndTime());
            hasParams = Boolean.TRUE;
        }
        if(hasParams){
            sqlBuilder.append(" and state=?");
            countBuilder.append(" and state=?");
        }else{
            sqlBuilder.append(" state=?");
            countBuilder.append(" state=?");
        }
        params.add(CommonCode.NUMBER1);

        long countNum = daoSuper.count(countBuilder.toString(), params);

        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }
        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<PayOrder> list = daoSuper.findBySql(sqlBuilder.toString(), params, PayOrder.class);

        List<PayOrderVo> data = new ArrayList<>();
        for (PayOrder c : list) {
            PayOrderVo vo = new PayOrderVo();
            BeanUtils.copyProperties(c, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }

    private PayOrder save(PayOrderReq req, Player player, PlayerRole playerRole){
        PayOrder payOrder = new PayOrder();
        payOrder.setId(workerId.nextId());
        payOrder.setGameId(player.getGameId());
        payOrder.setChannelId(player.getChannelId());
        payOrder.setPlayerId(player.getId());
        payOrder.setUserName(player.getUserName());
        payOrder.setSysRoleId(playerRole.getId());
        payOrder.setProductId(req.getProductId());
        payOrder.setProductName(req.getProductName());
        payOrder.setProductDesc(req.getProductDesc());
        payOrder.setMoney(req.getMoney());
        payOrder.setPrice(req.getPrice());
        payOrder.setBuyNumbers(req.getBuyNumbers());
        payOrder.setCurrency(CommonCode.NUMBER1);
        payOrder.setRoleId(req.getRoleId());
        payOrder.setRoleName(req.getRoleName());
        payOrder.setServerId(req.getServerId());
        payOrder.setServerName(req.getServerName());
        payOrder.setPayState(PayState.STATE_PAYING);
        payOrder.setState(Boolean.TRUE);
        payOrder.setCreateTime(new Date());
        payOrder.setExtension(req.getExtension());
        payOrder.setNotifyUrl(req.getNotifyUrl());
        payOrderDao.saveAndFlush(payOrder);
        return payOrder;
    }

    private boolean sign(PayOrderReq req, Game game) {
        StringBuilder signSourceBuilder = new StringBuilder();
        signSourceBuilder.append("userId=").append(CommonUtils.null2emptyString(req.getUserId()))
                .append("&productId=").append(CommonUtils.null2emptyString(req.getProductId()))
                .append("&productName=").append(CommonUtils.null2emptyString(req.getProductName()))
                .append("&productDesc=").append(CommonUtils.null2emptyString(req.getProductDesc()))
                .append("&money=").append(CommonUtils.null2emptyString(req.getMoney()))
                .append("&price=").append(CommonUtils.null2emptyString(req.getPrice()))
                .append("&buyNumbers=").append(CommonUtils.null2emptyString(req.getBuyNumbers()))
                .append("&roleId=").append(CommonUtils.null2emptyString(req.getRoleId()))
                .append("&roleName=").append(CommonUtils.null2emptyString(req.getRoleName()))
                .append("&roleLevel=").append(CommonUtils.null2emptyString(req.getRoleLevel()))
                .append("&serverId=").append(CommonUtils.null2emptyString(req.getServerId()))
                .append("&serverName=").append(CommonUtils.null2emptyString(req.getServerName()))
                .append("&extension=").append(CommonUtils.null2emptyString(req.getExtension()))
                .append("&notifyUrl=").append(CommonUtils.null2emptyString(req.getNotifyUrl()))
                .append("&key=").append(game.getGameKey());

        String signSource = signSourceBuilder.toString();
        logger.info("sign source : {}", signSource);
        String sign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
        return Objects.equals(sign, req.getSign());

    }
}
