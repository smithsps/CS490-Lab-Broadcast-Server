package edu.purdue.cs490.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable{

    ExecutorService executor;
    ServerSocket serverSocket;
    Socket clientSocket;
    BufferedReader inFromClient;
    BufferedWriter  outToClient;

    public Server(){
        executor = Executors.newFixedThreadPool(60);
        try{
            serverSocket = new ServerSocket(5000);
        }catch(Exception e){
            System.out.println("Server Socket Failed!");
        }
    }

    public Server(Socket client){
        this.clientSocket = client;
        try{
            this.outToClient = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        }catch(Exception e){
            System.out.println("Server Socket Failed!");
        }
    }

    public void analyze(String msg){
        System.out.println("msg: "+msg);
    }


    public void run(){
        try{
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //while(true){
            String read;
            while(((read = inFromClient.readLine()) != null) && !(read.equals(""))){
                //String read = inFromClient.readLine();
                analyze(read);
            }
        }catch(Exception e){
            System.out.println("Connection Lost!");
        }
    }

    public void startServer() {
        while(true){
            try{
                System.out.println("Waiting for client..");
                Socket cs2 = this.serverSocket.accept();
                Server cb2 = new Server(cs2);
                executor.execute(cb2);
            }catch(Exception e) {
                System.out.println("Whoops!");
            }
        }
    }



    public static void main(String[] args){
        try {
            Server cb = new Server();
            cb.startServer();
        }catch(Exception e) {
            System.out.println("Whoops! It didn't work!");
        }
    }
}