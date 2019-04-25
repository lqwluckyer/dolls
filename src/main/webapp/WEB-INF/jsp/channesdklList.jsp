<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>渠道SDK管理</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/bootstrap/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <style type="text/css">
        label{
            display:inline-block;
            width:100px;
            text-align: right;
            margin-top: 5px;
            margin-right: 10px;
            margin-left: 10px;
        }
        input{
            width:350px;
            margin-top: 5px;
        }
        li {
            list-style: none;
            font-size: 14px;
            margin: 2px;
        }
        .search_content {
            width: 500px;
            float: left;
            margin: 10px;
        }
        .clear{ clear:both}
    </style>
</head>
<body>
<div>
    <div class="search_content" >
        <label>游戏ID:</label>
        <input  id="reqId" name="reqId" type="text" class="easyui-textbox">
    </div>
    <div class="search_content">
        <label>游戏名:</label>
        <input  id="reqName" name="reqName" type="text" class="easyui-textbox">
    </div>
    <div class="search_content">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-search" onclick="showResult()" style="width:90px">搜索</a>
    </div>
</div>
<div class="clear"></div>
<div id="dataTools" style="display: none;">
    <a id="add" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="javascript:showAddDialog();">新增</a>
    <a id="save" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="javascript:showSaveDialog();">修改</a>
    <a id="remove" href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="javascript:remove();">删除</a>
</div>
<div id="dataList"></div>
<div id="dialog_add" class="easyui-dialog" data-options="buttons:'#dialog_tool',modal:true" closed="true" style="height: 500px;width: 500px;display: none;">
    <form id="saveData" method="post">
        <input type="hidden" id="id" name="id"/>
        <div>
            <label >SDK名称：</label>
            <input id="sdkName" type="text" class="easyui-textbox" name="sdkName" maxlength="255" required="true"/>
        </div>

        <div>
            <label >SDK简称(英文)：</label>
            <input id="sdkShortName" type="text" class="easyui-textbox" name="sdkShortName" maxlength="255" required="true"/>
        </div>

        <div>
            <label >登录认证地址：</label>
            <input type="text" id="authUrl" class="easyui-textbox" name="authUrl" maxlength="255" required="true" />
        </div>

        <div>
            <label >支付回调地址：</label>
            <input  type="text" id="payCallbackUrl" class="easyui-textbox" name="payCallbackUrl" maxlength="255" required="true" />
        </div>

        <div>
            <label >SDK全类名：</label>
            <input type="text" id="verifyClass" class="easyui-textbox" name="verifyClass" maxlength="1024" required="true" />
        </div>
        <div>
            <label >下单地址：</label>
            <input type="text" class="easyui-textbox" name="orderUrl"  maxlength="1024"/>
        </div>
        <div>
            <label >otherConfig：</label>
            <textarea id="otherConfig" rows="10" cols="36" readonly="readonly" name="otherConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfig()">配置JSON</a></span>
        </div>
        <div id="dialog_tool">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">保 存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
        </div>
    </form>
</div>
<div id="config" style="display: none">
    <ul>
        <li>
            <span style="margin-right: 200px;text-align: center;">key</span>
            <span style="margin-right: 200px;text-align: center;">value</span>
            <span ><a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="javascript:addConfig();"></a></span>
        </li>
    </ul>
    <div id="config_tool">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="javascript:saveConfig()" style="width:90px">保 存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#config').window('close')" style="width:90px">取 消</a>
    </div>
