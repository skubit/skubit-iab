package com.skubit.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LoginDto implements Dto {

    /**
     *
     */
    private static final long serialVersionUID = 1914921334887493435L;

    private String username;

    private String password;

    private String application;

    public LoginDto() {
    }

    public LoginDto(String username, String password, String application) {
        this.username = username;
        this.password = password;
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "application='" + application + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
