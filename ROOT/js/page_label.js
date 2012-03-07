function main() {
    doReloadLabels();
    $("#labels").delegate(".label", "click", on_click_label);
    $("#labels").delegate(".label .lb_name u", "click", on_click_rename);
    task_events_bind($("#tasks"), {
        reject: task_replace,
        renew: task_replace,
        restart: task_replace,
        done: task_replace
    });
}

function resizeLabelFont(selection, maxFontSize, minFontSize) {
    maxFontSize = maxFontSize || 60;
    minFontSize = minFontSize || 12;
    // 得到所有的子
    var jq = selection.children(".label_leaf");

    // 算最大/小值
    var max = 0;
    var min = -1;
    jq.each(function() {
        var c = $(this).attr("data-count") * 1;
        max = Math.max(max, c);
        min = min < 0 ? c : Math.min(c, min);
    });
    // 逐个计算字体大小
    var de = max - min;
    jq.each(function() {
        var c = $(this).attr("data-count") * 1;
        var fz = de == 0 ? minFontSize : Math.max(minFontSize, maxFontSize * ( c - min) / de);
        $(".lb_name",this).css("font-size", fz + "px");
    });
}

function groupLabels(selection) {
    // 准备按名字排序
    var nms = [];
    var jq = selection.children(".label_node").each(function() {
        var nm = $(this).attr("data-name");
        nms.push(nm);
        var children = $(this).attr("data-children").split(",");
        var jSub = $(this).children(".label_node_children");
        for(var i = 0; i < children.length; i++) {
            var child = children[i];
            var selector = '[data-name="' + child + '"]';
            //selector = ".label_leaf";
            var jq = selection.children(selector);
            jq.appendTo(jSub);
        }
        resizeLabelFont(jSub, 32, 12);
    });
    // 排序
    nms.sort();
    // 重新输出结果
    for(var i=0;i<nms.length;i++){
        var nm = nms[i];
        jq.filter('[data-name="'+nm+'"]').appendTo(jq.parent());
    }
}

function redrawLables(selection, lbs) {
    selection.empty();
    if(lbs && lbs.length > 0) {
        // 算最大/小值
        var max = 0;
        var min = -1;
        for(var i = 0; i < lbs.length; i++) {
            if(lbs[i].children)
                continue;
            var c = lbs[i].count;
            max = Math.max(max, c);
            min = min < 0 ? c : Math.min(c, min);
        }
        selection.attr("lb-count-max", max).attr("lb-count-min", min);
        // 输出
        for(var i = 0; i < lbs.length; i++) {
            var lb = lbs[i];
            var lbo = task_lable_obj(lb.name);
            var cssColor = lbo.color ? 'color:' + lbo.color + ';' : "";
            var cssBgColor = lbo.color ? 'background-color:' + lbo.color + ';' : "";

            var html = "";
            // 如果是节点
            if(lb.children) {
                html += '<div class="label label_node"';
                html += '     data-count="' + lb.count + '"';
                html += '     data-name="' + lb.name + '"';
                html += '     data-children="' + lb.children.join(",") + '">';
                html += '<span class="lb_name" style="' + cssColor + '">';
                html += '    <u>' + z.msg("lb.change") + ' : <b>"' + lb.name + '"</b></u>';
                html += '    <i>' + lbo.text + '</i>';
                html += '</span>';
                html += '<em style="' + cssBgColor + '">' + lb.count + '</em>';
                html += '<span class="label_node_children">';
                html += '</span>';
                html += '<u>' + z.msg("lb.change") + ' : <b>"' + lbo.name + '"</b></u>';
                html += '</div>';
            }
            // 否则是普通
            else {
                html = '<span class="label label_leaf"';
                html += '     data-count="' + lb.count + '"';
                html += '     data-name="' + lb.name + '"';
                if(lb.parent)
                    html += ' data-parent="' + lb.parent + '"';
                html += '>';
                html += '<span class="lb_name" style="' + cssColor + '">';
                html += '    <u>' + z.msg("lb.change") + ' : <b>"' + lb.name + '"</b></u>';
                html += '    <i>' + lbo.text + '</i>';
                html += '</span>';
                html += '<em style="' + cssBgColor + '">' + lb.count + '</em>';
                html += '</span>';
            }
            $(html).appendTo(selection);
        }

        // 绑定事件
        // $(".lb_name", selection).mouseover(on_lb_name_over).mouseout(on_lb_name_out);

        // 进行分组
        groupLabels(selection);

        // 重新计算字体大小
        resizeLabelFont(selection, 60);

        // 启用拖动和放置
        var onDragStop = function(event, ui) {
            var jq = $(".label_dragout");
            if(jq.size() > 0) {
                var top = jq.offset().top + jq.height();
                jq.droppable("destroy").animate({
                    "top": top,
                    "opactiy": 0
                }, 100, function() {
                    $(this).remove();
                });
            }
        };
        var onDragStart = function(event, ui) {
            selection.find(".label_hover").removeClass("label_hover");
            var jlb = ui.helper.parents(".label_node");
            if(jlb.size() == 0)
                return;
            // 计算辅助框位置
            var H = 30;
            var off = jlb.offset();
            // 拼装 HTML
            var html = '<div class="label_dragout">';
            html += z.msg("lb.dragout");
            html += '</div>';
            // 显示
            var jq = $(html).appendTo(document.body);
            jq.css({
                "opacity": 0,
                "position": "absolute",
                "top": off.top + H,
                "left": off.left,
                "width": jlb.width(),
                "height": H,
                "line-height": H - 4 + "px",
                "z-index": 200
            });
            jq.animate({
            "top": off.top,
            "opacity": 1.0
            }, 200).droppable({
                hoverClass: "label_dragout_hover",
                tolerance: "touch",
                drop: function(event, ui) {
                    var jTa = ui.helper;
                    // 发送请求
                    ajax.post("/ajax/label/ungroup", {
                        lbnm: ui.helper.data("name")
                    }, function() {
                        doReloadLabels();
                    });
                }
            });
        };
        selection.find(".label_leaf").draggable( "destroy" ).draggable({
            opacity: 0.4,
            revert: "invalid",
            revertDuration: 200,
            distance: 20,
            zIndex: 1000,
            start: onDragStart,
            stop: onDragStop
        });
        selection.children(".label").droppable("destroy").droppable({
            hoverClass: "label_droppable",
            accept: function(jTa) {
                return jTa.data("parent") != $(this).data("name");
            },
            drop: function(event, ui) {
                // jMe : 被放置的元素 ,  jTa : 被拖拽的元素
                var jTa = ui.helper;
                var jMe = $(this);

                // 得到标签名称
                var tanm = jTa.data("name");
                var mynm = jMe.data("name");

                // 不是节点标签，那么询问一个名字
                var grpas = "";
                if(!jMe.hasClass("label_node")) {
                    grpas = $.trim(window.prompt("Please give me a name"));
                    // 确保组不能为空
                    if(!grpas) {
                        grpas = mynm + "_" + tanm;
                    }
                }

                // 发送请求
                ajax.post("/ajax/label/group", {
                    grpas: grpas,
                    mynm: mynm,
                    tanm: tanm
                }, function() {
                    doReloadLabels();
                });
            }
        });
    }
}

