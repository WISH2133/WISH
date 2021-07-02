package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.mapper.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.stereotype.Service;

import javax.swing.text.DefaultEditorKit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2021/6/18
 */
@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ContactsMapper contactsMapper;


    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;


    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;


    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private TranRemarkMapper tranRemarkMapper;




    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    /*
    （1）获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
	tbl_clue
（2）通过线索对象提取客户信息
    tbl_clue和tbl_customer
（3）通过线索对象提取联系人信息，保存联系人
      tbl_clue和tbl_contacts
（4） 线索备注转换到客户备注以及联系人备注
	tbl_clue_remark转到tbl_customer_remark和tbl_contacts_remark
（5）“线索和市场活动”的关系转换到“联系人和市场活动”的关系
   tbl_clue_activity_relation，tbl_contacts_activity_relation
（6）如果有创建交易需求，创建一条交易
	tbl_tran

（7）如果创建了交易，则创建一条该交易下的交易历史
    tbl_tran_history
（8） 删除线索备注
（9）删除线索和市场活动的关系
（10）删除线索
     */
    @Override
    public void saveConvert(Map<String, Object> map) {
        User user=(User)map.get("sessionUser");
        String isCreateTran=(String)map.get("isCreateTran");

        //1)获取到线索id，通过线索id获取线索对象
        String clueId=(String)map.get("clueId");
        Clue clue=clueMapper.selectClueById(clueId);

        //2)通过线索对象提取客户信息
        Customer customer=new Customer();
        customer.setId(UUIDUtils.getUUID());
        customer.setOwner(user.getId());
        //客户表是与公司相关
        customer.setName(clue.getCompany());
        customer.setPhone(clue.getPhone());
        customer.setWebsite(clue.getWebsite());
        //customer对象在表中插入的时间，修改时间
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtils.formatDateTime(new Date()));
        customer.setAddress(clue.getAddress());
        customer.setDescription(clue.getDescription());
        customer.setContactSummary(clue.getContactSummary());
        customer.setNextContactTime(clue.getNextContactTime());

        //插入客户
        customerMapper.insertCustomer(customer);

        //3)通过线索对象提取联系人信息，保存联系人
        Contacts contacts=new Contacts();
        contacts.setId(UUIDUtils.getUUID());
        contacts.setOwner(user.getId());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());
        contacts.setFullName(clue.getFullName());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));

        contactsMapper.insertContacts(contacts);

        //4)线索备注转换到客户备注以及联系人备注
        //线索备注
        List<ClueRemark> clueRemarkList=clueRemarkMapper.selectClueRemarkByClueId(clueId);
        //通过循环将clueRemarkList中的每一个ClueRemark，插入到tbl_contacts和tbl_customer
        if(clueRemarkList!=null&& clueRemarkList.size()>0){
            CustomerRemark cur=null;
            ContactsRemark cor=null;

            List<CustomerRemark> curList=new ArrayList<>();
            List<ContactsRemark> corList=new ArrayList<>();

            for(ClueRemark cr:clueRemarkList){
                ///客户备注
                cur=new CustomerRemark();
                cur.setId(UUIDUtils.getUUID()); //每个表都有自己的主键
                cur.setNoteContent(cr.getNoteContent());
                cur.setCreateBy(cr.getCreateBy());
                cur.setCreateTime(cr.getCreateTime());
                cur.setEditBy(cr.getEditBy());
                cur.setEditTime(cr.getEditTime());
                cur.setEditFlag(cr.getEditFlag());
                cur.setCustomerId(customer.getId());

                curList.add(cur);

                //联系人备注
                cor=new ContactsRemark();
                cor.setId(UUIDUtils.getUUID()); //每个表都有自己的主键
                cor.setNoteContent(cr.getNoteContent());
                cor.setCreateBy(cr.getCreateBy());
                cor.setCreateTime(cr.getCreateTime());
                cor.setEditBy(cr.getEditBy());
                cor.setEditTime(cr.getEditTime());
                cor.setEditFlag(cr.getEditFlag());
                cor.setContactsId(contacts.getId());

                corList.add(cor);

            }
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            contactsRemarkMapper.insertContactsRemarkByList(corList);

        }

        //5.“线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> carList=clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);
        if(carList!=null&&carList.size()>0){
            ContactsActivityRelation coar=null;
            List<ContactsActivityRelation> coarList=new ArrayList<>();
            for(ClueActivityRelation car:carList){
                 coar=new ContactsActivityRelation();
                 coar.setId(UUIDUtils.getUUID());
                 coar.setActivityId(car.getActivityId());
                 coar.setContactsId(contacts.getId());

                 coarList.add(coar);
            }

            contactsActivityRelationMapper.insertContactsActivityRelationByList(coarList);
        }

        //6.如果有创建交易需求，创建一条交易
        if("true".equals(isCreateTran)){
            Tran tran=new Tran();
            tran.setId(UUIDUtils.getUUID());
            tran.setOwner(user.getId());
            tran.setMoney((String)map.get("amountOfMoney"));
            tran.setName((String)map.get("tradeName"));
            tran.setExpectedDate((String)map.get("expectedClosingDate"));
            tran.setCustomerId(customer.getId());
            tran.setStage((String)map.get("stage"));
            //其它的属性自己完成下
            tranMapper.insert(tran);

            //将线锁备注转到交易备注
            if(clueRemarkList!=null&&clueRemarkList.size()>0){
                TranRemark tr=null;
                List<TranRemark> trList=new ArrayList<>();
                for(ClueRemark cr:clueRemarkList){
                    tr=new TranRemark();
                    tr.setId(UUIDUtils.getUUID());
                    tr.setNoteContent(cr.getNoteContent());
                    tr.setCreateBy(cr.getCreateBy());
                    tr.setCreateTime(cr.getCreateTime());

                    trList.add(tr);
                }

                tranRemarkMapper.insertTranRemarkByList(trList);
            }

        }

        /*
        （8） 删除线索备注
        （9）删除线索和市场活动的关系
        （10）删除线索
         */
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        clueMapper.deleteClueById(clueId);



    }
}
