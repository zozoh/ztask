/**
 * 绑定 Task Plan 相关所有的事件
 *
 * @param this : .plan_flt ，jq 对象，表日期转换操作对象
 * @param selection : .plan_main , jq 对象，表绘制滚动日期的选区，内必有 .plan_scroller 作为滚动器
 */
function plan_events_bind(selection) {
    // 绑定事件
    this.delegate(".plan_range a", "click", plan_on_change_range);
    this.delegate(".plan_switch_prev", "click", plan_on_switch_prev);
    this.delegate(".plan_switch_today", "click", plan_on_switch_today);
    this.delegate(".plan_switch_next", "click", plan_on_switch_next);
    this.delegate(".plan_reload_btn", "click", plan_on_reload);
    selection.delegate(".plan_task", "click", plan_on_task_click);

    // 初始化自己的 .sflt 部分
    stack_flt_bind(this.find(".sflt"), {
        click: function(e, form) {
            var po = _plan_obj(this);
            po.jscroller.find(".plan_row_in").removeClass("plan_row_loaded");
            _plan_reload_row_in(po, true);
        }
    });

    // 开始重绘
    var a = $(".plan_range_hlt", this);
    var str = a.attr("href").substring(1);
    plan_redraw.apply(this, [str]);
}

/**
 * 标准事件: 计划中，任务被点击
 */
function plan_on_task_click(e) {
    var input = $("#flt_task .srch_keyword input");
    if(input.size() > 0) {
        input.val($(this).attr("task-id"));
        $("#flt_task .srch_do").click();
    }
}

/**
 * 标准事件: 时间向前一行
 */
function plan_on_switch_prev() {
    var po = _plan_obj(this);
    var cuRow = po.jplan.find(".plan_row_in").first();
    // 如果前面面没有行
    if(cuRow.prev().size() == 0) {
        var cuDate = z.d(cuRow.find(".plan_cell").first().attr("cell_date"));
        // 首先在前面插入 n 行，n 为当前视口有的行数
        for(var i = 0; i < po.opt.row; i++) {
            var rowDiv = $(_HTML_PLAN_ROW_).prependTo(po.jscroller);
            var offd = -1 * po.opt.rng * (i + 1);
            var rowFirstDate = z.offDate(cuDate, offd);
            _plan_draw_row(rowDiv, po.opt, rowFirstDate);
        }
        // 重新调整一下尺寸
        _plan_resize_cells(po.opt, po);
    }
    // 滚动一行
    _plan_goto_row(cuRow.prev());
}

/**
 * 标准事件: 时间向后一行
 */
function plan_on_switch_next() {
    var po = _plan_obj(this);
    var cuRow = po.jplan.find(".plan_row_in").last();
    // 如果后面没有行
    if(cuRow.next().size() == 0) {
        var cuDate = z.d(cuRow.find(".plan_cell").last().attr("cell_date"));
        // 首先在后面插入 n 行，n 为当前视口有的行数
        for(var i = 0; i < po.opt.row; i++) {
            var rowDiv = $(_HTML_PLAN_ROW_).appendTo(po.jscroller);
            var offd = po.opt.rng * i + 1;
            var rowFirstDate = z.offDate(cuDate, offd);
            _plan_draw_row(rowDiv, po.opt, rowFirstDate);
        }
    }
    // 重新调整一下尺寸
    _plan_resize_cells(po.opt, po);
    // 滚动一行
    _plan_goto_row(po.jplan.find(".plan_row_in").first().next());
}

/**
 * 标准事件: 时间移动到今天
 */
function plan_on_switch_today() {
    var po = _plan_obj(this);
    _plan_goto_row(po.jplan.find(".plan_row_today"));
}

/**
 * 将当前的视口，移动到给定的 row
 */
function _plan_goto_row(jrow) {
    var po = _plan_obj(jrow);
    // 重新加载内容
    _plan_reload_row_in(po);

    // 清除老的 .plan_row_in
    jrow.parent().children(".plan_row_in").removeClass("plan_row_in");
    // 标记 .plan_row_in
    var jq = jrow;
    for(var i = 0; i < po.opt.row; i++) {
        jq.addClass("plan_row_in");
        jq = jq.next();
    }
    // 滚动到第一个 .plan_row_in
    po.jscroller.animate({
        "top": -1 * jrow.outerHeight() * jrow.prevAll().size()
    }, 300, function() {
        // 更新当前月显示
        _plan_update_in_month(jq);
    });
}

/**
 * 标准事件: 切换 range
 */
function plan_on_change_range() {
    $(this).parent().children(".plan_range_hlt").removeClass("plan_range_hlt");
    var str = $(this).attr("href").substring(1);
    var jflt = $(this).addClass("plan_range_hlt").parents(".plan_flt");
    plan_redraw.apply(jflt, [str]);
}

