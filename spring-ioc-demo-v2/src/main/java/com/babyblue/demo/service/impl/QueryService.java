package com.babyblue.demo.service.impl;


import com.babyblue.demo.service.IModifyService;
import com.babyblue.demo.service.IQueryService;
import com.babyblue.framework.annotation.GPAutowired;
import com.babyblue.framework.annotation.GPService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 *
 * @author Tom
 */
@GPService
public class QueryService implements IQueryService {

    @GPAutowired
    private IModifyService modifyService;

    /**
     * 查询
     */
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        return "{name:\"" + name + "\",time:\"" + time + "\"}";
    }
}
