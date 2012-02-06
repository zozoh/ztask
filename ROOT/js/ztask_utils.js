/**
 * 缓存系统的全局设定
 *
 * @return 系统全局设定
 */
var _GINFO = null;
function ginfo() {
    if(!_GINFO)
        _GINFO = ajax.syncGet("/ajax/g/get").data;
    return _GINFO;
}

/**
 * 根据给定的 stack 对象，返回该对象的 HTML
 *
 * @param this : 无意义
 * @param s : TaskStack 对象
 * @return HTML 字符串表示一个 Task 对象
 */
function stack_html(s) {
    var html = '<div class="stack">';
    html += '<div class="stack_head">';
    html += '    <span class="stack_count">' + s.count + '</span>';
    html += '    <span class="stack_name">' + s.name + '</span>';
    html += '    <a class="lnkb stack_owner" href="/page/user#' + s.owner + '" >@' + s.owner + '</a>';
    html += '</div>';
    html += '<div class="stack_body">';
    html += '</div>';
    html += '</div>';
    return html;
}

/**
 * 在给定选区，返回给定 taskId 的父 Task 对象(不包括给定taskId)，为了方便使用，返回对象格式为
 * <pre>
 * {
 *     parents : ["4f29...56a", "4f29...569"],  # 按顺序的 parent 列表，0 为 top 的 Task
 *                                              # 如果为顶级 Task，那么就是空数组
 *     "4f29...56a" : DOM | jq,
 *     "4f29...569" : DOM | jq
 * }
 * </pre>
 * 这个函数要求， .task 的 DOM 需要有 "task" 这个 data
 *
 * @param this : 选区
 * @param taskId : 任务 ID
 *
 * @param 查询结果
 */
function task_parents_in_dom(taskId) {
    var re = {
        parents: []
    };
    var jTask = $(".id_" + taskId, this);
    var t = jTask.data("task");

    while(t && t.parentId) {
        jTask = $(".id_" + t.parentId, this);
        if(jTask.size() == 0)
            break;
        t = jTask.data("task");
        re.parents.unshift(t._id);
        re[t._id] = jTask;
    }

    return re;
}

/**
 * 根据 ginfo().formats 属性，将字符串格式化
 *
 * @param str : 需要被格式化的字符串
 * @return 格式化后的字符串
 */
function task_format_text(str) {
    var formats = ginfo() ? ginfo().formats : null;
    if(!str || !$.isArray(formats) || formats.length <= 0)
        return str;
    // 循环替换字符串
    for(var i = 0; i < formats.length; i++) {
        var f = formats[i];
        str = str.replace(new RegExp(f.regex, "ig"), f.tmpl);
    }
    return str;
}

/**
 * 根据 ginfo().formats 属性，将字符串格式化，并包裹成 comment 需要显示的 DOM
 *
 * @param str : 需要被格式化的字符串
 * @return 格式化后的字符串
 */
function task_wrap_comment(str) {
    var formats = ginfo() ? ginfo().formats : null;
    if(!str || !$.isArray(formats) || formats.length <= 0)
        return str;
    // 循环替换字符串
    for(var i = 0; i < formats.length; i++) {
        var f = formats[i];
        str = str.replace(new RegExp(f.regex, "ig"), f.tmpl);
    }
    return '<pre class="task_comment">' + str + '</pre>';
}

/**
 * 格式化 taskId 字符串，让其变成可显示的 html
 *
 * @param tid : 任务的ID
 *
 * @return HTML 字符串
 */
function task_format_id(tid) {
    var html = '<a href="/page/task#' + tid + '">';
    html += tid.replace(/(\w{8})(\w{6})(\w{4})(\w{6})/, "<u>$1</u><em>$2</em><b>$3</b><i>$4</i>");
    html += '</a>';
    return html;
}

/**
 * 格式化 task 的 text 字段，让其简要显示 task 的信息，以便显示在 crumb 上
 *
 * @param t : 任务对象
 *
 * @return 格式化后的字符串，符合 z.uname()
 */
function task_format_title(t) {
    var txt = t.text.replace(z.REG_NOWORD, "");
    return ":" + t._id + ":" + z.strBrief(txt, 16);
}

/**
 * 任务的菜单项默认配置 －－ 根据 stask.status
 */
var _TASK_MENU_ = {
    "task_push": "edit,join,del,gout,check,label",
    "task_ing": "label,edit,reject,hungup",
    "task_pause": "label,edit,reject",
    "task_done": "label,del,edit,join,check"
};

/**
 * 根据一个 Task 对象，生成一个 HTML 片段，并加入 DOM 节点
 * <p>
 * 配置信息包括:
 * <pre>
 * {
 *     menu : "del,edit,join,check",     # 自定义显示菜单项，如果没有这个，
 *                                       # 则默认根据评估过的状态来显示菜单项
 *                                       # 可能的状态有，task_push, task_done, task_ing
 *     goin : true | false | String,     # 是否显示 goin 的按钮，如果为字符串，则为给定样式
 *     mode : "html|replace|prepend|append"   # 几种不同得操作模式，默认为 append
 *                                            #   html - 仅仅返回 html 片段
 *                                            #   replace - 替换掉 this 指向的 DOM
 *                                            #   prepend - prependTo this 指向的 DOM
 *                                            #   append - appendTo this 指向的 DOM
 * }
 * </pre>
 *
 * @param this : 参考 DOM 对象
 * @param t : Task 对象
 * @param opt : [可选] 配置信息
 * @return HTML 字符串表示一个 Task 对象
 */
