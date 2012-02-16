function main() {
    // 初始化查询条件
    var kwd = z.pgan();
    if(kwd) {
        $(".srch_keyword input").val(kwd);
    }
    // 绑定左侧 Task 事件
    task_events_bind("#L", {
        detail: onClickDetail,
        reject: task_replace,
        renew: task_replace,
        restart: task_replace,
        done: task_replace
    });

    // 绑定右侧 Task 事件
    task_events_bind("#R", {
        reject: task_replace,
        renew: task_replace,
        restart: task_replace,
        done: task_replace
    });

    // 事件: 查看 Task 详情
    $("#L").delegate(".task_detail", "click", onClickDetail);
    $("#R").delegate(".task_refresh", "click", onClickDetail);

    // 初始化选择器
    if(z.pgan())
        $(".srch_keyword input").val(z.pgan());

    // 发出初始化事件
    $(".srch_do").click();
}

function drawDetail(t, subTasks) {
    // 首先清除
    var jTHei = $("#thierachy").empty();
    // 然后开始绘制右侧
    // 先绘制头部得 task
    var jTask = task_html.apply(jTHei, [t, {
        goin: "refresh",
        menu: "edit"
    }]);
    z.blinkIt(jTask, 500);
    // 调整布局
    adjustLayout();
    // 绘制详情
    task_draw_all_as_doc("#tsubs", t);
}

function onClickDetail() {
    var t = this.t || _task_obj(this).t;
    ajax.get("/ajax/task/self", {
        tid: t._id,
        recur: true
    }, function(re) {
        drawDetail(re.data, re.data.children);
    });
}

function on_draw_search_result(ts) {
    if(!ts)
        return;
    var jTasks = $("#tasks").empty();
    for(var i = 0; i < ts.length; i++) {
        appendTask(jTasks, ts[i]);
    }
    // 只有一个结果，点击它
    if(1 == ts.length) {
        $(".task_content", jTasks).first().click();
    }
}

/**
 * 将一个任务，以及其所有的子孙，显示成一篇文章的样子
 *
 * @param selector - 一个选区，里面将放置 task 的内容
 * @param t - 已经被加载了所有子孙节点的 task 对象
 * @param depth - 标题大纲级别，从 1 开始，如果未定以，则为 1
 */
function task_draw_all_as_doc(selector, t, depth) {
    depth = Math.min(6, Math.max(1, depth || 1));
    var selection = $(selector).empty();
    // 确保在 .task_doc 中
    if(!selection.hasClass("task_doc") && selection.parents("task_doc")) {
        selection.addClass("task_doc");
    }
    // 标题
    var jTitle = $('<h'+depth+'>'+task_format_text(t.text)+'</h'+depth+'>').appendTo(selection);

    // 正文
    if(t.comments && t.comments.length > 0) {
        for(var i = 0; i < t.comments.length; i++) {
            $('<pre class="task_doc_p_'+depth+'">' + task_format_text(t.comments[i]) + '</pre>').appendTo(selection);
        }
    } else {
        // 显示空
        if(depth == 1 && (!t.children || t.children.length == 0)) {
            $('<div class="task_doc_empty"></div>').appendTo(selection).text(z.msg("task.empty"));
        }
    }
    // 子任务
    if(t.children)
        for(var i = 0; i < t.children.length; i++) {
            var jDiv = $('<div class="task_doc_sub task_doc_sub_'+depth+'"></div>').appendTo(selection);
            task_draw_all_as_doc(jDiv, t.children[i], depth + 1);
        }
}

function appendTask(jTasks, t, menu) {
    task_html.apply(jTasks, [t, {
        menu: menu,
        viewType: "brief",
        goin: "detail"
    }]);
}

function initLayout() {
    // 绑定 Task Comment 事件
    task_detail_bind();

    // 绑定搜索事件
    task_search_bind("#LT", on_draw_search_result);
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