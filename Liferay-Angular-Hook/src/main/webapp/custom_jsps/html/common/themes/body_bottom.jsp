<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@ page import="com.liferay.portal.kernel.util.StringUtil"%>

<liferay-util:buffer var="html">
	<liferay-util:include page="/html/common/themes/body_bottom.portal.jsp" />
</liferay-util:buffer>

<liferay-util:buffer var="scrips">

	<%-- Angular modules --%>
	
	<script type="text/javascript" src="/html/js/dependencies/angular.min.js"></script>
	<script type="text/javascript" src="/html/js/dependencies/angular-ui-router.min.js"></script>
	<script type="text/javascript" src="/html/js/dependencies/jcs-auto-validate.min.js"></script>
	<script type="text/javascript" src="/html/js/dependencies/angular-ui-notification.min.js"></script>
	<script type="text/javascript" src="/html/js/dependencies/angular-translate.min.js"></script>
	<script type="text/javascript" src="/html/js/dependencies/angular-translate-loader-url.min.js"></script>
	
	<%-- Custom js files --%>
	
	<script type="text/javascript" src="/html/js/angular/angular-portlet-bootstrap.js"></script>
	
	<script type="text/javascript" src="/html/js/angular/portletUrlProvider.js"></script>
	<script type="text/javascript" src="/html/js/angular/liferayService.js"></script>
	<script type="text/javascript" src="/html/js/angular/hasPermission.js"></script>
	<script type="text/javascript" src="/html/js/angular/permissionService.js"></script>
	<script type="text/javascript" src="/html/js/angular/i18nErrorMessageResolver.js"></script>

</liferay-util:buffer>


<%=StringUtil.add(scrips, html, "\n")%>