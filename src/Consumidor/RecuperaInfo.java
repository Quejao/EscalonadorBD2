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
public class RecuperaInfo {
    private int idOperacao;
    private int indiceTransacao;
    private char operacao;
    private String itemDado;
    private String timeStamp;
    private int flag;

    public RecuperaInfo(int idOperacao, int indiceTransacao, char operacao, String itemDado, String timeStamp, int flag) {
        this.idOperacao = idOperacao;
        this.indiceTransacao = indiceTransacao;
        this.operacao = operacao;
        this.itemDado = itemDado;
        this.timeStamp = timeStamp;
        this.flag = flag;
    }

    public RecuperaInfo(int indiceTransacao, char operacao, String itemDado) {
        this.indiceTransacao = indiceTransacao;
        this.operacao = operacao;
        this.itemDado = itemDado;
    }

    public int getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(int idOperacao) {
        this.idOperacao = idOperacao;
    }

    public int getIndiceTransacao() {
        return indiceTransacao;
    }

    public void setIndiceTransacao(int indiceTransacao) {
        this.indiceTransacao = indiceTransacao;
    }

    public char getOperacao() {
        return operacao;
    }

    public void setOperacao(char operacao) {
        this.operacao = operacao;
    }

    public String getItemDado() {
        return itemDado;
    }

    public void setItemDado(String itemDado) {
        this.itemDado = itemDado;
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
