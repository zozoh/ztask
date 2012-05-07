/**
 * 绑定 Task Plan 相关所有的事件
 *
 * @param this : .plan_flt ，jq 对象，表日期转换操作对象
 * @param selection : #plan , jq 对象，表绘制滚动日期的选区，内必有 .plan_scroller 作为滚动器
 */
function plan_events_bind(selection) {

}

/**
 * 提供给事件处理函数使用的私有方法，它总结出一些必要的对象信息，并返回
 * @param ele 事件响应 DOM 对象
 * @return 常用的事件处理数据对象
 */
function _plan_obj(ele) {
    var me = $(ele);
    var jflt = $("#flt_plan .plan_flt");
    var jplan = $("#plan");
    return {
        me: me,
        jflt: jflt,
        jplan: jplan,
        jscroller: jplan.children(".plan_scroller").first()
    }
}