/**
 * 标准事件: 点击重新加载数据的按钮
 */
function plan_on_reload() {
    var po = _plan_obj(this);
    po.jscroller.find(".plan_row_in").removeClass("plan_row_loaded");
    _plan_reload_row_in(po, true);
}

/**
 * 本函数，根据当前被标记了 .plan_row_in 的单元格
 * @param po - 相关对象
 * @param force - 强制刷新全部
 */
function _plan_reload_row_in(po, force) {
    // 如果强制更新的话，那么重新加载全部个子
    if(force)
        po.jplan.find(".plan_row_loaded").removeClass("plan_row_loaded");

    // 确定行
    var jRows = po.jplan.find(".plan_row").not(".plan_row_loaded");
    if(jRows.size() == 0)
        return;

    // 显示加载
    jRows.find(".plan_cell_tasks").empty().html("loading...");

    // 准备查询关键字
    var kwd = "";
    // 准备时间范围
    var d0 = jRows.first().children(".plan_cell").first().attr("cell_date");
    var d1 = jRows.last().children(".plan_cell").last().attr("cell_date");
    kwd += "&D(" + d0 + "," + d1 + ")";

    // 准备其他条件
    var sform = stack_flt_get_form(po.jflt.find(".sflt"));
    if(sform.favo) {
        kwd += " S($favo)";
    } else if(sform.mine) {
        kwd += " S($mine)";
    } else if(sform.snms) {
        kwd += " S(" + sform.snms + ")";
    }

    // 获取
    var form = {
        keyword: kwd,
        order: "ASC",
        sortBy: "planAt",
        onlyTop: false,
        limit: 0
    };
    ajax.json("/ajax/task/query", form, function(re) {
        _plan_fill_task_to_cell(jRows, re.data, po.opt);
    });
}

/**
 * 根据 Task 列表，填充对应的单元格
 */
function _plan_fill_task_to_cell(jRows, ts, opt) {
    // 首先整理一下 ts
    var map = {};
    // 堆栈名和颜色的映射
    var colors = z.local.getObj("stack.color.", {
        "--": "#4477FF"
    });
    for(var i = 0; i < ts.length; i++) {
        var t = ts[i];
        var ds = z.dstr(z.d(t.planAt));
        var list = map[ds];
        if(!list) {
            list = [];
            map[ds] = list;
        }
        list.push(t);
        // 记录堆栈
        if(!colors[t.stack])
            colors[t.stack] = null;
    }
    // 填充堆栈颜色
    z.fillColor2Obj(colors);
    // 保存一下
    z.local.setObj("stack.color.", colors);

    // 开始循环每个单元格，填充内容
    jRows.find(".plan_cell_tasks").each(function() {
        var me = $(this);
        var ds = me.parent().attr("cell_date");
        var ts = map[ds];
        var html = "";
        // 生成内容
        if(ts && ts.length > 0) {
            for(var i = 0; i < ts.length; i++) {
                var t = ts[i];
                var color = colors[t.stack];
                html += '<div class="plan_task plan_task_' + t.status + ' id_' + t._id + '" ';
                html += ' style="color:' + color + '" task-id="' + t._id + '">';
                html += t.stack + ': <a class="task_content">' + task_format_text(t.text) + '</a>';
                html += '</div>';
            }
        }
        // 清除
        else {
        }
        // 写入 DOM
        me.html(html);
    });
    jRows.addClass("plan_row_loaded");
}

// HTML 的行模板
var _HTML_PLAN_ROW_ = '<div class="plan_row"></div>';
var _HTML_PLAN_CELL_ = '<div class="plan_cell"></div>';

/**
 * 在选区内重绘
 *
 * @param this 表示关联选区的 DOM 对象
 * @param str 表示配置字符串
 */
function plan_redraw(str) {
    var opt = plan_opt(str);
    var po = _plan_obj(this);

    po.jplan.attr("plan_opt", plan_optstr(opt));

    // 计算一下每个项目的宽高
    var w = po.jplan.innerWidth();
    var h = po.jplan.innerHeight();

    // 评估一下 off
    var firstDate = z.d(opt.date);
    if("week" == opt.mod) { // 周的话，寻找周日
        var d = z.d(opt.date);
        if(d.day > 0)
            d = z.offDate(d, d.day * -1);
        firstDate = d;
    }

    // 清空
    po.jscroller.empty();

    // 循环行，以及 range
    for(var x = 0; x < opt.row; x++) {
        var rowDiv = $(_HTML_PLAN_ROW_).appendTo(po.jscroller).addClass("plan_row_in");
        var rowFirstDate = z.offDate(firstDate, x * opt.rng);
        _plan_draw_row(rowDiv, opt, rowFirstDate);
    }

    // 显示当前月
    _plan_update_in_month(po.jplan);

    // 重新改变尺寸大小
    _plan_resize_cells(opt, po);

    // 默认显示属于我的
    this.find(".sflt_mine").click();

}

