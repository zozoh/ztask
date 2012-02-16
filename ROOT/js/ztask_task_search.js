/*
 * 绑定 search 控件的事件。配置对象:
 * <pre>
 * {
 *     after : function(ts, form){...}  # 处理搜索结果的回调
 *                                      #  this - .srch 的 jq 对象
 *                                      #  ts - 任务结果数组
 *                                      #  form - 提交的表单数据
 * }
 * </pre>
 *
 * @param selector 为 .srch 的 DOM 对象
 * @param opt - 配置对象，如果为 function,那么就相当于 {after:func}
 */
function task_search_bind(selector, opt) {
    var jsrch = $(selector);
    if( typeof opt == "function")
        opt = {
            after: opt
        };
    opt = opt || {};
    task_search_opt(jsrch, opt);
    // 绑定搜索按钮
    $(".srch_keyword input",jsrch).change(task_search_on_do_search);
    $(".srch_do",jsrch).click(task_search_on_do_search);
    $(".srch_toptask",jsrch).click(function() {
        var selection = task_search_selection(this);
        $(this).toggleClass("srch_toptask_on");
        $(".srch_do", selection).click();
    });
    // 初始化下拉菜单
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

function task_search_on_do_search() {
    var jsrch = task_search_selection(this);
    var opt = task_search_opt(jsrch);
    if(opt && typeof opt.after == "function") {
        var form = {
            keyword: $(".srch_keyword input", jsrch).val(),
            order: $(".srch_sort_order", jsrch).droplist("get").value,
            sortBy: $(".srch_sort_by", jsrch).droplist("get").value,
            onlyTop: $(".srch_toptask",jsrch).hasClass("srch_toptask_on"),
            limit: 100
        };
        ajax.json("/ajax/task/query", form, function(re) {
            opt.after.apply(jsrch, [re.data, form]);
        });
    }
}

/**
 * 从任何一个对象找到选区
 */
function task_search_selection(ele) {
    return $(ele).hasClass("task_search_opt") ? $(ele) : $(ele).parents(".task_search_opt");
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象
 * @param opt : 配置对象
 */
function task_search_opt(selection, opt) {
    if(!opt)
        return selection.data("task-search-opt");
    return selection.data("task-search-opt", opt).addClass("task_search_opt");
}