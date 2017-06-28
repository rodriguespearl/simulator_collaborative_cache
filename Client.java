//package com.collabcache.actors;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/

import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{

	int id;
	ArrayList<ArrayList<Integer>> cache;
	boolean running;
	int cacheSize;
	int totalClients;
	Random random;
	boolean needBlock;
	int blockNeeded;
	Master master;
	Tick tickObj;
	ArrayList<Double> timeTotal;
	ArrayList<Double> timeRetrieve;
	ArrayList<Double> timeProcess;
	String algorithm;
	CacheReplacementAlgorithm algorithmObj;
	String name;
	int curSize;
	GenerateRequest generateObj;

	boolean readNextBlock;
	int curBlockIndex;
	ArrayList<Integer> blocksNeeded;
	boolean firstReadForBlock;
	int currentReadBlockSize;

	final String FROM_CLIENT_CACHE = "FROM_CLIENT_CACHE";
	final String FROM_SERVER_CACHE = "FROM_SERVER_CACHE";
	final String FROM_SERVER_DISK = "FROM_SERVER_DISK";

	String block_source;

	//int currentRun;

	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";


	public Client(int id, ArrayList<ArrayList<Integer>> cache, int cacheSize, Master m, int totalClients, String runningAlgo){

		this.name = "CLIENT" + " " + id;

		this.cache = cache;
		this.cacheSize = cacheSize;
		this.curSize = 0;
		this.running = true;
		random = new Random();
		this.id = id;
		master = m;
		this.totalClients = totalClients;
		needBlock = false;
		timeTotal = new ArrayList<>();
		timeRetrieve = new ArrayList<>();
		timeProcess = new ArrayList<>();

		this.generateObj = new GenerateRequest();
		firstReadForBlock = false;
		currentReadBlockSize = -1;

		this.block_source = new String();
		this.block_source = "NONE";

		readNextBlock = false;
		curBlockIndex = -1;
		blocksNeeded = new ArrayList<>();

		algorithm = runningAlgo;
		algorithm = algorithm.toUpperCase();

		switch(algorithm){
		case "FIFO":
			algorithmObj = new FIFO(cacheSize, this.generateObj);
			break;
		case "LIFO":
			algorithmObj = new LIFO(cacheSize, this.generateObj);
			break;
		case "LRU":
			algorithmObj = new LRU(cacheSize, this.generateObj);
			break;
		case "MRU":
			algorithmObj = new MRU(cacheSize, this.generateObj);
			break;
		case "RANDOM":
			algorithmObj = new RANDOM(cacheSize, this.generateObj);
			break;
		default:
			SimulatorLogger.writeLog(name, "INVALID ALGORITHM", severeLevel);
			break;
		}

		this.curSize = algorithmObj.initCache(id, cache); // method to initially fill the cache with random blocks

		try{
			tickObj = new Tick();
			tickObj.start();
		}
		catch ( Exception e){
			e.getMessage();
		}

	}

	public void resetCache(int id, ArrayList<ArrayList<Integer>> cache, int totalClients){
		this.id = id;
		this.cache = cache;
		this.totalClients = totalClients;
		needBlock = false;
	}

	public void startThread(){
		running = true;
	}

	public void stopThread(){
		running = false;
	}



	public int checkForBlockInOwnCache(int requestBlock){

		/*boolean foundBlock = algorithmObj.checkForBlock(cache, requestBlock);

		if (!foundBlock){
			needBlock = true;
			//SimulatorLogger.writeLog(name, "Need to check other client's caches", infoLevel);
			setRequestBlock(requestBlock);
		}
		else{
			needBlock = false;
			//SimulatorLogger.writeLog(name, "I have the block", infoLevel);
		}*/
		boolean foundBlock = algorithmObj.checkForBlock(cache, requestBlock);

		if(foundBlock){
			return 0;
		}
		else{
			return -1;
		}

	}

	public void setRequestBlock(int block){
		blockNeeded = block;
	}

	public int getRequestBlock(){
		return blockNeeded;
	}

	public double avgRetrieveTime(){
		double avg = 0;
		for ( double num:timeRetrieve){
			avg += num;
		}
		avg/= timeRetrieve.size();
		//SimulatorLogger.writeLog(name, "Total requests: " + time.size() + " Average time: " + avg, infoLevel);
		
		//SimulatorLogger.writeLog(name, "timeRetrieve " + timeRetrieve + " Average time: " + avg, infoLevel);

		if(this.id == 0 ){
			SimulatorLogger.writeLog(name, "Total requests: " + timeRetrieve.size() + " Average time: " + avg, infoLevel);
		}

		return avg;
	}
	
	public double avgProcessTime(){
		double avg = 0;
		for ( double num:timeProcess){
			avg += num;
		}
		avg/= timeProcess.size();
		
		//SimulatorLogger.writeLog(name, "timeProcess " + timeProcess + " Average time: " + avg, infoLevel);
		
		//SimulatorLogger.writeLog(name, "Total requests: " + time.size() + " Average time: " + avg, infoLevel);

		if(this.id == 0 ){
			SimulatorLogger.writeLog(name, "Total requests: " + timeProcess.size() + " Average time: " + avg, infoLevel);
		}

		return avg;
	}
	
	public double avgTotalTime(){
		double avg = 0;
		for ( double num:timeTotal){
			avg += num;
		}
		
		avg/= timeTotal.size();
		
		//SimulatorLogger.writeLog(name, "timeTotal " + timeTotal + " Average time: " + avg, infoLevel);
		
		//SimulatorLogger.writeLog(name, "Total requests: " + time.size() + " Average time: " + avg, infoLevel);

		if(this.id == 0 ){
			SimulatorLogger.writeLog(name, "Total requests: " + timeTotal.size() + " Average time: " + avg, infoLevel);
		}

		return avg;
	}

	public void run(){
		//Make sure setCache is called before starting anything
		//double startTime, endTime;
		ArrayList<Integer> blocksReceived;
		BlockRequest blockReqObj;
		ProcessBlock procBlockObj;


		while(running){
			try{
				//SimulatorLogger.writeLog(name, "New request coming in", infoLevel);
				Thread.sleep(200); // pausing the thread for a bit

				blockReqObj = new BlockRequest();
				procBlockObj = new ProcessBlock();

				blocksReceived = new ArrayList<>();

				ArrayList<Double> startTimesRetrieve = new ArrayList<>();
				ArrayList<Double> endTimesRetrieve = new ArrayList<>();
				ArrayList<Double> startTimesProcess = new ArrayList<>();
				ArrayList<Double> endTimesProcess = new ArrayList<>();

				int requestBlockNumber;
				requestBlockNumber = generateObj.makeRequest();
				blocksNeeded = new ArrayList<>();
				blocksNeeded = generateObj.getCorrespondingBlocks(requestBlockNumber);

				requestBlockNumber = generateObj.makeRequest();
				blocksNeeded = new ArrayList<>();
				blocksNeeded = generateObj.getCorrespondingBlocks(requestBlockNumber);


				requestBlockNumber = blocksNeeded.get(0);

				//SimulatorLogger.writeLog(name, "Block set size " + blocksNeeded.size() + " request block " + requestBlockNumber, infoLevel);

				blockReqObj.init(this, blocksNeeded, blocksReceived, this.id, tickObj, startTimesRetrieve, endTimesRetrieve);
				procBlockObj.init(blocksNeeded, blocksReceived, tickObj, startTimesProcess, endTimesProcess);

				blockReqObj.start();
				procBlockObj.start();

				boolean processing = true;
				while(processing){
					if(blockReqObj.gotBlocks == true && procBlockObj.processingDone == true){
						processing = false;
					}
					else{
						Thread.sleep(500);
					}
				}

				blockReqObj.join();
				procBlockObj.join();

				//SimulatorLogger.writeLog(name, "Block and process joined", infoLevel);

				//swap the whole block in when all of them are received
				this.curSize = algorithmObj.swap(this.cache, this.curSize, blocksNeeded.get(0)); 

				if(procBlockObj.endTimes.size() != blockReqObj.startTimes.size()){
					//System.err.println("SOMETHING WRONG! FIX");
					SimulatorLogger.writeLog(name, "SOMETHING WRONG! Start and ends sizes are different", severeLevel);
				}
				else{
					int totalRequests = blockReqObj.startTimes.size();
					Double diff;
					
					//SimulatorLogger.writeLog(name, "In run blockReqStart " + blockReqObj.startTimes, infoLevel);
					//SimulatorLogger.writeLog(name, "In run blockReqEnd " + blockReqObj.endTimes, infoLevel);
					//SimulatorLogger.writeLog(name, "In run blockProcStart " + procBlockObj.startTimes, infoLevel);
					//SimulatorLogger.writeLog(name, "In run blockProcEnd " + procBlockObj.endTimes, infoLevel);
					
					for(int index = 0; index < totalRequests; index++){
						diff = blockReqObj.endTimes.get(index) - blockReqObj.startTimes.get(index);
						timeRetrieve.add(diff);
						
						diff = procBlockObj.endTimes.get(index) - procBlockObj.startTimes.get(index);
						timeProcess.add(diff);
						
						//diff = procBlockObj.endTimes.get(index) - blockReqObj.startTimes.get(index);
						/*SimulatorLogger.writeLog("TOTAL for retrieve " + timeRetrieve.get(index) + 
								" process " + timeProcess.get(index) + " total " + 
								(timeRetrieve.get(index)+timeProcess.get(index)), "For this", infoLevel);*/
						timeTotal.add(timeRetrieve.get(index)+timeProcess.get(index));
					}
				}
				//SimulatorLogger.writeLog(name, "One loop done", infoLevel);
			}
			catch ( Exception e)
			{
				SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
				e.printStackTrace();
			}
		}
		
		//SimulatorLogger.writeLog(name, "End retrieve " + timeRetrieve, infoLevel);
		//SimulatorLogger.writeLog(name, "End proc " + timeProcess, infoLevel);
		//SimulatorLogger.writeLog(name, "End total " + timeTotal, infoLevel);
		
	}

	/*	public void run(){
		//Make sure setCache is called before starting anything
		double startTime, endTime;
		while(running){
			try{
				Thread.sleep(200); // pausing the thread for a bit
				startTime = tickObj.getTicker();
				//SimulatorLogger.writeLog(name, "Generating request at time: " + startTime, infoLevel);

				int requestBlockNumber;

				if( this.readNextBlock == false || (curReadingBlock != null && (curBlockIndex >= curReadingBlock.size()))){
					//do the usual i.e. generate new request
					requestBlockNumber = generateObj.makeRequest();
					curReadingBlock = new ArrayList<>();
					curReadingBlock = generateObj.getCorrespondingBlocks(requestBlockNumber);
					requestBlockNumber = curReadingBlock.get(0); // changing the request to the first item of the set, 0 is block size
					curBlockIndex = 1; // already read first one, so move the pointer to the next one
					this.readNextBlock = true;
					firstReadForBlock = true;
					currentReadBlockSize = curReadingBlock.size();
					this.block_source = "NONE";
				}
				else{
					firstReadForBlock = false;
					requestBlockNumber = curReadingBlock.get(curBlockIndex);
					curBlockIndex++;
				}

				checkForBlockInOwnCache(requestBlockNumber);
				tickObj.increaseTickerBy(Constants.tick_increase_own_cache); // increase just once for now because this happens for every block

				if(!this.block_source.equals("NONE")){ //meaning there is some source, so we need to add to ticker
					if(this.block_source.equals(FROM_CLIENT_CACHE)){
						tickObj.increaseTickerBy(Constants.tick_increase_other_client);
					}
					else if (this.block_source.equals(FROM_SERVER_CACHE)){
						tickObj.increaseTickerBy(Constants.tick_increase_other_client);
						tickObj.increaseTickerBy(Constants.tick_increase_server_cache);
					}
					else if (this.block_source.equals(FROM_SERVER_DISK)){
						tickObj.increaseTickerBy(Constants.tick_increase_other_client);
						tickObj.increaseTickerBy(Constants.tick_increase_server_cache);
						tickObj.increaseTickerBy(Constants.tick_increase_server_disk);
					}

				}

				if (needBlock){ //needBlock set only if the client does not have the block itself
					//SimulatorLogger.writeLog(name, "Need to check for the block from other clients that are present", infoLevel);

					int request = Master.getBlockFromClientCache(id, blockNeeded);
					tickObj.increaseTickerBy(Constants.tick_increase_other_client);

					if ( request >= 0 ){ // found in client cache
						if (firstReadForBlock == true)
						{
							this.block_source = FROM_CLIENT_CACHE;
						}
					}
					else //check other places
					{
						//SimulatorLogger.writeLog(name, "Block is not present in any client, need to check server cache", infoLevel);

						tickObj.increaseTickerBy(Constants.tick_increase_server_cache);

						request = Master.checkForBlockFromServerCache(id, blockNeeded);
						if ( request >= 0 ){ // found in server cache
							//SimulatorLogger.writeLog(name, "Block retrieved from server cache", infoLevel);
							if ( firstReadForBlock == true ){
								this.block_source = FROM_SERVER_CACHE;
							}
						}

						//retrieve from server disk
						else
						{
							tickObj.increaseTickerBy(Constants.tick_increase_server_disk);

							//SimulatorLogger.writeLog(name, "Block retrieved from server disk", infoLevel);
							if ( firstReadForBlock == true ){ // found in server disk
								this.block_source = FROM_SERVER_DISK;
							}
						}


					}

					//SimulatorLogger.writeLog(name, "Adding the block to the cache", infoLevel);

					//it swaps only if the blocks are not present
					this.curSize = algorithmObj.swap(this.cache, this.curSize, blockNeeded); // adding newly retrieved block to own cache

				}

				endTime = tickObj.getTicker();
				//SimulatorLogger.writeLog(name, "End time " + endTime, infoLevel);
				Double diff = endTime-startTime;
				time.add(diff);
			}
			catch ( Exception e)
			{
				SimulatorLogger.writeLog(name, e.getMessage(), severeLevel);
				e.printStackTrace();
			}
		}
	}*/
}
