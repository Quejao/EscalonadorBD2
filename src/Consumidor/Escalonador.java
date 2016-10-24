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
public class Escalonador extends Thread {

    Thread th;

    LinkedList<ItemDado> transactionWaitRow;
    ArrayList<String> transactionExecutedList;
    ArrayList<String> data;
    HashMap<String, EstadoDoDado> currentDataState;
    List<Conflito> deadlockControl;

    public static final String statusExclusiveLocked = "X";
    public static final String statusSharedLocked = "S";
    public static final String statusUnlocked = "U";

    public List<Infos> info = new ArrayList();
    private DaoConsumidor infoDB = new DaoConsumidor();
    private Infos dataInfo = null;

    public Escalonador() {
        transactionWaitRow = new LinkedList<>();
        transactionExecutedList = new ArrayList<>();
        data = new ArrayList<>();
        currentDataState = new HashMap<>();
        deadlockControl = new ArrayList<>();
        info = null;
    }

    //metodo para verificacao de bloqueio requisitados
    public void lockRequest(String status, String transaction, String data) throws SQLException {
        if (status.equals(statusSharedLocked)) {
            sharedLock(data, transaction);
        } else {
            exclusiveLock(data, transaction);
        }
    }

    //metodo para aplicacao do bloqueio compartilhado
    public void sharedLock(String data, String transaction) throws SQLException {
        if (currentDataState.get(data).getState() == 0) {
            if (currentDataState.get(data).getTransaction().equals(transaction)) {
                dataInfo = new Infos(Integer.parseInt(transaction), 'R', data);
                infoDB.insertTable(dataInfo);

                transactionExecutedList.add(transaction);

                currentDataState.get(data).setState(1);
            }
            dataInfo = new Infos(Integer.parseInt(transaction), 'R', data);
            infoDB.insertTable(dataInfo);

            transactionExecutedList.add(transaction);
            currentDataState.get(data).setState(1);
        } else if (currentDataState.get(data).getState() == 1) {
            if (currentDataState.get(data).getTransaction().equals(transaction)) {
                dataInfo = new Infos(Integer.parseInt(transaction), 'R', data);
                infoDB.insertTable(dataInfo);

                transactionExecutedList.add(transaction);
            }
            dataInfo = new Infos(Integer.parseInt(transaction), 'R', data);
            infoDB.insertTable(dataInfo);

            transactionExecutedList.add(transaction);
            currentDataState.get(data).setState(1);
        } else {
            ItemDado novoItemDaFila = new ItemDado(1, transaction, data);
            transactionWaitRow.add(novoItemDaFila);
            Conflito c = new Conflito(transaction, currentDataState.get(data).getTransaction());
            deadlockControl.add(c);
        }
    }

    //metodo para aplicacao do bloqueio exclusivo
    public void exclusiveLock(String data, String transaction) throws SQLException {
        if (currentDataState.get(data).getState() == 0) {
            dataInfo = new Infos(Integer.parseInt(transaction), 'W', data);
            infoDB.insertTable(dataInfo);

            transactionExecutedList.add(transaction);

            currentDataState.get(data).setState(2);
        } else if (currentDataState.get(data).getState() == 2 && currentDataState.get(data).getTransaction().equals(transaction)) {
            dataInfo = new Infos(Integer.parseInt(transaction), 'W', data);
            infoDB.insertTable(dataInfo);

            transactionExecutedList.add(transaction);

            currentDataState.get(data).setState(2);
        } else if (currentDataState.get(data).getState() == 1 && currentDataState.get(data).getTransaction().equals(transaction)) {
            ItemDado novoItemDaFila = new ItemDado(2, transaction, data);
            transactionWaitRow.add(novoItemDaFila);
        } else {
            ItemDado novoItemDaFila = new ItemDado(2, transaction, data);
            transactionWaitRow.add(novoItemDaFila);
            Conflito c = new Conflito(transaction, currentDataState.get(data).getTransaction());
            deadlockControl.add(c);
        }
    }

