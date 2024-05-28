package org.fisco.bcos.asset.service;

import cn.hutool.json.JSONArray;

import java.math.BigInteger;

public interface OfficeService {
    public void initialize();
    public String loadOfficeAddr() throws Exception;
    public JSONArray queryOfficeItems(String warehouse);
    public void recordOfficeItem(String warehouse, BigInteger id, String item_name, BigInteger item_quantity,
                                 BigInteger item_price, BigInteger item_sum_value, String receipt_date,
                                 String receipt_department, String receipt_person, String approver,
                                 String addition);
    public void removeOfficeItem(String warehouse, BigInteger id);
}
