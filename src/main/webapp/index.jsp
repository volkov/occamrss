<%@ page import="svolkov.*" %>
<%@ page import="com.sun.syndication.feed.synd.SyndEntry" %>
<%@ page import="java.util.List" %>
<html>
<head>
<%@ page
language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
%>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<%
	FeedProvider provider = FeedFactory.getFeedProvider();
	List<SyndEntry> entries = provider.getEntries();
%>
<meta http-equiv="refresh" content="<%= provider.getMaxDelay() %>" >
<title>RssFeed</title>
</head>
<body>
<h1>Occam RSS Aggregator</h1>
<h2><%= provider.getLastUpdate() %></h2>
<table border=2>
<%
    for ( SyndEntry entry : entries ) {
        %>
        <tr>
        <td><a href="<%= entry.getLink() %>"><%= entry.getTitle() %></a></td>
        <td><%= entry.getPublishedDate() %></td>
        </td>
        <%
    }
%>
</table>
</body>
</html>
