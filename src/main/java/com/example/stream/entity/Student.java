package com.example.stream.entity;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zlf
 * @Description use for demo
 * @createTime 2019年05月15日 15:31:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = -665161200255985542L;
    private String id;
    private String name;
    private int age;
    private String pid;
    private List<Student> childStudents = CollUtil.newArrayList();


    public Student(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Student(String id, String name, int age, String pid) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

}
