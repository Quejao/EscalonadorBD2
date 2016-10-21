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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Leonardo
 */
public class Escalonador {
    ArrayList<String> transactionList;
    ArrayList<String> data;
    LinkedList<ItemDado> transactionRow;
    HashMap<String, Integer> listComponent;
    HashMap<String, EstadoDoDado> currentDataState;
    public List<RecuperaInfo> infos = new ArrayList();
            
    public static final String statusSharedLockedData = "S";
    public static final String statusExclusiveLockedData = "X";
    public static final String statusUnlockedData = "U";
    
    private DaoConsumidor infoDB = new DaoConsumidor();
    private RecuperaInfo recupera = null;

    public Escalonador() {
        transactionRow = new LinkedList<>();
        transactionList = new ArrayList<>();
        data = new ArrayList<>();
        currentDataState = new HashMap<>();
    }

    public void wakeRow(String dado) {
        if (dado.equals("")) {
            for (int i = 0; i < transactionRow.size(); i++) {
                ItemDado j = transactionRow.get(i);
                if (j.getEstado() == 1) {
                    transactionRow.remove(i);
                    LockRequest(statusSharedLockedData, j.getTransacao(), j.getDado());
                }
                if (j.getEstado() == 2) {
                    transactionRow.remove(i);
                    LockRequest(statusExclusiveLockedData, j.getTransacao(), j.getDado());
                }
            }
        }
        else {
            for (ItemDado i : transactionRow) {
                if (i.getDado().equals(dado)) {
                    System.out.println("Despertando " + dado);
                    if (i.getEstado() == 1) {
                        transactionRow.remove(i);
                        LockRequest(statusSharedLockedData, i.getTransacao(), i.getDado());
                    }
                    if (i.getEstado() == 2) {
                        transactionRow.remove(i);
                        LockRequest(statusExclusiveLockedData, i.getTransacao(), i.getDado());
                    }
                }
            }
        }
    }

    public void UnlockRequest(String transacao, String dado) {
        if (!dado.equals("infinito")) {
            if (currentDataState.get(dado).getEstado() == 2) {
                currentDataState.get(dado).setEstado(0);
                wakeRow(dado);
                currentDataState.get(dado).setEstado(1);
            }
            else if (currentDataState.get(dado).getEstado() == 1) {
                transactionList.remove(dado);
                if (transactionList.isEmpty()) {
                    currentDataState.get(dado).setEstado(0);
                    wakeRow(dado);
                }
            }
        }
    }

    public void SharedLockRequest(String transacao, String dado) {
        if (currentDataState.get(dado).getEstado() == 0) {
            if (currentDataState.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
                infoDB.insertTabel(recupera);
                
                transactionList.add(transacao);
                
                currentDataState.get(dado).setEstado(1);
            }
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
            infoDB.insertTabel(recupera);

            transactionList.add(transacao);
            currentDataState.get(dado).setEstado(1);
        }
        else if (currentDataState.get(dado).getEstado() == 1) {
            if (currentDataState.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
                infoDB.insertTabel(recupera);
                
                transactionList.add(transacao);
            }
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'R', dado);
            infoDB.insertTabel(recupera);
            
            transactionList.add(transacao);
            currentDataState.get(dado).setEstado(1);
        }
        else {
            ItemDado novoItemDaFila = new ItemDado(1, transacao, dado);
            transactionRow.add(novoItemDaFila);
        }
    }

    public void ExclusiveLockRequest(String transacao, String dado) {
        if (currentDataState.get(dado).getEstado() == 0) {
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'W', dado);
            infoDB.insertTabel(recupera);
            
            transactionList.add(transacao);
            
            currentDataState.get(dado).setEstado(2);
        }
        else if (currentDataState.get(dado).getEstado() == 2 && currentDataState.get(dado).getTransacao().equals(transacao)){
            recupera = new RecuperaInfo(Integer.parseInt(transacao), 'W', dado);
            infoDB.insertTabel(recupera);

            transactionList.add(transacao);

            currentDataState.get(dado).setEstado(2);
        }
        else if (currentDataState.get(dado).getEstado() == 1 && currentDataState.get(dado).getTransacao().equals(transacao)){
            ItemDado novoItemDaFila = new ItemDado(2, transacao, dado);
            transactionRow.add(novoItemDaFila);
        }
        else {
            ItemDado novoItemDaFila = new ItemDado(2, transacao, dado);
            transactionRow.add(novoItemDaFila);
        }
    }

    public void LockRequest(String status, String transacao, String dado) {
        if (status.equals(statusSharedLockedData)) {
            SharedLockRequest(transacao, dado);
        } else {
            ExclusiveLockRequest(transacao, dado);
        }
    }

    public void escalonar() {
        List<RecuperaInfo> informacao = infoDB.ConsumoLote();
        List<String> itemDado = infoDB.ItemDado();

        for(int i=0; i < itemDado.size(); i++){
            data.add(itemDado.get(i));
        }

        EstadoDoDado item = new EstadoDoDado("", 0);
        for (String dado : data) {
            currentDataState.put(dado, item);
        }

        for (int j=0; j < informacao.size(); j++) {
            if ("R".equals(String.valueOf(informacao.get(j).getOperacao()))){
                LockRequest(statusSharedLockedData, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
            }

            if ("W".equals(String.valueOf(informacao.get(j).getOperacao()))){
                LockRequest(statusExclusiveLockedData, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
            }

            if ("E".equals(String.valueOf(informacao.get(j).getOperacao()))){
                UnlockRequest(String.valueOf(informacao.get(j).getIndiceTransacao()), "infinito");
                verifyRow();
            }
        }
    }

    private void verifyRow() {
        ItemDado first;

        for (int i = 0; i < transactionRow.size(); i++) {
            first = transactionRow.get(i);

            if (currentDataState.get(first.getDado()).getEstado() == 0) {
                switch (first.getEstado()) {
                    case 1:
                        LockRequest(statusSharedLockedData, first.getTransacao(), first.getDado());
                        break;
                    case 2:
                        LockRequest(statusExclusiveLockedData, first.getTransacao(), first.getDado());
                        break;
                }
            }
        }
    }
    
}
