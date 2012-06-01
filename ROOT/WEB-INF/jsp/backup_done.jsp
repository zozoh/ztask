<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<title>Generate Backup</title>
<style rel="stylesheet" type="text/css">
h1{
    text-align:right;
    padding:4px 40px;
}
pre{
    margin:0 20px 40px 20px;
    padding: 10px 20px;
    border:1px dashed #CCC;
    background-color:#EEE;
    font-family: Courier;
}
</style>
</head>
<body>
<h1>Backup Generated!</h1>
<pre>Please check <a href="/backup/${obj}">${obj}</a></pre>
</body>
</html>
