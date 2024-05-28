package org.fisco.bcos.asset.controller;

import cn.hutool.json.JSONArray;
import org.fisco.bcos.asset.service.impl.OfficeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/asset")
public class OfficeController {
    @Autowired
    private OfficeServiceImpl officeServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    @GetMapping("query")
    public JSONArray query() {
        officeServiceImpl.initialize();
        return officeServiceImpl.queryOfficeItems("house01");
    }

    @PostMapping("/remove")
    public void remove(String id) {
        officeServiceImpl.initialize();
        officeServiceImpl.removeOfficeItem("house01", new BigInteger(id));
    }

    @PostMapping("/record")
    public void record(String id, String item_name, String item_quantity,
                       String item_price, String item_sum_value, String receipt_date,
                       String receipt_department, String receipt_person, String approver,
                       String addition) {
        officeServiceImpl.initialize();
        officeServiceImpl.recordOfficeItem("house01", new BigInteger(id),
                item_name, new BigInteger(item_quantity), new BigInteger(item_price),
                new BigInteger(item_sum_value), receipt_date, receipt_department,
                receipt_person, approver, addition);
    }
}
