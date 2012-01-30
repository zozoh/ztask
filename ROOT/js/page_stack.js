function main() {
    // 读取右侧所有的新 Task，按照时间逆序排列
    ajax.get("/ajax/task/tops", {
        a: 'NEW'
    }, function(re) {
        hierachy_init.apply($("#tasks"), [{
            top: '::task.top',
            append: function(obj) {
                $(task_html(obj)).appendTo(this);
            }
        }, re.data]);
    });
}

function doNewTask() {
    var jTxt = $("#sky .newtask textarea");
    var str = jTxt.toggleInput("get");
    ajax.post("/ajax/task/save", {
        tt: str
    }, function(re) {
        jTxt.val("").focus().blur();
    });
}

function onKeydownAtNewtastTextarea(e) {
    if(e.which == 13 && window.keyboard.shift) {
        doNewTask();
        return false;
    }
}

function initMenu() {
    // init HTML
    var html = '<div class="newtask">';
    html += '<textarea></textarea><a></a>';
    html += '</div>';
    // Add to DOM
    $(html).appendTo(this).find("textarea").toggleInput(z.msg("stack.new.tip"));
    // bind events
    $("#sky").delegate(".newtask textarea", "keydown", onKeydownAtNewtastTextarea);
    $("#sky").delegate(".newtask a", "click", doNewTask);
}

function adjustLayout() {
    $(".hierachy").css("height",this.height).each(hierachy_layout);
}