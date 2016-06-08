package br.univates.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matheus
 */
public class Dao {

    /**
     * 
     * @param obj - Object
     * @return Retorna um Array com os dados do Select 
     */
    public static ArrayList<String> getSelectId(Object obj) {

        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> aColumns = new ArrayList<>();
        int i = 0;

        String table = ManipulateClass.getTableClass(obj);
        String primaryKey = ManipulateClass.getPrimaryKeyClass(obj);
        String id = ManipulateClass.getValuePrimaryKey(obj);

        aColumns = Util.getNameColumns(table);

        String query = "SELECT * FROM " + table + " WHERE " + primaryKey + " = " + id;

        try (Connection con = ConFactory.getConnection()) {
            Statement statement = con.createStatement();
            statement.execute(query);
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                for (String string : aColumns) {
                    ret.add(rs.getString(aColumns.get(i)));
                    i++;
                }
            }
            con.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }
    
    /**
     * 
     * @param obj - Object
     * @param where - ClauseSQL onde é implementada a cláusula where da Query
     * @param groupBy
     * @param orderBy
     * @return Retorna um array bidimencional com os dados
     */
    public static ArrayList<String []> getSelectImplementsClause(Object obj, ClauseSQL where, ClauseSQL groupBy, ClauseSQL orderBy) {

        ArrayList<String[]> ret = new ArrayList<>();
        ArrayList<String> aColumns = new ArrayList<>();
        int i = 0, j = 0, lines = 0;
        String query; 
        
        String table = ManipulateClass.getTableClass(obj);
        String primaryKey = ManipulateClass.getPrimaryKeyClass(obj);
        String id = ManipulateClass.getValuePrimaryKey(obj);

        aColumns = Util.getNameColumns(table);

        if (where != null && groupBy == null && orderBy == null ) {
            query = "SELECT * FROM " + table + " WHERE " + where.getClausula();
        } else if (where != null && groupBy != null && orderBy == null) {
            query = "SELECT * FROM " + table + " WHERE " + where.getClausula() + " GROUP BY " + groupBy.getClausula();
        } else if (where != null && groupBy != null && orderBy != null) {
            query = "SELECT * FROM " + table + " WHERE " + where.getClausula() + " GROUP BY " + groupBy.getClausula() + " ORDER BY " + orderBy.getClausula();
        } else if (where != null && groupBy == null && orderBy != null){
            query = "SELECT * FROM " + table + " WHERE " + where.getClausula() + " ORDER BY " + orderBy.getClausula();
        } else if (where == null && groupBy == null && orderBy != null){
            query = "SELECT * FROM " + table + " ORDER BY " + orderBy.getClausula();
        } else {
            query = "SELECT * FROM " + table;
        }

        System.out.println(query);
        
        lines = Util.getContLinesSelect(table, where, groupBy, orderBy);
                
        try (Connection con = ConFactory.getConnection()) {
            Statement statement = con.createStatement();
            statement.execute(query);
            
            ResultSet rs = statement.getResultSet();
            String [][] linha = new String [lines][aColumns.size()];
            
            while (rs.next()) {
                for (String string : aColumns) {
                    linha[j][i] = rs.getString(aColumns.get(i));
                    i++;
                }
                
                ret.add(linha[j]);
                j ++;
                i = 0;
                
            }
            con.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     *
     * @param obj - Object
     * @return Retorna Sucesso na inserção
     */
    public static boolean setInsert(Object obj) {

        ArrayList<String[]> aColumnsDataType = new ArrayList();
        ArrayList<String> aValues = new ArrayList();
        String query, table;
        boolean result = true;

        try (Connection con = ConFactory.getConnection()) {
            query = Script.getInsert(obj);
            Statement statement = con.createStatement();
            System.out.println(query);
            result = statement.execute(query);

            if (!result) {
                System.out.println("Inserido com sucesso!");
            }

            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

    /**
     *
     * @param obj - Object
     * @return Retorna Sucesso na alteração
     */
    public static boolean setUpdate(Object obj) {

        String query;
        boolean result = true;

        try (Connection con = ConFactory.getConnection()) {
            query = Script.getUpdate(obj);
            Statement statement = con.createStatement();
            result = statement.execute(query);
            System.out.println(query);
            if (!result) {
                System.out.println("Alterado com sucesso!");
            }

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

}
