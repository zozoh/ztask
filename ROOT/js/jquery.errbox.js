(function($) {

var dom = {
    init: function(exname, reason, errlog) {
        var html = '';
        html += '<div class="errbox"><div class="errbox_in">';
        html += '   <div class="errTitle">';
        html += '       <span>' + z.msg("errbox.title") + '</span>';
        html += '   </div>';
        html += '   <div class="errInfo">';
        html += '       <div class="errns">';
        html += '           <table cellspacing="0" cellpadding="0" border="0">';
        html += '                   <colgroup >';
        html += '                       <col width="30%">';
        html += '                       <col width="70%">';
        html += '                   </colgroup>';
        html += '               <tr>';
        html += '                   <td class="key">' + z.msg("errbox.exname") + '</td>';
        html += '                   <td class="value">' + exname + '</td>';
        html += '               </tr>';
        html += '               <tr>';
        html += '                   <td class="key">' + z.msg("errbox.reason") + '</td>';
        html += '                   <td class="value">' + reason + '</td>';
        html += '               </tr>';
        html += '           </table>';
        html += '       </div>';
        html += '       <div class="errLogo">';
        html += '           <div class="emontion"></div>';
        html += '       </div>';
        html += '   </div>';
        html += '   <div class="showLog">';
        html += '       <div class="button">' + z.msg("errbox.showlog") + '</div>';
        html += '   </div>';
        html += '   <div class="errLog">';
        // html += '       <pre class="logcontent">' + errlog + '</pre>';
        html += '       <textarea class="logcontent"></textarea>';
        html += '   </div></div>';
        html += '</div>';
        $(this).append(html);
        $('.errbox .logcontent').val(errlog);
    }
};

var events = {
    bind: function() {
        var errbox = $('.errbox', $(this));
        errbox.delegate('.showLog .button', 'click', events.showLog);
    },
    showLog: function(e) {
        var button = $(this);
        var errInfo = button.parent().parent().find('.errInfo');
        var errLog = errInfo.parent().find('.errLog');
        if(button.hasClass('show')) {
            errLog.animate({
                height: '0px'
            }, 300, function() {
                errLog.hide();
            });
            errInfo.show().animate({
                height: '200px'
            }, 300, function() {
            });
            button.removeClass('show');
            button.html(z.msg("errbox.showlog"));
        } else {
            errInfo.animate({
                height: '0px'
            }, 300, function() {
                errInfo.hide();
            });
            errLog.show().animate({
                height: '200px'
            }, 300, function() {
            });
            button.addClass('show');
            button.html(z.msg("errbox.showerr"));
        }
    }
};

$.fn.extend({
    // 弹出一个错误提示框
    errbox: function(exname, reason, errlog) {
        dom.init.apply(this ,[exname, reason, errlog]);
        events.bind.apply(this);
    }
});
})(window.jQuery);
