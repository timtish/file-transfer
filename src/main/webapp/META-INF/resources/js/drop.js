/* File Drop area */

$(function() {
    ru.timtish.DropZone($('#mf'), $('#mfdd'));
});

ru.timtish.DropZone = function (dropZone, dropInfo) {
    if (typeof(window.FileReader) == 'undefined') ru.timtish.bridge.updateBrowser(dropInfo);

    dropZone.on("dragover", function() {
        dropZone.addClass('hover');
        return false;
    });

    dropZone.on("dragleave", function() {
        dropZone.removeClass('hover');
        return false;
    });

    var stateChange = function stateChange(event) {
        if (!event) event = window.event;
        if (event.target.readyState == 4) {
            if (event.target.status == 200) {
                dropInfo.text('Загрузка успешно завершена!');
            } else {
                dropInfo.text('Произошла ошибка!');
                dropInfo.addClass('error');
            }
        }
    };

    var uploadProgress = function uploadProgress(event) {
        if (!event) event = window.event;
        var percent = parseInt(event.loaded / event.total * 100);
        dropInfo.text('Загрузка: ' + percent + '%');
    };

    dropZone.on("drop", function(event) {
        if (!event) event = window.event;

        event.preventDefault();
        dropZone.removeClass('hover');
        dropZone.addClass('drop');

        var files = event.originalEvent.dataTransfer.files;

        for (var fileIndex = 0; fileIndex < files.length; fileIndex++) {
            var file = files[fileIndex];
            var xhr = new XMLHttpRequest();
            xhr.upload.addEventListener('progress', uploadProgress, false);
            xhr.onreadystatechange = stateChange;
            xhr.open('POST', 'file');
            xhr.setRequestHeader('X-FILE-NAME', file.name);
            var date = file.lastModifiedDate;
            if (date) {
                var date_str=('0' + date.getDate()).substr(-2,2) + '.' + ('0' + date.getMonth()).substr(-2,2) + '.' + date.getFullYear();
                xhr.setRequestHeader('X-FILE-DATE', date_str);
            }
            xhr.send(file);
        }
    });
};

var timtish = timtish || {};


/**
 * new DropZone($('#dragDiv'), function(files) {
         $.each(files, function (num, file) {
            luxoft.upload.file('upload', file, function(event) {
               if (event.complete) alert('Загрузка успешно завершена!');
               if (event.error) alert('Произошла ошибка загрузки!');
            })
         });
     });
 */
timtish.DropZone = function (dropZone, callback) {
    if (typeof(window.FileReader) == 'undefined') timtish.updateBrowser();

    var dragHtmlContainer = dropZone[0] == document ? $('body') : dropZone;

    dropZone.on("dragover", function() {
        dragHtmlContainer.addClass('file-drop');
        return false;
    });
    dropZone.on("dragleave", function() {
        dragHtmlContainer.removeClass('file-drop');
        return false;
    });
    dropZone.on("drop", function(event) {
        if (!event) event = window.event;
        event.preventDefault();
        dragHtmlContainer.removeClass('file-drop');

        var files = event.originalEvent.dataTransfer.files;
        callback(files);
    });
};

timtish.upload = {
    file: function(url, file, callback) {
        if (!file) return;
        var xhr = new XMLHttpRequest();

        if (callback) {
            xhr.upload.addEventListener('progress', function (event) {
                if (!event) event = window.event;
                var percent = parseInt(event.loaded / event.total * 100);
                callback({progress: percent, complete: false});
            }, false);
            xhr.onreadystatechange = function (event) {
                if (!event) event = window.event;
                if (event.target.readyState == 4) {
                    if (event.target.status == 200) {
                        callback({progress: 100, complete: true});
                    } else {
                        callback({error: event, complete: false})
                    }
                }
            };
        }
        xhr.open('POST', url);
        xhr.setRequestHeader('X-FILE-NAME', file.name);
        var date = file.lastModifiedDate;
        if (date) {
            var date_str=('0' + date.getDate()).substr(-2,2) + '.' + ('0'+date.getMonth()).substr(-2,2) + '.' + date.getFullYear();
            xhr.setRequestHeader('X-FILE-DATE', date_str);
        }
        xhr.send(file);
    }
}

timtish.updateBrowser = function() {
    var warning = 'Мы развиваемся, интернет развивается, технологии развиваются и браузеры развиваются...';
    var ieVersion = navigator.appVersion.match(/MSIE ([\d.]+)/);
    if (ieVersion) {
        warning += '<br/><a href="http://windows.microsoft.com/ru-ru/internet-explorer/download-ie" target="_blank">Обновите</a>' +
            ' Ваш браузер до последней версии (требуется перезагрузка) ' +
            'или <a href="http://www.whatbrowser.org" target="_blank">попробуйте</a> новый браузер!';
    } else {
        warning += '<br/><a href="http://www.whatbrowser.org">Попробуйте</a> новый браузер!';
    }
    $('body').append("<div class='updateBrowser'>" + warning + "</div>");
}

