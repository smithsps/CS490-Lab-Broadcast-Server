package edu.purdue.cs490.server;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteJDBC
{
  private static final Logger log = Logger.getLogger(SQLiteJDBC.class.getName());

  public SQLiteJDBC() {
      try {
          Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException e) {
          log.log(Level.SEVERE, "Could not load JDBC Driver.", e);
          System.exit(0);
      }

      createSQLdatabase();
  }

  public void createSQLdatabase()
  {
    Connection c;
    Statement stmt;
    String sql;

    // If db already exists don't attempt to recreate. Somewhat redundant as first statement fails anyways.
    // Maybe have CREATE TABLE IF NOT EXIST instead.
    if (new File(Server.getInstance().config.get("file.database")).isFile()) {
        log.info("Database file exists, using.");
        return;
    }

    log.info("Database file doesn't exist, creating one.");
    try {
      c = DriverManager.getConnection("jdbc:sqlite:" + Server.getInstance().config.get("file.database"));
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
                   " COURSES	   	TEXT	NOT NULL)";
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

      stmt = c.createStatement();
      sql = "CREATE TABLE ACCOUNTS " +
                "(username  TEXT    PRIMARY KEY     NOT NULL," +
                " password  TEXT    NOT NULL," +
                " active    INT     NOT NULL," +
                " verify    TEXT    NOT NULL)";
      stmt.executeUpdate(sql);

      stmt = c.createStatement();
      sql = "CREATE TABLE SESSIONS " +
                "(username  TEXT    PRIMARY KEY     NOT NULL," +
                " token     TEXT    NOT NULL," +
                " creation  INT     NOT NULL)";
      stmt.executeUpdate(sql);

      stmt = c.createStatement();
      sql = "CREATE TABLE LINUX" +
                "(name          TEXT   PRIMARY KEY     NOT NULL," +
                " lab           TEXT   NOT NULL," +
                " user          INT    NOT NULL," +
                " occupied      INT    NOT NULL," +
                " time          NUMERIC    NOT NULL," +
                " uptime        INT    NOT NULL)";
      stmt.executeUpdate(sql);

      stmt = c.createStatement();
      sql = "CREATE TABLE WINDOWS" +
                "(name          TEXT   PRIMARY KEY     NOT NULL," +
                " user          TEXT    NOT NULL," +
                " time          NUMERIC    NOT NULL)";
      stmt.executeUpdate(sql);

      stmt = c.createStatement();
      sql = "CREATE TABLE HISTORY" +
                "(name          TEXT   NOT NULL," +
                " occupied      INT    NOT NULL," +
                " time          NUMERIC    NOT NULL)";
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
      stmt.close();

      c.close();

///////////////////////////////END///////////////////////////////////////      
    } catch ( SQLException e ) {
      log.severe("Error while creating SQL Table: "  + e.getMessage());
    }
    log.finer("Table created successfully");
  }
}

