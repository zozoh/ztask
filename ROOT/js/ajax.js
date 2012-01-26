function ajaxError(e) {
    // alert("Ajax Error!\n" + $.dump(e));
    $.masker({
        closeIcon: true,
        show: function(){
            $(this).errbox(e.msg, e.data.reason, $.dump(e.data.stackTrace), true);
        }
    });
}

//.......................................................................
(function($) {
// 统一处理 Ajax 的返回
var handleAjaxCallback = function(re, callback) {
    if( typeof re == "string")
        re = eval("(" + re + ")");
    if(!re.ok) {
        if(re.msg == "ajax.expired") {
            window.top.location = "/";
            return;
        }
        ajaxError(re);
    } else if( typeof callback == "function") {
        callback(re);
    }
    return re;
};
//...................................................................
window.ajax = {
    handle: function(re, callback) {
        handleAjaxCallback(re, callback);
    },
    // 建立一个 push 长连接
    push: function(opt) {
        var endl = opt.endl ? opt.endl : "\n--[PROCESSING,ENDL]--\n";
        // create Request Object ...
        var xhr = new XMLHttpRequest();
        var i = 0;
        var old = "";
        xhr.onreadystatechange = function(e) {
            // 请求开始写入数据了
            if(xhr.readyState >= 3) {
                var str = xhr.responseText.substring(i);
                i += str.length;
                // 寻找最后一次结束的标记
                var pos = str.lastIndexOf(endl);
                // 没找到，则保存
                if(pos < 0) {
                    old += str;
                } else { // 否则保存最后的残缺片段，等下次更新
                    var s = old + str.substring(0, pos);
                    old = str.substring(pos + endl.length);
                    if( typeof opt.change == "function") {
                        opt.change(s.split(endl), opt);
                    }
                }
            }
            // 请求已经结束了
            if(xhr.readyState == 4) {
                if(xhr.status == 200) {
                    if( typeof opt.finish == "function") {
                        opt.finish(opt);
                    }
                } else {
                    alert("HTTP " + xhr.status + "\n" + xhr.responseText);
                }
            }
        };
        var queryString = "";
        var body = null;
        if(!opt.method || "GET" == opt.method) {
            if(opt.data) {
                var ss = [];
                for(var key in opt.data)ss.push(key + "=" + encodeURIComponent(opt.data[key]));
                queryString = ss.join("&");
                if(queryString)
                    opt.url += "?" + queryString;
            }
        } else if("POST" == opt.method) {
            if(opt.data) {
                var ss = [];
                for(var key in opt.data)ss.push(key + "=" + encodeURIComponent(opt.data[key]));
                body = ss.join("&");
            }
        } else {
            throw "Unsupported HTTP method '" + opt.method + "'";
        }
        xhr.open(opt.method ? opt.method : "GET", opt.url, true);
        xhr.setRequestHeader('Content-type', "application/x-www-form-urlencoded");
        if(body)
            xhr.send(body);
        else
            xhr.send();
    },
    // 普通 AJAX GET 请求 > Text
    getText: function(url, form, callback) {
        if( typeof form == "function") {
            callback = form;
            form = null;
        }
        var ERR_PREFIX = "#STRATO$_*#TEXT#ERR:>\n";
        var whenDone = function(text) {
            if(z.startsWith(text, ERR_PREFIX)) {
                ajaxError(text.substring(ERR_PREFIX.length));
                return;
            }
            if( typeof callback == "function")
                callback(text);
        };
        var ajaxOption = {
            url: url,
            data: form,
            dataType: "text",
            processData: true
        };
        $.ajax(ajaxOption).done(whenDone).fail(ajaxError);
    },
    // 发送同步请求 > Text
    syncGetText: function(url, form) {
        var re;
        $.ajax({
            type: "GET",
            async: false,
            url: url,
            data: form,
            dataType: "text",
            processData: true,
            success: function(text) {
                re = text;
            }
        });
        if(z.startsWith(re, ERR_PREFIX)) {
            ajaxError("ddd" + re.substring(ERR_PREFIX.length));
            return "";
        }
        return re;
    },
    // 普通 AJAX GET 请求 > AjaxReturn
    get: function(url, form, callback) {
        if( typeof form == "function") {
            callback = form;
            form = null;
        }
        $.ajax({
        url: url,
        data: form
        }).done(function(re) {
        handleAjaxCallback(re, callback);
        }).fail(ajaxError);
    },
    // 普通同步 AJAX GET 请求 > AjaxReturn
    syncGet: function(url, form) {
        if( typeof form == "function") {
            callback = form;
            form = null;
        }
        var re;
        $.ajax({
            type: "GET",
            async: false,
            url: url,
            data: form,
            dataType: "text",
            processData: true,
            success: function(text) {
                re = handleAjaxCallback(text);
            }
        });
        return re;
    },
    // 普通 AJAX POST 请求 > AjaxReturn
    post: function(url, form, callback) {
        if( typeof form == "function") {
            callback = form;
            form = null;
        }
        $.ajax({
        type: "POST",
        url: url,
        data: form
        }).done(function(re) {
        handleAjaxCallback(re, callback);
        }).fail(ajaxError);
    },
    // 普通同步 AJAX GET 请求 > AjaxReturn
    syncPost: function(url, form) {
        if( typeof form == "function") {
            callback = form;
            form = null;
        }
        var re;
        $.ajax({
            type: "POST",
            async: false,
            url: url,
            data: form,
            dataType: "text",
            processData: true,
            success: function(text) {
                re = handleAjaxCallback(text);
            }
        });
        return re;
    },
    // JSON 请求 > AjaxReturn
    json: function(url, obj, callback) {
        $.ajax({
        type: "POST",
        url: url,
        contentType: "application/jsonrequest",
        data: z.toJson(obj),
        }).done(function(re) {
        handleAjaxCallback(re, callback);
        }).fail(ajaxError);
    },
    // 普通 HTTP GET 请求 > 页面刷新
    formGet: function(url, form, target) {
        if( typeof form == "string") {
            target = form;
            form = null;
        }
        var html = '<form style="display:none;" method="GET" action="' + url + '"';
        if(target)
            html += '  target="' + target + '"';
        html += '></form>';
        var jForm = $(html).appendTo(document.body);
        if(form)
            for(var key in form)$('<textarea name="' + key + '"></textarea>').appendTo(jForm).text(form[key]);
        jForm.submit();
    },
    // 普通 HTTP POST 请求 > 页面刷新
    formPost: function(url, form, target) {
        if( typeof form == "string") {
            target = form;
            form = null;
        }
        var html = '<form style="display:none;" method="POST" action="' + url + '"';
        if(target)
            html += '  target="' + target + '"';
        html += '></form>';
        var jForm = $(html).appendTo(document.body);
        if(form)
            for(var key in form)$('<textarea name="' + key + '"></textarea>').appendTo(jForm).text(form[key]);
        jForm.submit();
    }
};
//...................................................................
})(window.jQuery);