/**
 * 更新一下当前月的显示
 */
function _plan_update_in_month(ele) {
    var po = _plan_obj(ele);
    var jq = po.jflt.find(".plan_month");
    var oldD = jq.attr("old-D");
    oldD = oldD ? z.d(oldD) : {};

    // 计算日期
    var dstr = po.jplan.find(".plan_row_in").first().find(".plan_cell").last().attr("cell_date");
    var d = z.d(dstr);

    // 显示
    jq.text(z.msg("d.MM." + d.month) + " / " + d.year + z.msg("d.yy"));
    // 闪烁
    if(oldD.year != d.year || oldD.month != d.month) {
        z.blinkIt(jq);
    }
    // 记录
    jq.attr("old-D", dstr);
}

/**
 * 创建一个行（包括其内的一组单元格，根据 opt 的设定）
 *
 * @param jrow 行的 jq 对象
 * @param opt 配置对象
 * @param firstDate 本行第一天的绝对日期
 */
function _plan_draw_row(jrow, opt, firstDate) {
    var css = {
        "float": "left",
        "overflow": "hidden",
        "position": "relative"
    };
    // 如果本行包括今天，那么就标记一下
    var today = z.today();
    var offDays = z.compareDate(today, firstDate);
    if(offDays >= 0 && offDays < opt.rng) {
        jrow.addClass("plan_row_today");
    }
    // 循环输出
    for(var i = 0; i < opt.rng; i++) {
        var rowCell = $(_HTML_PLAN_CELL_).css(css).appendTo(jrow);
        if(offDays == i)
            rowCell.addClass("plan_cell_today");
        _plan_draw_cell(rowCell, z.offDate(firstDate, i));
    }
}

/**
 * 创建一个 cell 初始化的 DOM 结构
 *
 * @param jcell 单元格的 jq 对象
 * @param d 日期对象
 */
function _plan_draw_cell(jcell, d) {
    var ds = z.dstr(d);
    d = z.d(ds);
    var html = '<div class="plan_cell_title">';
    html += '<em>' + d.date + '</em>';
    html += '<i>' + z.msg("d.ww." + (d.day + 1)) + '</i>';
    html += '</div>';
    html += '<div class="plan_cell_tasks"></div>';
    jcell.attr("cell_date", ds).html(html);
    jcell.addClass(d.month % 2 == 0 ? "plan_cell_month_odd" : "plan_cell_month_even");
}

function _plan_resize_cells(opt, po) {
    var w = -6 + po.jplan.innerWidth();
    var h = po.jplan.innerHeight();
    var css = {
        "width": w / opt.rng,
        "height": h / opt.row
    };
    po.jscroller.find(".plan_cell").css(css);
    // 调整 scroller 的竖向滚动距离
    var firstInRow = po.jscroller.children(".plan_row_in").first();
    var H = firstInRow.outerHeight();
    var index = firstInRow.prevAll().size();
    po.jscroller.css("top", -1 * H * index);
}

/**
 * 根据一个配置字符串，变成一个配置对象
 * <p> 配置字符串的格式为 "rng:7|row:1|off:week|"
 */
function plan_opt(str) {
    var sss = str.split("|");
    var re = {};
    for(var i = 0; i < sss.length; i++) {
        var ss = sss[i].split(":");
        re[ss[0]] = ss[1];
    }
    // 确认一下参考日期
    if(!re.date)
        re.date = z.todaystr();
    return re;
}

/**
 * 根据一个配置对象，变成一个配置对象字符串
 */
function plan_optstr(opt) {
    var ss = [];
    for(var key in opt) {
        ss.push(key + ":" + opt[key]);
    }
    return ss.join("|");
}

/**
 * 提供给事件处理函数使用的私有方法，它总结出一些必要的对象信息，并返回
 * @param ele 事件响应 DOM 对象
 * @return 常用的事件处理数据对象
 */
function _plan_obj(ele) {
    var me = $(ele);
    var jflt = $("#flt_plan .plan_flt");
    var jplan = $("#plan .plan_main");
    var str = jplan.attr("plan_opt");
    return {
        me: me,
        jflt: jflt,
        jplan: jplan,
        opt: str ? plan_opt(str) : {},
        jscroller: jplan.children(".plan_scroller").first()
    }
}