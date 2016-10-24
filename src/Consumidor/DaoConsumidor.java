/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

import Produtor.MinhaConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Leonardo
 */
public class DaoConsumidor {
    private static MinhaConexao myConnection;
    public int ultimoIndice = 0;
    
    //metodo para selecionar inforamcoes das operacoes do banco de dados
    public List<Infos> batchConsumption() throws SQLException{
        List<Infos> informacao = new ArrayList();

        myConnection = new MinhaConexao();
        myConnection.getConnection();

        Connection conn = myConnection.getConnection();
        try {
            String sql = "SELECT * FROM schedule";
            PreparedStatement stm = conn.prepareStatement(sql);
            
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Infos info = new Infos(rs.getInt("idoperacao"),
                                                                 rs.getInt("indicetransacao"),
                                                                 rs.getString("operacao").charAt(0),
                                                                 rs.getString("itemdado"),
                                                                 rs.getString("timestampj"),
                                                                 rs.getInt("flag"));
                //alteraFlag(info.getIdOperaction());

                informacao.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.release(conn);
        }

        return informacao;
    }
    
    //metodo para selecionar os itens de dados usados nas operacoes
    public List<String> ItemDado() throws SQLException{
        List<String> informacao = new ArrayList();

        myConnection = new MinhaConexao();
        myConnection.getConnection();
        
        Connection conn = myConnection.getConnection();

        try {
            String sql = "SELECT distinct itemdado FROM schedule WHERE itemdado IS NOT NULL";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                informacao.add(rs.getString("itemdado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.release(conn);
        }

        return informacao;
    }

    //metodo para inserir elementos na tabela de saida
    public boolean insertTable(Infos info) throws SQLException{
        boolean inserted = false;
        
        myConnection = new MinhaConexao();        
        myConnection.getConnection();
        Connection conn = myConnection.getConnection();
        
        try {
            String sql = "INSERT INTO scheduleout(indiceTransacao, operacao, itemDado, timestampj) VALUES (?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, info.getTransactionIndex());
            stm.setString(2, String.valueOf(info.getOperaction()));
            stm.setString(3, String.valueOf(info.getDataItem()));
            stm.setString(4, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
            
            stm.executeUpdate();
            changeFlag(info.getIdOperaction(),1);
            inserted = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.release(conn);
        }

        return inserted;
    }
    
    //metodo para alterar a flag de verificaco se a operacao ja foi escalonada
    public void changeFlag(int idOperacao,int i) throws SQLException{
        myConnection = new MinhaConexao();
        Connection conn = myConnection.getConnection();
        
        try {
            String sql = "UPDATE schedule SET flag = ? WHERE idoperacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, i);
            stm.setInt(2, idOperacao);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.release(conn);
        }
    }
    
    //metodo para selecionar a quantidade de transacoes existentes
    public int transactionsQuantity(){
        myConnection = new MinhaConexao();
        myConnection.getConnection();
        
        Connection conn = myConnection.getConnection();
        int i = 0;
        try {
            String sql = "SELECT distinct indicetransacao FROM schedule";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }
        return i;
    }
    
    //metodo para verificar quantos itens de dado uma transacao acessa
    public int transactionItensQuantity(int transacao){
        myConnection = new MinhaConexao();
        
        Connection conn = myConnection.getConnection();
        int i = 0;
        try {
            String sql = "SELECT distinct itemdado FROM schedule WHERE indicetransacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            
            stm.setInt(1, transacao);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }
        return i;
    }
    
    //metodo para retirada de transacoes que nao terminaram de executar da tabela de saida
    public boolean deleteTransactionOperation(int transactionIndex) throws SQLException{
        boolean deleted = false;
        
        myConnection = new MinhaConexao();
        myConnection.getConnection();

        Connection conn = myConnection.getConnection();
        
        try {
            String sql = "DELETE * FROM scheduleout WHERE indiceTransacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1,transactionIndex);
            stm.executeUpdate();
            deleted = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.release(conn);
        }

        return deleted;
    }
}
