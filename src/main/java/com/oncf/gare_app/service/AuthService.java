package com.oncf.gare_app.service;

import com.oncf.gare_app.dto.ChangePasswordRequest;
import com.oncf.gare_app.dto.LoginRequest;
import com.oncf.gare_app.dto.LoginResponse;
import com.oncf.gare_app.dto.UtilisateurResponse;

public interface AuthService {
    LoginResponse authenticate(LoginRequest loginRequest);
    UtilisateurResponse getCurrentUserProfile();
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logout();
}
