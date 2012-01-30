(function($) {
    var OPT_NAME = "masker-opt";
    //.........................................................................
    // HTML 生成相关方法
    var dom = {
        init: function(opt) {
            var html = '<div class="masker" style="display:none;"><div class="bg"></div>';
            // 标题
            if (opt.title) 
                html += '<div class="title">' + opt.title + '</div>';
            // 显示
            if (typeof opt.show == "function") 
                html += '<div class="fg"><div class="fg_body"></div></div>';
            // 关闭按钮
            if (opt.closeIcon) 
                html += '<div class="close"></div>';
            // 按钮区
            if (opt.btns) {
                html += '<ul class="btns">';
                for (var key in opt.btns) {
                    var uname = z.uname(key);
                    html += '<li func="' + key + '" class="' + uname.className + '">';
                    html += '<b>' + uname.text + '</b>';
                    html += '</li>';
                }
                html += '</ul>';
            }
            html += '</div>';
            // 设置初始的 CSS，所有的子节点将都是绝对定位
            var div = $(html).appendTo(document.body);
            div.children().css("position", "absolute");
            // 返回新生成的顶层元素
            return div;
        }
    };
    //.........................................................................
    // 布局相应的方法，所有方法 this 均为 .masker 的 jq 对象
    var layout = {
        resize: function() {
            var opt = this.data(OPT_NAME);
            // 改变尺寸
            var box = z.winsz();
            // 背景
            this.children(".bg").width(box.width).height(box.height).css({
                top: 0,
                left: 0
            });
            // 关闭按钮
            this.children(".close").css({
                top: 0,
                right: 0
            });
            // 前景
            var winbox = z.winsz();
            var f_w = opt.width;
            var f_h = opt.height;
            // 前景宽
            if (!f_w) 
                f_w = box.width * 0.618;
            else if (typeof f_w == "string" && /[0-9]+[%]/.test(f_w)) {
                var p = f_w.substring(0, f_w.length - 1) * 1;
                f_w = winbox.width * p / 100;
            }
            // 前景高
            if (!f_h) 
                f_h = f_w * 0.618;
            else if (typeof f_h == "string" && /[0-9]+[%]/.test(f_h)) {
                var p = f_h.substring(0, f_h.length - 1) * 1;
                f_h = winbox.height * p / 100;
            }
            // 顶点
            var f_t = parseInt((box.height - f_h) * 0.382);
            var f_l = parseInt((box.width - f_w) / 2);
            var fg = this.children(".fg").css({
                width: f_w,
                height: f_h,
                top: f_t,
                left: f_l
            });
            // 标题
            var tt = this.children(".title");
            tt.css({
                top: Math.max(Math.floor((f_t - tt.height()) * 0.618), 10),
                width: "100%",
                "text-align": "center"
            });
            // 按钮区域
            var btns = this.children(".btns");
            var btnTop = opt.show ? f_t + f_h : Math.floor(box.height * (1 - 0.618)) - btns.height();
            btns.css({
                top: btnTop + 6
            });
            // 回调
            if (typeof opt.resize == "function") 
                opt.resize.apply(fg.children(".fg_body"));
            return this;
        }
    }
    //.........................................................................
    var events = {
        clickCloseIcon: function() {
            commands.close.apply($(this).parents(".masker"));
        },
        keyEscape: function(e) {
            if (27 == e.which) {
                commands.close.apply($(document.body).children(".masker").last());
            }
        },
        clickBtn: function(e) {
            var func = $(this).attr("func");
            var div = $(this).parents(".masker");
            var opt = div.data(OPT_NAME);
            opt.btns[func].apply(this, [e, opt, div.find(".fg_body"), div.children(".bg")]);
        },
        bind: function(opt, div, fg) {
            // 绑定关闭事件
            if (opt.closeIcon) {
                div.children(".close").bind("click", events.clickCloseIcon);
                if ("true" != $(document.body).attr("masker_masked")) {
                    $(document.body).attr("masker_masked", "true");
                    $(window).bind("keydown", events.keyEscape);
                }
            }
            // 绑定按钮事件
            if (opt.btns) {
                div.delegate(".btns li", "click", events.clickBtn);
            }
        },
        unbind: function(div) {
            if ($(document.body).children(".masker").size() == 0) {
                $(window).unbind("keydown", events.keyEscape);
                $(document.body).removeAttr("masker_masked");
            }
            div.children(".close").unbind("click", events.clickCloseIcon);
            div.undelegate();
        }
    };
    //.........................................................................
    var commands = {
        resize: function() {
            layout.resize.apply(this);
        },
        close: function() {
            var opt = this.data(OPT_NAME);
            if (typeof opt.beforeClose == "function") 
                if (false == opt.beforeClose.apply(this.find(".fg_body"), [opt, this.children(".bg")])) {
                    return;
                }
            // 继续执行关闭流程
            commands._do_close_(this, opt);
        },
        _do_close_: function(div, opt) {
            // 取消事件和遮罩层
            div.remove();
            events.unbind(div);
            // 如果之前还有 masker，将其.__masker_ele 去掉，否则将body所有的子元素除去 .__masker_ele
            var body = $(document.body);
            var lastMasker = body.children(".masker").last();
            if (lastMasker.size() > 0) {
                lastMasker.removeClass("__masker_ele")
            } else {
                body.children().removeClass("__masker_ele");
            }
            if (typeof opt.afterClose == "function") 
                opt.afterClose(opt);
        }
    };
    //.........................................................................
    // 对 window 遮罩，中间提供了一层显示区域
    $.extend({
        masker: function(opt, arg0, arg1) {
            // 初始化模式
            if (typeof opt == "object") {
                // 将body所有的子元素加上 .__masker_ele
                $(document.body).children().addClass("__masker_ele");
                // 开始写入 dom
                var div = dom.init(opt).data(OPT_NAME, opt);
                var fg = $(".fg", layout.resize.apply(div));
                div.fadeIn(300, function() {
                    // 绑定事件
                    events.bind(opt, div, fg);
                    // 调用回调
                    if (typeof opt.show == "function") {
                        opt.show.apply(fg.children(".fg_body"), [opt, div.children(".bg")]);
                    }
                });
            } else if (typeof opt == "string") { /* 命令模式 */
                if ("function" != typeof commands[opt]) 
                    throw "$.masker: don't support command '" + opt + "'";
                div = $(document.body).children(".masker").last();
                if (div.size() > 0) 
                    commands[opt].apply(div, [arg0, arg1]);
            }
            // 返回支持链式赋值
            return this;
        }
    });
})(window.jQuery);
