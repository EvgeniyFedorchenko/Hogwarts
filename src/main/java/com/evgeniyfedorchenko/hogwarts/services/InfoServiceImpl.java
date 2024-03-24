package com.evgeniyfedorchenko.hogwarts.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfoServiceImpl implements InfoService {

    @Value("${server.port}")
    private int port;

    @Override
    public Integer getUsedPort() {
        return port;
    }
}
