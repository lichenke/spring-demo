package com.babyblue.demo.service.impl;


import com.babyblue.demo.service.IModifyService;
import com.babyblue.framework.annotation.GPService;

/**
 * 增删改业务
 *
 * @author Tom
 */
@GPService
public class ModifyService implements IModifyService {

    /**
     * 增加
     */
    public String add(String name, String addr) {

        return "add name=" + name + ",addr=" + addr;
    }

    /**
     * 修改
     */
    public String edit(Integer id, String name) {
        return "edit id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    public String remove(Integer id) {
        return "remove id=" + id;
    }

}
