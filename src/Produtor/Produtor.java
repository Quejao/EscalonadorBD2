package Produtor;

import Consumidor.DaoConsumidor;
import Consumidor.Escalonador;

public class Produtor extends Thread {
    private Thread t;
    private int numeroItens;
    private int numeroTransacoes;
    private int numeroAcessos;
    private static GerenciadorTransacao gerenciador;
    private boolean flag = true;
    
    DaoConsumidor recupera = new DaoConsumidor();
	
    public Produtor(int numeroItens, int numeroTransacoes, int numeroAcessos) {
        this.numeroItens = numeroItens;
        this.numeroTransacoes = numeroTransacoes;
        this.numeroAcessos = numeroAcessos;
    }
	
    public void run() {
        int ultimoIndice = 0;

        Escalonador escalonador = new Escalonador();
        recupera.lastOperationId();
        try {
            do {
                ultimoIndice = TransacaoDao.pegarUltimoIndice();
                gerenciador = new GerenciadorTransacao(numeroItens, numeroTransacoes, numeroAcessos, ultimoIndice);
                Schedule schedule = new Schedule(gerenciador.getListaTransacoes());
                TransacaoDao.gravarTransacoes(schedule);
                escalonador.escalonar();
                Thread.sleep( 3 * 1000 );
            } while(flag);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	
    public void setFlag(boolean state) {
        this.flag = state;
    }
	
    public void start() {
	if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }
}
