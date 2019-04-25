<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>玩家管理</title>
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
        <label>用户ID:</label>
        <input type="text" id="id" name="id" class="easyui-textbox" maxlength="255"/>
    </div>
    <div class="search_content">
        <label>渠道用户ID:</label>
        <input type="text" id="channelUserId" name="channelUserId" class="easyui-textbox" maxlength="255"/>
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
            url:'${pageContext.request.contextPath}/admin/userManager/getPlayerList',
            method:'POST',
            idField:'id',
            queryParams:params,
            striped:true,
            fitColumns:true,
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
                {field:'id', title:'用户ID', width:80,align:'center'},
                {field:'userName', title:'用户名', width:80,align:'center'},
                {field:'gameId', title:'游戏ID', width:80,align:'center'},
                {field:'channelId', title:'渠道ID', width:80,align:'center'},
                {field:'deviceId', title:'设备ID', width:80,align:'center' },
                {field:'loginIp', title:'登陆IP', width:80 ,align:'center'},
                {field:'channelUserId', title:'渠道用户ID', width:100 ,align:'center'},
                {field:'channelUserName', title:'渠道用户名', width:100,align:'center' },
                {field:'channelUserNick', title:'渠道用户呢称', width:100 ,align:'center'},
                {field:'createTime', title:'创建时间', width:100,align:'center',formatter: function(value,row,index){
                        if (value==null){
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    } },
                {field:'editTime', title:'修改时间', width:100,align:'center',formatter: function(value,row,index){
                        if (value==null){
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    } },
            ]],
        });
    }

    function showResult(){
        var id = $('[name="id"]').val();
        var gameId = $('#gameId').val();
        var channelId = $('#channelId').val();
        var params={
            id:id,
            gameId:gameId,
            channelId:channelId,
            channelUserId:$('[name="channelUserId"]').val(),
            startTime:$('[name="startTime"]').val(),
            endTime:$('[name="endTime"]').val()
        }
        if(id==null || id==""){
            if(gameId == null || gameId==""){
                $.messager.alert('校验失败','请选择游戏','info');
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
