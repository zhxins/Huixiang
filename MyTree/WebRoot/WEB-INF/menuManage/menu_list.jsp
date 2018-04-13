<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>菜单管理</title>
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" href="<%=basePath%>css/demo.css" type="text/css">
	<link rel="stylesheet" href="<%=basePath%>css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/jquery.ztree.core-3.4.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/jquery.ztree.excheck-3.4.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/jquery.ztree.exedit-3.4.js"></script>
  </head>
  
 <style type="text/css">
	.ztree li span.button.add {margin-left:2px; margin-right: -1px; background-position:-144px 0; vertical-align:top; *vertical-align:middle}
</style>
  
<body>
<script type="text/javascript">
var moveNodeId; //用来临时保存移动节点的对象id
//声明dtree的属性
var setting = {
	data : {
		simpleData : {
			enable : true
		}
	},
	callback : {
		beforeClick: beforeClick,//鼠标单击节点前触发的事件
		beforeEditName: beforeEditName, //点击编辑前触发的事件
		beforeRemove: beforeRemove, //点击删除前触发的事件
		beforeDrag: beforeDrag, //鼠标移动节点前触发
		beforeDrop: beforeDrop, //鼠标移动节点结束后触发
		onAsyncSuccess : onAsyncSuccess, //异步加载返回成功时触发的事件
		onDrop: zTreeOnDrop //移动事件完成
	},
	view: {
		dblClickExpand: false,
		addHoverDom: addHoverDom, //鼠标浮至节点时显示增加、修改、删除按钮
		removeHoverDom: removeHoverDom, //鼠标离开时，隐藏
		selectedMulti: false,
		nameIsHTML: true
	},
	edit: {
		enable: true,
		removeTitle: "删除",
		renameTitle: "修改",
		drag: {
			prev: true, //可以移动至节点之上
			next: true,//可以移动至节点之下
			inner: true, //可以将节点移动至另一节点之内，作为子节点
			isCopy: false,
			isMove:true,
			borderMax:20
		}
	}
};


//页面初始化时加载
$(document).ready(function(){
	if("${menuJson}"!="0"){
		$.fn.zTree.init($("#treeDemo"), setting,${menuJson});
	}else{
		$("#info").show();
	}
});

/*
 * 用于捕获异步加载正常结束的事件回调函数
 */
function onAsyncSuccess(event, treeId, treeNode, msg) {
	/*
	* 如果有移动和异步同时的情况，移动的那个节点对象会出现重复的两个
	* 所以在异步加载完之后，删除掉一个
	*/
	if(moveNodeId!=null){
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		var moveNodes = zTree.getNodesByParam("id", moveNodeId, treeNode);
		if(moveNodes!=null&&moveNodes.length>0){
			zTree.removeNode(moveNodes[0]);
		}
	}
}


//鼠标浮至节点时显示增加、修改、删除按钮
function addHoverDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.id).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.id
		+ "' title='新增' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.id);

	//点击新增时，跳转至新增页面
	if (btn) btn.bind("click", function(){
		window.location.href="menu_add.action?menuId="+treeNode.id;
		return false;
	});
}

//鼠标离开节点时隐藏增加、修改、删除按钮
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_"+treeNode.id).unbind().remove();
}

//点击修改时，跳转至修改页面
function beforeEditName(treeId, treeNode) {
	window.location.href="menu_edit.action?menuId="+treeNode.id;
	return false;
}

//点击删除时，触发的事件
function beforeRemove(treeId, treeNode) {
	var isSuccess=false;
	if(window.confirm("确定要删除["+treeNode.name+"]?")){
		 $.ajax({
		     	type: "post",//使用get方法访问后台
		     	dataType: "json",//返回json格式的数据
		     	url: "menu_delete.action",//要访问的后台地址
		     	data: {"menuId":treeNode.id},//要发送的数据
		     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
		     	async: false,//同步
		     	success: function(data){//msg为返回的数据，在这里做数据绑定
		         	if(data=="1"){
		         		 isSuccess=true;
		    			 alert("删除成功！");
		         	}else{
		         		isSuccess=false;
		         		alert("删除失败！");
		         	}
				}
			});
	}
	return isSuccess;
}

