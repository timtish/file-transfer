<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="ru.timtish.bridge.web.UrlConstants" %>
<%@ page import="ru.timtish.bridge.pipeline.AbstractStream" %>
<%@ page import="ru.timtish.bridge.box.*" %>
<html>
<head>
    <title>Files box</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="main.css" />
    <script type="text/javascript" src="js/jquery.js"></script>
</head>
<body>
<button value="Выделить" onclick=""></button>
<button value="Загрузить архивом" onclick=""></button>
<button value="Отправить почтой" onclick=""></button>

<a href="index.jsp">Добавить файл</a>
<a href="webdav">Подключить как сетевой диск</a>

<table>
    <caption>Box <%=request.getRemoteUser()%></caption>
    <tr><th>number</th><th>name</th><th>description</th><th>loaded</th></tr>
<%
    StreamsBox box = StreamsBox.getInstance();
    String user = request.getRemoteUser();
    Set<String> newKeys = new HashSet<String>();
    String newKeyList = request.getParameter(UrlConstants.PARAM_NEW_KEYS);
    if (newKeyList != null) {
        newKeys.addAll(Arrays.asList(newKeyList.split(",")));
    }
    int i = 1;
    BoxEntity dir = box.getBoxEntity(user, request.getContextPath());

    for (BoxEntity stream : dir.getChilds()) {
        boolean isFile = stream instanceof BoxFile;
        String key = isFile ? ((BoxFile) stream).getKey() : stream.getName();
        AbstractStream in = isFile ? ((BoxFile) stream).getInputStream() : null;
        %><tr>
            <td><input type="checkbox" id="<%=stream.getName()%>" /></td>
            <td><%=i++%></td>
            <td><a href="<%=isFile ? "get?key=" + key : stream.getName() + "/" %>" <%if(isFile && StreamStatus.CLOSED.equals(in.getStatus())){%>style="color: gray;text-decoration: line-through;"<%}else if(newKeys.contains(key)){%>style="color: green;"<%}else{%>style="color: blue;"<%}%>><%=stream.getName()%></a></td>
            <td><%=stream.getDescription() == null ? "" : stream.getDescription()%></td>
            <td><%=(isFile && stream.getSize() != null && stream.getSize() > 0) ? in.getReaded() * 100 / stream.getSize() + " %" : "?"%></td>
        </tr><%
    }
%>
</table>

</body>
</html>
