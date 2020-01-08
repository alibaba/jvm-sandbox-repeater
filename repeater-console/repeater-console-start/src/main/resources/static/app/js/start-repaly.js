var canReplay = false;
jQuery(function ($) {
    $(".btn-replay").on('click', function () {
        var traceId = $(this).attr('data-id');
        $('#replay-traceId').val(traceId);
        var appName = $(this).attr('data-app');
        $('#replay-appName').val(appName);
        var modal = $('#start-replay-modal');
        modal.on('shown.bs.modal', function () {
            $(this).css('display', 'block');
            var modalHeight = $(window).height() / 2 - $('#start-replay-modal .modal-dialog').height() / 2;
            $(this).find('.modal-dialog').css({
                'margin-top': modalHeight
            });
        });
        // 初始化场景选择列表
        innerPost("/module/byName.json", {'appName': appName}, function (response) {
            var sSelectArea = $("#machine-select-group");
            var sSelect = $("#machine-select");
            var sHelp = $("#machine-help");
            sSelect.empty();
            if (response.success && response.data.length > 0) {
                jQuery.each(response.data, function (i, val) {
                    var version = (val.version === '-' ? '' : ' 版本:' + val.version);
                    if (i === 0) {
                        sSelect.append('<option value="' + val.ip + '" selected>' + val.ip + '[' + val.environment + version + ']' + '</option>')
                    } else {
                        sSelect.append('<option value="' + val.ip + '">' + val.ip + '[' + val.environment + version + ']' + '</option>')
                    }
                });
                sSelectArea.show();
                sHelp.hide();
                canReplay = true;
            } else {
                sHelp.show();
                sSelectArea.hide()
                canReplay = false;
            }
        });
        modal.modal('show')
    });

    $("#start-replay-btn").on('click', function () {
        if (!canReplay) {
            notify("没有可用回放机器，请先挂载");
            return false;
        }
        var appName = $("#replay-appName").val();
        showLoading(10);
        $("#startReplayForm").ajaxSubmit({
            type: "post",
            url: "//" + host + "/replay/execute.json",
            success: function (data) {
                $("#start-replay-modal").modal('hide')
                hideLoading(10)
                if (data.success) {
                    openNewWindow(protocol + "//" + host + "/replay/detail.htm?repeatId=" + data.data + "&appName=" + appName,
                        "执行发起成功，您的浏览器阻止了结果页面自动打开，请先允许或点击前往 >> ")
                } else {
                    notice(data.message, data.success)
                }
            },
            error: function (XMLHttpRequest) {
                hideLoading(10);
                notice("服务抽风了，网络异常 " + XMLHttpRequest.responseText, false);
            }
        })
    });
});