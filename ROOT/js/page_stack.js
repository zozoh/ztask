function main(){
    var html = '<ul>';
    html += '<li><a href="javascript:void(0)">'+z.msg("main.menu.newtask") + '</a></li>';
    html += '</ul>';
    $("#sky .menu").html(html);
}

function adjustLayout(){
    var box = z.winsz();
    $("#sky").css("width",box.width);
    $("#chute").css("height",box.height);
    $("#arena").css("height",box.height);
}
