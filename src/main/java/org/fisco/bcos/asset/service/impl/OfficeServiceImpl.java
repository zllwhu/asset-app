package org.fisco.bcos.asset.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.fisco.bcos.asset.contract.Office_test;
import org.fisco.bcos.asset.service.OfficeService;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

@Service
public class OfficeServiceImpl implements OfficeService {
    static Logger logger = LoggerFactory.getLogger(OfficeServiceImpl.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    @Override
    public void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient(1);
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
    }

    @Override
    public String loadOfficeAddr() throws Exception {
        // load Office contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract_office.properties");
        prop.load(contractResource.getInputStream());
        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Office contract address failed, please deploy it first. ");
        }
        logger.info(" load Office address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    @Override
    public JSONArray queryOfficeItems(String warehouse) {
        JSONArray jsonArray = new JSONArray();
        try {
            String contractAddress = loadOfficeAddr();
            Office_test office_test = Office_test.load(contractAddress, client, cryptoKeyPair);
            List<Office_test.Struct0> result = office_test.select(warehouse);
            System.out.println("office items are as follows:");
            for (int i = 0; i < result.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.putOnce("id", result.get(i).id);
                jsonObject.putOnce("item_name", result.get(i).item_name);
                jsonObject.putOnce("item_quantity", result.get(i).item_quantity);
                jsonObject.putOnce("item_price", result.get(i).item_price);
                jsonObject.putOnce("item_sum_value", result.get(i).item_sum_value);
                jsonObject.putOnce("receipt_date", result.get(i).receipt_date);
                jsonObject.putOnce("receipt_department", result.get(i).receipt_department);
                jsonObject.putOnce("receipt_person", result.get(i).receipt_person);
                jsonObject.putOnce("approver", result.get(i).approver);
                jsonObject.putOnce("addition", result.get(i).addition);

                System.out.println("office item " + i);
                System.out.println(result.get(i).warehouse);
                System.out.println(result.get(i).id);
                System.out.println(result.get(i).item_name);
                System.out.println(result.get(i).item_quantity);
                System.out.println(result.get(i).item_price);
                System.out.println(result.get(i).item_sum_value);
                System.out.println(result.get(i).receipt_date);
                System.out.println(result.get(i).receipt_department);
                System.out.println(result.get(i).receipt_person);
                System.out.println(result.get(i).approver);
                System.out.println(result.get(i).addition);
                jsonArray.add(jsonObject);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryOfficeAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query office items failed, error message is %s\n", e.getMessage());
        }
        return jsonArray;
    }

    @Override
    public void recordOfficeItem(String warehouse, BigInteger id, String item_name, BigInteger item_quantity,
                                 BigInteger item_price, BigInteger item_sum_value, String receipt_date,
                                 String receipt_department, String receipt_person, String approver,
                                 String addition) {
        try {
            String contractAddress = loadOfficeAddr();

            Office_test office_test = Office_test.load(contractAddress, client, cryptoKeyPair);

            Office_test.Struct0 item = new Office_test.Struct0(warehouse, id, item_name, item_quantity, item_price
                    , item_sum_value, receipt_date, receipt_department, receipt_person, approver, addition);

            TransactionReceipt receipt = office_test.record(item);
            List<Office_test.InsertResultEventResponse> response = office_test.getInsertResultEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).count.compareTo(new BigInteger("1")) == 0) {
                    System.out.printf(
                            " record office item success => item: %s\n", item.item_name);
                } else {
                    System.out.printf(
                            " record office item failed, ret code is %s \n", response.get(0).count.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" recordOfficeItem exception, error message is {}", e.getMessage());
            System.out.printf(" record office item failed, error message is %s\n", e.getMessage());
        }
    }

    @Override
    public void removeOfficeItem(String warehouse, BigInteger id) {
        try {
            String contractAddress = loadOfficeAddr();

            Office_test office_test = Office_test.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = office_test.remove(warehouse, id);
            List<Office_test.RemoveResultEventResponse> response = office_test.getRemoveResultEvents(receipt);
            if (!response.isEmpty()) {
                if (response.get(0).count.compareTo(new BigInteger("1")) == 0) {
                    System.out.printf(
                            " remove office item success => item: %s\n", id);
                } else {
                    System.out.printf(
                            " remove office item failed, ret code is %s \n", response.get(0).count.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec. ");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" removeOfficeItem exception, error message is {}", e.getMessage());
            System.out.printf(" remove office item failed, error message is %s\n", e.getMessage());
        }
    }

}
