/*
 * 这是整个 page-ui 的启动函数，它负责调用各个 page-ui 的主函数，以及负责调用 adjustLayout
 */
(function($) {
$(document.body).ready(function() {
    // 初始化菜单
    if( typeof window.initLayout == "function") {
        window.initLayout.apply($("#menu"));
    }

    // 初始化 GInfo
    ginfo();

    // 监视键盘
    z.watchKeyboard();

    // 设置消息的自动更新机制
    var jMsg = $("#msg_count");
    if(jMsg.size() > 0) {
        var msgInter = $("#sky").attr("msg-inter") * 1;
        var _MSG_ID_;
        var onMessageOK = function(re) {
            var re = eval("(" + re + ")");
            var oldCount = jMsg.text() * 1;

            // 更新信息
            if(document.title)
                document.title = document.title.replace(/([(])([0-9]+)([)])(.*)/, "(" + re.data + ")$4");
            else if(window.title)
                window.title = window.title.replace(/([(])([0-9]+)([)])(.*)/, "(" + re.data + ")$4");
            jMsg.text(re.data);

            // 更新 #sky 样式
            if(re.data > 0) {
                jMsg.addClass("msg_found");
            } else {
                jMsg.removeClass("msg_found");
            }
            if(re.data > oldCount && typeof window.onMessageUpdate == "function") {
                window.onMessageUpdate.apply(jMsg, [re.data]);
            }

            // 设置下一轮回调
            _MSG_ID_ = window.setInterval(messagerHandler, msgInter);
        };
        var messagerHandler = function() {
            if(_MSG_ID_) {
                window.clearInterval(_MSG_ID_);
                _MSG_ID_ = null;
            }
            $.ajax({
            url: "/ajax//message/count"
            }).done(onMessageOK).fail(function() {
                // 容忍 ...
            });
        }
        messagerHandler();
    }

    // 注册 ajax 事件
    $("#logo img").ajaxStart(function() {
    this.style.visibility = "visible";
    }).ajaxStop(function() {
        this.style.visibility = "hidden";
    });
    // 调整界面布局
    _adjust_layout();
    // 随着窗口变化调整
    window.onresize = _adjust_layout;

    // 调用界面主函数
    if( typeof window.main == "function") {
        window.main();
    }

});
})(window.jQuery);

function _adjust_layout() {
    var box = z.winsz();
    box.scrollbar = z.scrollBarWidth();
    $("#sky").css("width", box.width);
    var jDetail = $("#task_detail").css({
        width: box.width,
        height: box.height
    });
    var detailWidth = $(".task_cmt",jDetail).width();
    $(".task_cmt_newer", jDetail).css("width", detailWidth - box.scrollbar);
    if( typeof window.adjustLayout == "function")
        adjustLayout.apply(box);
}