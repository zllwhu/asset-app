package org.fisco.bcos.asset.client;

import org.fisco.bcos.asset.contract.Office_test;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

public class OfficeTestClient {
    static Logger logger = LoggerFactory.getLogger(OfficeTestClient.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    public void initialize() throws Exception {
        @SuppressWarnings("resource")
        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient(1);
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
    }

    public void deployOfficeAndRecordAddr() {

        try {
            Office_test office_test = Office_test.deploy(client, cryptoKeyPair);
            System.out.println(
                    " deploy Office success, contract address is " + office_test.getContractAddress());

            recordOfficeAddr(office_test.getContractAddress());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(" deploy Office contract failed, error message is  " + e.getMessage());
        }
    }

    public void recordOfficeAddr(String address) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract_office.properties");
        FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
        prop.store(fileOutputStream, "contract address");
    }

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

    public void queryOfficeItems(String warehouse) {
        try {
            String contractAddress = loadOfficeAddr();
            Office_test office_test = Office_test.load(contractAddress, client, cryptoKeyPair);
            List<Office_test.Struct0> result = office_test.select(warehouse);
            System.out.println("office items are as follows:");
            for (int i = 0; i < result.size(); i++) {
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
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryOfficeAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query office items failed, error message is %s\n", e.getMessage());
        }
    }

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

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.OfficeClient deploy");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient query warehouse");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient record warehouse id item_name receipt_date receipt_person");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient remove warehouse id");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            Usage();
        }

        OfficeTestClient client = new OfficeTestClient();
        client.initialize();

        switch (args[0]) {
            case "deploy":
                client.deployOfficeAndRecordAddr();
                break;
            case "query":
                if (args.length < 2) {
                    Usage();
                }
                client.queryOfficeItems(args[1]);
                break;
            case "record":
                if (args.length < 12) {
                    Usage();
                }
                client.recordOfficeItem(args[1], new BigInteger(args[2]), args[3], new BigInteger(args[4]),
                        new BigInteger(args[5]), new BigInteger(args[6]), args[7], args[8], args[9], args[10], args[11]);
                break;
            case "remove":
                if (args.length < 2) {
                    Usage();
                }
                client.removeOfficeItem(args[1], new BigInteger(args[2]));
                break;
            default: {
                Usage();
            }
        }
        System.exit(0);
    }
}
