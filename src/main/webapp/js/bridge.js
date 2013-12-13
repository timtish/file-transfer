/*var namespace = function(){
    var o, d;
    $.each(arguments, function(v) {
        d = arguments[1].split(".");
        o = window[d[0]] = window[d[0]] || {};
        $.each(d.slice(1), function(v2){
            o = o[arguments[1]] = o[arguments[1]] || {};
        });
    });
}
namespace("ru.timtish");*/
var ru = {
    timtish: {}
}

/* File transfer functions */
ru.timtish.bridge = {
    updateBrowser: function (div) {
        var ieVersion = navigator.appVersion.match(/MSIE ([\d.]+)/);
        if (ieVersion) {
            div.addClass('warning').html('Мы развиваемся, интернет развивается, технологии развиваются и браузеры развиваются...' +
                '<br/><a href="http://windows.microsoft.com/ru-ru/internet-explorer/download-ie" target="_blank">Обновите</a>' +
                ' Ваш браузер до последней версии (требуется перезагрузка) ' +
                'или <a href="http://www.whatbrowser.org" target="_blank">попробуйте</a> новый браузер!');
        } else {
            div.addClass('warning').html('Мы развиваемся, интернет развивается, технологии развиваются и браузеры развиваются...' +
                '<br/><a href="http://www.whatbrowser.org">Попробуйте</a> новый браузер!');
        }
    },

    alert: function (str) {
        alert(str);
    },

    loadFilesAsZip: function (streamKeyList) {
        $.fileDownload("zip", {
            httpMethod: "POST",
            data: {
                keys: streamKeyList.join(',')
            }
        })
    },

    sendToEmail: function (streamKeyList, targetEmail) {
        $.ajax("mail", {
            type: "POST",
            data: {
                keys: streamKeyList.join(','),
                box: "box.zip",
                to: targetEmail
            }
        })
            .done(function (d) {
                this.alert("success " + d);
            })
            .fail(function (e) {
                this.alert("error " + e);
            })
    }
}
