/**
 * 绑定 Task 列表的所有事件， 这个函数将在给定的 DOM 做 delegate，来绑定所有事件
 * <p>
 * 作为标识，它将会给选区增加一个 class(.task_events_opt)
 * <pre>
 * {
 *     reload : function(){...},     # 当需要重新载入数据时的回调。 this 为选区的 jq 对象
 *     reject : function(t){...},    # 当任务被 reject 后的回调，this 为 jTask 对象
 *     renew : function(t){...},    # 当任务被 renew 后的回调，this 为 jTask 对象
 *     restart : function(t){...},    # 当任务被 restart 后的回调，this 为 jTask 对象
 *     done : function(t){...},    # 当任务被 done 后的回调，this 为 jTask 对象
 *     gout : function(t){...},    # 当任务被 gout 后的回调，this 为 jTask 对象
 *     remove : function(t){...},    # 当任务被 remove 后的回调，this 为 jTask 对象
 * }
 * </pre>
 *
 * @param selection : Task 列表的 DOM 对象
 * @param opt : 配置对象
 */
function task_events_bind(selection, opt) {
    var selection = task_opt($(selection), opt || {});
    //---------------------------------------------------------
    selection.delegate(".task_check", "click", function() {
        task_jtask(this).toggleClass("task_checked");
    });
    selection.delegate(".task_del", "click", task_events_on_del);
    selection.delegate(".task_edit", "click", task_events_on_edit);
    selection.delegate(".task_goin", "click", task_events_on_goin);
    selection.delegate(".task_join", "click", task_events_on_join);
    selection.delegate(".task_gout", "click", task_events_on_gout);
    selection.delegate(".task_reject", "click", task_events_on_reject);
    selection.delegate(".task_hungup", "click", task_events_on_hungup);
    selection.delegate(".task_pause", "click", task_events_on_restart);
    selection.delegate(".task_done", "click", task_events_on_renew);
    selection.delegate(".task_ing", "click", task_events_on_done);
    selection.delegate(".task_labels *", "click", task_events_on_label);
    selection.delegate(".task_content", "click", task_events_on_showDetail);

    // 标签事件
    selection.delegate(".task_lbe", "click", cancel_bubble);
    selection.delegate(".task_lbe input", "change", _task_lbe_on_change_);
    selection.delegate(".task_lbe input", "keyup", _task_lbe_on_keyup_);
    selection.delegate(".task_lbe input", "keydown", _task_lbe_on_esc_);
}

/**
 * 事件处理: 显示一个任务详细信息，包括 comments
 */
function task_events_on_showDetail(e) {
    var ee = _task_obj(this);
    var t = ee.t;
    t.comments = t.comments || [];
    // 只有纯 .task_content 被点击才有效
    if(!$(e.target).hasClass("task_content"))
        return;
    // 显示
    task_detail_show(t);
}

/**
 * 事件处理: 当点击标签编辑按钮
 */
function task_events_on_label(e) {
    // 删掉其他的标签编辑框
    $(".task_lbe").remove();
    // 去掉 body 的点击
    $(document.body).click();
    // 停止冒泡
    e.stopPropagation();
    // 开始显示
    var ee = _task_obj(this);
    var jLbs = $(".task_labels", ee.jTask);

    // 获取标签数值
    var lbs = [];
    jLbs.children(".task_labels_item").each(function() {
        lbs.push($(this).attr("val"));
    });
    // 建立一个 html，插入到标签容器中
    var jq = $(task_html_lbe()).prependTo($(".task_labels_gasket",ee.jTask)).attr("old-value", lbs.join(","));
    jq.css({
        width: ee.jTask.innerWidth() - 30,
        top: jLbs.outerHeight(),
    });
    // 设立 body 取消事件
    $(document.body).one("click", function() {
        $(".task_lbe").remove();
    });
    // 聚焦
    $("input", jq).val(lbs.join(",")).select();
}

function _task_lbe_on_change_(e) {
    var ee = _task_obj(this);
    var t = ee.t;
    var oldv = ee.jTask.find(".task_lbe").attr("old-value");
    var newv = t.labels ? t.labels.join(",") : "";
    if(oldv != newv) {
        ajax.post("/ajax/task/set/labels", {
            tid: t._id,
            lbs: newv
        }, function() {
            $(".task_lbe").remove();
        });
    }
}

function _task_lbe_on_esc_(e) {
    if(27 == e.which) {
        _task_lbe_do_cancel_();
    }
}

