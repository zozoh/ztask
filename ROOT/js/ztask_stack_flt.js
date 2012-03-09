/*
 * 绑定 stack-filter 控件的事件。配置对象:
 * <pre>
 * {
 *     after : function(stacks, form){...}  # 处理搜索结果的回调
 *                                          #  this - .sflt 的 jq 对象
 *                                          #  stacks - 任务结果数组
 *                                          #  form - 提交的表单数据
 * }
 * </pre>
 *
 * @param selector 为 .sflt 的 DOM 对象
 * @param opt - 配置对象，如果为 function,那么就相当于 {after:func}
 */
function stack_flt_bind(selector, opt) {
    var jflt = $(selector);
    if( typeof opt == "function")
        opt = {
            after: opt
        };
    opt = opt || {};
    stack_flt_opt(jflt, opt);

    // 初始化一个高亮标签
    if($(".sflt_li_hlt",jflt).size() == 0)
        $(".sflt_li", jflt).first().addClass("sflt_li_hlt");

    // 绑定事件 ...
    $(".sflt_li", jflt).click(stack_flt_on_click_li);
    $(".sflt_cus", jflt).dblclick(stack_flt_on_dblclick_cus);
}

/**
 * 事件: 编辑自定义过滤器标签
 */
function stack_flt_on_dblclick_cus() {
    stack_flt_do_edit_cus.apply(this);
}

/**
 * 事件: 点击过滤器标签
 */
function stack_flt_on_click_li() {
    // 对于自定义堆栈
    var snames = "";
    if($(this).hasClass("sflt_cus")) {
        // 未定义，编辑 ...
        if($(this).hasClass("sflt_cus_undefined")) {
            stack_flt_do_edit_cus.apply(this);
            return;
        }
    }

    $(this).parent().children(".sflt_li_on").removeClass("sflt_li_on");
    $(this).addClass("sflt_li_on");

    stack_flt_do_filter.apply(this);
}

/**
 * 编辑自定义堆栈过滤标签
 *
 * @param this 为 .sflt_cus 的 DOM 对象
 */
function stack_flt_do_edit_cus() {
    z.editIt(this, {
        after: function(newval, oldval) {
            newval = $.trim(newval);
            if(newval && newval != oldval) {
                this.removeClass("sflt_cus_undefined");
                this.text(newval).attr("href", "#s" + newval);
            }
            if(this.hasClass("sflt_li_on"))
                stack_flt_do_filter.apply(this);
        }
    });
}

/**
 * 根据当前的过滤器配置，执行查询
 *
 * @param this 为选区相关元素
 */
function stack_flt_do_filter() {
    var selection = stack_flt_selection(this);
    var opt = stack_flt_opt(selection);

    if(opt && typeof opt.after == "function") {
        var url = "/ajax/stack/query";
        var snames = "";
        if($(".sflt_cus", selection).filter(".sflt_li_on").not(".sflt_cus_undefined").size() > 0) {
            snames = $(".sflt_cus", selection).text();
        }
        var form = {
            favo: $(".sflt_favo",selection).hasClass("sflt_li_on"),
            mine: $(".sflt_mine",selection).hasClass("sflt_li_on"),
            snms: snames
        };
        ajax.get(url, form, function(re) {
            opt.after.apply(selection, [re.data, form]);
        });
    }
}

/**
 * 从任何一个对象找到选区
 */
function stack_flt_selection(ele) {
    return $(ele).hasClass("stack_flt_opt") ? $(ele) : $(ele).parents(".stack_flt_opt");
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象
 * @param opt : 配置对象
 */
function stack_flt_opt(selection, opt) {
    if(!opt)
        return selection.data("stack-flt-opt");
    return selection.data("stack-flt-opt", opt).addClass("stack_flt_opt");
}