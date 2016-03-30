package edu.purdue.cs490.server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteData
{

	//int grabLab(String labroom)
	//void updateLabPC(String labroom, String machine, int occupied)
	//void addBroadcaster(String username, String room, String help)
	//void removeBroadcaster(String username)
	//String grabAllBroadcasters()
	//String grabSpecificBroadcasters(String course)
	//void addUser(String username, String courses, String current, String languages)
	//void updateUserPreferences(String username, String courses, String current, String languages)

	private static final Logger log = Logger.getLogger(SQLiteData.class.getName());

	Connection c = null;
	Statement stmt = null;

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

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM "+labroom+";" );
			while ( rs.next() ) {
				int cnt  = rs.getInt("OCCUPIED");
				if(cnt == 1){
					total++;
				}
			}

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
		return total;
	}

	public void updateLabPC(String labroom, String machine, int occupied)
	{

		try {

			machine = machine.toUpperCase();
			labroom = labroom.toUpperCase();

			stmt = c.createStatement();
			stmt.executeUpdate(
					"UPDATE "+labroom+" SET OCCUPIED = "+occupied+" WHERE MACHINE_NAME = '"+machine+"';");

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
	public void addBroadcaster(String username, String room, String help)
	{

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

		try {
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened database successfully");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt.executeQuery("DELETE FROM BROADCASTERS WHERE USERNAME = '"+username+"';");

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public String grabAllBroadcasters()
	{

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

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
		return allBroadcasters;
	}

	public String grabSpecificBroadcasters(String course)
	{

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

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
		return courseBroadcasters;
	}

	public void addUser(String username, String courses, String current, String languages)
	{

		try {
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened databasfe successfully");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt.executeQuery("INSERT INTO USERS (USERNAME,COURSES,CURRENT,LANGUAGES)"+
					"VALUES('"+username+"', '"+courses+"', '"+current+"', '"+languages+"');");

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public void updateUserPreferences(String username, String courses, String current, String languages)
	{

		try {
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened database successfully");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt.executeUpdate(
					"UPDATE USERS SET COURSES = '"+courses+"', CURRENT = '"+current+
							"', LANGUAGES = '"+languages+"' WHERE USERNAME = '"+username+"';");

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}
}

