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
public class MainConsumidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Escalonador teste = new Escalonador();
        DaoConsumidor daoTeste = new DaoConsumidor();
        while(daoTeste.ItemDadoNEscalonado()){
            teste.escalonar();
        }
    }
    
}
