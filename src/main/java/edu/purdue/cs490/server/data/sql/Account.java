package edu.purdue.cs490.server.data.sql;

/**
 * A lazy data object for accounts.
 * Perhaps have getters/setters/constructor in future?
 */
public class Account{
    public String username;
    public String passwordHash;
    public Boolean active;
    public String verifyCode;
}
