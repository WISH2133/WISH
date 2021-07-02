package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.apache.logging.log4j.core.pattern.RelativeTimePatternConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 2021/6/18
 */
@Controller
public class ClueController {

    @Autowired
    private UserService userService;

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ClueService clueService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(Model model){
        //所有者
        List<User> userList=userService.queryAllUsers();
        //线索状态
        List<DicValue> clueStateList=dicValueService.queryDicValueByTypeCode("clueState");
        //线索来源
        List<DicValue> sourceList=dicValueService.queryDicValueByTypeCode("source");
        //称呼
        List<DicValue> appellationList=dicValueService.queryDicValueByTypeCode("appellation");

        model.addAttribute("userList",userList);
        model.addAttribute("clueStateList",clueStateList);
        model.addAttribute("sourceList",sourceList);
        model.addAttribute("appellationList",appellationList);

        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/saveCreateClue.do")
    public @ResponseBody Object saveCreateClue(Clue clue, HttpSession session){
        User user=(User)session.getAttribute(Contants.SESSION_USER);
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateBy(user.getId());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));

        ReturnObject returnObject=new ReturnObject();

        int ret=clueService.saveCreateClue(clue);

        if(ret>0){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/detailClue.do")
    public String detailClue(String id,Model model){
        //线索详情
        Clue clue=clueService.queryClueForDetailById(id);
        //与当前线索相关的市场活动
        List<Activity> activityList=activityService.queryActivityForDetailByClueId(id);


        model.addAttribute("clue",clue);
        model.addAttribute("activityList",activityList);
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/searchActivityNoBoundById.do")
    public @ResponseBody Object searchActivityNoBoundById(String activityName,String clueId){

        Map<String,Object> map=new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        List<Activity> activityList=activityService.searchActivityNoBoundById(map);

        return activityList;

    }

    //线索去关联市场活动
    @RequestMapping("/workbench/clue/saveBundActivity.do")
    public @ResponseBody Object saveBundActivity(String clueId,String[] activityId){

        ClueActivityRelation relation=null; //线索和市场活动的对象
        List<ClueActivityRelation> relationList=new ArrayList<>();

        for(String ai:activityId){
            relation=new ClueActivityRelation();
            relation.setId(UUIDUtils.getUUID());
            relation.setClueId(clueId);
            relation.setActivityId(ai);
            relationList.add(relation);
        }
        ReturnObject returnObject=new ReturnObject();
        int ret=clueActivityRelationService.saveCreateClueActivityRelationByList(relationList);
        if(ret>0){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            //查询activityId所关联市场活动
            List<Activity> activityList=activityService.queryActivityForDetailByIds(activityId);
            returnObject.setRetData(activityList); //data.retData
        }else{
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
        }


        return returnObject;

    }
    //删除关联关系
    @RequestMapping("/workbench/clue/saveUnbundActivity.do")
    public @ResponseBody Object saveUnbundActivity(ClueActivityRelation relation){

        ReturnObject returnObject=new ReturnObject();

        int ret=clueActivityRelationService.deleteClueActivityRelationByClueActivityId(relation);

        if(ret>0){
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }

        return returnObject;
    }

    //跳到转换页面
    @RequestMapping("/workbench/clue/convertClue.do")
    public String convertClue(String id,Model model){
        Clue clue=clueService.queryClueForDetailById(id);
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");

        model.addAttribute("clue", clue);
        model.addAttribute("stageList", stageList);

        return "workbench/clue/convert";
    }

    //转换线索工作
    @RequestMapping("/workbench/clue/saveConvertClue.do")
    public  @ResponseBody Object saveConvertClue(String clueId,String isCreateTran,String amountOfMoney,String tradeName,String expectedClosingDate,String stage,String activityId,HttpSession session){

        //Map封装参数
        Map<String,Object> map=new HashMap<>();
        map.put("clueId",clueId);
        map.put("isCreateTran",isCreateTran);
        map.put("amountOfMoney",amountOfMoney);
        map.put("tradeName",tradeName);
        map.put("expectedClosingDate",expectedClosingDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("sessionUser",session.getAttribute(Contants.SESSION_USER));

        ReturnObject returnObject=new ReturnObject();

        try{
            //调用业务层
            clueService.saveConvert(map);

            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace(); //a-b-c-d c() exception
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("转换失败");
        }
        return returnObject;
    }




}
