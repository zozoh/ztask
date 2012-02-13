<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<title>${msg['ui.login']} - zTask</title>
<link type="text/css" rel="stylesheet" href="${rs}/css/ztask.css"/>
<link type="text/css" rel="stylesheet" href="${rs}/css/page_login.css"/>
<script>
    function nmFocus(){
        document.getElementById('nm').focus();
    };
    window.onload = nmFocus;
</script>
</head>
<body>
<div align="center"><img src="${rs}/img/ztask_logo_big.png"></div>
<form method="post" action="${base}/do/login" class="login">
    <table border="0" cellspacing="4" cellpadding="4">
        <tr>
            <td class="fnm">${msg['login.unm']}</td>
            <td><input id="nm" name="nm" value=""/></td>
        </tr>
        <tr>
            <td class="fnm">${msg['login.pwd']}</td>
            <td><input name="pwd" type="password" value=""/></td>         
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td>
                <input class="btn" type="submit" value="${msg['login.signup']}"/>
            </td>           
        </tr>
    </table>    
</form>
<div class="footer">Nutz &copy; 2011</div>
</body>
</html>
