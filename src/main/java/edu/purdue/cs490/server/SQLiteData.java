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

	Connection c;

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
			// Somewhat susceptible to SQL Injection as
			// prepared statement can't be used on this, as a table name is appended.
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + labroom +  " WHERE OCCUPIED = 1");
			total = rs.getInt(1);
			rs.close();

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
		return total;
	}

	public void updateLabPC(String labroom, String machine, int occupied)
	{

		try {
			machine = machine.toUpperCase();
			labroom = labroom.toUpperCase();

			// Somewhat susceptible to SQL Injection as
			// prepared statement can't be used on this, as a table name is appended.
			PreparedStatement pstmt = c.prepareStatement("UPDATE " + labroom + " SET OCCUPIED = ? WHERE MACHINE_NAME = ?;");
			pstmt.setInt(1, occupied);
			pstmt.setString(2, machine);

			pstmt.executeUpdate();

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public void addBroadcaster(String username, String room, String help)
	{
		try {
			PreparedStatement pstmt = c.prepareStatement("INSERT INTO BROADCASTER (USERNAME,ROOM,HELP) VALUES (?, ?, ?);");
			pstmt.setString(1, username);
			pstmt.setString(2, room);
			pstmt.setString(3, help);

			pstmt.executeUpdate();
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public void removeBroadcaster(String username)
	{
		try {
			PreparedStatement pstmt = c.prepareStatement("DELETE FROM BROADCASTERS WHERE USERNAME = ?;");
			pstmt.setString(1, username);

			pstmt.executeUpdate();
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	//Perhaps have this return a Map or something? No need to return a formatted string.
	public String grabAllBroadcasters()
	{
		String allBroadcasters = null;
		try {
			Statement stmt = c.createStatement();
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
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
		return allBroadcasters;
	}

	//Can't we query this, rather than filter a query of everything?
	public String grabSpecificBroadcasters(String course)
	{
		String courseBroadcasters = null;
		try {
			Statement stmt = c.createStatement();
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
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
		return courseBroadcasters;
	}

	public void addUser(String username, String courses, String current, String languages)
	{
		try {
			PreparedStatement pstmt = c.prepareStatement("INSERT INTO USERS (USERNAME,COURSES,CURRENT,LANGUAGES)"+
														 "VALUES(?, ?, ?, ?);");
			pstmt.setString(1, username);
			pstmt.setString(2, courses);
			pstmt.setString(3, current);
			pstmt.setString(4, languages);

			pstmt.executeUpdate();
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public void updateUserPreferences(String username, String courses, String current, String languages)
	{
		try {
			PreparedStatement pstmt = c.prepareStatement("UPDATE USERS SET COURSES = ?, CURRENT = ?, " +
																		  "LANGUAGES = ? WHERE USERNAME = ?;");
			pstmt.setString(1, courses);
			pstmt.setString(2, current);
			pstmt.setString(3, languages);
			pstmt.setString(4, username);

			pstmt.executeUpdate();
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}
}

