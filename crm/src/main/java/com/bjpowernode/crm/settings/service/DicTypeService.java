package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.domain.DicType;

import java.util.List;

/**
 * 2021/6/9
 */
public interface DicTypeService {
    //查询所有字典类型dao(insert,delete,update,select)
    //业务（saveCreate,delete,saveEdit,query)
    List<DicType> queryAllDicTypes();
    //按字典类型编码查字典类型
    DicType queryDicTypeByCode(String code);
    //保存
    int saveCreateDicType(DicType dicType);
    //更新
    int saveEditDicType(DicType dicType);
    //删除
    int deleteDicTypeByCodes(String[] codes);
}
