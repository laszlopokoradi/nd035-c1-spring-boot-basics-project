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

    public File getFileById(Integer fileId) {
        return fileMapper.getFileById(fileId);
    }

    public File[] getFilesByUserId(Integer userId) {
        return fileMapper.getFilesByUserId(userId);
    }

    public File[] getFilesByUsername(String username) {
        return fileMapper.getFilesByUserName(username);
    }

    public Integer deleteFile(Integer fileId) {
        return fileMapper.deleteFile(fileId);
    }

    public boolean isFileNameInUse(Integer userId, String fileName) {
        File file = fileMapper.getFileByUserFileName(userId, fileName);
        return file != null;
    }
}
