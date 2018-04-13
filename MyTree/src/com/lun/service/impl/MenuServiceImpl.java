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
 * @Desc：左边树型菜单管理案例，前台用zTree插件展示，后台用XML保存数据
 * @Author：张轮
 * @Date：2013-12-11下午02:34:13
 */
public class MenuServiceImpl implements IMenuService{
	
	//用来临时保存树json格式字符串，因为用到了递归，所以需要该全局变量
	private StringBuffer jsonStr;
	
	/**
	 * @Desc：读取XML数据，并组装返回json格式字符串，供zTree前台展示
	 * @Author：张轮
	 * @Date：2013-12-11下午04:35:42
	 * @return 树json格式字符串
	 */
	public String loadTree(){
		try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//得到XML root根节点
			Element root=doc.getRootElement();
			
			//初始化json字符串
	    	jsonStr=new StringBuffer("[");
	    	
			//利用递归，循环加载所有节点
	    	jsonStr=loadAllChildNode(root);
	    	
	    	//如果XML有数据，补全完整的json字符串，如果没找到任何节点数据，返回0
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
	 * @Desc：递归读取XML节点，组装zTree插件需要的json格式字符串
	 * @Author：张轮
	 * @Date：2013-12-11下午04:53:19
	 * @param element XML节点对象
	 * @return 递归组装后的json格式字符串
	 */
    @SuppressWarnings("unchecked")
	public StringBuffer loadAllChildNode(Element element){
    	
		try {
			//得到当前节点下的子节点集合
			List<Element> elementList=element.elements();
			
			//循环根节点下所有的子节点
			if(elementList!=null&&elementList.size()>0){
				for(Element el:elementList){
			    	//得到id属性值，用来唯一标识某节点对象
					String menuId=el.attributeValue("id");
					
					//得到value属性值，也就是树菜单将要展示的结果值
					String menuValue=el.attributeValue("value");
					
					//过滤空节点以及没有id属性的节点
					if(menuId!=null){
						jsonStr.append("{id:'"+menuId+"',"); 
						jsonStr.append("pId:'"+el.getParent().attributeValue("id")+"',");
						jsonStr.append("name:'"+menuValue+"',");
						jsonStr.append("click:false,");
						
						//如果当前节点下还有子节点，则标致为父节点类型，前台页面显示时，节点前边会显示"+"号，图标也与子节点不一样
						if(el.nodeCount()>0){
							jsonStr.append("isParent:true");
						}else{
							jsonStr.append("isParent:false");
						}
						jsonStr.append("},");
					}
					
					//如果当前节点下还有子节点
					if(el.nodeCount()>0){
						//自己调用自己-递归
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
	 * @Desc：新增-保存数据，向XML新增一个节点
	 * @Author：张轮
	 * @Date：2013-12-12下午01:59:32
	 * @param menuName 菜单名称
	 * @param menuUrl 菜单URL
	 * @param menuRemark 备注
	 * @param menuId XML节点ID
	 */
	@SuppressWarnings("unchecked")
	public void save(String menuName,String menuUrl,String menuRemark,String menuId){
    	try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//利用XPath查找所有节点中，属性名 id 值为参数中menuId值的节点元素
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//得到maxId节点
			Element maxIdElement=(Element)doc.selectSingleNode("/leftMenu/maxId");
			String maxId=maxIdElement.getText();
			
			//为新增加的节点构建数据
			Element newElement=null;
			
			//如果是新增一级节点
			if(menuId.equals("0")){ 
				//创建一个新节点
				newElement=DocumentHelper.createElement("menuName");
		    	
				//插入节点到指定位置
				List<Element> list=currElement.elements();
				list.add(list.indexOf(maxIdElement),newElement);
			}else{//不是一级节点，默认追加至子节点末尾处
				newElement=currElement.addElement("menuName");
			}
			
			//增加属性
			newElement.addAttribute("id",maxId)
	    	.addAttribute("value",menuName)
	    	.addAttribute("url", menuUrl)
	    	.addAttribute("remark",menuRemark);
			
			//修改maxId节点的值，使其永远保持最大id值，类似数据库自增列的功能
	    	maxIdElement.setText((Integer.parseInt(maxId)+1)+"");
	    	
	    	//默认生成的 XML文件排版格式比较乱，指定了格式化的方式为缩进式
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//指定XML字符集编码，防止乱码
	    	format.setEncoding("UTF-8");
	    	
	    	//将Document中的内容写入文件中
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * @Desc：修改数据，更新XML节点属性
	 * @Author：张轮
	 * @Date：2013-12-12下午01:59:32
	 * @param menuName 菜单名称
	 * @param menuUrl 菜单URL
	 * @param menuRemark 备注
	 * @param menuId XML节点ID
	 */
	public void update(String menuName,String menuUrl,String menuRemark,String menuId){
		try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//利用XPath查找所有节点中，属性名 id 值为参数中id值的节点元素
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//修改属性值
			currElement.attribute("value").setValue(menuName);
			currElement.attribute("url").setValue(menuUrl);
			currElement.attribute("remark").setValue(menuRemark);
			
	    	//默认生成的 XML文件排版格式比较乱，指定了格式化的方式为缩进式
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//指定XML字符集编码，防止乱码
	    	format.setEncoding("UTF-8");
	    	
	    	//将Document中的内容写入文件中
	    	XMLWriter writer=new XMLWriter(new FileOutputStream(xmlFile),format);
	    	writer.write(doc);
	    	writer.close();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Desc：删除节点
	 * @Author：张轮
	 * @Date：2013-12-12下午02:35:09
	 * @param menuId XML节点ID
	 * @return 返回操作结果，1成功，0失败
	 */
	public int delete(String menuId){
		try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//利用XPath查找所有节点中，属性名 id 值为参数中id值的节点元素
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//删除该节点
			currElement.getParent().remove(currElement);
			
	    	//默认生成的 XML文件排版格式比较乱，指定了格式化的方式为缩进式
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//指定XML字符集编码，防止乱码
	    	format.setEncoding("UTF-8");
	    	
	    	//将Document中的内容写入文件中
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
	 * @Desc：移动节点，上下调整节点位置或调整节点所属层次关系。采用的逻辑是先将源节点复制一份至目标位置，然后将源节点删除
	 * @Author：张轮
	 * @Date：2013-12-12下午05:41:58
	 * @param menuId menuId XML节点ID
	 * @param targetMenuId 移动节点时，目标XML节点ID
	 * @param moveType 移动节点的类型。0移动到目标节点的内部、作为目标节点的子节点，1移动到目标节点上边，2移动到目标节点下边
	 * @return 返回操作结果，1成功，0失败
	 */
	@SuppressWarnings("unchecked")
	public int move(String menuId,String targetMenuId,Integer moveType){
		try {
			//构建xml文件
			File xmlFile=new File(ServletActionContext.getServletContext().getRealPath("")+ File.separator + "data.xml");
			
			//从文件读取XML，输入文件路径，返回XML文档
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			
			//利用XPath查找所有节点中，属性名 id 值为参数中menuId值的节点元素，也就是需要移动的节点对象
			Element currElement=(Element)doc.selectSingleNode("//*[@id='"+menuId+"']");
			
			//利用XPath查找移动节点时，鼠标放开时得到的目标节点对象
			Element targetElement=(Element)doc.selectSingleNode("//*[@id='"+targetMenuId+"']");
			
			//得到maxId节点
			Element maxIdElement=(Element)doc.selectSingleNode("/leftMenu/maxId");
			
			if(moveType!=null&&moveType.toString().equals("0")){ //移动到目标节点的内部、作为目标节点的子节点
				if(targetMenuId.equals("0")){ //如果是移动至根目录
					
					//将需要移动的节点插入到指定位置：目标节点上方
					List<Element> list=targetElement.elements();
					list.add(list.indexOf(maxIdElement),(Element)currElement.clone());
				}else{
					//将需要移动的节点插入至目标节点内，作为子节点，默认追加至目标子节点末尾处
					targetElement.add((Element)currElement.clone());
				}
			}else if(moveType!=null&&moveType.toString().equals("1")){ //移动到目标节点上边
				
				//将需要移动的节点插入到指定位置：目标节点上方
				List<Element> list=targetElement.getParent().elements();
				list.add(list.indexOf(targetElement),(Element)currElement.clone());
			}else if(moveType!=null&&moveType.toString().equals("2")){ //移动到目标节点下边
				
				//将需要移动的节点插入到指定位置：目标节点下方
				List<Element> list=targetElement.getParent().elements();
				list.add(list.indexOf(targetElement)+1,(Element)currElement.clone());
			}else{
				return 0;
			}
			
			//删除需要移动的原节点
			currElement.getParent().remove(currElement);
			
	    	//默认生成的 XML文件排版格式比较乱，指定了格式化的方式为缩进式
	    	OutputFormat format=OutputFormat.createPrettyPrint();
	    	
	    	//指定XML字符集编码，防止乱码
	    	format.setEncoding("UTF-8");
	    	
	    	//将Document中的内容写入文件中
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
