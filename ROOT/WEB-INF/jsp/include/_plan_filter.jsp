<div class="plan_flt">
    <div class="plan_range">
        <a href="#rng:7|row:4|mod:week" class="plan_rng_li">${msg['plan.range.4week']}</a>
        <a href="#rng:7|row:2|mod:week" class="plan_rng_li">${msg['plan.range.2week']}</a>
        <a href="#rng:1|row:1|mod:day" class="plan_rng_li">${msg['plan.range.1day']}</a>
        <a href="#rng:2|row:2|mod:day" class="plan_rng_li">${msg['plan.range.4day']}</a>
    </div>
    <div class="plan_switch">
        <a class="plan_switch_prev"></a>
        <a class="plan_switch_today">${msg['plan.switch.today']}</a>
        <a class="plan_switch_next"></a>
    </div>
    <div class="plan_stack">
        <%@include file="_stack_filter.jsp" %>
    </div>
    <div class="plan_reload_btn"></div>
    <div class="plan_month"></div>
</div>