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
 * @Desc��������Ͳ˵���������ǰ̨��zTree���չʾ����̨��XML��������
 * @Author������
 * @Date��2013-12-11����02:33:19
 */
public class MenuAction extends ActionSupport{
	
	private static final long serialVersionUID = -6820101061964169973L;

	//������ǰ̨ҳ���json�ַ�
	private String menuJson;
	
	//ҵ���ӿڶ���
	private IMenuService iMenuService;
	
	private String menuName; //�˵�����
	private String menuUrl; //�˵�url
	private String menuRemark; //��ע
	private String menuId;//XML�ڵ�ID
	
	//�ƶ��ڵ�ʱ��Ŀ��XML�ڵ�ID
	private String targetMenuId;
	
	//�ƶ��ڵ�����͡�0�ƶ���Ŀ��ڵ���ڲ�����ΪĿ��ڵ���ӽڵ㣬1�ƶ���Ŀ��ڵ��ϱߣ�2�ƶ���Ŀ��ڵ��±�
	private Integer moveType;
	
	/**
	 * @Desc�������������б�
	 * @Author������
	 * @Date��2013-12-11����04:56:54
	 * @return
	 */
	public String list(){
    	
    	//����ѯ�����Ľ����json���ݷ���
		menuJson=iMenuService.loadTree();
    	
        return SUCCESS ;
    }
	
	/**
	 * @Desc�����ӽڵ㣬��ת������ҳ��
	 * @Author������
	 * @Date��2013-12-11����06:02:53
	 * @return
	 */
	public String add(){
		return SUCCESS;
	}
	
	/**
	 * @Desc������-����
	 * @Author������
	 * @Date��2013-12-11����06:11:31
	 * @return
	 */
	public String save(){
		iMenuService.save(menuName, menuUrl, menuRemark, menuId);
		return "menuList";
	}
	
	/**
	 * @Desc���޸Ľڵ㣬��ת���޸�ҳ��
	 * @Author������
	 * @Date��2013-12-12����02:02:27
	 * @return
	 */
	public String edit(){
		try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//����XPath�������нڵ��У������� id ֵΪ������idֵ�Ľڵ�Ԫ��
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//�õ���ǰ�ڵ�����ֵ����������ǰ̨ҳ��չʾ
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
	 * @Desc���޸�-����
	 * @Author������
	 * @Date��2013-12-12����02:03:17
	 * @return
	 */
	public String update(){
		iMenuService.update(menuName, menuUrl, menuRemark, menuId);
		return "menuList";
	}
	
	/**
	 * @Desc��ɾ���ڵ�-ǰ̨����Ajax�첽ɾ��,���ز��������1�ɹ���0ʧ��
	 * @Author������
	 * @Date��2013-12-12����02:45:57
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
	 * @Desc���ƶ��ڵ㣬���µ����ڵ�λ�û�����ڵ�������ι�ϵ��ǰ̨����Ajax�첽�ƶ�,���ز��������1�ɹ���0ʧ��
	 * @Author������
	 * @Date��2013-12-12����02:52:34
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
	 * @Desc����ʼ�����ݣ���ɾ�����в������ݣ���XML���ݻָ�Ĭ�ϣ�ǰ̨����Ajax�첽����,���ز��������1�ɹ���0ʧ��
	 * @Author������
	 * @Date��2013-12-13����11:00:17
	 */
	public void initData(){
		PrintWriter out = null;
		try {
			ServletActionContext.getResponse().setContentType("text/html;chatset=UTF-8");
			out = ServletActionContext.getResponse().getWriter();
			
			//Դ�ļ�
			File srcFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data_backup.xml");
			
			//Ŀ���滻�ļ�
			File sourceFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//�����ļ�
			FileUtils.copyFile(srcFile, sourceFile);
			
			//���1����ɹ�
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
