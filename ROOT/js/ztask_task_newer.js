/**
 * 附加一个 new task 框到某一个 hierachy
 * <p>
 * 配置对象
 * <pre>
 * {
 *     done : function(t){...}    # 当保存成功后的回调，t 为新生成的 Task 对象
 * }
 * </pre>
 *
 * @param ele 一个和 hierachy 相关的 DOM
 */
function task_newer_appendTo(ele, opt) {
    var hie = hierachy_check_selection(ele);

    // init HTML
    var html = '<div class="newtask">';
    html += '<textarea></textarea><a></a>';
    html += '</div>';
    // Add to DOM
    var jNewer = $(html).appendTo(ele);
    jNewer.find("textarea").toggleInput(z.msg("stack.new.tip"));

    var opt = opt || {};
    task_newer_opt(jNewer, opt);

    // bind events
    $(".newtask textarea",hie).keydown(task_newer_on_keydown);
    $(".newtask a",hie).click(task_newer_do);
    // 事件 : 显示/隐藏新任务输入框
    hie.mousemove(function(e) {
        var jNT = $(".newtask", this);
        if(jNT.attr("in-ani") || !jNT.attr("overed-before"))
            return;
        var h = jNT.outerHeight();
        if((e.pageY + 80) < jNT.offset().top) {
            jNT.attr("in-ani",true).animate({
                "bottom": 8 - h
            }, 500, function() {
                $(this).removeAttr("in-ani").find("textarea").blur();
            });
        } else {
            jNT.attr("in-ani",true).animate({
                "bottom": 0
            }, 150, function() {
                $(this).removeAttr("in-ani");
            });
        }
    });
    $(".newtask",hie).one("mouseover", function() {
    $(this).attr("overed-before", true);
    }).css("width",            hie.outerWidth() -             z.scrollBarWidth());
}

/**
 * 事件处理: 当点击右侧新增按钮时的回调
 *
 * @param this 可能是 textarea 或者 新增按钮
 */
function task_newer_do() {
    var hie = hierachy_check_selection(this)
    var jNewer = $(this).parents(".newtask");
    var jTxt = $("textarea", jNewer);

    var str = jTxt.toggleInput("get");
    ajax.post("/ajax/task/save", {
        pid: $(".hierachy_crumb_item_hlt", hie).attr("task-id"),
        tt: str
    }, function(re) {
        var t = re.data;
        var jBlock = $(".hierachy_block_hlt", hie);
        // 动画显示新的项目
        var jTask = task_html.apply(jBlock, [t, {
            mode: "prepend"
        }]);

        jTask[0].scrollIntoView(false);
        z.blinkIt(jTask, 1500);
        // 下面，我们来判断一下，如果当前项目有 parentId，那么我们需要递归的更新一下所有的 task
        var map = task_parents_in_dom.apply($(".hierachy_scroller", hie)[0], [t._id]);
        if(map.parents.length > 0)
            ajax.post("/ajax/task/get", {
                tids: map.parents.join(",")
            }, function(re) {
                // 将得到的结果，与 map 进行比对, map 中，按照 id 存放了 jq 对象
                for(var i = 0; i < re.data.length; i++) {
                    var t = re.data[i];
                    var jq = map[t._id];
                    task_html.apply(jq, [t, {
                        mode: "replace"
                    }]);
                }
            });
        // 重新聚焦选择框
        jTxt.val("").focus().blur().select();
        // 调用回调
        var opt = task_newer_opt(jNewer);
        if( typeof opt.done == "function") {
            opt.done.apply(jNewer, [t]);
        }
    });
}

/**
 * 事件处理: 处理 newer 中的 textarea 事件，以便支持 shift + enter 快捷键
 */
function task_newer_on_keydown(e) {
    if(e.which == 13 && window.keyboard.shift) {
        task_newer_do.apply(this);
        return false;
    }
}

/**
 * 从任何一个对象找到选区
 */
function task_newer_selection(ele) {
    return $(ele).hasClass("task_newer_opt") ? $(ele) : $(ele).parents(".task_newer_opt");
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象
 * @param opt : 配置对象
 */
function task_newer_opt(selection, opt) {
    if(!opt)
        return selection.data("task-newer-opt");
    return selection.data("task-newer-opt", opt).addClass("task_newer_opt");
}