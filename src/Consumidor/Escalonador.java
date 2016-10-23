/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

import Produtor.GerenciadorTransacao;
import Produtor.Schedule;
import Produtor.TransacaoDao;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonardo
 */
public class Escalonador extends Thread{
    Thread th;
    
    ArrayList<String> listaTransacao;
    ArrayList<String> dados;
    
    HashMap<String, Integer> componFnteDaLista;
    HashMap<String, EstadoDoDado> estadoDadoCorrente;
    
    LinkedList<ItemDado> filaTransacao;
    
    public static final String statusDadoDesbloqueado = "U";
    public static final String statusDadoBloqueadoExclusivo = "X";
    public static final String statusDadoBloqueadoCompartilhado = "S";
    
    public List<RecuperaInfo> informacoes = new ArrayList();
    private DaoConsumidor infoBD = new DaoConsumidor();
    private RecuperaInfo recupera = null;

    public Escalonador() {
        filaTransacao = new LinkedList<>();
        listaTransacao = new ArrayList<>();
        dados = new ArrayList<>();
        estadoDadoCorrente = new HashMap<>();
    }

    //solicitacaoBloqueio(tipoBloquio, indicetransacao, operacao)
    public void solicitacaoBloqueio(String status, String transacao, String dado) throws SQLException {
        if (status.equals(statusDadoBloqueadoCompartilhado)) {
            lockS(dado, transacao);
        } else {
            lockX(dado, transacao);
        }
    }

    //solicitacaoDesbloqueio(indicetransacao, operacao)
    public void solicitacaoDesbloqueio(String transacao, String dado) throws SQLException {
        if (!dado.equals("infinito")) {
            if (estadoDadoCorrente.get(dado).getEstado() == 2) {
                estadoDadoCorrente.get(dado).setEstado(0);
                despertarFila(dado);
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
                listaTransacao.remove(dado);
                if (listaTransacao.isEmpty()) {
                    estadoDadoCorrente.get(dado).setEstado(0);
                    despertarFila(dado);
                }
            }
        }
    }

    //despertarFila(operacao)
    public void despertarFila(String dado) throws SQLException {
        if (dado.equals("")) {
            for (int i = 0; i < filaTransacao.size(); i++) {
                ItemDado j = filaTransacao.get(i);
                if (j.getEstado() == 1) {
                    filaTransacao.remove(i);
                    solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, j.getTransacao(), j.getDado());
                }
                if (j.getEstado() == 2) {
                    filaTransacao.remove(i);
                    solicitacaoBloqueio(statusDadoBloqueadoExclusivo, j.getTransacao(), j.getDado());
                }
            }
        }
        else {
            for (ItemDado i : filaTransacao) {
                if (i.getDado().equals(dado)) {
                    System.out.println("Despertando " + dado);
                    if (i.getEstado() == 1) {
                        filaTransacao.remove(i);
                        solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, i.getTransacao(), i.getDado());
                    }
                    if (i.getEstado() == 2) {
                        filaTransacao.remove(i);
                        solicitacaoBloqueio(statusDadoBloqueadoExclusivo, i.getTransacao(), i.getDado());
                    }
                }
            }
        }
    }

    //solicitacaoBloqueioCompartilhado(operacao, indicetransacao)
    public void lockS(String dado, String transacao) throws SQLException {
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
                infoBD.insereTabela(recupera);
                
                listaTransacao.add(transacao);
                
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
            infoBD.insereTabela(recupera);

            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
                infoBD.insereTabela(recupera);
                
                listaTransacao.add(transacao);
            }
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
            infoBD.insereTabela(recupera);
            
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);
        }
        else {
            ItemDado novoItemDaFila = new ItemDado(1, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
    }

    //solicitacaoBloqueioExclusivo(operacao, indicetransacao)
    public void lockX(String dado, String transacao) throws SQLException {
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'W', dado);
            infoBD.insereTabela(recupera);
            
            listaTransacao.add(transacao);
            
            estadoDadoCorrente.get(dado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 2 && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)){
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'W', dado);
            infoBD.insereTabela(recupera);

            listaTransacao.add(transacao);

            estadoDadoCorrente.get(dado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 1 && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)){
            ItemDado novoItemDaFila = new ItemDado(2, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
        else {
            ItemDado novoItemDaFila = new ItemDado(2, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
    }

    @Override
    public void run(){
        List<RecuperaInfo> informacao = null;
        try {
            informacao = infoBD.ConsumoLote();
        } catch (SQLException ex) {
            Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> itemDado = null;
        try {
            itemDado = infoBD.ItemDado();
        } catch (SQLException ex) {
            Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
        }

        for(int i=0; i < itemDado.size(); i++){
            dados.add(itemDado.get(i));
        }

        EstadoDoDado item = new EstadoDoDado("", 0);
        for (String dado : dados) {
            estadoDadoCorrente.put(dado, item);
        }

        for (int j=0; j < informacao.size(); j++) {
            if ("R".equals(String.valueOf(informacao.get(j).getOperacao()))){
                try {
                    solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if ("W".equals(String.valueOf(informacao.get(j).getOperacao()))){
                try {
                    solicitacaoBloqueio(statusDadoBloqueadoExclusivo, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if ("E".equals(String.valueOf(informacao.get(j).getOperacao()))){
                try {
                    solicitacaoDesbloqueio(String.valueOf(informacao.get(j).getIndiceTransacao()), "infinito");
                    verificarFila();
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void verificarFila() throws SQLException {
        ItemDado first;

        for (int i = 0; i < filaTransacao.size(); i++) {
            first = filaTransacao.get(i);

            if (estadoDadoCorrente.get(first.getDado()).getEstado() == 0) {
                switch (first.getEstado()) {
                    case 1:
                        solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, first.getTransacao(), first.getDado());
                        break;
                    case 2:
                        solicitacaoBloqueio(statusDadoBloqueadoExclusivo, first.getTransacao(), first.getDado());
                        break;
                }
            }
        }
    }

    @Override
    public void start() {
	if (th == null) {
            th = new Thread (this);
            th.start ();
        }
    }
}
