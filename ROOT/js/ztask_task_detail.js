/**
 * 这个函数将在 document.body 上 delegate 事件，用来处理 detail 面板的交互动作
 */
function task_detail_bind() {
    $(document.body).delegate(".task_cmt_add", "click", task_detail_cmt_on_add);
    $(document.body).delegate(".task_cmt_del", "click", task_detail_cmt_on_del);
    $(document.body).delegate(".task_cmt_edit", "click", task_detail_cmt_on_edit);
    $(document.body).delegate(".task_cmt_newer textarea", "keydown", task_detail_cmt_on_keydown);
    $(document.body).delegate(".task_detail_closer", "click", task_detail_on_close);
    $(document.body).delegate(".task_cmt_sort li", "click", function() {
        $(".task_cmt_sort .hlt").removeClass("hlt");
        $(this).addClass("hlt");
        var jDetail = $("#task_detail");
        task_detail_redraw_comments.apply(jDetail);
        z.local.set("ztask-comment-asc", task_detail_asc(jDetail));
    });
    $(document.body).delegate(".task_cmt_size", "click", function(e) {
        if($(this).attr("status") == "expand") {
            task_detail_cmt_collapse.apply(this, [e]);
        } else {
            task_detail_cmt_expand.apply(this, [e]);
        }
    });
    $(document.body).keydown(function(e) {
        if(27 == e.which)
            task_detail_on_close();
    });
}

/**
 * 事件: 扩大注释编写区域
 */
function task_detail_cmt_expand() {
    var jDetail = $(this).parents("#task_detail");
    var jNewer = $(".task_cmt_newer", jDetail);
    var jCmt = $(".task_cmt", jDetail);
    var h = jCmt.innerHeight();
    jNewer.css("height", h - 40);
    $(this).attr("status", "expand").text(z.msg("ui.collapse"));
}

/**
 * 事件: 缩小注释编写区域
 */
function task_detail_cmt_collapse() {
    var jDetail = $(this).parents("#task_detail");
    var jNewer = $(".task_cmt_newer", jDetail);
    var jCmt = $(".task_cmt", jDetail);
    jNewer.css("height", "");
    $(this).attr("status", "collapse").text(z.msg("ui.expand"));
}

/**
 * 事件: 处理关闭
 */
function task_detail_on_close() {
    $("#task_detail").animate({
        opacity: 0
    }, function() {
        $(this).css("top", 100000);
    });
}

/**
 * 事件: 处理注释的添加
 */
function task_detail_cmt_on_add() {
    var jDetail = $(this).parents("#task_detail");
    var jComments = $(".task_cmt_list", jDetail);
    var t = jDetail.data("task");
    var tid = jDetail.attr("task-id");
    var txt = $.trim($(".task_cmt_newer textarea",jDetail).val());
    if(!txt || txt.length < 5) {
        alert(z.msg("task.cmt.short"));
        return;
    }
    ajax.post("/ajax/do/comment/add", {
        tid: tid,
        txt: txt
    }, function(re) {
        // 清空选择框
        if($(".task_cmt_size", jDetail).attr("status") == "expand") {
            $(".task_cmt_size", jDetail).click();
        }
        $(".task_cmt_newer textarea",jDetail).val("");
        // 为数据对象加入文本
        t.comments.push(re.data);
        // 加入 DOM
        var jCmt;
        if(task_detail_asc(jDetail)) {
            jCmt = $(task_wrap_comment(re.data)).appendTo(jComments);
        } else {
            jCmt = $(task_wrap_comment(re.data)).prependTo(jComments);
        }
        jCmt[0].scrollIntoView(false);
        z.blinkIt(jCmt, 800);
    });
}

/**
 * 事件: 处理注释的编辑
 */
function task_detail_cmt_on_edit() {
    var jDetail = $(this).parents("#task_detail");
    var jCmt = $(this).parents(".task_cmt_item");
    var jPre = jCmt.find("pre");
    var t = jDetail.data("task");
    var tid = jDetail.attr("task-id");
    var index = task_detail_cmt_index(jCmt);
    z.editIt(jPre, {
        multi: true,
        after: function(newval, oldval) {
            ajax.post("/ajax/do/comment/set", {
                tid: tid,
                i: index,
                txt: newval
            }, function(re) {
                t.comments[index] = re.data;
                jPre.html(task_format_text(re.data));
                z.blinkIt(jCmt, 800);
            });
        }
    });
}

/**
 * 事件: 处理注释的删除
 */
function task_detail_cmt_on_del() {
    var jDetail = $(this).parents("#task_detail");
    var jCmt = $(this).parents(".task_cmt_item");
    var t = jDetail.data("task");
    var tid = jDetail.attr("task-id");
    var index = task_detail_cmt_index(jCmt);
    ajax.get("/ajax/do/comment/del", {
        tid: tid,
        i: index
    }, function(re) {
        t.comments = re.data.comments;
        z.removeIt(jCmt, {
            after: function() {
                task_detail_redraw_comments.apply(jDetail, [t]);
            }
        });
    });
}

