package com.kangjj.lib.bean;

import com.kangjj.lib.annotation.DbField;
import com.kangjj.lib.annotation.DbTable;

@DbTable("tb_user")
public class User {
    @DbField("u_id")
    private Integer id;
    private String name;
    private String password;
    private Integer Status;

    public User() {
    }

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", Status=" + Status +
                '}';
    }
}
