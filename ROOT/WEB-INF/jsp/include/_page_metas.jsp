<%@page import="java.util.Map, org.nutz.lang.Strings"%>
<%
String path = request.getRequestURI();
int posB = path.indexOf("/jsp");
int posE = path.lastIndexOf(".jsp");
path = path.substring(posB, posE).replace("/jsp/","page.");
Map<String,String> msg = (Map<String,String>)request.getAttribute("msg");
%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>(0) zTask - <%=msg.get(path)%> @${me.name}</title>