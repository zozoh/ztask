/**
 * 为 .hierachy 绘制初始数据，包括设置一下 .crumb 的根，以及对应的 .hierachy_block
 * <p>
 * 它的配置参数将会存放在 .hierachy 的 data("hierachy－opt") 中， 选项包括
 * <pre>
 * {
 *      scrollSpeed : 200,           # 滚动的速度，单位毫秒，默认 200
 *      direction : "left|right",    # 滚动的方向，向左还是向右。
 *                                   # 如果木有，从 .hierachy 读取 "hie-direction"，
 *                                   # 还木有的话，默认采用 "right"
 *      append : function(obj){...}      # [R] 回调，告诉 hierachy 如何 append 一个项目
 *                                       #    - @this 为当前的 .hierachy_block，绘制项目 HTML 用
 *      hltblock : function(jCrumb){...}    # 回调，某个 Block 被设置为 .hierachy_hlt 后，会被调用
 *                                          #    - @this 为当前新增加的 block 的 jq 对象
 *                                          #    - @jCrumb 为当前新增的 crumb 项目的 jq 对象
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
 * @param top : 第一个节点的面包屑名称，z.uname() 函数支持的项目描述字符串，表示根的说明
 * @param objs : 数据对象，必须为一个数组
 */
function hierachy_init(opt, top, objs) {
    // 0. 得到选区
    var hie = $(this);
    var jq = $(".hierachy_scroller", hie);

    // 如果已经被设置了，那么清除
    if(hierachy_opt(hie)) {
        hie.undelegate();
        $(".hierachy_crumb_item, .hierachy_block",hie ).remove();
    }

    // 1. 初始化 opt，并设置 .hierachy 的 class
    if(!opt.direction)
        opt.direction = z.sNull(hie.attr("hie-direction"), "right");
    hie.addClass("hierachy_" + opt.direction);

    // 2. 存储 opt
    hierachy_opt(hie, opt);

    // 3. 内部事件
    hie.delegate(".hierachy_crumb_item", "click", hierachy_handle_crumb_click);

    // 4. 自定义事件
    if(opt.events)
        for(var key in opt.events) {
            var ss = key.split(":");
            var func = opt.events[key];
            hie.delegate(ss[1], ss[0], func);
        }

    // 5. 增加第一层区块
    if(top)
        hierachy_add.apply(this, [top, objs]);

    // 6. 调整一下布局
    hierachy_layout.apply(this);
}

/**
 * 为 .hierachy_crumb 创建一个 ITEM 的 DOM
 *
 * @param this : 为 .hierachy_crumb 对象
 * @param um : 一个 z.uname() 函数支持的字符串
 * @return 创建的 jq 对象
 */
function hierachy_html_crumb(um) {
    $(".hierachy_crumb_item", this).removeClass("hierachy_crumb_item_hlt");
    var o = z.uname(um);
    var html = '<div class="hierachy_crumb_item ' + (um.className ? um.className : "") + '">';
    html += '<i></i><b>' + o.text + '</b>';
    html += '</div>';
    return $(html).appendTo(this).addClass("hierachy_crumb_item_hlt");
}

/**
 * 为 .hierachy_scroller 创建一个 BLOCK 的 DOM
 *
 * @param this : 为 .hierachy_scroller 对象
 * @return 创建的 jq 对象
 */
function hierachy_html_block() {
    $(".hierachy_block", this).removeClass("hierachy_block_hlt");
    var jBlock = $('<div class="hierachy_block hierachy_block_hlt"></div>').appendTo(this);
    jBlock.css(hierachy_viewport_size(this));
    return jBlock;
}

/**
 * 为 .hierachy 增加一个滚动区块，它会同时操作 .crumb 以及 .scrollor
 * <p>
 * 当然，根据 opt.direction 的设置不同，它也会显示不同的动画效果
 *
 * @param this : .hierachy 相关的 DOM 元素
 * @param pum : 一个 z.uname() 函数支持的字符串，用作添加 .crumb 项目
 * @param objs : 数据对象，必须为一个数组
 * @return 新增加 crumb 项目的 DOM 元素 (jq 包裹)
 */
function hierachy_add(pum, objs) {
    var hie = hierachy_check_selection(this);
    var opt = hierachy_opt(hie);
    // 删除虚的 crumb 项目和区块
    $(".hierachy_erratic", hie).remove();
    // 增加一个新的区块以及新的 crumb 项目
    var jCrumb = hierachy_html_crumb.apply($(".hierachy_crumb", hie), [pum]);
    var jBlock = hierachy_html_block.apply($(".hierachy_scroller", hie));

    // 绘制数据
    hierachy_redraw.apply(hie, [objs]);

    // 滚动
    hierachy_scrollTo(jCrumb);

    // 返回
    return jCrumb;
}

/**
 * 将当前高亮的区块回滚一个，如果当前已经是顶级区块了，什么也不做
 *
 * @this 一个和当前 hierachy 相关的 DOM 元素
 */
function hierachy_scrollback() {
    var hie = hierachy_check_selection(this);
    var hltBlock = $(".hierachy_crumb .hierachy_crumb_item_hlt", hie);
    var jq = hltBlock.prev();
    if(jq.size() > 0) {
        hierachy_scrollTo(jq);
    }
}

