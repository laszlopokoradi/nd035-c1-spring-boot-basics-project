package com.udacity.jwdnd.course1.cloudstorage.model;


import lombok.*;
import lombok.experimental.*;


@Accessors(chain = true)
@Getter
@Setter
public class Note {
    private Integer noteId;
    private String noteTitle;
    private String noteDescription;
    private User user;
}