    //metodo para aplicacao do desbloqueio
    public void unlockRequest(String transaction, String data) throws SQLException {
        if (!data.equals("test")) {
            if (currentDataState.get(data).getState() == 2) {
                currentDataState.get(data).setState(0);
                wakeRow(data);
                deadlockControl.remove(transaction);
                currentDataState.get(data).setState(1);
            } else if (currentDataState.get(data).getState() == 1) {
                transactionExecutedList.remove(data);
                if (transactionExecutedList.isEmpty()) {
                    currentDataState.get(data).setState(0);
                    wakeRow(data);
                    deadlockControl.remove(transaction);
                }
            }
        }
    }

    //metodo para retirada de elementos da fila de espera
    public void wakeRow(String data) throws SQLException {
        if (data.equals("")) {
            for (int i = 0; i < transactionWaitRow.size(); i++) {
                ItemDado j = transactionWaitRow.get(i);
                if (j.getState() == 1) {
                    transactionWaitRow.remove(i);
                    lockRequest(statusSharedLocked, j.getTransaction(), j.getData());
                }
                if (j.getState() == 2) {
                    transactionWaitRow.remove(i);
                    lockRequest(statusExclusiveLocked, j.getTransaction(), j.getData());
                }
            }
        } else {
            for (ItemDado i : transactionWaitRow) {
                if (i.getData().equals(data)) {
                    System.out.println("Tirando da fila de espera " + data);
                    if (i.getState() == 1) {
                        transactionWaitRow.remove(i);
                        lockRequest(statusSharedLocked, i.getTransaction(), i.getData());
                    }
                    if (i.getState() == 2) {
                        transactionWaitRow.remove(i);
                        lockRequest(statusExclusiveLocked, i.getTransaction(), i.getData());
                    }
                }
            }
        }
    }

    //verifica se existe deadlock e retorna a transacao mais antiga q esta em deadlock
    public int checkDeadlock() {
        int deadlock = -1;
        for (int i = 0; i < deadlockControl.size() - 1; i++) {
            for (int j = 1; j < deadlockControl.size(); j++) {
                if (deadlockControl.get(i).getDependentTransaction() == deadlockControl.get(j).getBlockTransaction()) {
                    deadlock = i;
                }
            }
        }
        return deadlock;
    }

    @Override
    public void run() {
        try {
            info = infoDB.batchConsumption();
        } catch (SQLException ex) {
            Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<String> itemDado = null;
        try {
            itemDado = infoDB.ItemDado();
        } catch (SQLException ex) {
            Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < itemDado.size(); i++) {
            data.add(itemDado.get(i));
        }

        EstadoDoDado item = new EstadoDoDado("", 0);
        for (String dado : data) {
            currentDataState.put(dado, item);
        }
        for (int j = 0; j < info.size(); j++) {
            if (!deadlockControl.isEmpty()) {
                int aux = checkDeadlock();
                if (aux > -1) {
                    try {
                        infoDB.deleteTransactionOperation(aux);
                        List<Infos> info2 = infoDB.selectTransactionOperations(aux);
                        while(!info2.isEmpty()){
                            info.add(info2.remove(1));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if ("R".equals(String.valueOf(info.get(j).getOperaction()))) {
                try {
                    lockRequest(statusSharedLocked, String.valueOf(info.get(j).getTransactionIndex()), info.get(j).getDataItem());
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if ("W".equals(String.valueOf(info.get(j).getOperaction()))) {
                try {
                    lockRequest(statusExclusiveLocked, String.valueOf(info.get(j).getTransactionIndex()), info.get(j).getDataItem());
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if ("E".equals(String.valueOf(info.get(j).getOperaction()))) {
                try {
                    unlockRequest(String.valueOf(info.get(j).getTransactionIndex()), "test");
                    verifyRow();
                } catch (SQLException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private void verifyRow() throws SQLException {
        ItemDado first;

        for (int i = 0; i < transactionWaitRow.size(); i++) {
            first = transactionWaitRow.get(i);

            if (currentDataState.get(first.getData()).getState() == 0) {
                switch (first.getState()) {
                    case 1:
                        lockRequest(statusSharedLocked, first.getTransaction(), first.getData());
                        break;
                    case 2:
                        lockRequest(statusExclusiveLocked, first.getTransaction(), first.getData());
                        break;
                }
            }
        }
    }

    @Override
    public void start() {
        if (th == null) {
            th = new Thread(this);
            th.start();
        }
    }
}
