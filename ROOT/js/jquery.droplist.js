/**
 * 在一个选区内绘制 Playobj 的编辑界面
 */
(function($) {
var OPT_NAME = "drop-option";
var OBJ_NAME = "drop-obj";
//..............................................................................
var util = {
    checkopt: function(opt) {
        if(!opt || !opt.data)
            throw "$.fn.droplist : lack data!";
        if(!opt.draw)
            opt.draw = function(obj) {
                if( typeof obj == "string")
                    return obj;
                return obj ? obj.text || obj.name : "--";
            }
        if(!opt.compare)
            opt.compare = function(obj1, obj2) {
                if( typeof obj1 == "string")
                    return (obj1 == obj2 ? 0 : -1);
                if(!obj1 || !obj2)
                    return -1;
                if( typeof obj1.id != "undefined" && obj1.id == obj2.id)
                    return 0;
                return obj1.value == obj2.value ? 0 : -1;
            }
        return opt;
    },
    opt: function(selection) {
        return selection.data(OPT_NAME);
    },
    selection: function(ele) {
        var me = $(ele);
        if(me.children(".drop").size() > 0)
            return me;
        if(me.hasClass("drop"))
            return me.parent();
        return me.parents(".drop").parent();
    }
};
//..............................................................................
var data = {
    get: function() {
        return util.selection(this).data(OBJ_NAME);
    }
};
//..............................................................................
var dom = {
    init: function() {
        var html = '<div class="drop">';
        html += '    <div class="drop_obj"></div>';
        html += '    <div class="drop_btn"></div>';
        html += '    <div class="drop_list"></div>';
        html += '</div>';
        return $(html).appendTo(this);
    },
    depose: function() {
        events.unbind.apply(this);
        return this.empty();
    }
};
//..............................................................................
var events = {
    bind: function() {
        this.delegate(".drop_obj", "click", events.showDropList);
        this.delegate(".drop_list_item", "click", events.changeObject);
    },
    unbind: function() {
        this.undelegate();
    },
    changeObject: function() {
        var selection = util.selection(this);
        var opt = util.opt(selection);
        $(this).parent().children().removeClass("drop_list_item_hlt");
        var obj = $(this).addClass("drop_list_item_hlt").data(OBJ_NAME);
        layout.setObj.apply(selection, [obj]);
        if( typeof opt.change == "function")
            opt.change.apply(selection, [obj]);
    },
    showDropList: function(e) {
        e.stopPropagation();
        $(".drop").parent().removeClass("drop_down");
        var selection = util.selection(this);
        selection.addClass("drop_down");
        $(document.body).one("click", function() {
            $(".drop").parent().removeClass("drop_down");
        });
    }
};
//..............................................................................
var layout = {
    // 重绘，由于重绘会向服务器读取数据，所有它可以接受回调
    redraw: function(obj) {
        var opt = util.opt(this);
        var selection = this;
        // 显示数据
        layout.setObj.apply(selection, [obj]);
        // 重新加载数据
        if($.isArray(opt.data)) {
            layout.redrawDropList.apply(selection, [opt.data]);
        } else if(opt.dataAsync) {
            opt.data(obj, function(objs) {
                layout.redrawDropList.apply(selection, [objs]);
            });
        } else {
            var objs = opt.data(obj);
            layout.redrawDropList.apply(selection, [objs]);
        }
        // 绘制后回调
        if( typeof opt.afterDraw == "function") {
            opt.afterDraw.apply(selection, [obj]);
        }
    },
    redrawDropList: function(objs) {
        var opt = util.opt(this);
        var selection = this;
        var current = selection.data(OBJ_NAME);
        var jList = $(".drop_list", selection).empty();
        for(var i = 0; i < objs.length; i++) {
            var hlt = opt.compare(current, objs[i]) == 0 ? "drop_list_item_hlt" : "";
            var html = '<div class="drop_list_item ' + hlt + '">' + opt.draw(objs[i]) + '</div>';
            $(html).appendTo(jList).data(OBJ_NAME, objs[i]);
        }
        // 如果没有对象，就用第一个数据对象
        if(objs && objs.length > 0 && !data.get()) {
            layout.setObj.apply(selection, [objs[0]]);
            $(".drop_list_item", jList).first().addClass("drop_list_item_hlt");
        }
    },
    setObj: function(obj) {
        var selection = this;
        var opt = util.opt(selection);
        var html = opt.draw(obj);
        $(".drop_obj", selection).html(html);
        selection.data(OBJ_NAME, obj);
    },
    // 根据选区调整各个部分的尺寸
    resize: function() {
        // 还没想好要调整什么 ...
    }
};
//..............................................................................
var commands = {
    set: function(obj) {
        layout.redraw.apply(this, [obj]);
    },
    redraw: function() {
        commands.set.apply(this, [commands.get.apply(this)]);
    },
    get: function(field) {
        var re = data.get.apply(this);
        return field ? re[field] : re;
    },
    depose: function() {
        dom.depose.apply(this);
    },
    resize: function() {
        layout.resize.apply(this);
    }
};
//..............................................................................
// opt.draw(obj) :  [可选函数] 该函数返回一段 HTML，默认的采用 obj.text || obj.name 来绘制
// opt.compare(a, b)   :  [可选] 比较两个对象是否相等，-1 a 小， 0 相等, 1 a 大
// opt.data(obj[, callback]) :  函数，返回一组数据，或者直接是一个数组也成,
//                              obj 是当前的对象，有助于你的函数获取正确数据
//                              callback，表示如果是异步的，获取完数据，会回调 callback(objs)
// opt.dataAsync :  Boolean 默认为 true。表示当 data 为函数时，你会接受 callback
// opt.afterDraw :  [可选] 回调，当绘制完成后的回调，this 将是选区
// opt.change    :  [可选] 回调，当值改变后，的回调

$.fn.extend({
    droplist: function(opt, obj) {
        // 检查有效选区
        if(this.size() == 0)
            throw "$.fn.droplist: unknown selection '" + this.selector + "'";
        /* 命令模式 */
        if(opt && ( typeof opt == "string")) {
            if("function" != typeof commands[opt])
                throw "$.fn.droplist: don't support command '" + opt + "'";
            var re = commands[opt].apply(this, [obj]);
            return typeof re == "undefined" ? this : re;
        }
        // 保存配置对象
        this.data(OPT_NAME, util.checkopt(opt));

        // 初始化模式
        var div = dom.init.apply(dom.depose.apply(this));
        layout.resize.apply(this);

        // 绑定事件
        events.bind.apply(this);

        // 看看是否需要重绘
        layout.redraw.apply(this, [obj]);

        // 返回支持链式赋值
        return this;
    }
});
//..............................................................................
})(window.jQuery);
