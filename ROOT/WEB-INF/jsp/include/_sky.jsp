<%
String url = request.getAttribute("page-url").toString();
%>
<div id="sky" rs="${re}" msg-inter="${msg_inter}" myname="${me.name}">
    <div id="logo"><img src="${rs}/img/loading.gif"></div>
    <div id="lnks" class="lnk_menu">
        <a href="${base}/page/stack" <%=url.endsWith("/stack")?"class=\"hlt\"":""%>>
            ${msg['ui.stack']}
        </a><a href="${base}/page/label" <%=url.endsWith("/label")?"class=\"hlt\"":""%>>
            ${msg['ui.label']}
        </a><a href="${base}/page/task" <%=url.endsWith("/task")?"class=\"hlt\"":""%>>
            ${msg['ui.task']}
        </a><a href="${base}/page/report" <%=url.endsWith("/report")?"class=\"hlt\"":""%>>
            ${msg['ui.report']}
        </a><a href="${base}/page/message" <%=url.endsWith("/message")?"class=\"hlt\"":""%>>
            ${msg['ui.message']} <i id="msg_count">0</i>
        </a>
    </div>
    <div id="menu" class="lnk_menu">
        <%
        String[][] menus = (String[][])request.getAttribute("page-menu");
        if(null!=menus){
            for(String[] menu : menus){
                if(null==menu)
                    continue; 
            %><a href="${base}/page/cus/<%=menu[0]%>"
               <%=url.endsWith("/cus/"+menu[0])?"class=\"hlt\"":""%>><%=menu[0]%>
            </a><%
            }
        }
        %>
    </div>
    <div id="corner" class="lnk_menu">
        <b style="color:#F80;">${me.name}</b>
        <a href="${base}/page/sys" <%=url.endsWith("/sys")?"class=\"hlt\"":""%>>
            ${msg['ui.sys']}
        </a>
        <a href="${base}/do/logout">${msg['ui.logout']}</a>
    </div>
</div>