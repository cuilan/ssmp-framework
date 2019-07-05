package cn.cuilan.bmp.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private long id;

    private String realName;

    private String password;

    private int age;

    public User(String realName, String password, int age) {
        this.realName = realName;
        this.password = password;
        this.age = age;
    }
}
