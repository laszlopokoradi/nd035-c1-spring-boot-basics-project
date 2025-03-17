package com.udacity.jwdnd.course1.cloudstorage;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.apache.ibatis.type.*;
import org.mybatis.spring.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;


@SpringBootApplication
@MappedTypes(User.class)
@MapperScan("com.udacity.jwdnd.course1.cloudstorage.mapper")
public class CloudStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageApplication.class, args);
    }

}
