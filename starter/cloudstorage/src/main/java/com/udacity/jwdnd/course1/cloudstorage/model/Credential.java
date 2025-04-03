package com.udacity.jwdnd.course1.cloudstorage.model;


import lombok.*;
import lombok.experimental.*;


@Accessors(chain = true)
@Getter
@Setter
public class Credential {
    private Integer credentialId;
    private String url;
    private String username;
    private String plainPassword;
    private String password;
    private Integer userId;
}
