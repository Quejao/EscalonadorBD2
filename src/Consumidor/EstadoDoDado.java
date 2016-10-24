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
public class EstadoDoDado {
    private String transaction;
    private int state;

    public EstadoDoDado(String transaction, int state) {
        this.transaction = transaction;
        this.state = state;
    }

    public EstadoDoDado() {
    }
    
    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
}
