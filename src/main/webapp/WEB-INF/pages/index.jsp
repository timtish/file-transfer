<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>Add file</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/main.css'/>" />
    <link rel="stylesheet" type="text/css" href="<s:url value='/css/index.css'/>" />
    <script src="http://code.jquery.com/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="<s:url value='/js/jquery.MultiFile.js'/>" type="text/javascript"></script>
    <script src="<s:url value='/js/bridge.js'/>" type="text/javascript"></script>
</head>
<body>
<div id="menu">
    <a class="menu1" href="box.html">Все файлы</a>
</div>
<form action="put_mp" method="post" enctype="multipart/form-data" accept-charset="UTF-8" id="mf">
    <label for="file1">Файл для зрагузки (можно перетащить в эту область)</label>
    <input type="file" id="file1" name="file" multiple="" required="" /><br/>
    <label for="description1">Комментарий</label>
    <input type="text" id="description1" name="description" size="30" /><br/>
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
</body>
<script src="<s:url value='/js/drop.js'/>" type="text/javascript"></script>
</html>
