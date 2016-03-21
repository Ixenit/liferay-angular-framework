<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.util.StringUtil" %>

<liferay-util:buffer var="html">
   <liferay-util:include page="/html/common/themes/top_head.portal.jsp" />
   <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"></link>
   <link rel="stylesheet" href="/html/js/dependencies/angular-ui-notification.min.css"></link>
</liferay-util:buffer>

<%
   html = StringUtil.add(
         html,
         "<base href='/'>",
         "\n");
%>

<%= html %>