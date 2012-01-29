<%
String url = request.getRequestURI();
%>
<div id="sky">
    <div class="logo"></div>
    <div class="lnks">
        <a href="${base}/page/stack" <%=url.endsWith("stack.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.stack']}
        </a><a href="${base}/page/label" <%=url.endsWith("label.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.label']}
        </a><a href="${base}/page/task" <%=url.endsWith("task.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.task']}
        </a><a href="${base}/page/user" <%=url.endsWith("user.jsp")?"class=\"hlt\"":""%>>
            ${msg['ui.user']}
        </a>
    </div>
    <div class="menu"></div>
    <div class="info">
        <b>${me.name}</b>
        | <a href="${base}/do/logout">${msg['ui.logout']}</a>
    </div>
</div>