</div>
</body>
<script type="application/javascript">

    window.onload=function(){
        var params={}
        initDataList(params);
    }

    function showAddDialog() {
        $("#dialog_add").window({
            top:$(window).height() * 0.1,
            left:$(window).width() * 0.2
        });
        $("#dialog_add").dialog('open').dialog('setTitle', '保存渠道SDK');
        $('#saveData').form('clear');
    }

    function showSaveDialog() {
        var row = $('#dataList').datagrid('getSelected');
        if(row){
            $("#dialog_add").dialog('open').dialog('setTitle', '编辑渠道SDK');
            $('#saveData').form('load', row);
        }else{
            $.messager.alert('校验失败','请选择一条记录','info');
        }
    }

    function initDataList(params) {
        $("#dataList").datagrid({
            url:'${pageContext.request.contextPath}/admin/channelsdk/getChannelSDKList',
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
                {field:'id', title:'ID', width:80,align:'center', sortable:true},
                {field:'sdkName', title:'SDK名称', width:80,align:'center', sortable:true},
                {field:'sdkShortName', title:'SDK简称', width:80,align:'center', sortable:true},
                {field:'authUrl', title:'登陆认证地址', width:80,align:'center'},
                {field:'payCallbackUrl', title:'支付回调', width:80,align:'center' },
                {field:'verifyClass', title:'SDK验证类', width:80 ,align:'center'},
                {field:'orderUrl', title:'SDK下单地址', width:100 ,align:'center'},
                {field:'otherConfig', title:'otherConfig', width:100 ,align:'center'},
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
            toolbar: '#dataTools'
        });
    }



    function save() {
        $('#saveData').form('submit', {
            url:'${pageContext.request.contextPath}/admin/channelsdk/saveChannelSDK',
            onSubmit: function(){
                var sdkName = $("#sdkName").val();
                if(sdkName==null || sdkName==""){
                    $.messager.alert('校验失败','SDK名不能为空','info');
                    return false;
                }

                var sdkShortName = $("#sdkShortName").val();
                if(sdkShortName==null || sdkShortName==""){
                    $.messager.alert('校验失败','SDK简称不能为空','info');
                    return false;
                }
                var authUrl = $("#authUrl").val();
                if(authUrl==null || authUrl==""){
                    $.messager.alert('校验失败','登陆认证地址不能为空','info');
                    return false;
                }
                var payCallbackUrl = $("#payCallbackUrl").val();
                if(payCallbackUrl==null || payCallbackUrl==""){
                    $.messager.alert('校验失败','支付回调地址不能为空','info');
                    return false;
                }
                var verifyClass = $("#verifyClass").val();
                if(verifyClass==null || verifyClass==""){
                    $.messager.alert('校验失败','SDK全类名不能为空','info');
                    return false;
                }
                return true;
            },
            success:function(data){
                var respData = JSON.parse(data);
                if(respData.code==0){
                    $('#dialog_add').dialog('close');
                    $('#dataList').datagrid('reload');
                }else{
                    $.messager.alert('保存失败',respData.msg,'info');
                }
            }
        });
    }

    function remove() {

        var row = $('#dataList').datagrid('getSelected');
        if(row){
            $.messager.confirm('确认操作', '确认删除这条记录吗？', function(r){
                if (r){
                    var url = '${pageContext.request.contextPath}/admin/channelsdk/removeChannelSDK';
                    $.ajax({
                        type: 'POST',
                        url: url,
                        data: { id: row.id},
                        success: function(data){
                            if(data.code==0){
                                $('#dataList').datagrid('reload');
                            }else{
                                $.messager.alert('删除失败',data.msg,'info');
                                $('#dataList').datagrid('reload');
                            }
                        },
                        dataType: 'json'
                    });
                }
            });
        }else{
            $.messager.alert('校验失败','请选择一条记录','info');
        }
    }
    
    function showConfig() {
        var size = $("#config ul").children().length;
        if(size>1){
            for(var i=1; i<size;i++){
                $('#config_li'+i).remove();
            }
        }
        if($("#id").val()==null || $("#id").val()==""){

        }else{
            var row = $('#dataList').datagrid('getSelected');
            if(row != null && row.otherConfig!=null){
                var config = JSON.parse(row.otherConfig);

                for(var i=0;i<config.length;i++){
                    var c = config[i];
                    var li = $("<li id='config_li"+(i+1)+"'></li>");
                    li.append('<input type="text" id="key'+(i+1)+'" class="easyui-textbox" style="width:225px;text-align: center;" name="configKey" required="true" value="'+c.key+'"/>')
                        .append('<input type="text" id="value'+(i+1)+'" class="easyui-textbox" style="width:225px;text-align: center;" name="configValue" required="true" value="'+c.value+'"/>')
                        .append('<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:removeConfig(this)" style="width:90px">删除</a>');

                    $("#config ul").append(li);
                }

            }
        }

        $('#config').dialog({
            width:600,
            height:400,
            modal:true,
            title:'配置JSON',
            collapsible:false,
            minimizable:false,
            maximizable:false,
            buttons:'#config_tool'
        });
    }

    function addConfig() {
        var size = $("#config ul").children().length;
        var li = $("<li id='config_li"+size+"'></li>");
        li.append('<input type="text" id="key'+size+'" class="easyui-textbox" style="width:225px;text-align: center;" name="configKey" required="true" />')
            .append('<input type="text" id="value'+size+'" class="easyui-textbox" style="width:225px;text-align: center;" name="configValue" required="true" />')
            .append('<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:removeConfig(this)" style="width:90px">删除</a>');

        $("#config ul").append(li);
    }

    function saveConfig() {
        var keyArr = $("input[name='configKey']");
        var valueArr = $("input[name='configValue']");

        var configJson = [];
        for(var i=0;i<keyArr.length;i++){
            var k=$(keyArr[i]).val();
            var v=$(valueArr[i]).val();
            if(k==null || k==""){
                $.messager.alert('校验失败','key不能为空','info');
                return ;
            }
            if(v==null || v==""){
                $.messager.alert('校验失败','value不能为空','info');
                return ;
            }
            var row = {};
            row.key= k;
            row.value = v;
            configJson.push(row);
        }
        $('#config').dialog('close');
        var easyUI_row = {};
        easyUI_row.otherConfig = JSON.stringify(configJson);
        $('#saveData').form('load', easyUI_row);
    }

    function removeConfig(_self) {
        $(_self).parent().remove();
    }

    function showResult(){
        var params={
            id:$('[name="reqId"]').val(),
            sdkName:$('[name="reqName"]').val()
        }
        initDataList(params);
    }
</script>
</html>
