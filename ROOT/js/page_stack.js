function main() {
    // 读取左侧所有的堆栈列表
    var topStackUrl = $(document.body).attr("url-stack-top");
    ajax.get(topStackUrl, function(re) {
        hierachy_init.apply($("#stacks"), [{
            direction: "left",
            append: function(obj) {
                var jq = $(stack_html(obj)).appendTo(this).data("stack", obj);
            },
            hltblock: function(jCrumb) {
                var jStacks = $(".stack", this);
                // 寻找一个堆栈
                var jq = jCrumb.next();
                var jStack = null;
                if(jq.size() > 0) {
                    var stackName = jq.text();
                    for(var i = 0; i < jStacks.size(); i++) {
                        jStack = $(jStacks[i]);
                        var txt = $(".stack_name",jStack).text();
                        if(txt == stackName) {
                            jq = jStack;
                            break;
                        } else {
                            jStack = null;
                        }
                    }
                }
                // 默认的选择第一个堆栈
                if(!jStack) {
                    jq = jStacks.first();
                }
                // 设置高亮
                if(jq.hasClass("stack_hlt"))
                    return;
                jq.find(".stack_head").click();
            },
            events: {}
        }, "::stack.top", re.data]);
    });
    // 读取右侧所有的新 Task，按照时间逆序排列
    ajax.get("/ajax/task/topnews", function(re) {
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
        }, "::task.top", re.data]);
    });
    // 事件 : 堆栈 ...
    stack_events_bind($("#stacks"), {
    });
    // 事件 : 堆栈任务 ...
    task_events_bind($("#stacks"), {
        reload: function() {
            var jStack = $("#stacks .hierachy_block_hlt .stack_hlt");
            stack_do_reload.apply(jStack);
        },
        reject: function(t) {
            stack_inc(this, -1);
            var jBlock = $("#tasks .hierachy_block_hlt");
            z.removeIt(this, {
                after: function() {
                    // 移除右侧的临时块，以便刷新数据
                    $("#tasks .hierachy_erratic").remove();
                    // 如果右侧的 task-id 与被移除的 t.parentId 匹配，加入右侧块
                    var rightTaskId = $("#tasks .hierachy_crumb_item_hlt").attr("task-id");
                    if((!t.parentId && !rightTaskId) || t.parentId == rightTaskId) {
                        var oldTask = $("." + t._id, jBlock);
                        var jq = task_html.apply(ta, [t, {
                            mode: (oldTask.size() == 0 ? "prepend" : "replace")
                        }]);
                        jq[0].scrollIntoView(false);
                        z.blinkIt(jq, 2000);
                    }
                }
            });
        }
    });
    // 事件 : 任务 ...
    task_events_bind($("#tasks"), {
        reload: function() {
            doReloadTasks($("#tasks .hierachy_crumb_item_hlt").attr("task-id"));
        }
    });
    // 事件 : 压入堆栈
    $("#tasks").delegate(".task_push", "click", function() {
        doPushToStack(task_jtask(this));
    });
}

/**
 * 将给定的 jTask 压入当前高亮的堆栈
 * <p>
 * 本函数执行完毕后，会自动重新加载堆栈
 *
 * @param jTask : task 的 jq 对象
 */
function doPushToStack(jTask) {
    var jStack = $("#stacks .hierachy_block_hlt .stack_hlt");
    var snm = $(".stack_name", jStack).text();
    ajax.post("/ajax/do/push", {
        tid: jTask.attr("task-id"),
        s: snm
    }, function() {
        // 移除 jTask
        z.removeIt(jTask);
        // 重新加载 stack
        stack_do_reload.apply(jStack);
    });
}

function redrawTasks(tasks) {
    $("#tasks .hierachy_erratic").remove();
    hierachy_redraw.apply($("#tasks"), [tasks]);
}

// 有 taskId 就读 children 否则读 topnews
function doReloadTasks(taskId) {
    if(taskId) {
        ajax.get("/ajax/task/children", {
            tid: taskId
        }, function(re) {
            redrawTasks(re.data);
        });
    } else {
        ajax.get("/ajax/task/topnews", function(re) {
            redrawTasks(re.data);
        });
    }
}

function initLayout() {
    task_newer_appendTo("#tasks");
}

function adjustLayout() {
    $("#tasks .newtask").css("width", this.width / 2 - this.scrollbar);
    $(".hierachy").css("height",this.height).each(hierachy_layout);
}