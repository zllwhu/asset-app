package org.fisco.bcos.asset.controller;

import org.fisco.bcos.asset.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/asset")
public class AssetController {
    @Autowired
    private AssetService assetService;

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
}
