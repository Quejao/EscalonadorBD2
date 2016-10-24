/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

/**
 *
 * @author Leonardo
 */
public class infos {
    private int idOperaction;
    private int transactionIndex;
    private char operaction;
    private String dataItem;
    private String timeStamp;
    private int flag;

    public infos(int idOperaction, int transactionIndex, char operaction, String dataItem, String timeStamp, int flag) {
        this.idOperaction = idOperaction;
        this.transactionIndex = transactionIndex;
        this.operaction = operaction;
        this.dataItem = dataItem;
        this.timeStamp = timeStamp;
        this.flag = flag;
    }

    public infos(int transactionIndex, char operaction, String dataItem) {
        this.transactionIndex = transactionIndex;
        this.operaction = operaction;
        this.dataItem = dataItem;
    }

    public int getIdOperaction() {
        return idOperaction;
    }

    public void setIdOperaction(int idOperaction) {
        this.idOperaction = idOperaction;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public char getOperaction() {
        return operaction;
    }

    public void setOperaction(char operaction) {
        this.operaction = operaction;
    }

    public String getDataItem() {
        return dataItem;
    }

    public void setDataItem(String dataItem) {
        this.dataItem = dataItem;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