function _task_lbe_do_cancel_() {
    var jq = $(".task_lbe");
    if(jq.size() == 0)
        return;
    var ee = _task_obj(jq);
    _task_lbe_redraw_labels_.apply(jq, [jq.attr("old-value")]);
    jq.remove();
}

function _task_lbe_on_keyup_(e) {
    _task_lbe_redraw_labels_.apply(this, [$(this).val()]);
}

// 这是一个绘制函数，用来绘制 task_labels, this 为 可以找到 jTask 的元素
function _task_lbe_redraw_labels_(s) {
    s = $.trim(s);
    var ss = s ? s.split(/[, ]+/) : [];
    var ee = _task_obj(this);
    var jLbs = $(".task_labels", ee.jTask);
    var t = ee.t;
    t.labels = ss;
    // 移除旧的
    var jLbs = $(".task_labels", ee.jTask).empty();
    jLbs.find(".task_labels_item").remove();
    // 添加新的
    jLbs.html(task_html_labels(t.labels));
}

/**
 * 事件处理: 当点击完成按钮
 */
function task_events_on_done() {
    var ee = _task_obj(this);
    ajax.post("/ajax/do/pop", {
        tid: ee.t._id,
        done: true
    }, function(re) {
        ee.jTask.data("task", re.data);
        if( typeof ee.opt.done == "function") {
            ee.opt.done.apply(ee.jTask, [re.data]);
        }
    });
}

/**
 * 事件处理: 当点击拒绝动按钮
 */
function task_events_on_reject() {
    var ee = _task_obj(this);
    ajax.post("/ajax/do/pop", {
        tid: ee.t._id,
        done: false
    }, function(re) {
        ee.jTask.data("task", re.data);
        if( typeof ee.opt.reject == "function") {
            ee.opt.reject.apply(ee.jTask, [re.data]);
        }
    });
}

/**
 * 事件处理: 当点击重新开始按钮
 */
function task_events_on_restart() {
    var ee = _task_obj(this);
    ajax.post("/ajax/do/restart", {
        tid: ee.t._id,
    }, function(re) {
        ee.jTask.data("task", re.data);
        if( typeof ee.opt.restart == "function") {
            ee.opt.restart.apply(ee.jTask, [re.data]);
        }
    });
}

/**
 * 事件处理: 当点击重做按钮
 */
function task_events_on_renew() {
    var ee = _task_obj(this);
    ajax.post("/ajax/do/pop", {
        tid: ee.t._id,
        done: false
    }, function(re) {
        ee.jTask.data("task", re.data);
        if( typeof ee.opt.renew == "function") {
            ee.opt.renew.apply(ee.jTask, [re.data]);
        }
    });
}

/**
 * 事件处理: 当点击挂起按钮
 */
function task_events_on_hungup() {
    var ee = _task_obj(this);
    ajax.post("/ajax/do/hungup", {
        tid: ee.t._id,
    }, function(re) {
        ee.jTask.data("task", re.data);
        var jBlock = ee.jTask.parent();
        // 移除当前的 jTask
        z.removeIt(ee.jTask, function() {
            // 生成一个新的 Task 插入队尾
            var newTask = task_html.apply(jBlock, [re.data, {
                goin: false
            }]);
            newTask[0].scrollIntoView(false);
            z.blinkIt(newTask, 1500);
        });
    });
}

/**
 * 事件处理: 当点击左移动按钮
 */
function task_events_on_gout() {
    var ee = _task_obj(this);
    var jBlock = ee.jTask.parents(".hierachy_block");
    var leftBlock = jBlock.prev();
    // 看看是否为顶级任务
    if(!ee.t.parentId) {
        alert(z.msg("task.istop"));
        return;
    }
    // 执行
    ajax.post("/ajax/task/gout", {
        tid: ee.t._id
    }, function(re) {
        ee.jTask.data("task", re.data);
        if( typeof ee.opt.gout == "function") {
            ee.opt.gout.apply(ee.jTask, [re.data]);
        } else {
            z.removeIt(ee.jTask, leftBlock.size() > 0 ? {
                prependTo: leftBlock,
            } : null);
        }
    });
}

/**
 * 事件处理: 当点击加入按钮
 */
