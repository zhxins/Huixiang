package com.lun.service;

/**
 * @Desc：左边树型菜单管理案例，前台用zTree插件展示，后台用XML保存数据
 * @Author：张轮
 * @Date：2013-12-11下午02:34:36
 */
public interface IMenuService {

	/**
	 * @Desc：读取XML数据，并组装返回json格式字符串，供zTree前台展示
	 * @Author：张轮
	 * @Date：2013-12-11下午04:35:42
	 * @return 树json格式字符串
	 */
	public String loadTree();
	
	/**
	 * @Desc：新增-保存数据，向XML新增一个节点
	 * @Author：张轮
	 * @Date：2013-12-12下午01:59:32
	 * @param menuName 菜单名称
	 * @param menuUrl 菜单URL
	 * @param menuRemark 备注
	 * @param menuId XML节点ID
	 */
	public void save(String menuName,String menuUrl,String menuRemark,String menuId);
	
	/**
	 * @Desc：修改数据，更新XML节点属性
	 * @Author：张轮
	 * @Date：2013-12-12下午01:59:32
	 * @param menuName 菜单名称
	 * @param menuUrl 菜单URL
	 * @param menuRemark 备注
	 * @param menuId XML节点ID
	 */
	public void update(String menuName,String menuUrl,String menuRemark,String menuId);
	
	/**
	 * @Desc：删除节点
	 * @Author：张轮
	 * @Date：2013-12-12下午02:35:09
	 * @param menuId XML节点ID
	 * @return 返回操作结果，1成功，0失败
	 */
	public int delete(String menuId);
	
	/**
	 * @Desc：移动节点，上下调整节点位置或调整节点所属层次关系。采用的逻辑是先将源节点复制一份至目标位置，然后将源节点删除
	 * @Author：张轮
	 * @Date：2013-12-12下午05:41:58
	 * @param menuId menuId XML节点ID
	 * @param targetMenuId 移动节点时，目标XML节点ID
	 * @param moveType 移动节点的类型。0移动到目标节点的内部、作为目标节点的子节点，1移动到目标节点上边，2移动到目标节点下边
	 * @return 返回操作结果，1成功，0失败
	 */
	public int move(String menuId,String targetMenuId,Integer moveType);
}
