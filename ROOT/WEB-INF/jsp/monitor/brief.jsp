<%@include file="/WEB-INF/jsp/include/_setup.jsp" %>
<html>
    <head>
        <title>${title}</title>
        <style rel="stylesheet" type="text/css">
            h1 {
                text-align: right;
                padding: 4px 40px;
            }

            pre {
                margin: 0 20px 40px 20px;
                padding: 10px 20px;
                border: 1px dashed #CCC;
                background-color: #EEE;
                font-family: Monaco, Courier;
                font-size: 12px;
                line-height: 20px;
            }

            b {
                position: fixed;
                display: inline-block;
                padding: 2px 14px;
                font-size:12px;
                background-color: rgba(255,255,255,0.8);
                border-radius:20px 0 0 20px;
                text-shadow:1px 1px 0 #FFF;
                right: 5px;
                top: 5px;
                color: #080;
            }
            .notify{
                text-align: center;
                background-color:#CCC;
                padding:6px;
            }
        </style>
    </head>
    <body>
        <h1>${title}</h1><b>${now}</b>
        <pre>${obj}</pre>
        <div class="notify">
            <c:forEach var="atom" items="${$atoms}">
                - <a href="/monitor/notify?tnm=${atom.name}">${atom.name}</a>
            </c:forEach>
        </div>
    </body>
</html>
