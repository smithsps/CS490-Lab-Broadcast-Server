package edu.purdue.cs490.server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteData
{
  private static final Logger log = Logger.getLogger(SQLiteData.class.getName());

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
      log.log(Level.FINE, e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    return total;
  }
}

