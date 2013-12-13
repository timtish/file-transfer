<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="ru.timtish.bridge.web.util.UrlConstants" %>
<%@ page import="ru.timtish.bridge.pipeline.AbstractStream" %>
<%@ page import="ru.timtish.bridge.box.*" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.io.File" %>
<html>
<head>
    <title>Files box</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="main.css" />
    <link rel="stylesheet" type="text/css" href="box.css" />
    <script type="text/javascript" src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
    <script type="text/javascript" src="js/bridge.js"></script>
    <script type="text/javascript" src="js/comet.js"></script>
    <script type="text/javascript" src="js/jquery.fileDownload.js"></script>
</head>
<body id="mf">
<div id="menu">
    <a class="menu1" href="index.jsp">Добавить файл</a>
    <button id="selected-as-zip">Загрузить архивом</button>
    <button id="send-to-email">Отправить почтой</button>
    <button id="new-box">Объединить в новый пакет</button>
    <a href="webdav">Подключить как сетевой диск</a>
</div>

<table id="content">
    <colgroup>
        <col style="width: 24px">
        <col style="width: 2em">
        <col style="width: 40%">
        <col style="width: 45%">
        <col style="width: 5em">
    </colgroup>
<%

    WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
    StreamsBox streamsBox = ac.getBean(StreamsBox.class);
    String box = request.getParameter(UrlConstants.PARAM_BOX);
    String user = request.getRemoteUser();
    String path = request.getParameter(UrlConstants.PARAM_BOX_PATH);
    Set<String> newKeys = new HashSet<String>();
    String newKeyList = request.getParameter(UrlConstants.PARAM_NEW_KEYS);
    if (newKeyList != null) {
        newKeys.addAll(Arrays.asList(newKeyList.split(",")));
    }
    int i = 1;
    BoxEntity dir = BoxUtil.getBoxEntity(user, box, path);
    if (dir == null) dir = streamsBox.getRoot();

%>

    <caption>Box <%=dir.getName()%></caption>
    <label for="mf" id="mfdd"></label>
    <tr class="table-header"><th></th><th>№</th><th>файл</th><th class="file-description">комментарий</th><th>кеш</th></tr>
    <!--jsp:useBean id="streamsBox" beanName="streamsBox" type="ru.timtish.bridge.box.StreamsBox"/-->
    <!-- todo: remove scriptlets -->

<%
    for (BoxEntity stream : dir.getChilds()) {
        boolean isFile = stream instanceof BoxFile;
        String key = isFile ? BoxUtil.findStreamKey(streamsBox, ((BoxFile)stream).getInputStream()) : null;
        String boxId = BoxUtil.getId(stream);
        AbstractStream in = isFile ? ((BoxFile) stream).getInputStream() : null;
        %><tr>
            <td><input type="checkbox" id="<%=boxId%>" class="sf" /></td>
            <td class="<%=in != null ? "type-" + BoxUtil.contentTypeIcon(in.getContentType()) : ""%>" title="<%=in != null ? in.getContentType() : ""%>"><%=i++%></td>
            <td><a href="<%=isFile ? "get?key=" + key : stream.getName() + File.separatorChar %>" <%if(isFile && StreamStatus.CLOSED.equals(in.getStatus())){%>style="color: gray;text-decoration: line-through;"<%}else if(newKeys.contains(key)){%>style="color: green;"<%}else{%>style="color: blue;"<%}%>><%=stream.getName()%></a></td>
            <td><%=stream.getDescription() == null ? "" : stream.getDescription()%></td>
            <td><%=(isFile && stream.getSize() != null && stream.getSize() > 0) ? in.getReaded() * 100 / stream.getSize() + " %" : "?"%></td>
        </tr><%
    }
%>
</table>
</body>
<script type="text/javascript" src="js/drop.js"></script>
<script type="text/javascript">$(function(){
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

    ru.timtish.comet.open("ws://localhost:8083/bridge/events");
})</script>
</html>
