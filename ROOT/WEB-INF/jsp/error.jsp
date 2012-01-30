<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<%@ page import="java.io.PrintStream"%>
<%@ page import="org.nutz.lang.Lang"%>
<%@ page import="java.lang.Exception"%>
<%@ page import="org.nutz.web.WebException"%>
<%
    Object obj = request.getAttribute("obj");
    boolean isRe = false;
    String ename = "";
    String reason = "";
    if( obj instanceof WebException ) {
        WebException re = (WebException) obj;
        isRe = true;
        ename = re.getKey();
        reason = re.getReason();
        if(reason == null){
            reason = "";
        }
    } else {
        ename = obj.getClass().getName();
    }
%>
<html>
<head>
	<title>${msg['err.page.title']}</title>
	<link rel="stylesheet" type="text/css" href="${rs}/css/zstack.css" />
	<link rel="stylesheet" type="text/css" href="${rs}/css/jquery.errbox.css" />
	<script>
	    var isRe = <%=isRe%>;
	    var ename = '<%=ename%>';
	    var reason = '<%=reason%>';
	</script>
	<script language="JavaScript" src="${rs}/js/jquery.js"></script>
    <script language="JavaScript" src="${rs}/js/z.js"></script>
    <script language="JavaScript" src="${rs}/js/jquery.errbox.js"></script>
    <script>
        $(document).ready(function(){
            $(document.body).errbox(z.msg(ename), reason, $('.hid_log').html(), false);
        });
    </script>
    <style>
        body {
            background: #678 url(${rs}/css/pics/dft_bg.jpg) repeat center center;
        }
        .hid_log{
            display: none;
        }
    </style>
</head>
<body>
<pre class="hid_log">
<%
	Throwable e = (Throwable) request.getAttribute("obj");
	StringBuilder sb = new StringBuilder();
	PrintStream ps = new PrintStream(Lang.ops(sb),true);
	e.printStackTrace(ps);
	pageContext.getOut().print(sb);
%>
</pre>
<%@include file="/WEB-INF/jsp/include/_msgs.jsp" %>
</body>
</html>