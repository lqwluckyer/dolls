<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>渠道管理</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/static/css/easyui/bootstrap/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/js/easyui/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <style type="text/css">
        label {
            display: inline-block;
            width: 100px;
            text-align: right;
            margin-top: 5px;
            margin-right: 10px;
            margin-left: 10px;
        }

        input {
            width: 350px;
            margin-top: 5px;
        }

        li {
            list-style: none;
            font-size: 14px;
            margin: 2px;
        }

        select {
            text-align: center;
            height: 28px;
            line-height: 28px;
            width: 348px;
        }
        .search_content {
            width: 300px;
            float: left;
            margin: 10px;
        }
        .clear{ clear:both}
    </style>
</head>
<body>
<div>
    <div class="search_content" >
        <label style="width: 80px;">渠道ID:</label>
        <input  id="reqId" name="reqId" type="text" class="easyui-textbox" style="width: 180px;">
    </div>
    <div class="search_content">
        <label style="width: 80px;">渠道名:</label>
        <input  id="reqName" name="reqName" type="text" class="easyui-textbox" style="width: 180px;">
    </div>
    <div class="search_content">
        <label style="width: 80px;">游戏ID:</label>
        <input  id="reqGameId" name="reqGameId" type="text" class="easyui-textbox" style="width: 180px;">
    </div>
    <div class="search_content">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-search" onclick="showResult()" style="width:90px">搜索</a>
    </div>
</div>
<div class="clear"></div>
<div id="dataTools" style="display: none;">
    <a id="add" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'"
       onclick="javascript:showAddDialog();">新增</a>
    <a id="save" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'"
       onclick="javascript:showSaveDialog();">修改</a>
    <a id="remove" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove'"
       onclick="javascript:remove();">删除</a>
</div>
<div id="dataList"></div>

<div id="dialog_add" class="easyui-dialog" data-options="buttons:'#dialog_tool',modal:true" closed="true"
     style="height: 600px;width: 600px;display: none;">
    <form id="saveData" method="post">
        <input type="hidden" id="id" name="id"/>
        <input type="hidden" id="gameId" name="gameId"/>
        <input type="hidden" id="sdkId" name="sdkId"/>
        <div>
            <label>渠道名称：</label>
            <input id="channelName" type="text" class="easyui-textbox" name="channelName" maxlength="255"
                   required="true"/>
        </div>

        <div>
            <label>游戏：</label>
            <input type="text" id="allGames" class="easyui-combobox" maxlength="255" required="true"/>
        </div>

        <div>
            <label>SDK：</label>
            <input type="text" id="allSdks" class="easyui-combobox" maxlength="255" required="true"/>
        </div>

        <div>
            <label>登陆处理bean：</label>
            <input type="text" id="loginHandingBean" class="easyui-textbox" name="loginHandingBean" maxlength="255"
                   required="true"/>
        </div>

        <div>
            <label>下单处理bean：</label>
            <input type="text" id="orderHandingBean" class="easyui-textbox" name="orderHandingBean" maxlength="255"
                   required="true"/>
        </div>

        <div>
            <label>打开支付：</label>
            <select id="openPay" name="openPay">
                <option value="true" selected="selected">打开</option>
                <option value="false">关闭</option>
            </select>
        </div>

        <div>
            <label>打开登陆：</label>
            <select id="openLogin" name="openLogin">
                <option value="true" selected="selected">打开</option>
                <option value="false">关闭</option>
            </select>
        </div>

        <div>
            <label>打开注册：</label>
            <select id="openRegister" name="openRegister">
                <option value="true" selected="selected">打开</option>
                <option value="false">关闭</option>
            </select>
        </div>

        <div>
            <label>参数配置(json)：</label>
            <textarea id="parametersConfig" rows="10" cols="36" readonly="readonly" name="parametersConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfigJson('parametersConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>登陆配置(json)：</label>
            <textarea id="loginReqConfig" rows="10" cols="36" readonly="readonly" name="loginReqConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton"
                     onclick="showConfig('loginReqConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>登陆配置签名(json)：</label>
            <textarea id="loginSignConfig" rows="10" cols="36" readonly="readonly" name="loginSignConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton"
                     onclick="showConfig('loginSignConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>登陆响应配置(json)：</label>
            <textarea id="loginRespConfig" rows="10" cols="36" readonly="readonly" name="loginRespConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfigJson('loginRespConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>下单配置(json)：</label>
            <textarea id="orderReqConfig" rows="10" cols="36" readonly="readonly" name="orderReqConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfig('orderReqConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>下单签名配置(json)：</label>
            <textarea id="orderSignConfig" rows="10" cols="36" readonly="readonly" name="orderSignConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfig('orderSignConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>下单响应配置(json)：</label>
            <textarea id="orderRespConfig" rows="10" cols="36" readonly="readonly" name="orderRespConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfigJson('orderRespConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>支付回调配置(json)：</label>
            <textarea id="callbackConfig" rows="10" cols="36" readonly="readonly" name="callbackConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfigJson('callbackConfig')">配置JSON</a></span>
        </div>
        <div>
            <label>支付回调签名配置(json)：</label>
            <textarea id="callbackSignConfig" rows="10" cols="36" readonly="readonly"
                      name="callbackSignConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfig('callbackSignConfig')">配置JSON</a></span>
        </div>
        <div id="dialog_tool">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()"
               style="width:90px">保 存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"
               onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
        </div>
    </form>
