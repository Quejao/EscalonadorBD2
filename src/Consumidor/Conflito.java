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
public class Conflito {
    String blockTransaction;
    String dependentTransaction;

    public String getBlockTransaction() {
        return blockTransaction;
    }

    public void setBlockTransaction(String blockTransaction) {
        this.blockTransaction = blockTransaction;
    }

    public String getDependentTransaction() {
        return dependentTransaction;
    }

    public void setDependentTransaction(String dependentTransaction) {
        this.dependentTransaction = dependentTransaction;
    }

    public Conflito(String blockTransaction, String dependentTransaction) {
        this.blockTransaction = blockTransaction;
        this.dependentTransaction = dependentTransaction;
    }
}
