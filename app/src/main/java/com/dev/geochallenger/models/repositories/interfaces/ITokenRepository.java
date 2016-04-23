package com.dev.geochallenger.models.repositories.interfaces;

/**
 * Created by Yuriy Diachenko on 23.04.2016.
 */
public interface ITokenRepository {

    void setToken(String token);

    String getToken();

    void clearToken();
}
