<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<script language="JavaScript" src="${rs}/js/ztask_stack_events.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_task_events.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_task_newer.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_task_search.js"></script>
<script language="JavaScript" src="${rs}/js/ztask_stack_flt.js"></script>

<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_stack.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_newer.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_stack_flt.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_task_search.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/page_stack.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_stack.js"></script>

</head>
<body url-stack-top="${base}/ajax/stack/tops">
	<% /*==========================================顶部固定条=*/ %>
	<%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
	<% /*===============================================过滤器=*/ %>
	<div id="filters">
	    <div id="flt_stack" class="flt_block sflt">
            <%@include file="/WEB-INF/jsp/include/_stack_filter.jsp" %>
	    </div> <%// End of #flt_stack %>
	    <div id="flt_task" class="flt_block srch">
	        <%@include file="/WEB-INF/jsp/include/_task_search.jsp" %>
	    </div> <%// End of #flt_task %>
	</div>
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
    <% /*========================================Task Detail DOM=*/ %>
    <%@include file="/WEB-INF/jsp/include/_task_detail.jsp" %>
	<% /*==========================================本地化字符串支持=*/ %>	
	<%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>