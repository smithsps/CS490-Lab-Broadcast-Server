package edu.purdue.cs490.server;

import java.sql.*;

public class SQLiteJDBC
{
  public void createSQLdatabase()
  {
    Connection c = null;
    Statement stmt = null;
    String sql = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:test.db");
      System.out.println("Opened database successfully");
	  c.setAutoCommit(false);

///////////////////////////////CREATE///////////////////////////////////////
      stmt = c.createStatement();
      sql = "CREATE TABLE USERS " +
                   "(USERNAME	    TEXT    NOT NULL, " + 
                   " COURSES        TEXT    NOT NULL, " +
                   " CURRENT		TEXT	NOT NULL, " +
                   " LANGUAGES		TEXT	NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE BROADCASTERS " +
                   "(USERNAME	   	TEXT    NOT NULL, " + 
                   " ROOM	       	TEXT    NOT NULL, " +
                   " HELP		   	TEXT	NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LABS" +
                   "(LAB_ROOM       TEXT    NOT NULL, " + 
                   " MACHINE_NAME   TEXT    NOT NULL, " + 
                   " PC_AMOUNT      INT		NOT NULL, " +
                   " OS				TEXT	NOT NULL) "; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LWSNB160 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LWSNB158 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LWSNB148 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LWSNB146 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE LWSNB131 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE HAASG56 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE HAASG40 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "CREATE TABLE HAAS257 " +
                   "(MACHINE_NAME   TEXT    NOT NULL, " + 
                   " OCCUPIED       INT     NOT NULL)"; 
      stmt.executeUpdate(sql);
      
      stmt.close();
      //c.close();

///////////////////////////////INSERT///////////////////////////////////////

///////////////////////////////INSERT LAB TABLE/////////////////////////////
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('LWSNB160', 'WINDOWS160', 25, 'WINDOWS');"; 
      stmt.executeUpdate(sql);

      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('LWSNB158', 'SSLAB', 24, 'LINUX');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('LWSNB148', 'POD', 25, 'LINUX');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('LWSNB146', 'MOORE', 24, 'LINUX');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('LWSNB131', 'WINDOWS131', 25, 'WINDOWS');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('HAASG56', 'WINDOWS56', 24, 'WINDOWS');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('HAASG40', 'BORG', 24, 'LINUX');"; 
      stmt.executeUpdate(sql);
      
      stmt = c.createStatement();
      sql = "INSERT INTO LABS (LAB_ROOM,MACHINE_NAME,PC_AMOUNT,OS) " +
                   "VALUES ('HAAS257', 'XINU', 21, 'LINUX');"; 
      stmt.executeUpdate(sql);
      
///////////////////////////////INSERT PC STATUS TABLES///////////////////////
	  for(int i = 1; i < 26; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO LWSNB160 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('WINDOWS160-"+i+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 1; i < 25; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO LWSNB158 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('SSLAB"+String.format("%02d",i)+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 0; i < 5; i++){
	  	for(int j = 1; j < 6; j++){
			stmt = c.createStatement();
			sql = "INSERT INTO LWSNB148 (MACHINE_NAME, OCCUPIED) " +
					   "VALUES ('POD"+i+"-"+j+"', 0);"; 
			stmt.executeUpdate(sql);
		}
	  }
	  
	  for(int i = 1; i < 25; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO LWSNB146 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('MOORE"+String.format("%02d",i)+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 1; i < 26; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO LWSNB131 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('WINDOWS131-"+i+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 1; i < 25; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO HAASG56 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('WINDOWS56-"+i+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 1; i < 25; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO HAASG40 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('BORG"+String.format("%02d",i)+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  
	  for(int i = 1; i < 22; i++){
		stmt = c.createStatement();
		sql = "INSERT INTO HAAS257 (MACHINE_NAME, OCCUPIED) " +
				   "VALUES ('XINU"+String.format("%02d",i)+"', 0);"; 
		stmt.executeUpdate(sql);
	  }
	  

      stmt.close();
      c.commit();
      //c.close();

///////////////////////////////SELECT///////////////////////////////////////
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM LABS;" );
      while ( rs.next() ) {
         String lab = rs.getString("LAB_ROOM");
         String  mach = rs.getString("MACHINE_NAME");
         int cnt  = rs.getInt("PC_AMOUNT");
         String  os = rs.getString("OS");
         /*System.out.println( "LAB_ROOM = " + lab );
         System.out.println( "MACHINE_NAME = " + mach );
         System.out.println( "PC_AMOUNT = " + cnt );
         System.out.println( "OS = " + os );
         System.out.println();*/
      }
      rs.close();
      stmt.close();
      
      stmt = c.createStatement();
      rs = stmt.executeQuery( "SELECT * FROM LWSNB146;" );
      while ( rs.next() ) {
         String mach = rs.getString("MACHINE_NAME");
         int cnt  = rs.getInt("OCCUPIED");
         /* System.out.println( "MACHINE_NAME = " + mach );
         System.out.println( "OCCUPIED = " + cnt );
         System.out.println(); */
      }
      rs.close();
      stmt.close();
      
      c.close();

///////////////////////////////DELETE///////////////////////////////////////
/*	  stmt = c.createStatement();
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
*/      
///////////////////////////////END///////////////////////////////////////      
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    System.out.println("Table created successfully");
  }
}

