function main() {
    // 读取左侧所有的堆栈列表
    // 初始化右侧所有的新 Task，按照时间逆序排列
    hierachy_init.apply($("#tasks"), [{
        direction: 'right',
        append: function(t) {
            task_html.apply(this, [t]);
        },
        events: {
            "click:.hierachy_crumb_item_hlt": function() {
                doReloadTasks($(this).attr("task-id"));
            }
        }
    }, "::task.results"]);
    // 事件 : 计划 ...
    plan_events_bind.apply($("#flt_plan .plan_flt"), [$("#plan .plan_main")]);
    // 事件 : 任务 ...
    task_events_bind($("#tasks"), {
        reload: function() {
            doReloadTasks($("#tasks .hierachy_crumb_item_hlt").attr("task-id"));
        },
        reject: task_replace,
        renew: task_replace,
        restart: task_replace,
        hungup: task_replace,
        done: task_replace
    });
    // 事件 : 压入堆栈
    $("#tasks").delegate(".task_push", "click", function() {
        // doPushToStack(task_jtask(this));
    });
    // 初始化 : 锁定日期
    init_pgan();
}

function redrawTasks(tasks) {
    $("#tasks .hierachy_erratic").remove();
    hierachy_redraw.apply($("#tasks"), [tasks]);
}

function init_pgan() {
    var pgan = z.pgan() || "";

    // 显示左侧
    plan_redraw.apply(this, [pgan]);

    // 显示右侧列表
    $(".srch_keyword input").val("%(NEW)");
    $(".srch_toptask").click();
}

function initLayout() {
    // 新增 Task 的编辑框
    task_newer_appendTo("#tasks");

    // 绑定 Task Comment 事件
    task_detail_bind();

    // 绑定任务搜索事件
    task_search_bind("#flt_task", function(ts) {
        hierachy_clear($("#tasks"))
        redrawTasks.apply(this, [ts]);
    });
    // 绑定日期过滤事件

}

function adjustLayout() {
    var skyH = $("#sky").outerHeight();
    var fH = $("#filters").css("width", this.width).outerHeight();
    $("#flt_plan").css("height", $("#filters").innerHeight());
    $("#plan").css({
        "height": this.height,
        "padding-top": skyH + fH
    });
    $("#tasks .newtask").css("width", this.width * 0.3 - this.scrollbar);
    $(".hierachy").css("height",this.height).each(hierachy_layout);
    plan_on_resize($("#flt_plan"));
}