function task_html(t, opt) {
    opt = opt || {};
    var html = '<div class="task id_' + t._id + '" task-id="' + t._id + '">';
    /*
     * 标签
     */
    html += '<ul class="task_labels">';
    if($.isArray(t.labels) && t.labels.length > 0)
        for(var i = 0; i < t.labels.length; i++) {
            html += '<li class="task_labels_item">' + t.labels[i] + '</li>';
        }
    html += '</ul>';
    /*
     * 内容
     */
    html += '<div class="task_content">' + task_format_text(t.text) + '</div>';
    /*
     * 判断状态
     */
    var statusText, statusClass;
    if("NEW" == t.status || (t.number[0] > 0 && "DONE" != t.status)) {
        if(t.number[0] > 0 && t.number[3] == 0) {
            statusText = "...";
            statusClass = "task_wait";
        } else {
            statusText = z.msg("task.do.push");
            statusClass = "task_push";
        }
    } else if("ING" == t.status) {
        statusText = z.msg("task.do.ing");
        statusClass = "task_ing";
    } else if("HUNGUP" == t.status) {
        statusText = z.msg("task.do.pause");
        statusClass = "task_pause";
    } else if("DONE" == t.status) {
        statusText = z.msg("task.do.done");
        statusClass = "task_done";
    } else {
        throw "Uknow task status '" + t.status + "'";
    }
    /*
     * 编辑菜单
     */
    var menu = opt.menu || _TASK_MENU_[statusClass];
    if(menu) {
        menu = menu.split(",");
        html += '<div class="menu task_menu">';
        for(var i = 0; i < menu.length; i++) {
            html += '<a class="task_' + menu[i] + '">' + z.msg("task.menu." + menu[i]) + '</a>';
        }
        html += '</div>';
    }
    /*
     * 主动作按钮
     */
    html += '<div class="task_btn ' + statusClass + '">';
    html += '    <div class="task_status">' + statusText + '</div>';
    html += '</div>';
    /*
     * 节点任务的统计数据
     */
    if(t.number && t.number[0] > 0) {
        html += '<div class="task_nums">';
        html += '<div class="task_num_ing">' + t.number[2] + '</div>';
        html += '<div class="task_num_done">' + t.number[1] + '</div>';
        html += '<div class="task_num_all">' + t.number[0] + '</div>';
        html += '</div>';
    }
    /*
     * 拆分按钮(没有子节点) 或者 查看子节点
     */
    if(false != opt.goin) {
        var s = typeof opt.goin == "string" ? opt.goin : "detail";
        html += '<a class="task_goin task_' + s + '">' + z.msg("task.goin." + s) + '</a>';
    }

    /*
     * 任务信息
     */
    html += '<div class="task_uinfo">';
    if(t.stack && "--" != t.stack)
        html += '<span class="task_stack">' + z.msg("ui.stack") + ' : <b>' + t.stack + '</b></span>';
    if(t.parentId) {
        html += '    <span class="task_ID">' + z.msg("task.parent") + ":" + task_format_id(t.parentId) + '</span>';
    }
    html += '    <span class="task_ID">ID:' + task_format_id(t._id) + '</span>';
    html += '    <span><a class="lnk" href="/page/user#' + t.owner + '">@' + t.owner + '</a>';
    html += '    ' + t.lastModified + '</span>';
    html += '</div>';
    html += '</div>';

    // 根据 mode 返回
    var jq = null;
    opt.mode = opt.mode || "append";
    // append
    if("append" == opt.mode) {
        jq = $(html).appendTo(this);
    }
    // prepend
    else if("prepend" == opt.mode) {
        jq = $(html).prependTo(this);
    }
    // replace
    else if("replace" == opt.mode) {
        jq = $(html).replaceAll(this);
    }
    // HTML
    else if("html" == opt.mode) {
        return html;
    }
    // 错误
    else {
        throw "Uknown mode '" + opt.mode + "'";
    }

    return jq.data("task", t);
}

/**
 * 生成一段关于 Task labels 编辑泡泡的 HTML
 */
function task_html_lbe() {
    var html = '<div class="task_lbe">';
    html += '<div class="task_lbe_tip">' + z.msg("task.label.edit.tip") + '</div>';
    html += '<input>';
    //html += '<div class="task_lbe_btns">';
    //html += '<a class="task_lbe_cancel">' + z.msg("ui.cancel") + '</a>';
    //html += '<a class="task_lbe_ok"">' + z.msg("ui.ok") + '</a>';
    //html += '</div>';
    html += '</div>';
    return html;
}

/**
 * 取消冒泡的默认函数
 */
function cancel_bubble(e) {
    e.stopPropagation();
}