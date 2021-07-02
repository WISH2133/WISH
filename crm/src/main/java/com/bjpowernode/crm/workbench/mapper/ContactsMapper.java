package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Contacts;

public interface ContactsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    int insert(Contacts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    int insertSelective(Contacts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    Contacts selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    int updateByPrimaryKeySelective(Contacts record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_contacts
     *
     * @mbggenerated Sat Jun 06 15:44:47 CST 2020
     */
    int updateByPrimaryKey(Contacts record);

    /**
    * 保存创建的联系人
    */
    int insertContacts(Contacts contacts);
}