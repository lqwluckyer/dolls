<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>首页</title>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/bootstrap/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/easyui/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/easyui/easyui-lang-zh_CN.js"></script>

    <style>
        li {
            list-style: none;
            font-size: 14px;
            margin: 2px;
        }
    </style>
</head>
<body class="easyui-layout">
<div data-options="region:'north',split:true" style="height:100px;"></div>
<div data-options="region:'south',split:true" style="height:100px;"></div>
<div data-options="region:'west',split:true" style="width:280px;">
    <div id="menu" class="easyui-accordion" style="width:100%;height:100%; font-size: 16px;">
        <div title="渠道管理">
            <ul>
                <li><a href="#" onclick="addTab('渠道管理列表','${pageContext.request.contextPath}/admin/channel/showList')">渠道列表</a></li>
            </ul>
        </div>
        <div title="游戏管理">
            <ul>
                <li><a href="#" onclick="addTab('游戏管理列表','${pageContext.request.contextPath}/admin/game/showList')">游戏列表</a></li>
            </ul>
        </div>
        <div title="sdk管理">
            <ul>
                <li><a href="#" onclick="addTab('sdk管理列表','${pageContext.request.contextPath}/admin/channelsdk/showList')">sdk列表</a></li>
            </ul>
        </div>
        <div title="玩家管理">
            <ul>
                <li><a href="#" onclick="addTab('玩家管理','${pageContext.request.contextPath}/admin/userManager/showList')">玩家管理</a></li>
            </ul>
            <ul>
                <li><a href="#" onclick="addTab('玩家角色管理','${pageContext.request.contextPath}/admin/userManager/showPlayerList')">玩家角色管理</a></li>
            </ul>
        </div>
    </div>
</div>
<div data-options="region:'center'" style="padding:5px;">
    <div id="center" class="easyui-tabs" fit="true" border="false">
        <div id="home" title="home" style="height: 100%; width: 100%;">
             公仔SDK管理后台
        </div>
    </div>
</div>
</body>
<script type="application/javascript">

    window.onload=function(){
        initAccordion();
    }

    function addTab(title, url){
        if ($('#center').tabs('exists', title)){
            $('#center').tabs('select', title);
        } else {
            var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
            $('#center').tabs('add',{
                title:title,
                content:content,
                closable:true
            });
        }
    }

    function initAccordion() {

        var content = '<ul><li><a href="#" onclick="addTab(\'支付订单管理\',\'${pageContext.request.contextPath}/admin/payOrder/showList\')">支付订单管理</a></li></ul>';
        $('#menu').accordion('add', {
            title: '支付订单管理',
            content: content,
            selected: false
        });

    }
</script>

</html>
