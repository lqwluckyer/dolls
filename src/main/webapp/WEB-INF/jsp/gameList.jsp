<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>游戏管理</title>
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
            <label >游戏名称：</label>
            <input id="gameName" type="text" class="easyui-textbox" name="gameName" maxlength="255" required="true"/>
        </div>

        <div>
            <label >游戏原名称：</label>
            <input id="originalName" type="text" class="easyui-textbox" name="originalName" maxlength="255" required="true"/>
        </div>

        <div>
            <label >gameKey：</label>
            <input type="text" class="easyui-textbox" name="gameKey" maxlength="255" required="true" />
        </div>

        <div>
            <label >gameSecret：</label>
            <input  type="text" class="easyui-textbox" name="gameSecret" maxlength="255" required="true" />
        </div>

        <div>
            <label >RSAPubKey：</label>
            <input type="text" class="easyui-textbox" name="gameRSAPubKey" maxlength="1024" required="true" />
        </div>
        <div>
            <label >RSAPriKey：</label>
            <input type="text" class="easyui-textbox" name="gameRSAPriKey"  maxlength="1024" required="true" />
        </div>
        <div>
            <label >otherConfig：</label>
            <textarea id="otherConfig" rows="10" cols="36" readonly="readonly" name="otherConfig"></textarea>
            <span><a href="javascript:void(0)" class="easyui-linkbutton" onclick="showConfig()">配置JSON</a></span>
        </div>
        <div id="dialog_tool">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveGame()" style="width:90px">保 存</a>
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
        $("#dialog_add").dialog('open').dialog('setTitle', '保存游戏');
        $('#saveData').form('clear');
    }

    function showSaveDialog() {
        var row = $('#dataList').datagrid('getSelected');
        if(row){
            $("#dialog_add").dialog('open').dialog('setTitle', '编辑游戏');
            $('#saveData').form('load', row);
        }else{
            $.messager.alert('校验失败','请选择一条记录','info');
        }
    }
    
    function initDataList(params) {
        $("#dataList").datagrid({
            url:'${pageContext.request.contextPath}/admin/game/getGameList',
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
                {field:'id', title:'游戏ID', width:80,align:'center', sortable:true},
                {field:'gameName', title:'游戏名称', width:80,align:'center', sortable:true},
                {field:'originalName', title:'游戏原名称', width:80,align:'center', sortable:true},
                {field:'gameKey', title:'gameKey', width:80,align:'center'},
                {field:'gameSecret', title:'gameSecret', width:80 ,align:'center'},
                {field:'gameRSAPubKey', title:'RSAPubKey', width:80 ,align:'center'},
                {field:'gameRSAPriKey', title:'RSAPriKey', width:100 ,align:'center'},
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

    function saveGame() {
        $('#saveData').form('submit', {
            url:'${pageContext.request.contextPath}/admin/game/saveGame',
            onSubmit: function(){
                var gameName = $("#gameName").val();
                if(gameName==null || gameName==""){
                    $.messager.alert('校验失败','游戏名不能为空','info');
                    return false;
                }

                var originalName = $("#originalName").val();
                if(originalName==null || originalName==""){
                    $.messager.alert('校验失败','游戏名不能为空','info');
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
                    var url = '${pageContext.request.contextPath}/admin/game/removeGame';
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
            gameName:$('[name="reqName"]').val()
        }
        initDataList(params);
    }

</script>
</html>
