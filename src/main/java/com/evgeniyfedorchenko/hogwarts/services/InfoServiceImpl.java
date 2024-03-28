package com.evgeniyfedorchenko.hogwarts.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
public class InfoServiceImpl implements InfoService {

    @Value("${server.port}")
    private int port;

    @Override
    public Integer getUsedPort() {
        return port;
    }

    @Override
    public long optimizeAlgorithm() {

        long n = 1_000_000;
        return n * (n + 1) / 2;

//        return IntStream.rangeClosed(1, 1_000_000).sum();

    }

}
