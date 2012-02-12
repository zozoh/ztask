/**
 * 绑定 Stack 列表的所有事件， 这个函数将在给定的 DOM 做 delegate，来绑定所有事件
 * <p>
 * 作为标识，它将会给选区增加一个 class(.stack_events_opt)
 * <pre>
 * {
 *     reload : function(){...},     # 当需要重新载入数据时的回调。 this 为选区的 jq 对象
 * }
 * </pre>
 *
 * @param selection : Stack 列表的 DOM 对象
 * @param opt : 配置对象
 */
function stack_events_bind(selection, opt) {
    var selection = stack_opt($(selection), opt || {});
    //---------------------------------------------------------
    selection.delegate(".stack", "click", stack_events_on_active);
    selection.delegate(".stack_reload", "click", stack_events_on_reload);
    selection.delegate(".stack_view_item", "click", stack_events_on_change_view_type);
    selection.delegate(".stack_name", "click", stack_events_on_goin);
    selection.delegate(".stack_favo", "click", stack_events_on_watchOrNot);

}

/**
 * 事件处理: 进入查看一个堆栈的子堆栈
 */
function stack_events_on_watchOrNot(e) {
    e.stopPropagation();
    var ee = _stack_obj(this);
    // 收藏或者取消收藏，预先判断一下
    var jFavo = $(this);
    var isOn = jFavo.hasClass("stack_favo_on");
    var url = isOn ? "/ajax/stack/do/unwatch" : "/ajax/stack/do/watch";
    // 发送请求
    ajax.post(url, {
        s: ee.s.name
    }, function() {
        // 变取消
        if(isOn) {
            jFavo.removeClass("stack_favo_on");
            jFavo.attr("title", z.msg("stack.unwatch.tip"));
        }
        // 变收藏
        else {
            jFavo.addClass("stack_favo_on");
            jFavo.attr("title", z.msg("stack.watch.tip"));
        }
    });
}

/**
 * 事件处理: 进入查看一个堆栈的子堆栈
 */
function stack_events_on_goin(e) {
    e.stopPropagation();
    var ee = _stack_obj(this);
    var hie = hierachy_check_selection(ee.jStack);
    // 定义处理函数
    var func = function(ss) {
        hierachy_add.apply(hie[0], [ee.s.name, ss]);
    };
    // 读取数据，如果有子节点，进入
    ajax.post("/ajax/stack/children", {
        s: ee.s.name
    }, function(re) {
        if(re.data && re.data.length > 0) {
            hierachy_add.apply(hie[0], ["::" + ee.s.name, re.data]);
        }
    });
}

/**
 * 事件处理: 高亮当前堆栈，如果内容为空，重新加载
 */
function stack_events_on_active() {
    var ee = _stack_obj(this);
    if(!ee.jStack.hasClass("stack_hlt")) {
        $(".stack_hlt", ee.selection).removeClass("stack_hlt");
        ee.jStack.addClass("stack_hlt");
        // 没有设置显示类型 ...
        if($(".stack_view_item_hlt", ee.jStack).size() == 0) {
            $(".stack_view_full",ee.jStack).addClass("stack_view_item_hlt");
        }
        // 没有内容，重新加载
        if($(".task",ee.jStack).size() == 0) {
            $(".stack_reload", ee.jStack).click();
        }
    }
}

/**
 * 事件处理: 重新加载当前堆栈任务
 */
function stack_events_on_reload() {
    // 设置高亮
    if(!$(this).hasClass("stack_hlt")) {
        var ee = _stack_obj(this);
        $(".stack_hlt", ee.selection).removeClass("stack_hlt");
        $(this).parents(".stack").toggleClass("stack_hlt");
    }
    // 重新加载
    stack_do_reload.apply(this);
}

/**
 * 事件处理: 改变当前堆栈类型的显示方式
 */
function stack_events_on_change_view_type() {
    if(!$(this).hasClass("stack_view_item_hlt")) {
        $(this).parent().find(".stack_view_item_hlt").removeClass("stack_view_item_hlt");
        $(this).addClass("stack_view_item_hlt");
    }
    stack_redraw_mytasks.apply(this);
}

/**
 * 重新绘制当前 stack 的数据
 *
 * @param this - 一个 DOM 对象，它可以是 .stack 或者其内任意一个元素
 * @param ts - Task 的数据对象列表，如果没有，从 DOM 中取得数据
 * @param animate - 是否动画显示重绘过程
 */
