package com.lun.action;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.lun.service.IMenuService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @Desc：左边树型菜单管理案例，前台用zTree插件展示，后台用XML保存数据
 * @Author：张轮
 * @Date：2013-12-11下午02:33:19
 */
public class MenuAction extends ActionSupport{
	
	private static final long serialVersionUID = -6820101061964169973L;

	//返回至前台页面的json字符
	private String menuJson;
	
	//业务层接口对象
	private IMenuService iMenuService;
	
	private String menuName; //菜单名称
	private String menuUrl; //菜单url
	private String menuRemark; //备注
	private String menuId;//XML节点ID
	
	//移动节点时，目标XML节点ID
	private String targetMenuId;
	
	//移动节点的类型。0移动到目标节点的内部、作为目标节点的子节点，1移动到目标节点上边，2移动到目标节点下边
	private Integer moveType;
	
	/**
	 * @Desc：加载树对象列表
	 * @Author：张轮
	 * @Date：2013-12-11下午04:56:54
	 * @return
	 */
	public String list(){
    	
    	//将查询出来的结果以json数据返回
		menuJson=iMenuService.loadTree();
    	
        return SUCCESS ;
    }
	
	/**
	 * @Desc：增加节点，跳转至增加页面
	 * @Author：张轮
	 * @Date：2013-12-11下午06:02:53
	 * @return
	 */
	public String add(){
		return SUCCESS;
	}
	
	/**
	 * @Desc：增加-保存
	 * @Author：张轮
	 * @Date：2013-12-11下午06:11:31
	 * @return
	 */
	public String save(){
		iMenuService.save(menuName, menuUrl, menuRemark, menuId);
		return "menuList";
	}
	
	/**
	 * @Desc：修改节点，跳转至修改页面
	 * @Author：张轮
	 * @Date：2013-12-12下午02:02:27
	 * @return
	 */
	public String edit(){
		try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//利用XPath查找所有节点中，属性名 id 值为参数中id值的节点元素
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//得到当前节点属性值，用来返回前台页面展示
			menuId=currElement.attributeValue("id");
			menuName=currElement.attributeValue("value");
			menuUrl=currElement.attributeValue("url");
			menuRemark=currElement.attributeValue("remark");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * @Desc：修改-保存
	 * @Author：张轮
	 * @Date：2013-12-12下午02:03:17
	 * @return
	 */
	public String update(){
		iMenuService.update(menuName, menuUrl, menuRemark, menuId);
		return "menuList";
	}
	
	/**
	 * @Desc：删除节点-前台利用Ajax异步删除,返回操作结果，1成功，0失败
	 * @Author：张轮
	 * @Date：2013-12-12下午02:45:57
	 */
	public void delete(){
		PrintWriter out = null;
		try {
			ServletActionContext.getResponse().setContentType("text/html;chatset=UTF-8");
			out = ServletActionContext.getResponse().getWriter();
			out.print(iMenuService.delete(menuId));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	/**
	 * @Desc：移动节点，上下调整节点位置或调整节点所属层次关系，前台利用Ajax异步移动,返回操作结果，1成功，0失败
	 * @Author：张轮
	 * @Date：2013-12-12下午02:52:34
	 */
	public void move(){
		PrintWriter out = null;
		try {
			ServletActionContext.getResponse().setContentType("text/html;chatset=UTF-8");
			out = ServletActionContext.getResponse().getWriter();
			out.print(iMenuService.move(menuId, targetMenuId, moveType));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	/**
	 * @Desc：初始化数据，将删除所有操作数据，将XML数据恢复默认，前台利用Ajax异步调用,返回操作结果，1成功，0失败
	 * @Author：张轮
	 * @Date：2013-12-13上午11:00:17
	 */
	public void initData(){
		PrintWriter out = null;
		try {
			ServletActionContext.getResponse().setContentType("text/html;chatset=UTF-8");
			out = ServletActionContext.getResponse().getWriter();
			
			//源文件
			File srcFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data_backup.xml");
			
			//目标替换文件
			File sourceFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//复制文件
			FileUtils.copyFile(srcFile, sourceFile);
			
			//输出1代表成功
			out.print(1);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}

	public String getMenuJson() {
		return menuJson;
	}

	public void setMenuJson(String menuJson) {
		this.menuJson = menuJson;
	}

	public IMenuService getiMenuService() {
		return iMenuService;
	}

	public void setiMenuService(IMenuService iMenuService) {
		this.iMenuService = iMenuService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getMenuRemark() {
		return menuRemark;
	}

	public void setMenuRemark(String menuRemark) {
		this.menuRemark = menuRemark;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getTargetMenuId() {
		return targetMenuId;
	}

	public void setTargetMenuId(String targetMenuId) {
		this.targetMenuId = targetMenuId;
	}

	public Integer getMoveType() {
		return moveType;
	}

	public void setMoveType(Integer moveType) {
		this.moveType = moveType;
	}
}