</div>
<div id="config" style="display: none">
    <ul>

    </ul>
    <div id="config_tool">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="javascript:saveConfig()"
           style="width:90px">保 存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"
           onclick="javascript:$('#config').window('close')" style="width:90px">取 消</a>
    </div>
</div>
</body>
<script type="application/javascript">

    var configType = null;
    var methodType = null;
    window.onload = function () {
        var params={}
        initDataList(params);
        initGame();
        initSDK();
    }

    function showAddDialog() {
        $("#dialog_add").window({
            top: ($(window).height() * 0.1),
            left: ($(window).width() * 0.2)
        });

        $("#dialog_add").dialog('open').dialog('setTitle', '保存渠道');
        $('#saveData').form('clear');
    }

    function showSaveDialog() {
        var row = $('#dataList').datagrid('getSelected');
        if (row) {
            $('#allGames').combobox('select', row.gameId);
            $('#allSdks').combobox('select', row.sdkId);
            $('#openRegister').val(row.openRegister);
            $('#openLogin').val(row.openLogin);
            $('#openPay').val(row.openPay);
            $("#dialog_add").dialog('open').dialog('setTitle', '编辑渠道');
            $('#saveData').form('load', row);
        } else {
            $.messager.alert('校验失败', '请选择一条记录', 'info');
        }
    }

    function initDataList(params) {
        $("#dataList").datagrid({
            url: '${pageContext.request.contextPath}/admin/channel/getChannelList',
            method: 'POST',
            idField: 'id',
            queryParams:params,
            striped: true,
            fitColumns: false,
            singleSelect: true,
            rownumbers: true,
            pagination: true,
            pagePosition: 'bottom',
            pageNumber: 1,
            pageSize: 20,
            nowrap: true,
            loadMsg: '数据加载中...',
            showFooter: true,
            columns: [[
                {field: 'id', title: '渠道ID', width: 80,align:'center', sortable: true},
                {field: 'channelName', title: '渠道名称', width: 80,align:'center', sortable: true},
                {field: 'gameId', title: '游戏ID', width: 80,align:'center', sortable: true},
                {field: 'sdkId', title: 'SDK ID', width: 80,align:'center'},
                {field: 'loginHandingBean', title: '登陆处理bean', width: 100,align:'center'},
                {field: 'orderHandingBean', title: '下单处理bean', width: 100,align:'center'},
                {field: 'openPay', title: '打开支付', width: 100,align:'center'},
                {field: 'openLogin', title: '打开登陆', width: 100,align:'center'},
                {field: 'openRegister', title: '打开注册', width: 100,align:'center'},
                {field: 'parametersConfig', title: '参数配置(json)', width: 80,align:'center'},
                {field: 'loginReqConfig', title: '登陆配置(json)', width: 80,align:'center'},
                {field: 'loginSignConfig', title: '登陆配置签名(json)', width: 100,align:'center'},
                {field: 'loginRespConfig', title: '登陆响应配置(json)', width: 100,align:'center'},
                {field: 'orderReqConfig', title: '下单配置(json)', width: 100,align:'center'},
                {field: 'orderSignConfig', title: '下单签名配置(json)', width: 100,align:'center'},
                {field: 'orderRespConfig', title: '下单响应配置(json)', width: 100,align:'center'},
                {field: 'callbackConfig', title: '支付回调配置(json)', width: 100,align:'center'},
                {field: 'callbackSignConfig', title: '支付回调签名配置(json)', width: 100,align:'center'},
                {
                    field: 'createTime', title: '创建时间', width: 100,align:'center', formatter: function (value, row, index) {
                        if (value == null) {
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    }
                },
                {
                    field: 'editTime', title: '修改时间', width: 100,align:'center', formatter: function (value, row, index) {
                        if (value == null) {
                            return "";
                        } else {
                            return timeStamp2String(value);
                        }
                    }
                },
            ]],
            toolbar: '#dataTools'
        });
    }


    function save() {
        $('#saveData').form('submit', {
            url: '${pageContext.request.contextPath}/admin/channel/saveChannel',
            onSubmit: function () {
                var channelName = $("#channelName").val();
                if (channelName == null || channelName == "") {
                    $.messager.alert('校验失败', '渠道名不能为空', 'info');
                    return false;
                }

                var parametersConfig = $("#parametersConfig").val();
                if (parametersConfig == null || parametersConfig == "") {
                    $.messager.alert('校验失败', '参数配置不能为空', 'info');
                    return false;
                }
                var gameId = $("#gameId").val();
                if (gameId == null || gameId == "") {
                    $.messager.alert('校验失败', '请选择游戏', 'info');
                    return false;
                }
                var sdkId = $("#sdkId").val();
                if (sdkId == null || sdkId == "") {
                    $.messager.alert('校验失败', '请选择SDK', 'info');
                    return false;
                }

                return true;
            },
            success: function (data) {
                var respData = JSON.parse(data);
                if (respData.code == 0) {
                    $('#dialog_add').dialog('close');
                    $('#dataList').datagrid('reload');
                } else {
                    $.messager.alert('保存失败', respData.msg, 'info');
                }
            }
        });
    }

    function remove() {

        var row = $('#dataList').datagrid('getSelected');
        if (row) {
            $.messager.confirm('确认操作', '确认删除这条记录吗？', function (r) {
                if (r) {
                    var url = '${pageContext.request.contextPath}/admin/channel/removeChannel';
                    $.ajax({
                        type: 'POST',
                        url: url,
                        data: {id: row.id},
                        success: function (data) {
                            if (data.code == 0) {
                                $('#dataList').datagrid('reload');
                            } else {
                                $.messager.alert('删除失败', data.msg, 'info');
                                $('#dataList').datagrid('reload');
                            }
                        },
                        dataType: 'json'
                    });
                }
            });
        } else {
            $.messager.alert('校验失败', '请选择一条记录', 'info');
        }
    }

    function showConfig(type) {
        $('#config ul li').remove();
        configType = type;
        methodType = 1;
        var li = $("<li id='config_li'></li>");
        li.append('<span style="margin-right: 225px;text-align: center;">type</span>')
            .append('<span style="margin-right: 225px;text-align: center;">key</span>')
            .append('<span style="margin-right: 225px;text-align: center;">value</span>')
            .append('<span ><a href="#" class="easyui-linkbutton" data-options="iconCls:\'icon-add\'" onclick="javascript:addConfig(1);">新增</a></span>');
        $("#config ul").append(li);

        if ($("#id").val() == null || $("#id").val() == "") {

        } else {
            var row = $('#dataList').datagrid('getSelected');
            var configJson = row[type];
            if(configJson == null){
                $.messager.alert('校验失败', '参数类型错误', 'info');
                return;
            }
            if (row != null && configJson != null && configJson.length > 0) {
                var config = JSON.parse(configJson);
                for (var i = 0; i < config.length; i++) {
                    var c = config[i];
                    var li = $("<li ></li>");
                    li.append('<input type="text"  class="easyui-textbox" style="width:250px;text-align: center;" name="configType" required="true" value="' + c.type + '"/>')
                        .append('<input type="text"  class="easyui-textbox" style="width:250px;text-align: center;" name="configKey" required="true" value="' + c.key + '"/>')
                        .append('<input type="text"  class="easyui-textbox" style="width:250px;text-align: center;" name="configValue" required="true" value="' + c.value + '"/>')
                        .append('<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:removeConfig(this)" style="width:30px">删除</a>');

                    $("#config ul").append(li);
                }
            }
        }

        $('#config').dialog({
            width: 900,
            height: 400,
            modal: true,
            title: '配置JSON',
            collapsible: false,
            minimizable: false,
            maximizable: false,
            buttons: '#config_tool'
        });
    }

    function showConfigJson(type) {
        $('#config ul li').remove();
        configType = type;
        methodType = 2;
        var li = $("<li id='config_li'></li>");
        li.append('<span style="margin-right: 225px;text-align: center;">key</span>')
            .append('<span style="margin-right: 225px;text-align: center;">value</span>')
            .append('<span ><a href="#" class="easyui-linkbutton" data-options="iconCls:\'icon-add\'" onclick="javascript:addConfig(2);">新增</a></span>');
        $("#config ul").append(li);

        if ($("#id").val() == null || $("#id").val() == "") {

        } else {
            var row = $('#dataList').datagrid('getSelected');
            var configJson = row[type];
            if(configJson == null){
                $.messager.alert('校验失败', '参数类型错误', 'info');
                return;
            }
            if (row != null && configJson != null && configJson.length > 0) {
                var config = JSON.parse(configJson);
                for (var o in config) {
                    // 取出JSON中未知的KEY和对应的值
                    var li = $("<li></li>");
                    li.append('<input type="text" class="easyui-textbox" style="width:250px;text-align: center;" name="configKey" required="true" value="' + o + '"/>')
                        .append('<input type="text" class="easyui-textbox" style="width:250px;text-align: center;" name="configValue" required="true" value="' + config[o] + '"/>')
                        .append('<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:removeConfig(this)" style="width:90px">删除</a>');

                    $("#config ul").append(li);
                }
            }
        }

        $('#config').dialog({
            width: 600,
            height: 400,
            modal: true,
            title: '配置JSON',
            collapsible: false,
            minimizable: false,
            maximizable: false,
            buttons: '#config_tool'
        });
    }

    function addConfig(type) {
        var size = $("#config ul").children().length;
        var li = $("<li id='config_li" + size + "'></li>");
        if(type==1){
            li.append('<input type="text"  class="easyui-textbox" style="width:250px;text-align: center;" name="configType" required="true"/>');
        }
        li.append('<input type="text" id="key' + size + '" class="easyui-textbox" style="width:250px;text-align: center;" name="configKey" required="true" />')
            .append('<input type="text" id="value' + size + '" class="easyui-textbox" style="width:250px;text-align: center;" name="configValue" required="true" />')
            .append('<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:removeConfig(this)" style="width:90px">删除</a>');
        $("#config ul").append(li);
    }

    function saveConfig() {
        var keyArr = $("input[name='configKey']");
        var valueArr = $("input[name='configValue']");
        var typeArr = $("input[name='configType']");

        var configJson = [];
        var jsonObj = {};
        for (var i = 0; i < keyArr.length; i++) {
            var k = $(keyArr[i]).val();
            var v = $(valueArr[i]).val();
            if (k == null || k == "") {
                $.messager.alert('校验失败', 'key不能为空', 'info');
                return;
            }
            if (v == null || v == "") {
                $.messager.alert('校验失败', 'value不能为空', 'info');
                return;
            }

            if (methodType ==2) {
                jsonObj[k] = v;
            }else if (methodType ==1){
                var t = $(typeArr[i]).val();
                if (t == null || t == "") {
                    $.messager.alert('校验失败', 'type不能为空', 'info');
                    return;
                }
                var row = {};
                row.type = t;
                row.key = k;
                row.value = v;
                configJson.push(row);
            }
        }
        $('#config').dialog('close');
        var easyUI_row = {};
        if(methodType ==2){
            easyUI_row[configType] = JSON.stringify(jsonObj);
        }else if(methodType ==1){
            easyUI_row[configType] = JSON.stringify(configJson);
        }

        $('#saveData').form('load', easyUI_row);
    }

    function removeConfig(_self) {
        $(_self).parent().remove();
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
                        }
                    });
                } else {
                    console.log("getSimpleGame fail msg:" + data);
                }
            }
        });
    }

    function initSDK() {
        $.ajax({
            type: 'POST',
            url: '${pageContext.request.contextPath}/admin/channelsdk/getSimpleSDKList',
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    $('#allSdks').combobox({
                        valueField: 'id',
                        textField: 'sdkName',
                        data: data.rows,
                        onSelect: function (res) {
                            $("#sdkId").val(res.id);
                        }
                    });
                } else {
                    console.log("getSimpleSDKList fail msg:" + data);
                }
            }
        });
    }

    function showResult(){
        var params={
            id:$('[name="reqId"]').val(),
            channelName:$('[name="reqName"]').val(),
            gameId:$('[name="reqGameId"]').val(),
        }
        initDataList(params);
    }

</script>
</html>