function stack_redraw_mytasks(ts, animate) {
    // 找到 body
    var jStack = stack_jstack(this);
    var viewType = $(".stack_view_item_hlt", jStack).attr("val");
    var jBody = $(".stack_body", jStack);
    // 检查数据
    if(!ts) {
        ts = [];
        $(".task",jStack).each(function() {
            ts.push($(this).data("task"));
        });
    }
    // 循环添加
    if(ts) {
        jBody.empty();
        var opt = {
            goin: false,
            mode: "append",
            viewType: viewType
        };
        var i = 0;
        // 快速显示
        if(!animate) {
            for(; i < ts.length; i++) {
                var jTask = task_html.apply(jBody, [ts[i], opt]);
                if("hide" != viewType)
                    z.blinkIt(jTask);
            }
        }
        // 动画显示
        else {
            var func = function() {
                if(i >= ts.length)
                    return;
                var jTask = task_html.apply(jBody, [ts[i++], opt]);
                if("hide" != viewType) {
                    jTask[0].scrollIntoView(false);
                    z.blinkIt(jTask, {
                        speed: 80,
                        after: func
                    });
                } else {
                    func();
                }
            };
            func();
        }
    }
}

/**
 * 重新加载当前 stack 的数据
 *
 * @param this - 一个 DOM 对象，它可以是 .stack 或者其内任意一个元素
 */
function stack_do_reload() {
    var jStack = stack_jstack(this);
    var snm = $(".stack_name", jStack).text();
    var viewType = $(".stack_view_item_hlt", jStack).attr("val");
    var jReload = $(".stack_reload", jStack);
    jReload.find("img").css("visibility", "visible");
    jReload.find("b").css("visibility", "hidden");
    ajax.get("/ajax/stack/detail", {
        s: snm
    }, function(re) {
        // 重绘堆栈
        var isHlt = jStack.hasClass("stack_hlt");
        var newJStack = $(stack_html(re.data.stack)).replaceAll(jStack);
        newJStack.data("stack", re.data.stack);
        $(".stack_view_item[val="+viewType+"]",newJStack).addClass("stack_view_item_hlt");
        if(isHlt)
            newJStack.addClass("stack_hlt");
        // 重新排序，将 HUNGUP 的放在底部
        var list = [];
        for(var i = 0; i < re.data.tasks.length; i++) {
            if(re.data.tasks[i].status != "HUNGUP")
                list.push(re.data.tasks[i]);
        }
        for(var i = 0; i < re.data.tasks.length; i++) {
            if(re.data.tasks[i].status == "HUNGUP")
                list.push(re.data.tasks[i]);
        }
        // 重绘堆栈内的 Tasks
        stack_redraw_mytasks.apply(newJStack, [list, true]);
    });
}

/**
 * 修改某个 stack 的 count 值
 *
 * @param ele 任意一个和 jStack 相关的 DOM 对象
 * @param n 值，可正可负
 */
function stack_inc(ele, n) {
    var jCount = stack_jstack(ele).find(".stack_count");
    jCount.text(jCount.text() * 1 + n);
}

/**
 * 从任何一个对象找到相关的 jStack
 *
 * @return jStack 对象
 */
function stack_jstack(ele) {
    return $(ele).hasClass("stack") ? $(ele) : $(ele).parents(".stack");
}

/**
 * 从任何一个对象找到选区
 */
function stack_selection(ele) {
    return $(ele).hasClass("stack_events_opt") ? $(ele) : $(ele).parents(".stack_events_opt");
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象
 * @param opt : 配置对象
 */
function stack_opt(selection, opt) {
    if(!opt)
        return selection.data("stack-events-opt");
    return selection.data("stack-events-opt", opt).addClass("stack_events_opt");
}

/**
 * 提供给事件处理函数使用的私有方法，它根据 ele 参数总结出一些必要的对象信息，并返回
 * @param ele 事件响应 DOM 对象
 * @return 常用的事件处理数据对象
 */
function _stack_obj(ele) {
    var me = $(ele);
    var jStack = stack_jstack(ele);
    var selection = stack_selection(ele);
    return {
        me: me,
        jStack: jStack,
        s: jStack.data("stack"),
        selection: selection,
        opt: task_opt(selection)
    }
}