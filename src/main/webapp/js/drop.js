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
                var date_str=('0' + date.getDate()).substr(-2,2) + '.' + ('0'+date.getMonth()).substr(-2,2) + '.' + date.getFullYear();
                xhr.setRequestHeader('X-FILE-DATE', date_str);
            }
            xhr.send(file);
        }
    });
};
