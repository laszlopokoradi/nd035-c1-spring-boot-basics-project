package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.springframework.stereotype.*;


@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public Integer createCredential(Credential credential) {
        return credentialMapper.insert(credential);
    }

    public Integer updateCredential(Credential credential) {
        return credentialMapper.updateCredential(credential);
    }

    public Credential getCredentialById(Integer credentialId) {
        return credentialMapper.getCredentialById(credentialId);
    }

    public Credential[] getCredentialsByUserId(Integer userId) {
        return credentialMapper.getCredentialsByUserId(userId);
    }

    public Credential[] getCredentialsByUsername(String userName) {
        return credentialMapper.getCredentialsByUsername(userName);
    }

    public Integer deleteCredential(Integer credentialId) {
        return credentialMapper.deleteCredentialById(credentialId);
    }

    public Credential[] getDecryptedCredentialsByUser(User user) {
        Credential[] credentials = credentialMapper.getCredentialsByUserId(user.getUserId());
        for (Credential credential : credentials) {
            // Assumes Credential has a transient plainPassword property.
            String decryptedPassword = encryptionService.decryptValue(credential.getPassword(), user.getSalt());
            credential.setPlainPassword(decryptedPassword);
        }
        return credentials;
    }
}
