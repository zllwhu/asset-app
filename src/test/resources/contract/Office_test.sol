pragma solidity >=0.4.24 <0.6.11;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract Office_test {
    struct Item {
        string warehouse;
        int256 id;
        string item_name;
        int256 item_quantity;
        int256 item_price;
        int256 item_sum_value;
        string receipt_date;
        string receipt_department;
        string receipt_person;
        string approver;
        string addition;
    }

    // event
    event InsertResult(int256 count);
    event RemoveResult(int256 count);

    TableFactory tableFactory;
    string constant TABLE_NAME = "t_office_test";
    constructor() public {
        tableFactory = TableFactory(0x1001);
        // 办公资产领用登记表t_office
        // key: warehouse
        // field: id, item_name, item_quantity, item_price, item_sum_value,
        //        receipt_date, receipt_department, receipt_person, approver, addition
        tableFactory.createTable(
            TABLE_NAME,
            "warehouse",
            "id,item_name,item_quantity,item_price,item_sum_value,receipt_date,receipt_department,receipt_person,approver,addition"
        );
    }

    /*
     * 根据资产仓库查询资产领用情况
     * 参数：warehouse
     * 返回：Item[]
    */
    function select(string memory warehouse) public view returns (Item[] memory) {
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        Entries entries = table.select(warehouse, condition);
        Item[] memory items = new Item[](uint256(entries.size()));
        for (int256 i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            items[uint256(i)].id = entry.getInt("id");
            items[uint256(i)].item_name = entry.getString("item_name");
            items[uint256(i)].item_quantity = entry.getInt("item_quantity");
            items[uint256(i)].item_price = entry.getInt("item_price");
            items[uint256(i)].item_sum_value = entry.getInt("item_sum_value");
            items[uint256(i)].receipt_date = entry.getString("receipt_date");
            items[uint256(i)].receipt_department = entry.getString("receipt_department");
            items[uint256(i)].receipt_person = entry.getString("receipt_person");
            items[uint256(i)].approver = entry.getString("approver");
            items[uint256(i)].addition = entry.getString("addition");
        }
        return items;
    }

    /*
     * 资产领用登记
     * 参数：item
     * 返回：status_code
    */
    function record(Item item)
    public returns(int256) {
        Table table = tableFactory.openTable(TABLE_NAME);
        Entry entry = table.newEntry();
        entry.set("warehouse", item.warehouse);
        entry.set("id", item.id);
        entry.set("item_name", item.item_name);
        entry.set("item_quantity", item.item_quantity);
        entry.set("item_price", item.item_price);
        entry.set("item_sum_value", item.item_sum_value);
        entry.set("receipt_date", item.receipt_date);
        entry.set("receipt_department", item.receipt_department);
        entry.set("receipt_person", item.receipt_person);
        entry.set("approver", item.approver);
        entry.set("addition", item.addition);
        int256 count = table.insert(item.warehouse, entry);
        if (count == 1) {
            emit InsertResult(count);
            return 0;
        } else {
            return -1;
        }
    }

    /*
     * 资产归还登记
     * 参数：warehouse, id
     * 返回：status_code
    */
    function remove(string memory warehouse, int256 id) public returns (int256) {
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        condition.EQ("warehouse", warehouse);
        condition.EQ("id", id);
        int256 count = table.remove(warehouse, condition);
        if (count == 1) {
            emit RemoveResult(count);
            return 0;
        } else {
            return -1;
        }
    }
}
