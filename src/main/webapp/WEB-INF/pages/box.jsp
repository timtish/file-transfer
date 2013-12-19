<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>Files box</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/main.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/box.css'/>"/>
    <script type="text/javascript" src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
    <script type="text/javascript" src="<s:url value='/js/bridge.js'/>"></script>
    <script type="text/javascript" src="<s:url value='/js/comet.js'/>"></script>
    <script type="text/javascript" src="<s:url value='/js/jquery.fileDownload.js'/>"></script>
</head>
<body id="mf">
<%@ include file="../templates/menu.jspf" %>
<table id="content">
    <colgroup>
        <col style="width: 24px">
        <col style="width: 2em">
        <col style="width: 40%">
        <col style="width: 45%">
        <col style="width: 5em">
    </colgroup>
    <caption>Box <c:out value="${box.name}"/>${box.name}</caption>
    <label for="mf" id="mfdd"></label>
    <tr class="table-header">
        <th></th>
        <th>№</th>
        <th>файл</th>
        <th class="file-description">комментарий</th>
        <th>кеш</th>
    </tr>
    <c:forEach var="file" items="${box.files}"><tr>
        <td><input type="checkbox" id="${file.id}" class="sf" /></td>
        <td class="type-BoxUtil.contentTypeIcon(in.getContentType())" title="getContentType() i"></td>
        <td><a href="${file.address}get_file?key=${file.key} : stream.getName() + File.separatorChar streamStatus.CLOSED.equals(in.getStatus()))" style="color: gray;text-decoration: line-through;">${file.name}</a></td>
        <td>${file.description}</td>
        <td>${file.size}(isFile && stream.getSize() != null && stream.getSize() > 0) ? in.getReaded() * 100 / stream.getSize() + " %" : "?"</td>
    </tr></c:forEach>
</table>
</body>
<script type="text/javascript" src="<s:url value='/js/drop.js'/>"></script>
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

    ru.timtish.comet.open("ws://${pageContext.request.serverName}:${pageContext.request.serverPort}/${pageContext.request.contextPath}/events");
})</script>
</html>
