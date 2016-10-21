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
    private String transacao;
    private int estado;

    public EstadoDoDado(String transacao, int estado) {
        this.transacao = transacao;
        this.estado = estado;
    }

    public EstadoDoDado() {
    }
    
    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
}
