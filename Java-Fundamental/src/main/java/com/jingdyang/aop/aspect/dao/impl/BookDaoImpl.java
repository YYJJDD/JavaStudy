package com.jingdyang.aop.aspect.dao.impl;


import com.jingdyang.aop.aspect.dao.BookDao;
import org.springframework.stereotype.Repository;

@Repository
public class BookDaoImpl implements BookDao {

    public String findName(int id, String password) {
        System.out.println("id:" + id);
//        if(true)throw new NullPointerException();
        return "itcast";
    }
}
