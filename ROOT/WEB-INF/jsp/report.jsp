<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<%@page import="java.util.Map"%>
<%@page import="org.nutz.lang.util.NutMap"%>
<html>
<head>
<%@include file="/WEB-INF/jsp/include/_page_metas.jsp" %>
<%@include file="/WEB-INF/jsp/include/_common_rs.jsp" %>

<link rel="stylesheet" type="text/css" href="${rs}/css/page_report.css"/>

<script language="JavaScript" src="${rs}/js/page.js"></script>
<script language="JavaScript" src="${rs}/js/page_report.js"></script>

</head>
<body url-stack-top="${base}/ajax/stack/tops">
    <% /*==========================================顶部固定条=*/ %>
    <%@include file="/WEB-INF/jsp/include/_sky.jsp" %>
    <% /*============================================页面内容=*/ %>
    <h1>   
        <ul id="year">
            <li id="year_prev" class="yybtn">
                 <a href="${base}/page/report?yy=${year-1}">&lt;&lt;${msg['rpt.yy.prev']}</a>
            </li>
            <li id="year_current">${year}</li>
            <li id="year_next" class="yybtn">
                 <a href="${base}/page/report?yy=${year+1}">${msg['rpt.yy.next']}&gt;&gt;</a>
            </li>
        </ul>
    </h1>
    <div id="weeks" class="scro">
        <%
        Map<String,String> msgs = (Map<String,String>)request.getAttribute("msg");
        NutMap[][] cells = (NutMap[][])request.getAttribute("cells");
        for(int i=1;i<cells.length;i++){
            NutMap[] weeks = cells[i];
            String title = msgs.get("d.MM."+i);
        %>
        <div class="cell_m" index="<%=i%>">
            <h5><%=title%></h5>
        <%
            for(int w=0;w<weeks.length;w++){
                NutMap map = weeks[w];
                String hasReportCss = map.getAs(Boolean.class, "report")?"cell_reported":"";
                String currentWeekCss = map.getAs(Boolean.class, "current")?"cell_current_week":"";
                String force = map.getAs(Boolean.class, "current")?"?force=true":"";
        %>
                <a target="_blank" href="${base}/page/do/report/${year}-<%=map.get("d")%><%=force%>"
                    class="cell_w <%=hasReportCss%> <%=currentWeekCss%>" 
                    index="<%=w%>">
                        <em>#<%=map.get("wyy")%>${msg['d.ww']}</em>
                        <b><%=map.get("d")%></b></a>
        <%
            } // ~ end loop weeks
        %>
        </div>
        <%
        } // ~ end loop month
        %>
    </div>
</body>
</html>