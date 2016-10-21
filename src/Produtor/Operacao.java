package Produtor;

public class Operacao {
    private Acesso acesso;
    private int indice;
    private Dado dado;


    public Operacao(String nome, Acesso acesso, int meuIndice) {
        this.setAcesso(acesso);
        this.setIndice(meuIndice);
        this.setDado(new Dado(nome));
    }

    public Operacao(Acesso acesso, int ultimoIndice) {
        this.setAcesso(acesso);
        this.setIndice(ultimoIndice);
        this.setDado(new Dado(null));
    }

    public Dado getDado() {
        return dado;
    }

    public void setDado(Dado dado) {
        this.dado = dado;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Acesso getAcesso() {
        return acesso;
    }

    public void setAcesso(Acesso acesso) {
        this.acesso = acesso;
    }
}
