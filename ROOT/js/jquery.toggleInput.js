(function($) {
    /**
     * 修改 INPUT 的行为，让其在空白时刻，显示提示文字
     */
    $.fn.extend({
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ start ~
        toggleInput: function(opt) {
            // 格式化参数
            if (!opt) 
                opt = {};
            else if ("get" == opt) {
                return this.hasClass("blank") ? null : this.val();
            } else if ((typeof opt) == "string") {
                opt = {
                    defaultText: opt,
                    keepOriginal: true
                };
            }
            // 检查丢失的必要属性 
            if (!opt.className) 
                opt.className = "blank";
            if (!opt.defaultText) 
                opt.defaultText = "Please Input Value...";
            
            // 保存设置
            this.data("opt", opt);
            
            // 初始化显示
            if (!opt.keepOriginal) {
                this.val("");
            }
            if (!$.trim(this.val())) {
                this.addClass(opt.className).val(opt.defaultText);
                if (typeof opt.blank == "function") {
                    opt.blank.apply(this[0]);
                }
            } else if (typeof opt.valued == "function") {
                opt.valued.apply(this[0]);
            }
            
            // 绑定事件
            this.focus(function() {
                if ($(this).hasClass($(this).data("opt").className)) {
                    $(this).val("");
                    $(this).removeClass($(this).data("opt").className);
                }
            }).keyup(function() {
                var opt = $(this).data("opt");
                if (!$.trim($(this).val())) {
                    if (typeof opt.blank == "function") {
                        opt.blank.apply(this);
                    }
                } else if (typeof opt.valued == "function") {
                    opt.valued.apply(this);
                }
            }).blur(function() {
                var opt = $(this).data("opt");
                if (!$.trim($(this).val())) {
                    $(this).val(opt.defaultText).addClass(opt.className);
                    if (typeof opt.blank == "function") {
                        opt.blank.apply(this);
                    }
                }
            });
            // 返回自身以便链式赋值
            return this;
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ end ~
    });
})(window.jQuery);


