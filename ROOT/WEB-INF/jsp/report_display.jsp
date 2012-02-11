<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
<head>
<title>${msg['rpt.weekly']}:${rpt.fullName}</title>
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
<h1>${msg['rpt.weekly']}:${rpt.fullName}</h1>
<pre>${obj}</pre>
</body>
</html>