/**
 * 事件: 处理输入时的 Ctrl + Enter
 */
function task_detail_cmt_on_keydown(e) {
    // Ctrl + Enter 那么就提交
    if(e.which == 13 && window.keyboard.ctrl) {
        $(this).parents(".task_cmt_newer").find(".task_cmt_add").click();
    }
}

/**
 * 左侧信息栏要显示的字段
 */
var _TFS = "parentId,_id,stack,owner,creater,status,pushAt,popAt,startAt,hungupAt,createTime,lastModified".split(",");

/**
 * 绘制左侧详细信息栏
 *
 * @param this 为 #task_detail
 * @param t 任务对象
 */
function task_detail_redraw_info(t) {
    var jq = $(".task_brief", this).empty();
    // 内容
    var html = '<div class="task_text">' + task_format_text(t.text) + '</div>';
    // 摘要
    html += '<table border="0" cellspacing="1" cellpadding="2"><tbody>';
    for(var i = 0; i < _TFS.length; i++) {
        html += '<tr><td class="task_brief_fnm">' + z.msg("task.f." + _TFS[i]) + '</td>';
        html += '<td class="task_brief_fval">' + z.sNull(t[_TFS[i]], "--") + '</td></tr>';
    }
    html += '</tbody></table>';
    // 历史
    html += '<div class="task_hiss">';
    if(!t.history || t.history.length == 0) {
        html += '<div class="task_hiss_empty">' + z.msg("task.his.empty") + '</div>';
    } else {
        html += '<h6>' + z.msg("task.his.title") + '</h6>';
        for(var i = 0; i < t.history.length; i++) {
            var his = t.history[i];
            html += '<div class="task_hiss_item">';
            html += '<em>' + his.at + '</em>';
            html += '<b>@' + his.user + '</b>';
            html += '<em>' + z.msg("task.his." + his.type) + '</em>'
            html += '<em>' + z.msg("ui.stack") + '</em>'
            html += '<a target="_blank" href="/page/stack#s:\'' + his.stack + '\'">[' + his.stack + ']</a>';
            html += '<em>' + z.msg("task.his.status") + '</em>';
            html += '<a target="_blank" href="/page/task#%(' + his.status + ')">"';
            html += z.msg("task.st." + his.status);
            html += '"</a>';
            html += '</div>';
        }
    }
    html += '</div>';

    // 加入 DOM
    jq.html(html);
}

/**
 * 绘制右侧评论
 *
 * @param this 为 #task_detail
 * @param t 任务对象
 */
function task_detail_redraw_comments(t) {
    t = t || $(this).data("task");
    var asc = task_detail_asc(this);
    var jComments = $(".task_cmt_list", this).empty();
    for(var i = 0; i < t.comments.length; i++) {
        if(asc) {
            $(task_wrap_comment(t.comments[i])).appendTo(jComments);
        } else {
            $(task_wrap_comment(t.comments[i])).prependTo(jComments);
        }
    }
}

/**
 * @param jDetail 为 #task_detail
 * @return jDetail 的 comment 排序方式是否是 ASC
 */
function task_detail_asc(jDetail) {
    return $(".task_cmt_sort .hlt", jDetail).hasClass("asc");
}

/**
 * 自动根据排序方式，判断出当前 jCmt 的下标
 *
 * @param jCmt 为 .task_cmt_item
 * @return 当前的 jCmt 的下标
 */
function task_detail_cmt_index(jCmt) {
    var jDetail = jCmt.parents("#task_detail");
    return task_detail_asc(jDetail) ? jCmt.prevAll().size() : jCmt.nextAll().size();
}

/**
 * 显示一个任务详细信息编辑界面，它必须需要界面上准备好一个 "#task_detail" 的 DOM 结构
 *
 * @param t : 任务对象
 */
function task_detail_show(t) {
    t.comments = t.comments || [];
    // 显示 task_detail
    var jDetail = $("#task_detail").attr("task-id", t._id).data("task", t);
    var box = z.winsz();

    var asc = z.local.get("ztask-comment-asc");
    // 确定一个排序方式
    if($(".task_cmt_sort .hlt", jDetail).size() == 0) {
        if("false" != asc)
            $(".task_cmt_sort .asc", jDetail).addClass("hlt");
        else
            $(".task_cmt_sort .desc", jDetail).addClass("hlt");
    }

    // 播放动画
    jDetail.css("display", "block").css("top", box.height).animate({
        "top": 0,
        "opacity": 1
    }, 200, function() {
        _adjust_layout();
        // 全局调整 layout
        $(".task_cmt_newer textarea", this).toggleInput(z.msg("task.cmt.add.tip"));
    });
    // 显示左侧内容
    task_detail_redraw_info.apply(jDetail, [t]);

    // 显示右侧内容
    task_detail_redraw_comments.apply(jDetail, [t]);
}