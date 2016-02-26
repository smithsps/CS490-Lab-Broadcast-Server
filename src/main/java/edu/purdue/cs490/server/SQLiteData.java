package edu.purdue.cs490.server;

import java.sql.*;

public class SQLiteData
{
  public int grabLab(String labroom)
  {
    Connection c = null;
    Statement stmt = null;
    String sql = null;
    int total = 0;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:test.db");
      System.out.println("Opened database successfully");
	  c.setAutoCommit(false);

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM "+labroom+";" );
      while ( rs.next() ) {
         int cnt  = rs.getInt("OCCUPIED");
         if(cnt == 1){
         	total++;
         }
      }
      rs.close();
      stmt.close();
      c.close();
	  
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    return total;
  }
  
  /*public int deleteBroadcaster()
  {
    Connection c = null;
    Statement stmt = null;
    String sql = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:test.db");
      System.out.println("Opened database successfully");
	  c.setAutoCommit(false);

	  stmt = c.createStatement();
      String sql = "DELETE from COMPANY where ID=2;";
      stmt.executeUpdate(sql);
      c.commit();

      ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
      while ( rs.next() ) {
         int id = rs.getInt("id");
         String  name = rs.getString("name");
         int age  = rs.getInt("age");
         String  address = rs.getString("address");
         float salary = rs.getFloat("salary");
         System.out.println( "ID = " + id );
         System.out.println( "NAME = " + name );
         System.out.println( "AGE = " + age );
         System.out.println( "ADDRESS = " + address );
         System.out.println( "SALARY = " + salary );
         System.out.println();
      }
      rs.close();
      stmt.close();
      c.close();

    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
  }*/
  
///////////////////////////////END///////////////////////////////////////   
}

