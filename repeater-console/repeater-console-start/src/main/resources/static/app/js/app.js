var host = window.location.host;
var protocol = window.location.protocol;
jQuery(function ($) {
    $("[data-toggle='tooltip']").tooltip();
    $("[data-toggle='popover']").popover();
    $(".refresh-btn").on("click",function () {
        window.location.reload();
    });
});

var innerPost = function (uri, data, callback) {
    $.post("//" + host + uri, data, callback)
};

/**
 * 异步ajax请求
 * @param uri
 * @param data
 * @param type
 * @param callback
 */
var innerAsyncAjax = function (uri, data, type, callback) {
    showLoading(100)
    $.ajax({
        type: type,
        url: "//" + host + "/" + uri,
        data: data,
        success: function (res) {
            hideLoading(100)
            callback(res)
        },
        async: true,
        dataType: 'json',
        error: function (XMLHttpRequest) {
            hideLoading(100)
            notice("服务抽风了，网络异常 " + XMLHttpRequest.responseText, false);
        }
    });
};

/**
 * 同步的ajax请求
 * @param uri
 * @param data
 * @param type
 * @param callback
 */
var innerSyncAjax = function (uri, data, type, callback) {
    showLoading(500)
    $.ajax({
        type: type,
        url: "//" + host + "/" + uri,
        data: data,
        success: function (res) {
            hideLoading(500)
            callback(res)
        },
        async: false,
        dataType: 'json',
        error: function (XMLHttpRequest) {
            hideLoading(500)
            notice("服务抽风了，网络异常 " + XMLHttpRequest.responseText, false);
        }
    });
};

var rnd = function (n, m) {
    var random = Math.floor(Math.random() * (m - n + 1) + n);
    return random;
};

var showLoading = function (time) {
    $("#fake-loader").fadeIn(time);
};

var hideLoading = function (time) {
    $("#fake-loader").fadeOut(time);
};

var notice = function (message, status) {
    if (status) {
        $("#success-message-area").html(message);
        $("#success-modal").modal('show')
    } else {
        $("#danger-message-area").html(message);
        $("#danger-modal").modal('show')
    }
};

var noticeUrl = function (message, url) {
    $("#url-notice-content").text(message);
    $("#url-notice-url").attr('href', url);
    $("#url-modal").modal('show')
};

var confirmTwice = function (message, callback) {
    $(document).off('click', '.confirm-btn');
    $("#confirm-modal-message").text(message);
    $("#confirm-modal").modal("show");
    $(document).on('click', '.confirm-btn', callback);
};

var checkTwice = function (message, callback) {
    $(document).off('click', '.check-btn');
    $("#check-modal-message").text(message);
    $("#check-modal").modal("show");
    $(document).on('click', '.check-btn', callback);
};

var confirmDismiss =  function () {
    $("#confirm-modal").modal("hide");
};

var checkDismiss =  function () {
    $("#check-modal").modal("hide");
};

var notify = function (msg) {
    $.notify({
        // options
        icon: "fa fa-bell-o",
        message: msg
    }, {
        // settings
        type: 'warning',
        animate: {
            enter: 'animated bounceInRight',
            exit: 'animated bounceOutRight'
        },
        placement: {
            from: "top",
            align: "right"
        },
        delay: 1000,
        template: '<div data-notify="container" class="col-xs-4 col-sm-3 alert alert-{0}" role="alert"><button type="button" aria-hidden="true" class="close" data-notify="dismiss">&times;</button><span data-notify="icon"></span> <span data-notify="title">{1}</span> <span data-notify="message">{2}</span><div class="progress" data-notify="progressbar"><div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div></div><a href="{3}" target="{4}" data-notify="url"></a></div>'
    });
};
/**
 * bind data into dom
 * @param rootElement root element
 * @param json
 */
var bindData = function (rootElement, json) {
    var el = $("#" + rootElement);
    if (el === undefined) {
        console.log("no valid root element found in dom,selector is " + rootElement)
        return false;
    }
    var inputBinders = el.find('input[data-bind]');
    inputBinders.each(function () {
        var key = $(this).attr('data-bind');
        var value = json[key];
        if (value !== undefined) {
            $(this).val(value)
        }
    });
    var textareaBinders = el.find('textarea[data-bind]');
    textareaBinders.each(function () {
        var key = $(this).attr('data-bind');
        var value = json[key];
        if (value !== undefined) {
            $(this).val(value)
        }
    });
};

var openNewWindow = function (url, msg) {
    var wd = window.open(url);
    if (wd == null) {
        noticeUrl(msg, url)
    }
};