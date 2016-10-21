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
    public int lastIndex = 0;

    public List<RecuperaInfo> ConsumoLote(){
        List<RecuperaInfo> infoList = new ArrayList();

        myConnection = new MinhaConexao();
        myConnection.getConnection();

        int lastOp = lastOperationId();

        Connection conn = myConnection.getConnection();
        try {
            String sql = "SELECT * FROM schedule WHERE idoperacao >= ? AND idoperacao <= ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, lastOp-50);
            stm.setInt(2, lastOp);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RecuperaInfo info = new RecuperaInfo(rs.getInt("idoperacao"),
                                                                 rs.getInt("indicetransacao"),
                                                                 rs.getString("operacao").charAt(0),
                                                                 rs.getString("itemdado"),
                                                                 rs.getString("timestampj"),
                                                                 rs.getInt("flag"));
                alterFlag(info.getIdOperacao());

                infoList.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }

        return infoList;
    }
    
    public void alterFlag(int idOperacao){
        myConnection = new MinhaConexao();
        myConnection.getConnection();

        Connection conn = myConnection.getConnection();
        
        try {
            String sql = "UPDATE schedule SET flag = 1 WHERE idoperacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, idOperacao);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }
    }

    public List<String> ItemDado(){
        List<String> infoList = new ArrayList();

        myConnection = new MinhaConexao();
        myConnection.getConnection();
        
        int lastOp = lastOperationId();
        
        Connection conn = myConnection.getConnection();

        try {
            String sql = "SELECT distinct itemdado FROM schedule WHERE (idoperacao >= ? AND idoperacao <= ?) AND itemdado IS NOT NULL";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, lastOp-50);
            stm.setInt(2, lastOp);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                infoList.add(rs.getString("itemdado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }

        return infoList;
    }
    
    public int lastOperationId(){
        myConnection = new MinhaConexao();
        myConnection.getConnection();

        Connection conn = myConnection.getConnection();

        int lastId = 0;

        try {
            String sql = "SELECT MAX(idoperacao) FROM schedule WHERE flag <> 2";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            rs.next();
            lastId = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }

        return lastId;
    }

    public boolean insertTabel(RecuperaInfo info){
        boolean inserted = false;
        
        System.out.println("Meu idoperacao: "+info.getIdOperacao());
        
        myConnection = new MinhaConexao();
        myConnection.getConnection();

        Connection conn = myConnection.getConnection();
        
        try {
            String sql = "INSERT INTO scheduleout(indiceTransacao, operacao, itemDado, timestampj) VALUES (?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, info.getIndiceTransacao());
            stm.setString(2, String.valueOf(info.getOperacao()));
            stm.setString(3, String.valueOf(info.getItemDado()));
            stm.setString(4, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
            
            stm.executeUpdate();
            inserted = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            myConnection.desconect(conn);
        }

        return inserted;
    }
}
