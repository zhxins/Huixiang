<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>菜单管理</title>

		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="stylesheet" href="<%=basePath%>css/demo.css"
			type="text/css">
		<link rel="stylesheet"
			href="<%=basePath%>css/zTreeStyle/zTreeStyle.css" type="text/css">
		<script type="text/javascript"
			src="<%=basePath%>js/jquery-1.4.4.min.js"></script>
		<script type="text/javascript"
			src="<%=basePath%>js/jquery.ztree.core-3.4.js"></script>
		<script type="text/javascript"
			src="<%=basePath%>js/jquery.ztree.excheck-3.4.js"></script>
		<script type="text/javascript"
			src="<%=basePath%>js/jquery.ztree.exedit-3.4.js"></script>
	</head>
	<body>
	<div>当前位置：菜单管理 > <font color="#ff6600">修改</font></div>
		<form action="menu_update.action" method="post" name="updateForm" id="updateForm">
		<input type="hidden" name="menuId" value="${menuId }">
			<table width="97%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="8%">
						菜单名称
					</td>
					<td>
						<label>
							<input type="text"  name="menuName" id="menuName" value="${menuName }" />
						</label>
					</td>
					<td width="7%">
						菜单URL
					</td>
					<td>
						<label>
							<input type="text" name="menuUrl" id="menuUrl" value="${menuUrl }"/>
						</label>
					</td>
				</tr>
				<tr>
					<td height="70px">
						菜单说明
					</td>
					<td colspan="3">
						<label>
							<textarea name="menuRemark" id="menuRemark" style="width: 70%; height: 60px;">${menuRemark }</textarea>
						</label>
					</td>
				</tr>
			</table>

			<div style="text-align: center;">
				<label>
					<input type="submit" value="保存" />
				</label>
				<label>
					<input type="button" value="返回" onclick="window.history.go(-1);" />
				</label>
		</form>
	</body>
</html>
