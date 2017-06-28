//package com.collabcache.actors;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Master extends Thread{

	static ArrayList<ArrayList<ArrayList<Integer>>> cacheArrays; // all clients caches stored here
	static int currentEndClient;
	Client clients[];
	Client limitClients[];
	Tick tickObj;
	String algorithm;
	static String name;

	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";
	static ArrayList<ArrayList<ArrayList<Integer>>> allBlocks;
	
	static GenerateRequest generateObj;

	public Master(String algo){
		name = "MASTER";
		currentEndClient = 1;
		tickObj = new Tick();
		algorithm = new String();
		algorithm = algo;
		
		allBlocks = ReadTraceFile.buildBlockList();
		generateObj= new GenerateRequest();
	}

	public static synchronized int getBlockFromClientCache(int clientId, int blockRequested){
		int index;
		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(blockRequested);
		for( index = 0; index < currentEndClient; index++ ){
			if ( index != clientId ){
				if ( cacheArrays.get(index).contains(allRequestingBlocks)){
					//SimulatorLogger.writeLog(name, "For client "+ clientId + " Found block with client " + index, infoLevel);
					return index;
				}
			}
		}
		return -1;
	}

	public static synchronized int checkForBlockFromServerCache(int clientId, int blockRequested){

		return Server.getBlockFromServerCache(blockRequested);

	}

	public static synchronized int getBlockFromServerDisk(int clientId, int blockRequested){

		return 0;

	}

	public void run(){
		int index;
		try{

			PrintWriter writerClientNumbers = new PrintWriter("clients"+ this.algorithm + ".txt", "UTF-8");
			PrintWriter writerTimeTakenRetrieve = new PrintWriter("timesRetrieve"+ this.algorithm + ".txt", "UTF-8");
			PrintWriter writerTimeTakenProcess = new PrintWriter("timesProcess"+ this.algorithm + ".txt", "UTF-8");
			PrintWriter writerTimeTakenTotal = new PrintWriter("timesTotal"+ this.algorithm + ".txt", "UTF-8");

			writerClientNumbers.print("numberOfClients = [");
			writerTimeTakenRetrieve.print("timeRetrieve = [");
			writerTimeTakenProcess.print("timeProcess = [");
			writerTimeTakenTotal.print("timeTotal = [");

			while(currentEndClient < Constants.endClient){
				SimulatorLogger.writeLog(name, "\n\n\n\n\nCurrent client end " + currentEndClient, infoLevel);

				//reset server cache
				Server.fillCache(Constants.serverCachSize);
				
				//reset all clients
				clients = new Client[currentEndClient];
				cacheArrays = new ArrayList<>();

				//create the clients
				for(index = 0; index < currentEndClient; index++){
					ArrayList<ArrayList<Integer>> cacheForClient = new ArrayList<>();
					cacheArrays.add(cacheForClient);
					Client curClient = new Client(index, cacheForClient, Constants.clientCacheSize, this, currentEndClient, algorithm);
					clients[index] = curClient;
				}

				// start the clients
				for(index = 0; index < currentEndClient; index++){
					clients[index].startThread();
					clients[index].start();
				}

				Thread.sleep(2000);

				int ctr = 0;

				while ( ctr < 200 ){
					Thread.sleep(500);
					ctr++;
				}

				//SimulatorLogger.writeLog(name, "GOING TO ATTEMPT STOPPING CLIENTS", infoLevel);
				for(index = 0; index < currentEndClient; index++){
					clients[index].tickObj.stopTickerFlag();
					clients[index].stopThread();
				}
				for(index = 0; index < currentEndClient; index++){
					clients[index].tickObj.join();
					clients[index].join();
				}

				//SimulatorLogger.writeLog(name, "STOPPED ALL THE CLIENTS AND CHECKING", infoLevel);

				double averageTime = 0;
				for(index = 0; index < currentEndClient; index++){
					averageTime += clients[index].avgRetrieveTime();
				}

				double avgVal = (averageTime/currentEndClient);

				SimulatorLogger.writeLog(name, "Total clients " + currentEndClient + " avg RETRIEVE time " 
						+ averageTime + " average: " + (averageTime/currentEndClient), infoLevel);
				
				writerTimeTakenRetrieve.print(String.format("%.2f", avgVal) + ", ");
				
				averageTime = 0;
				for(index = 0; index < currentEndClient; index++){
					//SimulatorLogger.writeLog("\nPROCESS", "Time " + clients[index].avgProcessTime(), infoLevel);
					averageTime += clients[index].avgProcessTime();
				}

				avgVal = (averageTime/currentEndClient);

				SimulatorLogger.writeLog(name, "Total clients " + currentEndClient + " avg PROCESS time " 
						+ averageTime + " average: " + (averageTime/currentEndClient), infoLevel);

				writerTimeTakenProcess.print(String.format("%.2f", avgVal) + ", ");
				
				averageTime = 0;
				for(index = 0; index < currentEndClient; index++){
					averageTime += clients[index].avgTotalTime();
				}

				avgVal = (averageTime/currentEndClient);

				SimulatorLogger.writeLog(name, "Total clients " + currentEndClient + " avg TOTAL time " 
						+ averageTime + " average: " + (averageTime/currentEndClient), infoLevel);

				writerTimeTakenTotal.print(String.format("%.2f", avgVal) + ", ");
				
				writerClientNumbers.print(currentEndClient + ", ");
				
				currentEndClient += Constants.increaseBy;
				SimulatorLogger.writeLog(name, "New end client number " + currentEndClient, infoLevel);
			}
			writerClientNumbers.print("]");
			writerTimeTakenRetrieve.print("]");
			writerTimeTakenProcess.print("]");
			writerTimeTakenTotal.print("]");

			writerClientNumbers.close();
			writerTimeTakenRetrieve.close();
			writerTimeTakenProcess.close();
			writerTimeTakenTotal.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
			e.printStackTrace();
		} catch (InterruptedException e) {
			SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		Constants.setConstants(); // setting constants from the properties file
		SimulatorLogger.startLogger();
		Tick ticker = new Tick();
		ticker.start();
		Master master = new Master(Constants.algo);
		master.start();

	}
}
