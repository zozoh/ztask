<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<link rel="stylesheet" type="text/css" href="${rs}/css/page_stack.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_stack.js"></script>

</head>
<body>
	<% /*==========================================顶部固定条=*/ %>
	<%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*==========================================左侧堆栈列表=*/ %>
    <div id="chute"><div id="chute_inner" class="block"></div></div>
    <% /*==========================================右侧堆栈列表=*/ %>
    <div id="arena"><div id="arena_inner" class="block"></div></div>
	<% /*==========================================本地化字符串支持=*/ %>	
	<%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>