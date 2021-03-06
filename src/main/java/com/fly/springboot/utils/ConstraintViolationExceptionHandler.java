package com.fly.springboot.utils;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fly
 * @date 2018/5/13 22:05
 * @description     ConstraintViolationExceptionHandler 处理器 处理实体类的异常
 **/
public class ConstraintViolationExceptionHandler {

    /**
     * 获取批量异常信息
     * @param e
     * @return
     */
    public static String getMessage(ConstraintViolationException e){
        List<String> msgList = new ArrayList<String>();
        for(ConstraintViolation<?> constraintViolation : e.getConstraintViolations()){
            msgList.add(constraintViolation.getMessage());
        }
        String messages = StringUtils.join(msgList.toArray(), ";");
        return messages;
    }
}
