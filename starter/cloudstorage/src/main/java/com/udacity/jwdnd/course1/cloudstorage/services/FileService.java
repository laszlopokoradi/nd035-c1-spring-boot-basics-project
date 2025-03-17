package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.springframework.stereotype.*;


@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public Integer uploadFile(File file) {
        return fileMapper.insert(file);
    }

    public File getFile(Integer fileId) {
        return fileMapper.getFileById(fileId);
    }

    public File[] getFilesForUser(User user) {
        return fileMapper.getFilesByUserId(user.getUserId());
    }
}
