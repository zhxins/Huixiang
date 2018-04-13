package com.lun.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.struts2.ServletActionContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import com.lun.service.IMenuService;

/**
 * @Desc��������Ͳ˵���������ǰ̨��zTree���չʾ����̨��XML��������
 * @Author������
 * @Date��2013-12-11����02:34:13
 */
public class MenuServiceImpl implements IMenuService{
	
	//������ʱ������json��ʽ�ַ�������Ϊ�õ��˵ݹ飬������Ҫ��ȫ�ֱ���
	private StringBuffer jsonStr;
	
	/**
	 * @Desc����ȡXML���ݣ�����װ����json��ʽ�ַ�������zTreeǰ̨չʾ
	 * @Author������
	 * @Date��2013-12-11����04:35:42
	 * @return ��json��ʽ�ַ���
	 */
	public String loadTree(){
		try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//�õ�XML root���ڵ�
			Element root=doc.getRootElement();
			
			//��ʼ��json�ַ���
	    	jsonStr=new StringBuffer("[");
	    	
			//���õݹ飬ѭ���������нڵ�
	    	jsonStr=loadAllChildNode(root);
	    	
	    	//���XML�����ݣ���ȫ������json�ַ��������û�ҵ��κνڵ����ݣ�����0
	    	if(jsonStr!=null&&jsonStr.length()>5){
				jsonStr.deleteCharAt(jsonStr.length()-1);
				jsonStr.append("]");
				return jsonStr.toString();
	    	}else{
	    		return "0";
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
    }
    
	/**
	 * @Desc���ݹ��ȡXML�ڵ㣬��װzTree�����Ҫ��json��ʽ�ַ���
	 * @Author������
	 * @Date��2013-12-11����04:53:19
	 * @param element XML�ڵ����
	 * @return �ݹ���װ���json��ʽ�ַ���
	 */
    @SuppressWarnings("unchecked")
	public StringBuffer loadAllChildNode(Element element){
    	
		try {
			//�õ���ǰ�ڵ��µ��ӽڵ㼯��
			List<Element> elementList=element.elements();
			
			//ѭ�����ڵ������е��ӽڵ�
			if(elementList!=null&&elementList.size()>0){
				for(Element el:elementList){
			    	//�õ�id����ֵ������Ψһ��ʶĳ�ڵ����
					String menuId=el.attributeValue("id");
					
					//�õ�value����ֵ��Ҳ�������˵���Ҫչʾ�Ľ��ֵ
					String menuValue=el.attributeValue("value");
					
					//���˿սڵ��Լ�û��id���ԵĽڵ�
					if(menuId!=null){
						jsonStr.append("{id:'"+menuId+"',"); 
						jsonStr.append("pId:'"+el.getParent().attributeValue("id")+"',");
						jsonStr.append("name:'"+menuValue+"',");
						jsonStr.append("click:false,");
						
						//�����ǰ�ڵ��»����ӽڵ㣬�����Ϊ���ڵ����ͣ�ǰ̨ҳ����ʾʱ���ڵ�ǰ�߻���ʾ"+"�ţ�ͼ��Ҳ���ӽڵ㲻һ��
						if(el.nodeCount()>0){
							jsonStr.append("isParent:true");
						}else{
							jsonStr.append("isParent:false");
						}
						jsonStr.append("},");
					}
					
					//�����ǰ�ڵ��»����ӽڵ�
					if(el.nodeCount()>0){
						//�Լ������Լ�-�ݹ�
						loadAllChildNode(el);
					}
				}
			}
			return jsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
   
    /**
	 * @Desc������-�������ݣ���XML����һ���ڵ�
	 * @Author������
	 * @Date��2013-12-12����01:59:32
	 * @param menuName �˵�����
	 * @param menuUrl �˵�URL
	 * @param menuRemark ��ע
	 * @param menuId XML�ڵ�ID
	 */
	@SuppressWarnings("unchecked")
	public void save(String menuName,String menuUrl,String menuRemark,String menuId){
    	try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//����XPath�������нڵ��У������� id ֵΪ������menuIdֵ�Ľڵ�Ԫ��
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//�õ�maxId�ڵ�
			Element maxIdElement=(Element)doc.selectSingleNode("/leftMenu/maxId");
			String maxId=maxIdElement.getText();
			
			//Ϊ�����ӵĽڵ㹹������
			Element newElement=null;
			
			//���������һ���ڵ�
			if(menuId.equals("0")){ 
				//����һ���½ڵ�
				newElement=DocumentHelper.createElement("menuName");
		    	
				//����ڵ㵽ָ��λ��
				List<Element> list=currElement.elements();
				list.add(list.indexOf(maxIdElement),newElement);
			}else{//����һ���ڵ㣬Ĭ��׷�����ӽڵ�ĩβ��
				newElement=currElement.addElement("menuName");
			}
			
			//��������
			newElement.addAttribute("id",maxId)
	    	.addAttribute("value",menuName)
	    	.addAttribute("url", menuUrl)
	    	.addAttribute("remark",menuRemark);
			
			//�޸�maxId�ڵ��ֵ��ʹ����Զ�������idֵ���������ݿ������еĹ���
	    	maxIdElement.setText((Integer.parseInt(maxId)+1)+"");
	    	
	    	//Ĭ�����ɵ� XML�ļ��Ű��ʽ�Ƚ��ң�ָ���˸�ʽ���ķ�ʽΪ����ʽ
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//ָ��XML�ַ������룬��ֹ����
	    	format.setEncoding("UTF-8");
	    	
	    	//��Document�е�����д���ļ���
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * @Desc���޸����ݣ�����XML�ڵ�����
	 * @Author������
	 * @Date��2013-12-12����01:59:32
	 * @param menuName �˵�����
	 * @param menuUrl �˵�URL
	 * @param menuRemark ��ע
	 * @param menuId XML�ڵ�ID
	 */
	public void update(String menuName,String menuUrl,String menuRemark,String menuId){
		try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//����XPath�������нڵ��У������� id ֵΪ������idֵ�Ľڵ�Ԫ��
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//�޸�����ֵ
			currElement.attribute("value").setValue(menuName);
			currElement.attribute("url").setValue(menuUrl);
			currElement.attribute("remark").setValue(menuRemark);
			
	    	//Ĭ�����ɵ� XML�ļ��Ű��ʽ�Ƚ��ң�ָ���˸�ʽ���ķ�ʽΪ����ʽ
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//ָ��XML�ַ������룬��ֹ����
	    	format.setEncoding("UTF-8");
	    	
	    	//��Document�е�����д���ļ���
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Desc��ɾ���ڵ�
	 * @Author������
	 * @Date��2013-12-12����02:35:09
	 * @param menuId XML�ڵ�ID
	 * @return ���ز��������1�ɹ���0ʧ��
	 */
	public int delete(String menuId){
		try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//����XPath�������нڵ��У������� id ֵΪ������idֵ�Ľڵ�Ԫ��
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//ɾ���ýڵ�
			currElement.getParent().remove(currElement);
			
	    	//Ĭ�����ɵ� XML�ļ��Ű��ʽ�Ƚ��ң�ָ���˸�ʽ���ķ�ʽΪ����ʽ
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//ָ��XML�ַ������룬��ֹ����
	    	format.setEncoding("UTF-8");
	    	
	    	//��Document�е�����д���ļ���
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
	    	return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * @Desc���ƶ��ڵ㣬���µ����ڵ�λ�û�����ڵ�������ι�ϵ�����õ��߼����Ƚ�Դ�ڵ㸴��һ����Ŀ��λ�ã�Ȼ��Դ�ڵ�ɾ��
	 * @Author������
	 * @Date��2013-12-12����05:41:58
	 * @param menuId menuId XML�ڵ�ID
	 * @param targetMenuId �ƶ��ڵ�ʱ��Ŀ��XML�ڵ�ID
	 * @param moveType �ƶ��ڵ�����͡�0�ƶ���Ŀ��ڵ���ڲ�����ΪĿ��ڵ���ӽڵ㣬1�ƶ���Ŀ��ڵ��ϱߣ�2�ƶ���Ŀ��ڵ��±�
	 * @return ���ز��������1�ɹ���0ʧ��
	 */
	@SuppressWarnings("unchecked")
	public int move(String menuId,String targetMenuId,Integer moveType){
		try {
			//����xml�ļ�
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//���ļ���ȡXML�������ļ�·��������XML�ĵ�
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//����XPath�������нڵ��У������� id ֵΪ������menuIdֵ�Ľڵ�Ԫ�أ�Ҳ������Ҫ�ƶ��Ľڵ����
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//����XPath�����ƶ��ڵ�ʱ�����ſ�ʱ�õ���Ŀ��ڵ����
			Element targetElement=(Element)doc.selectSingleNode("//*[@id='"+targetMenuId+"']");
			
			//�õ�maxId�ڵ�
			Element maxIdElement=(Element)doc.selectSingleNode("/leftMenu/maxId");
			
			if(moveType!=null&&moveType.toString().equals("0")){ //�ƶ���Ŀ��ڵ���ڲ�����ΪĿ��ڵ���ӽڵ�
				if(targetMenuId.equals("0")){ //������ƶ�����Ŀ¼
					
					//����Ҫ�ƶ��Ľڵ���뵽ָ��λ�ã�Ŀ��ڵ��Ϸ�
					List<Element> list=targetElement.elements();
					list.add(list.indexOf(maxIdElement),(Element)currElement.clone());
				}else{
					//����Ҫ�ƶ��Ľڵ������Ŀ��ڵ��ڣ���Ϊ�ӽڵ㣬Ĭ��׷����Ŀ���ӽڵ�ĩβ��
					targetElement.add((Element)currElement.clone());
				}
			}else if(moveType!=null&&moveType.toString().equals("1")){ //�ƶ���Ŀ��ڵ��ϱ�
				
				//����Ҫ�ƶ��Ľڵ���뵽ָ��λ�ã�Ŀ��ڵ��Ϸ�
				List<Element> list=targetElement.getParent().elements();
				list.add(list.indexOf(targetElement),(Element)currElement.clone());
			}else if(moveType!=null&&moveType.toString().equals("2")){ //�ƶ���Ŀ��ڵ��±�
				
				//����Ҫ�ƶ��Ľڵ���뵽ָ��λ�ã�Ŀ��ڵ��·�
				List<Element> list=targetElement.getParent().elements();
				list.add(list.indexOf(targetElement)+1,(Element)currElement.clone());
			}else{
				return 0;
			}
			
			//ɾ����Ҫ�ƶ���ԭ�ڵ�
			currElement.getParent().remove(currElement);
			
	    	//Ĭ�����ɵ� XML�ļ��Ű��ʽ�Ƚ��ң�ָ���˸�ʽ���ķ�ʽΪ����ʽ
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//ָ��XML�ַ������룬��ֹ����
	    	format.setEncoding("UTF-8");
	    	
	    	//��Document�е�����д���ļ���
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
	    	return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
