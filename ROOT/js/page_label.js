function main() {
    doReloadLabels();
    $("#labels").delegate(".label", "click", onClickLabel);
    task_events_bind($("#tasks"), {

    });
}

function redrawLables(lbs) {
    var jLbs = $("#labels").empty();
    if(lbs && lbs.length > 0) {
        for(var i = 0; i < lbs.length; i++) {
            var lb = lbs[i];
            var html = '<div class="label" lb-id="' + lb._id + '">';
            html += '<b class="lb_name">' + lb.name + '</b>';
            if(lb.count)
                html += '<em>' + lb.count + '</em>';
            html += '</div>';
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
                menu: "edit,del,label",
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
}

function adjustLayout() {
    $("#L,#R").css("height", this.height);
}