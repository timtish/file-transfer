$(function(){
    $('#selected-as-zip').on('click', function () {
        var sf = $('input:checked.sf');
        if (sf.length == 0) {
            ru.timtish.bridge.alert("Файлы не выбраны");
            return;
        }
        var streamKeyList = [];
        for (var i = 0; i < sf.length; i++) streamKeyList.push(sf[i].id);
        ru.timtish.bridge.loadFilesAsZip(streamKeyList);
    });
    $('#send-to-email').on('click', function () {
        var sf = $('input:checked.sf');
        if (sf.length == 0) {
            ru.timtish.bridge.alert("Файлы не выбраны");
            return;
        }
        var streamKeyList = [];
        for (var i = 0; i < sf.length; i++) streamKeyList.push(sf[i].id);
        ru.timtish.bridge.sendToEmail(streamKeyList, 'timtish@gmail.com');
    });

    //ru.timtish.comet.open("ws://#{pageContext.request.serverName}:#{pageContext.request.serverPort}#{pageContext.request.contextPath}/events");
})