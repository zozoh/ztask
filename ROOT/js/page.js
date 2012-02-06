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