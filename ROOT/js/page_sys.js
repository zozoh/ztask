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
    $(".save").click(onSave);
}

function adjustLayout() {
    var btnH = $("#btns").outerHeight();
    $("#arena").css({
        width: this.width,
        height: this.height - btnH
    });
}