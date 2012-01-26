<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_main.js"></script>

</head>
<body>
	<% /*==========================================顶部固定条=*/ %>
	<div id="sky"></div>
    <% /*==========================================左侧堆栈列表=*/ %>
    <div id="chute"></div>
    <% /*==========================================右侧堆栈列表=*/ %>
    <div id="arena"> I am main</div>
	<% /*==========================================本地化字符串支持=*/ %>	
	<%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>