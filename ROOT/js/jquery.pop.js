(function($) {
var OPT_NAME = "pop-opt";
//......................................................................................
// 帮助函数集合
var util = {
    opt: function(ele) {
        return util.popAnchor(ele).data(OPT_NAME);
    },
    selection: function(ele) {
        var jq = $(ele);
        if(jq.hasClass("pop"))
            return jq;
        if(jq.next().first().hasClass("pop"))
            return jq.next().first();
        return jq.parents(".pop");
    },
    popAnchor: function(ele) {
        var jq = $(ele);
        if(jq.next().first().hasClass("pop"))
            return jq;
        return util.selection(ele).prev().first();
    }
};
//.........................................................................
// HTML 生成相关方法
var dom = {
    init: function(opt) {
        var html = '<div class="pop"><div class="pop_wrapper">';
        // 标题
        if(opt.title)
            html += '<div class="pop_title">' + opt.title + '</div>';
        // 显示
        if( typeof opt.show == "function")
            html += '<div class="pop_body"></div>';
        // 关闭按钮
        html += '<div class="pop_close"></div>';
        // 按钮区
        if(opt.btns) {
            html += '<ul class="pop_btns">';
            for(var key in opt.btns) {
                var uname = z.uname(key);
                html += '<li func="' + key + '" class="' + uname.className + '">';
                html += '<b>' + uname.text + '</b>';
                html += '</li>';
            }
            html += '</ul>';
        }
        html += '</div></div>';
        // 计算尺寸
        var w = opt.width || 400;
        var h = opt.height || w * 0.618;
        var off = $(this).offset();

        // 设置初始的 CSS
        var div = $(html);
        var org = {
            w: $(this).width(),
            h: $(this).height(),
            l: off.left,
            t: off.top
        };
        div.css({
        "position": "fixed",
        "opacity": 0.1,
        "width": org.w,
        "height": org.h,
        "left": org.l,
        "top": org.t
        }).data("org-size", org);
        // 设置尺寸
        var hTitle = div.find(".pop_title").outerHeight();
        var hBtns = div.find(".pop_btns").css("width",w).outerHeight();
        div.find(".pop_body").css("height", h - hTitle - hBtns);
        // 显示
        div.insertAfter(this).animate({
            "opacity": 1.0,
            "width": w,
            "height": h,
            "left": (off.left - (w / 2)),
            "top": (off.top - (h / 2))
        }, 200, function() {
            if( typeof opt.show == "function") {
                opt.show.apply(div.find(".pop_body")[0], [opt, util.popAnchor(div)[0]]);
            }
        });
        // 返回
        return div;
    }
};
//.........................................................................
var events = {
    clickCloseIcon: function() {
        util.popAnchor(this).pop("close");
    },
    keyEscape: function(e) {
        if(27 == e.which) {
            $(".pop").each(function() {
                util.popAnchor(this).pop("close");
            });
        }
    },
    clickBtn: function(e) {
        var func = $(this).attr("func");
        var div = util.selection(this);
        var opt = div.data(OPT_NAME);
        opt.btns[func].apply(this, [e, opt, div]);
    },
    bind: function(opt, div) {
        // 绑定关闭事件
        div.delegate(".pop_close", "click", events.clickCloseIcon);
        if("true" != $(document.body).attr("pop_esc_bind")) {
            $(document.body).attr("pop_esc_bind", "true");
            $(window).bind("keydown", events.keyEscape);
        }

        // 绑定按钮事件
        if(opt.btns) {
            div.delegate(".pop_btns li", "click", events.clickBtn);
        }
    }
};
//.........................................................................
var commands = {
    close: function() {
        var opt = util.opt(this);
        var div = util.selection(this);
        var anchor = util.popAnchor(this);

        // 关闭前
        if( typeof opt.beforeClose == "function") {
            if(false == opt.beforeClose.apply(div.find(".pop_body")[0], [opt, anchor[0]]))
                return;
        }

        // 关闭
        var div = util.selection(this);
        if(div.size() > 0) {
            var org = div.data("org-size");
            // 为了更平滑的动画，将内部内容全部虚化
            div.find("*").css("opacity",0);
            div.undelegate().animate({
                "opacity": 0.1,
                "width": org.w,
                "height": org.h,
                "left": org.l,
                "top": org.t
            }, 200, function() {
                // 关闭后
                if( typeof opt.afterClose == "function") {
                    opt.afterClose.apply(div.find(".pop_body")[0], [opt, anchor[0]]);
                }
                // 移除
                $(this).remove();
            });
        }
    },
    anchor: function() {
        return util.popAnchor(this);
    }
};
//.........................................................................
// 根据某个元素，绘制出一个弹出层，这个弹出层将根据该元素定位
// opt.title       - 标题，可选
// opt.width       - 弹出层宽度，整数
// opt.height      - 弹出层高度，整数
// opt.show        - callback
// opt.beforeClose - callback, return false 将阻止继续关闭
// opt.afterClose  - callback
// opt.btns        - {...}   // 按钮区，可选
//     - 每个事件处理函数调用方式为为 apply(this, [e, opt, div]);
//
//  callback 为函数，格式为
//     function(opt, ele){...}  # this 为 pop DIV.pop_body(DOM)，ele 为宿主元素(DOM)
$.fn.extend({
    pop: function(opt) {
        opt = opt || {};
        // 初始化模式
        if( typeof opt == "object") {
            this.data(OPT_NAME, opt);
            // 开始写入 dom
            var div = dom.init.apply(this,[opt]).data(OPT_NAME, opt);
            // 开始绑定事件
            events.bind.apply(this, [opt, div]);
        } else if( typeof opt == "string") { /* 命令模式 */
            if("function" != typeof commands[opt])
                throw "$.pop: don't support command '" + opt + "'";
            commands[opt].apply(this);
        }
        // 返回支持链式赋值
        return this;
    }
});
})(window.jQuery);
