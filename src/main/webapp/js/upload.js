ru.timtish.bridge.upload = {

    file: function() {
        var btn = $('#upload-file-selector');
        ru.timtish.bridge.upload.files(btn[0].files);
        btn.replaceWith(btn.clone(true)); // clear selected file value
    },

    files: function(files) {
        $.each(files, function (num, file) {
            timtish.upload.file('file', file, ru.timtish.bridge.upload.onFileUpload)
        });
    },

    onFileUpload: function(event) {
        if (event.complete) alert('File successfully uploaded!');
        if (event.error) alert('Error loading file!');
    }
};

$(function() {
    $('#upload-file-selector').change(ru.timtish.bridge.upload.file);
});


/*
div.input-file {
 position: relative;
 overflow: hidden;
 display: inline-block;
 padding: 0;
}

#menu div.input-file {
 position: relative;
 overflow: hidden;
 display: inline-block;
 padding: 0;
 height: 76px;
}

#menu div.input-file > button {
 padding: 0 18px;
 height: 76px;
 font-family: sans-serif;
}

.input-file > input[type=file]{
 position: absolute;
 left: 0;
 top: 0;
 width: 100%;
 height: 100%;
 transform: scale(20);
 letter-spacing: 10em;
 -ms-transform: scale(20);
 opacity: 0;
 cursor: pointer;
}
*/
