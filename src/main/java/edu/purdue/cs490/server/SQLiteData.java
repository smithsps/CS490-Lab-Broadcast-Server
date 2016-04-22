package edu.purdue.cs490.server;

import java.sql.*;

import edu.purdue.cs490.server.data.sql.Account;
import org.sqlite.SQLiteErrorCode;

import java.time.LocalTime;
import java.util.Map;
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
			c = DriverManager.getConnection("jdbc:sqlite:" + Server.getInstance().config.get("DatabaseFile"));
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
			pstmt.close();

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
			pstmt.close();
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
			pstmt.close();
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

	/**
	 * I think this is best deprecated, maybe this can add to a different way?
	 * @param username
	 * @param courses
	 * @param current
	 * @param languages
     */
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
			pstmt.close();
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
			pstmt.close();
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Attempts to add an account.
	 * @param username purdue username
	 * @param passwordHash bcrypt hash, includes salt.
	 * @throws SQLException
	 *
	 * At the moment this fails if there is an existing account with the same name
	 * But what if there is an existing account that is not active/verified?
	 * What should the proper process be?
	 *
	 * Also this bypasses the current user system for logins atm.
	 * Best to decide how to deal with this with David.
     */
	public void createAccount(String username, String passwordHash, String verifyCode) throws SQLException {
		//Should abort on already existing username.
		PreparedStatement pstmt = c.prepareStatement("INSERT INTO accounts (username, password, active, verify) " +
													 "VALUES(?,?,0,?)");
		pstmt.setString(1, username);
		pstmt.setString(2, passwordHash);
		pstmt.setString(3, verifyCode);

		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * Retrieves entire account: username, password
	 * @param username purdue username
	 * @return Account data class
	 * @throws SQLException
     */
	public Account getAccount(String username) throws SQLException {
		PreparedStatement pstmt = c.prepareStatement("SELECT username, password, active, verify FROM accounts WHERE username = ?");
		pstmt.setString(1, username);

		ResultSet r = pstmt.executeQuery();
		Account account = new Account();
		account.username = r.getString(1);
		account.passwordHash = r.getString(2);
		account.active = r.getBoolean(3);
		account.verifyCode = r.getString(4);

		r.close();
		pstmt.close();

		return account;
	}

	/**
	 * Sets if the the account is verified.
	 * @param username
	 * @param active Account Verified
	 * @throws SQLException
     */
	public void setAccountActive(String username, String verifyCode, Boolean active) throws SQLException {
		PreparedStatement pstmt = c.prepareStatement("UPDATE accounts SET active = ? WHERE username = ? AND verify = ?");
		pstmt.setInt(1, (active) ? 1 : 0);
		pstmt.setString(2, username);
		pstmt.setString(3, verifyCode);

		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * Sets and replaces session token for user. Used on login.
	 * @param username
	 * @param token session token, randomly generated.
	 * @throws SQLException
     */
	public void createSession(String username, String token) throws SQLException{
		PreparedStatement pstmt = c.prepareStatement("INSERT OR REPLACE INTO sessions (username, token, creation)" +
													 "VALUES (?,?,?)");
		pstmt.setString(1, username);
		pstmt.setString(2, token);
		pstmt.setTime(3, Time.valueOf(LocalTime.now()));

		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * Retrieves user login session
	 * @param username
	 * @return session token
	 * @throws SQLException
     */
	public String getSession(String username) throws SQLException {
		PreparedStatement pstmt = c.prepareStatement("SELECT token FROM sessions WHERE username = ?");
		pstmt.setString(1, username);

		ResultSet rs = pstmt.executeQuery();
		pstmt.close();

		return rs.getString(1);
	}
}

