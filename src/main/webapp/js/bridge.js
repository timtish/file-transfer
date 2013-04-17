var namespace = function(){
    var o, d;
    $.each(arguments, function(v) {
        d = arguments[1].split(".");
        o = window[d[0]] = window[d[0]] || {};
        $.each(d.slice(1), function(v2){
            o = o[arguments[1]] = o[arguments[1]] || {};
        });
    });
}

namespace("ru.timtish.bridge");

ru.timtish.bridge.updateBrowser = function updateBrowser(div) {
    var ieVersion = navigator.appVersion.match(/MSIE ([\d.]+)/);
    if (ieVersion) {
        div.html('Мы развиваемся, интернет развивается, технологии развиваются и браузеры развиваются...<br/><a href="http://windows.microsoft.com/ru-ru/internet-explorer/download-ie" target="_blank">Обновите</a> Ваш браузер до последней версии (требуется перезагрузка) или <a href="http://www.whatbrowser.org" target="_blank">попробуйте</a> новый браузер!');
        div.addClass('warning');
    } else {
        div.html('Мы развиваемся, интернет развивается, технологии развиваются и браузеры развиваются...<br/><a href="http://www.whatbrowser.org">Попробуйте</a> новый браузер!');
        div.addClass('warning');
    }
}

