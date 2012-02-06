function main() {
    ajax.get("/ajax/g/get", {
        smtp: true
    }, function(re) {
        $("#arena textarea").val(z.toJson(re.data, true));
    });
}

function onSave() {
    var s = $("#arena textarea").val();
    try {
        var g = z.seval(s);
        ajax.json("/ajax/g/set", g, function(re) {
            $("#arena textarea").val(z.toJson(re.data, true));
        });
    } catch(E) {
        alert("Error JSON Syntax : \n" + E);
    }
}

function initLayout() {
    // init HTML
    var html = '<ul>';
    html += '<li><a class="save">' + z.msg("sys.save") + '</a></li>';
    html += '</ul>';
    // Add to DOM
    $(html).appendTo(this);
    // 初始化事件
    $("#menu .save").click(onSave);
}

function adjustLayout() {
    $("#arena").css({
        width: this.width,
        height: this.height
    });
}