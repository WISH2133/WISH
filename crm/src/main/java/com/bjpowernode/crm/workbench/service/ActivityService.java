package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    int saveCreateActivity(Activity activity);

    List<Activity> queryActivityForPageByCondition(Map<String, Object> map);

    long queryCountOfActivityByCondition(Map<String, Object> map);

    Activity queryActivityById(String id);

    int saveEditActivity(Activity activity);

    int deleteActivityByIds(String[] ids);

    List<Activity> queryAllActivityForDetail();

    List<Activity> queryActivityForDetailByIds(String[] ids);

    int saveCreateActivityByList(List<Activity> activityList);

    Activity queryActivityForDetailById(String id);

    List<Activity> queryActivityForDetailByName(String name);

    //根据线索去查询与该线索相关的市场活动
    List<Activity> queryActivityForDetailByClueId(String clueId);

    //根据线索查询与线索无关的市场活动
    List<Activity> searchActivityNoBoundById(Map<String,Object> map);


}
