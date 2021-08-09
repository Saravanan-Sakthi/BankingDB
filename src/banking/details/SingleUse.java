package banking.details;

import banking.BankingEngine;
import banking.databasemanagement.DatabaseUtil;

public enum SingleUse {
    object;
    public DataRecord record= new DataRecord();
    public BankingEngine engine= new BankingEngine();
    public DatabaseUtil db= new DatabaseUtil();
}
