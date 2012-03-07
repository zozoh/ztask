<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<script language="JavaScript" src="${rs}/js/ztask_task_events.js"></script>

<link rel="stylesheet" type="text/css" href="${rs}/css/page_label.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_label.js"></script>

</head>
<body>
    <% /*==========================================顶部固定条=*/ %>
    <%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*===============================================左侧=*/ %>
    <div id="L"><div class="WP scro hierachy_right"><div id="tasks"></div></div></div>
    <% /*===============================================右侧=*/ %>
    <ul id="label_btns" class="disselect">
        <li><a class="lb_do_sync">${msg['lb.sync']}</a></li>
        <li><a class="lb_do_reload">${msg['ui.reload']}</a></li>
    </ul>
    <div id="R" class="disselect"><div class="WP"><div id="labels" class="scro"></div></div></div>
    <% /*========================================Task Detail DOM=*/ %>
    <%@include file="/WEB-INF/jsp/include/_task_detail.jsp" %>
    <% /*==========================================本地化字符串支持=*/ %>   
    <%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>