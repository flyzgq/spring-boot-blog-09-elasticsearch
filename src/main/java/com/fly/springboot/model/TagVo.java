package com.fly.springboot.model;

import java.io.Serializable;

/**
 * @author fly
 * @date 2018/6/2 22:18
 * @description
 **/
public class TagVo implements Serializable {

    private static final long serialVersionUID = -2904663786457613809L;

    private String name;

    private Long count;

    public TagVo(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
