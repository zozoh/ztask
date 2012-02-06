<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<script language="JavaScript" src="${rs}/js/ztask_task_events.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_task_newer.js"></script>

<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_newer.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/page_task.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_task.js"></script>

</head>
<body>
    <% /*==========================================顶部固定条=*/ %>
    <%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*===============================================左侧=*/ %>
    <div id="L"><div class="WP">
        <div id="LT" class="T srch">
            <div class="srch_keyword"><input placeholder="${msg['srch.keyword.tip']}"></div>
            <a href="#" class="srch_do">${msg['srch.do']}</a>
            <div class="srch_sort">
                <span class="srch_sort_txt">${msg['srch.sort.txthead']}</span>
                <span class="srch_sort_by"></span>
                <span class="srch_sort_order"></span>
                <span class="srch_sort_txt">${msg['srch.sort.txttail']}</span>
            </div>
        </div>
        <div id="tasks" class="B hierachy_right"></div>
    </div></div>
    <% /*===============================================右侧=*/ %>
    <div id="R"><div class="WP">
        <div id="thierachy" class="hierachy_right"></div>
        <div id="tsubs" class="hierachy hierachy_right"><div class="hierachy_arena">
            <div id="tsubs_crumb" class="hierachy_crumb"></div>
            <div id="tsubs_viewport" class="hierachy_viewport">
                <div id="tsubs_scroller" class="hierachy_scroller"></div>    
            </div>
        </div></div>
    </div></div>
    <% /*========================================Task Detail DOM=*/ %>
    <%@include file="/WEB-INF/jsp/include/_task_comment.jsp" %>
    <% /*==========================================本地化字符串支持=*/ %>   
    <%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>