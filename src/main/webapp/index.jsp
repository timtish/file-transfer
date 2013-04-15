<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Add file</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="main.css" />
</head>
<body>
<form action="put_mp" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
    <label for="description1">Комментарий</label>
    <input type="text" id="description1" name="description" size="30" /><br/>
    <label for="file">Выберите файл</label>
    <input type="file" id="file" name="file" multiple="" required="" /><br/>
    <input type="submit" value="add file from folder" />
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
</body>
</html>
