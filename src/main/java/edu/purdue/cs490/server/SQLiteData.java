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
}

