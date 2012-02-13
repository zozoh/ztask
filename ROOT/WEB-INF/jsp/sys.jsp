<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<link rel="stylesheet" type="text/css" href="${rs}/css/ztask_hierachy.css"/>
<link rel="stylesheet" type="text/css" href="${rs}/css/page_sys.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_sys.js"></script>

</head>
<body>
    <% /*==========================================顶部固定条=*/ %>
    <%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*==========================================主区域=*/ %>
    <div id="arena">
        <textarea></textarea>
        <ul style="position:absolute;top:80px;left:0px;font-size:14px;line-height:28px;">
             <li><a target="_blank" href="${base}/monitor/schedule">Schedules</a></li>
             <li><a target="_blank" href="${base}/monitor/threads">Threads</a></li>
        </ul>
    </div>
    <% /*==========================================本地化字符串支持=*/ %>   
    <%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>