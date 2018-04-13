package com.lun.service;

/**
 * @Desc��������Ͳ˵���������ǰ̨��zTree���չʾ����̨��XML��������
 * @Author������
 * @Date��2013-12-11����02:34:36
 */
public interface IMenuService {

	/**
	 * @Desc����ȡXML���ݣ�����װ����json��ʽ�ַ�������zTreeǰ̨չʾ
	 * @Author������
	 * @Date��2013-12-11����04:35:42
	 * @return ��json��ʽ�ַ���
	 */
	public String loadTree();
	
	/**
	 * @Desc������-�������ݣ���XML����һ���ڵ�
	 * @Author������
	 * @Date��2013-12-12����01:59:32
	 * @param menuName �˵�����
	 * @param menuUrl �˵�URL
	 * @param menuRemark ��ע
	 * @param menuId XML�ڵ�ID
	 */
	public void save(String menuName,String menuUrl,String menuRemark,String menuId);
	
	/**
	 * @Desc���޸����ݣ�����XML�ڵ�����
	 * @Author������
	 * @Date��2013-12-12����01:59:32
	 * @param menuName �˵�����
	 * @param menuUrl �˵�URL
	 * @param menuRemark ��ע
	 * @param menuId XML�ڵ�ID
	 */
	public void update(String menuName,String menuUrl,String menuRemark,String menuId);
	
	/**
	 * @Desc��ɾ���ڵ�
	 * @Author������
	 * @Date��2013-12-12����02:35:09
	 * @param menuId XML�ڵ�ID
	 * @return ���ز��������1�ɹ���0ʧ��
	 */
	public int delete(String menuId);
	
	/**
	 * @Desc���ƶ��ڵ㣬���µ����ڵ�λ�û�����ڵ�������ι�ϵ�����õ��߼����Ƚ�Դ�ڵ㸴��һ����Ŀ��λ�ã�Ȼ��Դ�ڵ�ɾ��
	 * @Author������
	 * @Date��2013-12-12����05:41:58
	 * @param menuId menuId XML�ڵ�ID
	 * @param targetMenuId �ƶ��ڵ�ʱ��Ŀ��XML�ڵ�ID
	 * @param moveType �ƶ��ڵ�����͡�0�ƶ���Ŀ��ڵ���ڲ�����ΪĿ��ڵ���ӽڵ㣬1�ƶ���Ŀ��ڵ��ϱߣ�2�ƶ���Ŀ��ڵ��±�
	 * @return ���ز��������1�ɹ���0ʧ��
	 */
	public int move(String menuId,String targetMenuId,Integer moveType);
}
