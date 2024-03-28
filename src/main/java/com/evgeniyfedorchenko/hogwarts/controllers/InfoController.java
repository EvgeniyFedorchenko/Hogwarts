package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.services.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/info")
public class InfoController {

    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping(path = "/port")
    public Integer getUsedPort() {
        return infoService.getUsedPort();
    }

    @GetMapping(path = "/algo")
    public long optimizeAlgorithm() {
        return infoService.optimizeAlgorithm();
    }
}
