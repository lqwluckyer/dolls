package com.game.sdk.dolls.service.impl;

import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.ChannelSDKDao;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.request.channelsdk.ChannelSDKReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.ChannelSDKService;
import com.game.sdk.dolls.utils.PageUtils;
import com.game.sdk.dolls.vo.ChannelSDKVo;
import com.game.sdk.dolls.vo.SimpleChannelSDKVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChannelSDKServiceImpl implements ChannelSDKService {

    @Autowired
    private ChannelSDKDao channelSDKDao;

    @Autowired
    private DaoSuper daoSuper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public EasyUIResp getChannelSDKList(ChannelSDKReq req) {
        EasyUIResp resp = new EasyUIResp();
        StringBuilder sqlBuilder = new StringBuilder("select * from t_channel_sdk where state=1 ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_channel_sdk where state=1 ");
        List params = new ArrayList();

        if(req.getId()!=null){
            sqlBuilder.append(" and id=?");
            countBuilder.append(" and id=?");
            params.add(req.getId());
        }
        if(StringUtils.isNotBlank(req.getSdkName())){
            sqlBuilder.append(" and sdk_name like ?");
            countBuilder.append(" and sdk_name like ?");
            params.add("%"+req.getSdkName()+"%");
        }
        long countNum = daoSuper.count(countBuilder.toString(), params);
        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }

        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<ChannelSDK> list = daoSuper.findBySql(sqlBuilder.toString(), params, ChannelSDK.class);

        List<ChannelSDKVo> data = new ArrayList<>();
        for (ChannelSDK sdk : list) {
            ChannelSDKVo vo = new ChannelSDKVo();
            BeanUtils.copyProperties(sdk, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }

    @Override
    public EasyUIResp saveChannelSDK(ChannelSDKReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (StringUtils.isBlank(req.getSdkName())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("SDK名为空");
            return resp;
        }
        if (StringUtils.isBlank(req.getSdkShortName())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("SDK简称名为空");
            return resp;
        }

        if (StringUtils.isBlank(req.getVerifyClass())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("验证SDK为空");
            return resp;
        }

        if (StringUtils.isBlank(req.getPayCallbackUrl())) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("支付回调地址为空");
            return resp;
        }

        ChannelSDK sdk = null;
        if (req.getId() == null) {
            sdk = new ChannelSDK();
            sdk.setCreateTime(new Date());
        } else {
            sdk = channelSDKDao.getOne(req.getId());
            if (sdk == null) {
                resp.setCode(CommonCode.ARGS_ERROR);
                resp.setMsg("该SDK不存在");
                return resp;
            }
            sdk.setEditTime(new Date());
        }

        sdk.setId(req.getId());
        sdk.setSdkName(req.getSdkName());
        sdk.setSdkShortName(req.getSdkShortName());
        sdk.setAuthUrl(req.getAuthUrl());
        sdk.setPayCallbackUrl(req.getPayCallbackUrl());
        sdk.setVerifyClass(req.getVerifyClass());
        sdk.setOrderUrl(req.getOrderUrl());
        sdk.setOtherConfig(req.getOtherConfig());
        channelSDKDao.saveAndFlush(sdk);
        cacheManager.saveChannelSDK(sdk);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp removeChannelSDK(ChannelSDKReq req) {
        EasyUIResp resp = new EasyUIResp();
        if (req.getId() == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("SDK ID为空");
            return resp;
        }
        ChannelSDK sdk = channelSDKDao.getOne(req.getId());
        if (sdk == null) {
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("该SDK不存在");
            return resp;
        }
        sdk.setId(req.getId());
        sdk.setState(Boolean.FALSE);
        sdk.setEditTime(new Date());
        channelSDKDao.saveAndFlush(sdk);
        cacheManager.removeChannelSDK(sdk.getId());
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public EasyUIResp getSimpleSDKList() {
        EasyUIResp resp = new EasyUIResp();
        String sql = "select id,sdk_name from t_channel_sdk where state=1 order by id desc";
        List<Object[]> list = daoSuper.findBySql(sql, null);
        List<SimpleChannelSDKVo> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(Object[] obj : list){
                SimpleChannelSDKVo vo = new SimpleChannelSDKVo();
                vo.setId(Integer.valueOf(obj[0].toString()));
                vo.setSdkName(obj[1] == null ? StringUtils.EMPTY : obj[1].toString());
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
