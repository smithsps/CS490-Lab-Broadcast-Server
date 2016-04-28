package edu.purdue.cs490.server;

import java.sql.*;

import edu.purdue.cs490.server.data.sql.Account;
import edu.purdue.cs490.server.data.sql.Broadcaster;
import edu.purdue.cs490.server.data.sql.User;
import org.sqlite.SQLiteErrorCode;

import java.time.LocalTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

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
	//String grabUserPreferences(String username)

	private static final Logger log = Logger.getLogger(SQLiteData.class.getName());

	Connection c;

	public SQLiteData(){
		try {
			c = DriverManager.getConnection("jdbc:sqlite:" + Server.getInstance().config.get("file.database"));
		}catch( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	public int grabLinuxLab(String labroom) throws SQLException
	{
		int total = 0;

		PreparedStatement pstmt = c.prepareStatement("SELECT COUNT(*) FROM LINUX WHERE OCCUPIED = 1 AND LAB = ?");
		pstmt.setString(1, labroom);

		ResultSet rs = pstmt.executeQuery();
		total = rs.getInt(1);
		rs.close();

		return total;
	}

	public int gradWindowsLab(String labroom) throws SQLException
	{
		int total = 0;

		PreparedStatement pstmt = c.prepareStatement("SELECT COUNT(*) FROM WINDOWS WHERE TIME >= Datetime('now', '-5 minutes')");
		//pstmt.setString(1, labroom);

		ResultSet rs = pstmt.executeQuery();
		total = rs.getInt(1);
		rs.close();

		return total;
	}

	public void updateLinux(String name, String lab, String current_user, int time, int uptime, Boolean occupied)
	{
		try {
			name = name.toLowerCase();
			lab = lab.toUpperCase();

			if (current_user == null) {
				current_user = "";
			}


			PreparedStatement pstmt = c.prepareStatement("INSERT OR REPLACE INTO LINUX (name, lab, user, occupied, time, uptime) " +
																			"VALUES(?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, lab);
			pstmt.setString(3, current_user);
			pstmt.setBoolean(4, occupied);
			pstmt.setTimestamp(5, new Timestamp(time));
			pstmt.setInt(6, uptime);

			pstmt.executeUpdate();
			pstmt.close();

			updateHistory(name, occupied, time);
		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public void updateWindows(String name, String user, int time)
	{
		try {
			name = name.toLowerCase();
			user = user.toLowerCase();


			PreparedStatement pstmt = c.prepareStatement("INSERT OR REPLACE INTO LINUX (name, user, time) " +
					"VALUES(?, ?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, user);
			pstmt.setTimestamp(3, new Timestamp(time));


			pstmt.executeUpdate();
			pstmt.close();

			//As a windows computer only reports when someone is logged in, occupied is always true.
			updateHistory(name, true, time);

		} catch ( SQLException e ) {
			log.log(Level.WARNING, e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	public void updateHistory(String name, Boolean occupied, int time) throws SQLException{
		PreparedStatement pstmt = c.prepareStatement("INSERT INTO HISTORY (name, occupied, time) VALUES (?, ?, ?)");
		pstmt.setString(1, name);
		pstmt.setBoolean(2, occupied);
		pstmt.setTimestamp(3, new Timestamp(time));

		pstmt.executeUpdate();
		pstmt.close();
	}

	public void addBroadcaster(String username, String room, String courses)
	{
		try {
			PreparedStatement pstmt = c.prepareStatement("INSERT INTO BROADCASTERS (USERNAME,ROOM,COURSES) VALUES (?, ?, ?);");
			pstmt.setString(1, username);
			pstmt.setString(2, room);
			pstmt.setString(3, courses);

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
	public ArrayList<Broadcaster> grabAllBroadcasters()  throws SQLException{
		ArrayList<Broadcaster> broadcasters	= new ArrayList<Broadcaster>();

		PreparedStatement pstmt = c.prepareStatement("SELECT * FROM BROADCASTERS;");
		ResultSet r = pstmt.executeQuery();

		while(r.next()) {
			Broadcaster bc = new Broadcaster();
			bc.username = r.getString(1);
			bc.room = r.getString(2);
			bc.courses = r.getString(3);
			broadcasters.add(bc);
		}
		r.close();
		pstmt.close();

		return broadcasters;

	}

	//Can't we query this, rather than filter a query of everything?
	public ArrayList<Broadcaster> grabSpecificBroadcasters(String course) throws SQLException{
		ArrayList<Broadcaster> broadcasters	= new ArrayList<Broadcaster>();

		PreparedStatement pstmt = c.prepareStatement("SELECT * FROM BROADCASTERS;");
		ResultSet r = pstmt.executeQuery();

		while(r.next()) {
			if(r.getString("COURSES").contains(course)) {
				Broadcaster bc = new Broadcaster();
				bc.username = r.getString(1);
				bc.room = r.getString(2);
				bc.courses = r.getString(3);
				broadcasters.add(bc);
			}
		}
		r.close();
		pstmt.close();

		return broadcasters;

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

	public User grabUserPreferences(String username) throws SQLException{

		PreparedStatement pstmt = c.prepareStatement("SELECT * FROM USERS WHERE USERNAME = '"+username+"';");
		ResultSet r = pstmt.executeQuery();

		User user = new User();
		user.username = r.getString(1);
		user.courses = r.getString(2);
		user.current = r.getString(3);
		user.languages = r.getString(4);
	
		
		r.close();
		pstmt.close();

		return user;

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
		PreparedStatement pstmt = c.prepareStatement("SELECT username, password, active, verify FROM ACCOUNTS WHERE username = ?");
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

