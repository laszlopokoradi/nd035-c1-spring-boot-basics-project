package com.udacity.jwdnd.course1.cloudstorage.model;


import lombok.*;
import lombok.experimental.*;


@Accessors(chain = true)
@Getter
@Setter
public class File {
    private Integer fileId;
    private String filename;
    private String contentType;
    private String fileSize;
    private Integer userId;
    private byte[] fileData;

    public File(String filename, String contentType, String fileSize, Integer userId, byte[] fileData) {
        this.filename = filename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.userId = userId;
        this.fileData = fileData;
    }
}
