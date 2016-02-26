package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.*;

public class TCPHandler implements Runnable{

    Socket clientSocket;
    BufferedWriter outToClient;
    BufferedReader inFromClient;

    public TCPHandler(Socket client) {
        this.clientSocket = client;
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Server Socket Failed!");
        }
    }

    public void analyze(String msg) {
        System.out.println("msg: "+msg);
    }

    // Silly incomplete
    // Probably better abstracted to a class
    public String handleHTTPRequestClient(String request) {
        String body = "";
		SQLiteData reqLab = new SQLiteData();
		int lwsnb160 = reqLab.grabLab("LWSNB160");
		int lwsnb158 = reqLab.grabLab("LWSNB158");
		int lwsnb148 = reqLab.grabLab("LWSNB148");
		int lwsnb146 = reqLab.grabLab("LWSNB146");
		int lwsnb131 = reqLab.grabLab("LWSNB131");
		int haasg56 = reqLab.grabLab("HAASG56");
		int haasg40 = reqLab.grabLab("HAASG40");
		int haas257 = reqLab.grabLab("HAAS257");
        //String[] reqTokens = request.split("\n");
		String labCount = "lwsnb160:"+Integer.toString(lwsnb160)+"/25, " +
		"lwsnb158:"+Integer.toString(lwsnb158)+"/25" +
		"lwsnb148:"+Integer.toString(lwsnb148)+"/25" +
		"lwsnb146:"+Integer.toString(lwsnb146)+"/25" +
		"lwsnb131:"+Integer.toString(lwsnb131)+"/25" +
		"haas56:"+Integer.toString(haasg56)+"/25" +
		"haas40:"+Integer.toString(haasg40)+"/25" +
		"haas257:"+Integer.toString(haas257)+"/25";
		
        //String verb = reqTokens[0];
        //String requestURI = reqTokens[1];
		System.out.println("LWSNB160"+lwsnb160);
        try {
            // We SHOULD use the content length in the header to read the body
            // but atm we are lazy and just add a newline in the script
            body = inFromClient.readLine();
            // 200 = Success, and since we are always successful we always success.
            this.outToClient.write("HTTP/1.1 200");
            //this.outToClient.write(labCount);
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Unable to write to socket");
        }

        return body;
    }
    
    public String handleHTTPRequestMachine(String request) {
        String body = "";

        //String[] reqTokens = request.split("\n");

        //String verb = reqTokens[0];
        //String requestURI = reqTokens[1];

        try {
            // We SHOULD use the content length in the header to read the body
            // but atm we are lazy and just add a newline in the script
            body = inFromClient.readLine();
			//read body to input machine data into sql database
			
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Unable to write to socket");
        }

        return body;
    }

    public void run() {
        try {
            String message = "";
            String read;

            while(((read = inFromClient.readLine()) != null) && !(read.equals(""))){
                message += read + '\n';
            }

            if (message.contains("PUT")) {
                String body = handleHTTPRequestClient(message);
                message += body;
            }else if(message.contains("GET HTTP")){
            	String body = handleHTTPRequestMachine(message);
            }

            analyze(message);

            this.outToClient.close();
            this.inFromClient.close();
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            // Need specific Exceptions not general
            System.out.println("Connection Lost!");
        }
    }
}