/**
 * 删除所有的临时区块
 *
 * @this 一个和当前 hierachy 相关的 DOM 元素
 */
function hierachy_clean_erratic() {
    $(".hierachy_erratic", hierachy_check_selection(this)).remove();
}

/**
 * 将 scroller 滚动到给定的 .block | .crumb
 * <p>
 * 并且，所有的后续节点，将被加上 .hierachy_erratic 标识
 *
 * @param jq : 给定的 .hierachy_crumb_item 或 .hierachy_block
 */
function hierachy_scrollTo(jq) {
    var hie = jq.parents(".hierachy");
    var opt = hierachy_opt(hie);

    // 移除当前高亮的项目标记
    $(".hierachy_crumb_item_hlt",hie).removeClass("hierachy_crumb_item_hlt");
    $(".hierachy_block_hlt",hie).removeClass("hierachy_block_hlt");

    // 得到 viewport 的尺寸
    var vp = hierachy_viewport_size(jq);
    // 计算出距离
    var index = jq.prevAll().size();
    var scrollWidth = index * vp.width * -1;
    // 计算动画样式
    var animatOption = "left" == opt.direction ? {
        right: scrollWidth
    } : {
        left: scrollWidth
    };
    // 计算之前和之后的对象
    // 这里需要预先设定好 xxx_hlt 的样式，以便其他方法使用他们，因为之后我们要做个动画，在动画后设置它们就晚了
    var jCrumbMe = $($(".hierachy_crumb_item", hie)[index]).addClass("hierachy_crumb_item_hlt");
    var jBlockMe = $($(".hierachy_block", hie)[index]).addClass("hierachy_block_hlt");

    // 调用回调
    if( typeof opt.hltblock == "function")
        opt.hltblock.apply(jBlockMe, [jCrumbMe]);

    // 动画:: 滚动 ... 完毕之后设置样式为 .hierachy_erratic
    $(".hierachy_scroller", hie).animate(animatOption, opt.scrollSpeed || 200, function() {
        // Crumb
        jCrumbMe.prevAll().andSelf().removeClass("hierachy_erratic");
        jCrumbMe.nextAll().addClass("hierachy_erratic");
        // Block
        jBlockMe.prevAll().andSelf().removeClass("hierachy_erratic");
        jBlockMe.nextAll().addClass("hierachy_erratic");
    });
}

/**
 * 重绘
 *
 * @param this : .hierachy 的 相关的 DOM 元素
 * @param objs : 数据对象，必须为一个数组
 */
function hierachy_redraw(objs) {
    var hie = hierachy_check_selection(this);
    var opt = hierachy_opt(hie);
    var jBlock = $(".hierachy_block_hlt", hie).empty();
    if(objs && objs.length > 0)
        for(var i = 0; i < objs.length; i++) {
            opt.append.apply(jBlock, [objs[i]]);
        }
    hierachy_layout.apply(this);
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
    var height = H - crumbHeight;
    var vp = $(".hierachy_viewport", jq).css("height", height);
    $(".hierachy_scroller", vp).css("height", height);
    $(".hierachy_block",vp).css({
        "width": vp.innerWidth(),
        "height": height
    });
}

/**
 * 事件处理: 点击 crumb 项目
 * <p>
 * 如果当前项目不是高亮的，则滚动到对应的区块上
 */
function hierachy_handle_crumb_click() {
    var jCrumb = $(this);
    if(!jCrumb.hasClass("hierachy_crumb_item_hlt"))
        hierachy_scrollTo(jCrumb);
}

/**
 * 根据任意一个 DOM 得到 .hierachy_viewport 的 DOM 对象
 *
 * @return .hierachy_viewport 的 DOM 对象
 */
function hierachy_viewport(ele) {
    var hie = $(ele);
    if(!hie.hasClass("hierachy"))
        hie = hie.parents(".hierachy");
    return hie.find(".hierachy_viewport");
}

/**
 * 根据任意一个 DOM 得到 .hierachy_viewport 的 size 对象
 * <pre>
 * {
 *     width  : 300,    # 视口宽
 *     height : 400     # 视口高
 * }
 * </pre>
 *
 * @return .hierachy_viewport 的 DOM 对象
 */
function hierachy_viewport_size(ele) {
    var jViewport = hierachy_viewport(ele);
    return {
        width: jViewport.innerWidth(),
        height: jViewport.innerHeight()
    };
}

/**
 * 从任何一个对象，找到其所属的 .hierachy
 *
 * @param ele 任何一个对象
 */
function hierachy_selection(ele) {
    return $(ele).hasClass("hierachy") ? $(ele) : $(ele).parents(".hierachy");
}

/**
 * 从任何一个对象，找到其所属的 .hierachy，如果没有找到，抛错
 *
 * @param ele 任何一个对象
 */
function hierachy_check_selection(ele) {
    var hie = hierachy_selection(ele);
    if(hie.size() == 0) {
        var E = "Fail to find parents .hierachy for '" + $(ele).selector + "'";
        alert(E);
        throw E;
    }
    return hie;
}

/**
 * 获取或者设置 opt 对象到选区
 *
 * @param selection : 选区对象，必然有 .hierachy 选择器
 * @param opt : 配置对象
 */
function hierachy_opt(selection, opt) {
    if(!opt)
        return selection.data("hierachy-opt");
    return selection.data("hierachy-opt", opt);
}