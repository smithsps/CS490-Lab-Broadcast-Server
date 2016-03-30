package edu.purdue.cs490.server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteData
{
  private static final Logger log = Logger.getLogger(SQLiteData.class.getName());

  Connection c = null;
  Statement stmt = null;
  String sql = null;
    
  
  public SQLiteData(){
	   try {
      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	  c.setAutoCommit(false);
	   }catch( SQLException e ) {
      log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
	   }
  }
  
  public int grabLab(String labroom)
  {
    int total = 0;
    try {
      /*c = DriverManager.getConnection("jdbc:sqlite:test.db");
      System.out.println("Opened database successfully");
	  c.setAutoCommit(false);*/

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM "+labroom+";" );
      while ( rs.next() ) {
         int cnt  = rs.getInt("OCCUPIED");
         if(cnt == 1){
         	total++;
         }
      }
      /*rs.close();
      stmt.close();
      c.close();*/
	  
    } catch ( SQLException e ) {
      log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
    }
    return total;
  }
  
	public void updateLabPC(String labroom, String machine, int occupied)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		try {
		  /*c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);*/

		  machine = machine.toUpperCase();
		  labroom = labroom.toUpperCase();

		  stmt = c.createStatement();
		  stmt.executeUpdate(
			"UPDATE "+labroom+" SET OCCUPIED = "+occupied+" WHERE MACHINE_NAME = '"+machine+"';");
		  /*rs.close();
		  stmt.close();
		  c.close();*/
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	public void addBroadcaster(String username, String room, String help)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("INSERT INTO BROADCASTER (USERNAME,ROOM,HELP)"+
			"VALUES('"+username+"', '"+room+"', '"+help+"');");
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void removeBroadcaster(String username)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("DELETE FROM BROADCASTERS WHERE USERNAME = '"+username+"';");
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public String grabAllBroadcasters()
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		String allBroadcasters = null;
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM BROADCASTERS;");
		  
		  while ( rs.next() ) {
			 allBroadcasters += rs.getString("USERNAME");
			 allBroadcasters += " ";
			 allBroadcasters += rs.getString("ROOM");
			 allBroadcasters += " ";
			 allBroadcasters += rs.getString("HELP");
			 allBroadcasters += "\n";
		  }
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
		return allBroadcasters;
	}
	
	public String grabSpecificBroadcasters(String course)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		String courseBroadcasters = null;
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM BROADCASTERS;");
		  
		  while ( rs.next() ) {
			 if(rs.getString("HELP").contains(course)){
				 courseBroadcasters += rs.getString("USERNAME");
				 courseBroadcasters += " ";
				 courseBroadcasters += rs.getString("ROOM");
				 courseBroadcasters += " ";
				 courseBroadcasters += rs.getString("HELP");
				 courseBroadcasters += "\n";
			 }
		  }
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
		return courseBroadcasters;
	}
	
	public void addUser(String username, String courses, String current, String languages)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery("INSERT INTO USERS (USERNAME,COURSES,CURRENT,LANGUAGES)"+
			"VALUES('"+username+"', '"+courses+"', '"+current+"', '"+languages+"');");
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	
	public void updateUserPreferences(String username, String courses, String current, String languages)
	{
		/*Connection c = null;
		Statement stmt = null;
		String sql = null;*/
		try {
		  c = DriverManager.getConnection("jdbc:sqlite:test.db");
		  System.out.println("Opened database successfully");
		  c.setAutoCommit(false);

		  stmt = c.createStatement();
		  ResultSet rs = stmt.executeQuery(
			"UPDATE USERS SET COURSES = '"+courses+"', CURRENT = '"+current+"', LANGUAGES = '"+languages+
			"' WHERE USERNAME = '"+username+"';");
		  rs.close();
		  stmt.close();
		  c.close();
		  
		} catch ( SQLException e ) {
		  log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
}

