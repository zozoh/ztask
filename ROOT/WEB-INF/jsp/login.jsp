<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<title>${msg['ui.login']} - zTask</title>
<link type="text/css" rel="stylesheet" href="${rs}/css/zstack.css"/>
<link type="text/css" rel="stylesheet" href="${rs}/css/page_login.css"/>
</head>
<body>
<h1>zTask</h1>
<form method="post" action="${base}/do/login" class="login">
    <table border="0" cellspacing="4" cellpadding="4">
        <tr>
            <td>${msg['login.unm']}</td>
            <td><input name="nm" value="zozoh"/></td>
        </tr>
        <tr>
            <td>${msg['login.pwd']}</td>
            <td><input name="pwd" type="password" value="123456"/></td>         
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <input type="submit" value="${msg['login.signup']}"/>
            </td>           
        </tr>
    </table>    
</form>
<div class="footer">Nutz &copy; 2011</div>
</body>
</html>
