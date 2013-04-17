<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Add file</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="main.css" />
    <script src="http://code.jquery.com/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="js/jquery.MultiFile.js" type="text/javascript"></script>
    <script src="js/bridge.js" type="text/javascript"></script>
</head>
<body>
<form action="put_mp" method="post" enctype="multipart/form-data" accept-charset="UTF-8" id="mf">
    <label for="description1">Комментарий</label>
    <input type="text" id="description1" name="description" size="30" /><br/>
    <label for="file1">Выберите (или перетащите сюда) файл</label>
    <input type="file" id="file1" name="file" multiple="" required="" /><br/>
    <input type="submit" value="add file from folder" />
    <label for="mf" id="mfdd"></label>
</form>
<form action="put_st" method="post" enctype="application/x-www-form-urlencoded" accept-charset="UTF-8">
    <label for="description2">Комментарий</label>
    <input type="text" id="description2" name="description" size="30" /><br/>
    <label for="url">Адрес</label>
    <input type="text" id="url" name="url" size="40" required=""/><br/>
    <input type="submit" value="add file from url" />
</form>
<form action="put_txt" method="post" enctype="application/x-www-form-urlencoded" accept-charset="UTF-8">
    <label for="name">Название</label>
    <input type="text" id="name" name="name" size="30" /><br/>
    <label for="description3">Комментарий</label>
    <input type="text" id="description3" name="description" size="30" /><br/>
    <label for="data">Текст</label>
    <textarea name="data" id="data" rows="6" cols="40"  required=""></textarea><br/><!-- todo: auto resize -->
    <input type="submit" value="add text" />
</form>
<a href="box.jsp">already added</a>
<script>
    var dropZone = $('#mf');
    var dropInfo = $('#mfdd');

    if (typeof(window.FileReader) == 'undefined') ru.timtish.bridge.updateBrowser(dropInfo);

    dropZone[0].ondragover = function() {
        dropZone.addClass('hover');
        return false;
    };

    dropZone[0].ondragleave = function() {
        dropZone.removeClass('hover');
        return false;
    };

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
    }

    var uploadProgress = function uploadProgress(event) {
        if (!event) event = window.event;
        var percent = parseInt(event.loaded / event.total * 100);
        dropInfo.text('Загрузка: ' + percent + '%');
    }

    dropZone[0].ondrop = function(event) {
        if (!event) event = window.event;

        event.preventDefault();
        dropZone.removeClass('hover');
        dropZone.addClass('drop');

        for (var fileIndex = 0; fileIndex < event.dataTransfer.files.length; fileIndex++) {
            var file = event.dataTransfer.files[fileIndex];
            var xhr = new XMLHttpRequest();
            xhr.upload.addEventListener('progress', uploadProgress, false);
            xhr.onreadystatechange = stateChange;
            xhr.open('POST', 'put_file');
            xhr.setRequestHeader('X-FILE-NAME', file.name);
            var date = file.lastModifiedDate;
            if (date) {
                var date_str=('0' + date.getDate()).substr(-2,2) + '.' + ('0'+date.getMonth()).substr(-2,2) + '.' + date.getFullYear();
                xhr.setRequestHeader('X-FILE-DATE', date_str);
            }
            xhr.send(file);
        }
    };

</script>
</body>
</html>
