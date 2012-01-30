<%
String url = request.getRequestURI();
%>
<div id="sky">
    <div id="logo"><img src="${rs}/img/loading.gif"></div>
    <div id="lnks">
        <a href="${base}/page/stack" <%=url.endsWith("stack.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.stack']}
        </a><a href="${base}/page/label" <%=url.endsWith("label.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.label']}
        </a><a href="${base}/page/task" <%=url.endsWith("task.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.task']}
        </a><a href="${base}/page/user" <%=url.endsWith("user.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.user']}
        </a><a href="${base}/page/faq" <%=url.endsWith("user.jsp")?"class=\"hlt\"":""%>>
            FAQ
        </a><a href="${base}/page/sys" <%=url.endsWith("sys.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.sys']}
        </a>
    </div>
    <div id="menu"></div>
    <div id="corner">
        <b>${me.name}</b>
        | <a href="${base}/do/logout">${msg['ui.logout']}</a>
    </div>
</div>