function task_events_on_join() {
    var ee = _task_obj(this);
    // 首先去掉自己的选择
    ee.jTask.removeClass("task_checked");
    // 然后获取 ID
    var tids = [];
    ee.jTask.parent().children(".task_checked").each(function() {
        tids.push($(this).attr("task-id"));
    });
    // 看看是否为空
    if(tids.length == 0) {
        alert(z.msg("task.nochecked"));
        return;
    }
    // 执行
    ajax.post("/ajax/task/set/parent", {
        tids: tids.join(","),
        pid: ee.t._id
    }, function(re) {
        ee.jTask.data("task", re.data);
        // 移除掉临时块，以便刷新
        $(".hierachy_erratic", ee.selection).remove();
        // 移除掉返回的 task 对应的 .task 的 DOM
        for(var i = 0; i < tids.length; i++) {
            $(".id_" + tids[i], ee.selection).remove();
        }
        // 闪烁一下
        var jTask = task_html.apply(ee.jTask, [re.data, {
            mode: "replace"
        }]);
        z.blinkIt(jTask, 800);
    });
}

/**
 * 事件处理: 进入一个任务
 */
function task_events_on_goin() {
    var ee = _task_obj(this);
    var hie = hierachy_selection(ee.jTask);
    if(!hie || hie.size() == 0)
        return;
    // 准备标题
    var title = task_format_title(ee.t);
    // 发送请求
    ajax.get("/ajax/task/children", {
        tid: ee.t._id
    }, function(re) {
        hierachy_add.apply(hie[0], [title, re.data]).attr("task-id", ee.t._id);
    });
}

/**
 * 事件处理: 编辑
 */
function task_events_on_edit() {
    var ee = _task_obj(this);
    z.editIt(ee.jTask.find(".task_content"), {
        multi: true,
        after: function(newval, oldval) {
            if(newval && newval != oldval) {
                newval = $.trim(newval.replace(/[\n]{1,}/g, " ")).replace(/[ ][ ]/, " ");
                if(newval.length < 5) {
                    alert(z.msg("e.t.short_task"));
                    return;
                } else if(newval.length > 140) {
                    alert(z.msg("e.t.long_task"));
                    return;
                }
                ajax.post("/ajax/task/set/text", {
                    tid: ee.t._id,
                    txt: newval
                }, function(re) {
                    // 找到所有对应的 TASK，进行数据修改
                    $(".id_"+ee.t._id).each(function() {
                        var jq = $(".task_content", ee.jTask).html(task_format_text(re.data.text));
                        ee.t.text = re.data.text;
                        z.blinkIt(this);
                    });
                });
            }
        }
    });
}

/**
 * 事件处理: 删除，之后重载一下 .task > parent ...
 */
function task_events_on_del() {
    var ee = _task_obj(this);
    var recurDelete = ee.t.number[0] > 0 ? window.confirm(z.msg("task.del.tip")) : true;
    ajax.get("/ajax/task/del", {
        tid: ee.t._id,
        r: recurDelete
    }, function(re) {
        // 进行 remove 前的回调
        if( typeof ee.opt.remove == "function") {
            ee.opt.remove.apply(ee.jTask, [ee.t]);
        }
        // 删除临时区块
        $(".hierachy_erratic", ee.selection).remove();
        // 递归删除的，直接显示个动画
        if(recurDelete) {
            z.removeIt(ee.jTask);
        }
        // 否则需要重新加载
        else {
            if(ee.opt && typeof ee.opt.reload == "function")
                ee.opt.reload.apply(ee.selection);
        }
    });
}

/**
 * 从任何一个对象找到相关的 jTask
 *
 * @return jTask 对象
 */
function task_jtask(ele) {
    return $(ele).hasClass("task") ? $(ele) : $(ele).parents(".task");
}

/**
 * 从任何一个对象找到选区
 */
function task_selection(ele) {
    return $(ele).hasClass("task_events_opt") ? $(ele) : $(ele).parents(".task_events_opt");
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象
 * @param opt : 配置对象
 */
function task_opt(selection, opt) {
    if(!opt)
        return selection.data("task-events-opt");
    return selection.data("task-events-opt", opt).addClass("task_events_opt");
}

/**
 * 提供给事件处理函数使用的私有方法，它根据 ele 参数总结出一些必要的对象信息，并返回
 * @param ele 事件响应 DOM 对象
 * @return 常用的事件处理数据对象
 */
function _task_obj(ele) {
    var me = $(ele);
    var jTask = task_jtask(ele);
    var selection = task_selection(ele);
    return {
        me: me,
        jTask: jTask,
        t: jTask.data("task"),
        selection: selection,
        opt: task_opt(selection)
    }
}