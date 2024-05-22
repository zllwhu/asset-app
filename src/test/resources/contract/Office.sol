pragma solidity >=0.4.24 <0.6.11;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract Office {
    // event
    event InsertResult(int256 count);
    event RemoveResult(int256 count);

    TableFactory tableFactory;
    string constant TABLE_NAME = "t_office";
    constructor() public {
        tableFactory = TableFactory(0x1001);
        // 办公资产领用登记表t_office
        // key: warehouse
        // field: id, item_name, receipt_date, receipt_person
        tableFactory.createTable(
            TABLE_NAME,
            "warehouse",
            "id,item_name,receipt_date,receipt_person"
        );
    }

    /*
     * 根据资产仓库查询资产领用情况
     * 参数：warehouse
     * 返回：id, item_name, receipt_date, receipt_person
    */
    function select(string memory warehouse) public view returns
    (int256[] memory, string[] memory, string[] memory, string[] memory) {
        Table table = tableFactory.openTable(TABLE_NAME);
        Condition condition = table.newCondition();
        Entries entries = table.select(warehouse, condition);
        int256[] memory id_list = new int256[](uint256(entries.size()));
        string[] memory item_name_list = new string[](uint256(entries.size()));
        string[] memory receipt_date_list = new string[](uint256(entries.size()));
        string[] memory receipt_person_list = new string[](uint256(entries.size()));
        for (int256 i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            id_list[uint256(i)] = entry.getInt("id");
            item_name_list[uint256(i)] = entry.getString("item_name");
            receipt_date_list[uint256(i)] = entry.getString("receipt_date");
            receipt_person_list[uint256(i)] = entry.getString("receipt_person");
        }
        return (id_list, item_name_list, receipt_date_list, receipt_person_list);
    }

    /*
     * 资产领用登记
     * 参数：warehouse, id, item_name, receipt_date, receipt_person
     * 返回：status_code
    */
    function record(string memory warehouse, int256 id, string memory item_name, string memory receipt_date, string memory receipt_person)
    public returns(int256) {
        Table table = tableFactory.openTable(TABLE_NAME);
        Entry entry = table.newEntry();
        entry.set("warehouse", warehouse);
        entry.set("id", id);
        entry.set("item_name", item_name);
        entry.set("receipt_date", receipt_date);
        entry.set("receipt_person", receipt_person);
        int256 count = table.insert(warehouse, entry);
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