//鼠标移动节点前，触发的事件
function beforeDrag(treeId, treeNodes) {
	for (var i=0,l=treeNodes.length; i<l; i++) {
		if (treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}

//鼠标移动节点结束后，触发的事件
function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	var isMove=false;
		if(targetNode==null){//说明移动到至了一级节点
			if(window.confirm("确定要将["+treeNodes[0].name+"]移动为一级菜单?")){
				$.ajax({
			     	type: "post",//使用get方法访问后台
			     	dataType: "json",//返回json格式的数据
			     	url: "menu_move.action",//要访问的后台地址
			     	data: {"moveType":0,"menuId":treeNodes[0].id,"targetMenuId":0},//要发送的数据
			     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
			     	async: false,//同步
			     	success: function(data){//msg为返回的数据，在这里做数据绑定
			         	if(data=="1"){
			         		isMove=true;
			    			alert("移动成功！");
			         	}else{
			         		isMove=false;
			         		alert("移动失败！");
			         	}
					}
				});
			}else{
				isMove=false;
			}
		}else{
			if(moveType=="inner"){
				//设置级别，因为是目标节点的子节点，在目标级别的基础上加2
				var targetLevel=targetNode.level;
				targetLevel+=1;
				
				if(window.confirm("确定要将["+treeNodes[0].name+"]移动作为["+targetNode.name+"]的子节点?")){
					$.ajax({
				     	type: "post",//使用get方法访问后台
				     	dataType: "json",//返回json格式的数据
				     	url: "menu_move.action",//要访问的后台地址
				     	data: {"moveType":0,"menuId":treeNodes[0].id,"targetMenuId":targetNode.id},//要发送的数据
				     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
				     	async: false,//同步
				     	success: function(data){//msg为返回的数据，在这里做数据绑定
				         	if(data=="1"){
				         		isMove=true;
				    			 alert("移动成功！");
				         	}else{
				         		isMove=false;
				         		alert("移动失败！");
				         	}
						}
					});
				}else{
					isMove=false;
				}
		}else if(moveType=="prev"){
			//设置排序
			var targetLevel=targetNode.levelOrder;
			targetLevel-=1;

			//移动的时候，可能不只调换了顺序，连父节点也可能改变了
			var alertTitle;
			if(treeNodes[0].pid==targetNode.pid){
				alertTitle="确定要将["+treeNodes[0].name+"]移动至["+targetNode.name+"]上方?";
			}else{
				if(targetNode.getParentNode()!=null){
					alertTitle="确定要将["+treeNodes[0].name+"]移动作为["+targetNode.getParentNode().name+"]的子节点，\n并移动至["+targetNode.name+"]上方?";
				}else{
					alertTitle="确定要将["+treeNodes[0].name+"]移动作为一级菜单，\n并移动至["+targetNode.name+"]上方?";
				}
			}
			if(window.confirm(alertTitle)){
				$.ajax({
			     	type: "post",//使用get方法访问后台
			     	dataType: "json",//返回json格式的数据
			     	url: "menu_move.action",//要访问的后台地址
			     	data: {"moveType":1,"menuId":treeNodes[0].id,"targetMenuId":targetNode.id},//要发送的数据
			     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
			     	async: false,//同步
			     	success: function(data){//msg为返回的数据，在这里做数据绑定
			         	if(data=="1"){
			         		isMove=true;
			    			 alert("移动成功！");
			         	}else{
			         		isMove=false;
			         		alert("移动失败！");
			         	}
					}
				});
			}else{
				isMove=false;
			}
		}else if(moveType=="next"){
			//设置排序
			var targetLevel=targetNode.levelOrder;
			targetLevel+=1;

			//移动的时候，可能不只调换了顺序，连父节点也可能改变了
			var alertTitle;
			if(treeNodes[0].pid==targetNode.pid){
				alertTitle="确定要将["+treeNodes[0].name+"]移动至["+targetNode.name+"]下方?";
			}else{
				if(targetNode.getParentNode()!=null){
					alertTitle="确定要将["+treeNodes[0].name+"]移动作为["+targetNode.getParentNode().name+"]的子节点，\n并移动至["+targetNode.name+"]下方?";
				}else{
					alertTitle="确定要将["+treeNodes[0].name+"]移动作为一级菜单，\n并移动至["+targetNode.name+"]下方?";
				}
			}
			if(window.confirm(alertTitle)){
				$.ajax({
			     	type: "post",//使用get方法访问后台
			     	dataType: "json",//返回json格式的数据
			     	url: "menu_move.action",//要访问的后台地址
			     	data: {"moveType":2,"menuId":treeNodes[0].id,"targetMenuId":targetNode.id},//要发送的数据
			     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
			     	async: false,//同步
			     	success: function(data){//msg为返回的数据，在这里做数据绑定
			         	if(data=="1"){
			         		isMove=true;
			    			 alert("移动成功！");
			         	}else{
			         		isMove=false;
			         		alert("移动失败！");
			         	}
					}
				});
			}else{
				isMove=false;
			}
		}else{
			isMove=false;
		}
	}
	//将移动的对象保存起来
	moveNodeId=treeNodes[0].id;
	return isMove;
}

//节点移动结束时执行
function zTreeOnDrop(event, treeId, treeNodes, targetNode, moveType) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	zTree.cancelSelectedNode();
};


/*
 * 鼠标单击树节点之前产生的事件
 * 主要实现了点击节点名称，也能实现展开合拢节点的功能
 */
function beforeClick(treeId, treeNode, clickFlag) {
	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
	
	//如果已经处于展状态，则收拢，否则展开
	zTree.expandNode(treeNode);
	return (treeNode.click != false);
}

//初始化数据
function initData(){
	if(window.confirm("这将删除您所有的操作数据，确定要初始化数据？")){
		$.ajax({
	     	type: "post",//使用get方法访问后台
	     	dataType: "json",//返回json格式的数据
	     	url: "menu_initData.action",//要访问的后台地址
	     	complete :function(){$("#load").hide();},//AJAX请求完成时隐藏loading提示
	     	async: false,//同步
	     	success: function(data){//msg为返回的数据，在这里做数据绑定
	         	if(data=="1"){
	         		window.location.href="menu_list.action";
	    			alert("初始化成功！");
	         	}else{
	         		alert("初始化失败！");
	         	}
			}
		});
	}
}

</script>
	<div style="text-align: center;">
		<table>
			<tr>
				<td>
					<!-- tree节点begin -->
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
						<input type="button" onclick="window.location.href='menu_add.action?menuId=0'" value="添加一级菜单"  />
					</div>
					<!-- tree节点end -->
			
					<!-- 空数据提示 -->
					<div  style="text-align: center;">
						<label>
							<span id="info" style="color: red;width: 100%;text-align: center;display: none;">没有找到相应数据</span>
						</label>
					</div>
				</td>
				<td style="vertical-align: text-top;font-size: 20px;color: red;">
					1、鼠标悬浮至菜单节点上试试<br/>
					2、鼠标点住某节点不放，拖动至其它节点试试<br/>
					3、点击[初始化数据]将删除您的所有操作数据，将XML数据恢复默认<br/>
					<input type="button" onclick="initData();" value="初始化数据"  />
				</td>
			</tr>
		</table>
	</div>
</body>
</html>