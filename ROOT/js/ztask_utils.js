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
 * 根据一个 Task 对象，生成一个 HTML 片段
 *
 * @param this : 无意义
 * @param t : Task 对象
 * @return HTML 字符串表示一个 Task 对象
 */
function task_html(t) {
    var html = '<div class="task">';
    html += t.title;
    html += '</div>';
    return html;
}

/**
 * 为 .hierachy_crumb 创建一个 ITEM 的 DOM
 *
 * @param this : 为 .hierachy_crumb 对象
 * @param um : 一个 z.uname() 函数支持的字符串
 * @return 创建的 jq 对象
 */
function hierachy_html_crumb(um) {
    var o = z.uname(um);
    var html = '<div class="hierachy_crumb_item ' + (um.className ? um.className : "") + '">';
    html += '<i></i><b>' + o.text + '</b>';
    html += '</div>';
    return $(html).appendTo(this);
}

/**
 * 为 .hierachy_scroller 创建一个 BLOCK 的 DOM
 *
 * @param this : 为 .hierachy_scroller 对象
 * @return 创建的 jq 对象
 */
function hierachy_html_block() {
    return $('<div class=".hierachy_block"></div>').appendTo(this);
}

/**
 * 为 .hierachy 绘制初始数据，包括设置一下 .crumb 的根，以及对应的 .hierachy_block
 * <p>
 * 它的配置参数将会存放在 .hierachy 的 data("hierachy_opt") 中， 选项包括
 * <pre>
 * {
 *      top : "::ui.top",  // [R] z.uname() 函数支持的项目描述字符串，表示根的说明
 *      append : function(obj){...}  // [R] 回调，this 为当前的 .hierachy_block，绘制项目 HTML 用
 *      events : {   // 整个 .hierachy 应该 delegate 的事件
 *          // key 是一个冒号分隔的字符串，左为事件名，右为选择器
 *          "click:.sub_item" : function(){...}
 *           ...
 *      }
 * }
 * </pre>
 *
 * @param this : .hierachy 的 DOM 元素
 * @param opt : 配置参数
 * @param objs : 数据对象，必须为一个数组
 */
function hierachy_init(opt, objs) {
    // 0. 得到选区
    var hie = $(this);
    var jq = $(".hierachy_scroller", hie);

    // 1. 创建 .hierachy_crumb_item
    hierachy_html_crumb.apply($(".hierachy_crumb", hie), [opt.top]);

    // 2. 创建一个 .hierachy_block
    var jBlock = hierachy_html_block.apply($(".hierachy_scroller", hie));

    // 3. 在这个块上循环 objs
    if(objs && objs.length > 0)
        for(var i = 0; i < objs.length; i++) {
            opt.append.apply(jBlock, [objs[i]]);
        }

    // 4. 绑定事件
    if(opt.events)
        for(var key in opt.events) {
            var ss = key.split(":");
            var func = opt.events[key];
            hie.delegate(ss[1], ss[0], func);
        }

    // 5. 存储 opt
    hie.data("hierachy_opt", opt);
}

/**
 * 自动调整 .hierachy 的布局
 * <p>
 * 即， .hierachy 下面有 .crumb 和 .viewport
 * <p>它还需要配合 ztask_hierachy.css 来设置元素样式
 *
 * @param this : .hierachy 的 DOM 元素
 */
function hierachy_layout() {
    var jq = $(".hierachy_arena", this);
    // 设置 viewport 高度
    var H = jq.innerHeight();
    var crumbHeight = $(".hierachy_crumb", jq).outerHeight();
    $(".hierachy_viewport", jq).css("height", H - crumbHeight);
}