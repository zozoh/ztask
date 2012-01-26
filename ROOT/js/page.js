/*
 * 这是整个 page-ui 的启动函数，它负责调用各个 page-ui 的主函数，以及负责调用 adjustLayout
 */
(function($) {
$(document.body).ready(function() {
    // 调用界面主函数
    if( typeof window.main == "function") {
        window.main();
    }

    // 调整界面布局
    if( typeof window.adjustLayout == "function") {
        // 初次调整
        adjustLayout();
        // 随着窗口变化调整
        window.onresize = adjustLayout;
    }
    
});
})(window.jQuery);