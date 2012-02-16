<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<script language="JavaScript" src="${rs}/js/ztask_task_events.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_task_search.js"></script>

<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_task_search.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/page_task.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_task.js"></script>

</head>
<body page-labels="${page_labels}">
    <% /*==========================================顶部固定条=*/ %>
    <%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*===============================================左侧=*/ %>
    <div id="L"><div class="WP">
        <div id="LT" class="T srch">
            <%@include file="/WEB-INF/jsp/include/_task_search.jsp" %>
        </div>
        <div id="tasks" class="B scro hierachy_right"></div>
    </div></div>
    <% /*===============================================右侧=*/ %>
    <div id="R"><div class="WP">
        <div id="thierachy" class="hierachy_right"></div>
        <div id="tsubs"></div>
    </div></div>
    <% /*========================================Task Detail DOM=*/ %>
    <%@include file="/WEB-INF/jsp/include/_task_detail.jsp" %>
    <% /*==========================================本地化字符串支持=*/ %>   
    <%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>