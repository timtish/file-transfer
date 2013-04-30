<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="ru.timtish.bridge.box.StreamsBox" %>
<%@ page import="ru.timtish.bridge.pipeline.AbstractStream" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="ru.timtish.bridge.web.util.UrlConstants" %>
<%@ page import="ru.timtish.bridge.box.BoxUtil" %>
<html>
<head>
    <title>Streams</title>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="main.css" />
</head>
<body>
<table>
    <caption>Streams</caption>
    <tr><th>number</th><th>name</th><th>description</th><th>loaded</th></tr>
    <jsp:useBean id="box" beanName="streamBox" type="ru.timtish.bridge.box.StreamsBox"/>
<%
    Set<String> newKeys = new HashSet<String>();
    String newKeyList = request.getParameter(UrlConstants.PARAM_NEW_KEYS);
    if (newKeyList != null) {
        newKeys.addAll(Arrays.asList(newKeyList.split(",")));
    }
    int i = 1;
    for (String key : box.getKeys()) {
        AbstractStream stream = box.getStream(key);
        %><tr>
            <td><%=i++%></td>
            <td><a href="get?key=<%=key%>" <%if(newKeys.contains(key)){%>style="color: green;"<%}else{%>style="color: #00008b;"<%}%>><%=stream.getName()%></a></td>
            <td><%=stream.getDescription()%></td>
            <td><%=(stream.getSize() != null && stream.getSize() > 0) ? stream.getReaded() * 100 / stream.getSize() + " %" : "?"%></td>
        </tr><%
    }
%>
</table>

<a href="index.jsp">add file</a>

</body>
</html>
