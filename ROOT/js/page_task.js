function main() {
    // 初始化查询条件
    var kwd = z.pgan();
    if(kwd) {
        $(".srch_keyword input").val(kwd);
    }
    // 事件
    $(".srch_keyword input").change(onClickSearch);
    $(".srch_do").click(onClickSearch).click();
    // 绑定通用 Task 事件
    task_events_bind(document.body, {
        reject: task_replace,
        renew: task_replace,
        done: task_replace
    });
    // 事件: 查看 Task 详情
    $("#L").delegate(".task_detail", "click", onClickDetail);
    $("#R").delegate(".task_refresh", "click", onClickDetail);
}

function drawDetail(t, subTasks) {
    // 首先清除
    var jTHei = $("#thierachy").empty();
    // 然后开始绘制右侧
    // 先绘制头部得 task
    var jTask = task_html.apply(jTHei, [t, {
        goin: "refresh",
        menu: "edit,label"
    }]);
    z.blinkIt(jTask, 500);
    // 如果没有 newer ，绘制它
    if($("#tsubs .newtask").size() == 0)
        task_newer_appendTo("#tsubs");
    // 调整布局
    adjustLayout();
    // 绘制底部的 .hierachy
    hierachy_init.apply($("#tsubs"), [{
        direction: 'right',
        append: function(t) {
            task_html.apply(this, [t]);
        },
        events: {
            "click:.hierachy_crumb_item_hlt": function() {
                ajax.get("/ajax/task/children", {
                    tid: $(this).attr("task-id")
                }, function(re) {
                    $("#tsubs .hierachy_erratic").remove();
                    hierachy_redraw.apply($("#tsubs"), [re.data]);
                });
            }
        }
    }, task_format_title(t), subTasks]);
    // 最后保存一下 TaskId
    $("#tsubs .hierachy_crumb_item_hlt").attr("task-id", t._id);
}

function onClickDetail() {
    var t = task_jtask(this).data("task");
    ajax.get("/ajax/task/self", {
        tid: t._id
    }, function(re) {
        drawDetail(re.data, re.data.children);
    });
}

function onClickSearch() {
    var jsrch = $(this).parents(".srch");
    if(jsrch.size() <= 0)
        return;
    var form = {
        keyword: $(".srch_keyword input", jsrch).val(),
        order: $(".srch_sort_order", jsrch).droplist("get").value,
        sortBy: $(".srch_sort_by", jsrch).droplist("get").value,
        limit: 100
    };
    ajax.json("/ajax/task/query", form, function(re) {
        var ts = re.data;
        var jTasks = $("#tasks").empty();
        for(var i = 0; i < ts.length; i++) {
            appendTask(jTasks, ts[i]);
        }
        // 只有一个结果，点击它
        if(1 == ts.length) {
            $(".task_goin", jTasks).first().click();
        }
    });
}

function appendTask(jTasks, t, menu) {
    task_html.apply(jTasks, [t, {
        menu: menu,
        goin: "detail"
    }]);
}

function initLayout() {
    // 绑定 Task Comment 事件
    task_detail_bind();
    
    // 初始化下拉菜单
    var jsrch = $("#LT");
    $(".srch_sort_by", jsrch).droplist({
        data: [{
            text: z.msg('task.createTime'),
            value: "createTime"
        }, {
            text: z.msg('task.lastModified'),
            value: "lastModified"
        }, {
            text: z.msg('task.popAt'),
            value: "popAt"
        }, {
            text: z.msg('task.pushAt'),
            value: "pushAt"
        }, {
            text: z.msg('task.startAt'),
            value: "startAt"
        }]
    });
    $(".srch_sort_order", jsrch).droplist({
        data: [{
            text: z.msg('srch.desc'),
            value: "DESC"
        }, {
            text: z.msg('srch.asc'),
            value: "ASC"
        }]
    });
}

function adjustLayout() {
    $("#L,#R").css("height", this.height);
    var H = $("#LT").parents(".WP").innerHeight();
    var tH = $("#LT").outerHeight();
    var bH = H - tH;
    $(".B").css("height", bH);
    var detailH = $("#thierachy").outerHeight();
    $("#tsubs").css("height", H - detailH);
    var W = $("#tsubs").innerWidth();
    $("#tsubs .newtask").css("width", W - this.scrollbar);
}