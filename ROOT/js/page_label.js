function main() {
    doReloadLabels();
    $("#labels").delegate(".label", "click", onClickLabel);
    task_events_bind($("#tasks"), {

    });
}

function redrawLables(lbs) {
    var jLbs = $("#labels").empty();
    if(lbs && lbs.length > 0) {
        // 算最大/小值
        var max = 0;
        var min = -1;
        for(var i = 0; i < lbs.length; i++) {
            var c = lbs[i].count;
            max = Math.max(max, c);
            min = min < 0 ? c : Math.min(c, min);
        }
        jLbs.attr("lb-count-max", max).attr("lb-count-min", min);
        var de = max - min;
        // 输出
        for(var i = 0; i < lbs.length; i++) {
            var lb = lbs[i];
            var fz = Math.max(14, 60 * (lb.count - min) / de);
            var fontSize = "font-size:" + fz + "px;";
            var style = lb.color ? ' color:' + lb.color + ';' : "";
            var html = '<span class="label" lb-id="' + lb._id + '"  lb-count="' + lb.count + '"';
            html += ' style="' + style + fontSize + '">';
            html += '<b class="lb_name">' + lb.name + '</b>';
            html += '<em ' + (lb.color ? 'style="background-color:' + lb.color + ';"' : "") + '>' + lb.count + '</em>';
            html += '</span>';
            $(html).appendTo(jLbs);
        }
    }
}

function doReloadLabels() {
    ajax.get("/ajax/label/tops", function(re) {
        redrawLables(re.data);
    });
}

function doSyncLabels() {
    ajax.get("/ajax/do/sync/labels", function(re) {
        doReloadLabels();
    });
}

function onClickLabel() {
    var lbName = $(this).find(".lb_name").text();
    ajax.json("/ajax/task/query", {
        keyword: "#(" + lbName + ")"
    }, function(re) {
        var ts = re.data;
        var jTasks = $("#tasks").empty();
        for(var i = 0; i < ts.length; i++) {
            task_html.apply(jTasks, [ts[i], {
                menu: "edit,del",
                goin: false
            }]);
        }
    });
}

function initLayout() {
    // init HTML
    var html = '<ul>';
    html += '<li><a class="lb_do_sync">' + z.msg("lb.sync") + '</a></li>';
    html += '<li><a class="lb_do_reload">' + z.msg("ui.reload") + '</a></li>';
    html += '</ul>';
    // Add to DOM
    $(html).appendTo(this);
    // 初始化事件
    $("#menu").delegate(".lb_do_sync", "click", doSyncLabels);
    $("#menu").delegate(".lb_do_reload", "click", doReloadLabels);
    // 绑定 Task Comment 事件
    task_detail_bind();
}

function adjustLayout() {
    $("#L,#R").css("height", this.height);
}