<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>支付订单管理</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/bootstrap/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <style type="text/css">
        .search_content {
            width: 280px;
            float: left;
            margin: 10px;
        }
        .clear{ clear:both}
    </style>
</head>
<body>
<div>
    <div class="search_content">
        <label>游戏:</label>
        <input type="hidden" id="gameId">
        <input type="text" id="allGames" class="easyui-combobox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>渠道:</label>
        <input type="hidden" id="channelId">
        <input type="text" id="allChannels" class="easyui-combobox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>订单号:</label>
        <input type="text" id="id" name="id" class="easyui-textbox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>渠道订单号:</label>
        <input type="text" id="channelOrderId" name="channelOrderId" class="easyui-textbox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>用户ID:</label>
        <input type="text" id="playerId" name="playerId" class="easyui-textbox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>角色ID:</label>
        <input type="text" id="roleId" name="roleId" class="easyui-textbox" maxlength="255"/>
    </div>
    <div class="search_content" style="width: 400px;">
        <label>创建时间:</label>
        <input class="easyui-datetimebox" name="startTime" data-options="showSeconds:true" style="width:150px">
        -
        <input class="easyui-datetimebox" name="endTime" data-options="showSeconds:true" style="width:150px">
    </div>
    <div class="search_content" style="width: 130px;">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-search" onclick="showResult()" style="width:90px">搜索</a>
    </div>
</div>
<div class="clear"></div>
<div id="dataList"></div>

</body>
<script type="application/javascript">

    window.onload=function(){
        initGame();
    }

    function initDataList(params) {
        $("#dataList").datagrid({
            url:'${pageContext.request.contextPath}/admin/payOrder/getPayOrderList',
            method:'POST',
            idField:'id',
            queryParams:params,
            striped:true,
            fitColumns:false,
            singleSelect:true,
            rownumbers:true,
            pagination:true,
            pagePosition:'bottom',
            pageNumber:1,
            pageSize:20,
            nowrap:true,
            loadMsg:'数据加载中...',
            showFooter:true,
            columns:[[
                {field:'id', title:'订单ID', width:150, align:'center'},
                {field:'playerId', title:'用户ID', width:150,align:'center'},
                {field:'gameId', title:'游戏ID', width:80,align:'center'},
                {field:'channelId', title:'渠道ID', width:80,align:'center'},
                {field:'sysRoleId', title:'系统角色ID', width:150 ,align:'center'},
                {field:'channelOrderId', title:'渠道订单ID', width:150 ,align:'center'},
                {field:'money', title:'金额(单位:分)', width:80 ,align:'center'},
                {field:'payState', title:'支付状态', width:80,align:'center' ,formatter: function(value,row,index){
                            if (value==0){
                                return "待支付";
                            }else if(value == 1){
                                return "待发货"
                            } else if(value == 2){
                                return "成功"
                            } else if(value == 3){
                                return "发货失败"
                            }else {
                                return "异常订单";
                            }
                        }},
                {field:'productId', title:'物品ID', width:150 ,align:'center'},
                {field:'productName', title:'物品名', width:150 ,align:'center'},
                {field:'productDesc', title:'物品说明', width:150 ,align:'center'},
                {field:'roleId', title:'角色ID', width:100 ,align:'center'},
                {field:'serverId', title:'区服ID', width:100 ,align:'center'},
                {field:'extension', title:'扩展内容', width:150 ,align:'center'},
                {field:'notifyUrl', title:'CP发货地址', width:100 ,align:'center'},
                {field:'cpResp', title:'CP响应内容', width:100 ,align:'center'},
                {field:'createTime', title:'创建时间', width:150,align:'center',formatter: function(value,row,index){
                        if (value==null){
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    } },
                {field:'sdkOrderTime', title:'渠道回调时间', width:150,align:'center',formatter: function(value,row,index){
                        if (value==null){
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    } },
                {field:'completeTime', title:'CP发货时间', width:150,align:'center',formatter: function(value,row,index){
                        if (value==null){
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    } },
            ]]
        });
    }

    function showResult(){
        var id = $('[name="id"]').val();
        var gameId = $('#gameId').val();
        var channelId = $('#channelId').val();
        var params={
            id:id,
            playerId:$('[name="playerId"]').val(),
            gameId:gameId,
            channelId:channelId,
            channelOrderId:$('[name="channelOrderId"]').val(),
            roleId:$('[name="roleId"]').val(),
            startTime:$('[name="startTime"]').val(),
            endTime:$('[name="endTime"]').val()
        }
        if(id == null || id==""){
            if(gameId == null || gameId==""){
                $.messager.alert('校验失败', '游戏名不能为空', 'info');
                return;
            }
            if(channelId == null || channelId==""){
                $.messager.alert('校验失败','请选择渠道','info');
                return;
            }
        }
        initDataList(params);
    }

    function initGame() {
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/admin/game/getSimpleGame',
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    $('#allGames').combobox({
                        valueField: 'id',
                        textField: 'gameName',
                        data: data.rows,
                        onSelect: function (res) {
                            $("#gameId").val(res.id);
                            initChannel();
                        }
                    });
                } else {
                    console.log("getSimpleGame fail msg:" + data);
                }
            }
        });
    }

    function initChannel() {
        var gameId = $('#gameId').val();
        if(gameId == null || gameId==""){
            $.messager.alert('校验失败','请选择游戏','info');
            return;
        }
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/admin/channel/getSimpleChannelByGame?gameId='+gameId,
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    $('#allChannels').combobox({
                        valueField: 'id',
                        textField: 'channelName',
                        data: data.rows,
                        onSelect: function (res) {
                            $("#channelId").val(res.id);
                        }
                    });
                } else {
                    console.log("getSimpleSDKList fail msg:" + data);
                }
            }
        });
    }
</script>
</html>