function doReloadLabels() {
    $("#labels").empty().text(z.msg("ui.reload") + " ... ");
    ajax.get("/ajax/label/all", function(re) {
        redrawLables($("#labels"), re.data);
    });
}

function doSyncLabels() {
    $("#labels").empty().text(z.msg("lb.sync") + " ... ");
    ajax.get("/ajax/do/sync/labels", function(re) {
        doReloadLabels();
    });
}

function doReloadTask(lbnm) {
    ajax.json("/ajax/task/query", {
        keyword: "#(" + lbnm + ")"
    }, function(re) {
        var ts = re.data;
        var jTasks = $("#tasks").empty();
        for(var i = 0; i < ts.length; i++) {
            task_html.apply(jTasks, [ts[i], {
                menu: "edit,del",
                goin: false
            }]);
        }
    });
}

function on_click_label(e) {
    e.stopPropagation();
    if($(this).hasClass("label_hover")) {
        $(this).removeClass("label_hover");
        return;
    } else {
        $(this).parents("#labels").find(".label_hover").removeClass("label_hover");
        $(this).addClass("label_hover");
    }
    if($(this).hasClass("label_node"))
        return;
    var lbnm = $(this).data("name");
    doReloadTask(lbnm);
}

function on_click_rename(e) {
    e.stopPropagation();
    var jlb = $(this).parents(".label").first();
    var lbnm = jlb.data("name");
    var new_lbnm = $.trim(window.prompt(z.msg("lb.rename"), lbnm));
    if(new_lbnm && lbnm != new_lbnm) {
        ajax.post("/ajax/label/rename", {
            lbnm: lbnm,
            new_lbnm: new_lbnm
        }, function(re) {
            var lb = re.data;
            var lbo = task_lable_obj(lb.name);
            // 首先替换掉当前 label DOM
            jlb.attr("data-name", lb.name).attr("data-count", lb.count);
            jlb.data("name",lb.name).data("count", lb.count);
            var jlbnm = jlb.children(".lb_name");
            if(jlb.hasClass("label_node")) {
                jlbnm.css("background-color", lbo.color || "");
            } else {
                jlbnm.css("color", lbo.color || "");
            }
            jlb.children("em").css("background-color", lbo.color || "");
            jlbnm.find("u b").text(lb.name);
            $("i",jlbnm).text(lbo.text);
            jlb.removeClass("label_hover");

            // 如果不是分类，那么，重新加载左侧标签数据
            if(jlb.hasClass("label_leaf")) {
                doReloadTask(lb.name);
            }
        });
    }
}

function initLayout() {
    // 初始化事件
    $("#label_btns").delegate(".lb_do_sync", "click", doSyncLabels);
    $("#label_btns").delegate(".lb_do_reload", "click", doReloadLabels);
    // 绑定 Task Comment 事件
    task_detail_bind();
}

function adjustLayout() {
    $("#L,#R").css("height", this.height);
}