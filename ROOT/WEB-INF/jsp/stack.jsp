<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_hierachy.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/page_stack.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_stack.js"></script>

</head>
<body>
	<% /*==========================================顶部固定条=*/ %>
	<%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*==========================================左侧堆栈列表=*/ %>
    <div id="stacks" class="hierachy"><div class="hierachy_arena">
        <div id="stacks_crumb" class="hierachy_crumb"></div>
        <div id="stacks_viewport" class="hierachy_viewport">
            <div id="stack_scroller" class="hierachy_scroller"></div>    
        </div>
    </div></div>
    <% /*==========================================右侧堆栈列表=*/ %>
    <div id="tasks" class="hierachy"><div class="hierachy_arena">
        <div id="tasks_crumb" class="hierachy_crumb"></div>
        <div id="tasks_viewport" class="hierachy_viewport">
            <div id="tasks_scroller" class="hierachy_scroller"></div>    
        </div>
    </div></div>
	<% /*==========================================本地化字符串支持=*/ %>	
	<%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>