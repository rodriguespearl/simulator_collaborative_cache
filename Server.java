//package com.collabcache.actors;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/


import java.util.ArrayList;
import java.util.Random;

public class Server {

	static int cacheSize;
	static ArrayList<ArrayList<Integer>> serverCache;
	final static String name = "SERVER";
	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";
	static int curSize;
	static GenerateRequest generateObj;
	
	
	// method to initially fill the cache with random blocks
	public static void fillCache(int size){
		generateObj= new GenerateRequest();
		curSize = 0;
		cacheSize = size;
		serverCache = new ArrayList<>();
		int block;
		
		while(curSize < cacheSize ){
			block = generateObj.makeRequest(); //random.nextInt(upperBoundOnBlockGeneration)+1;
			ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(block); // get the entire set of blocks

			if ( (allRequestingBlocks.size()+curSize) <= cacheSize ){
				if (!serverCache.contains(allRequestingBlocks)){ // if not already present, add it in
					serverCache.add(allRequestingBlocks);
					curSize += allRequestingBlocks.size(); // even though we have array list of blocks, we cannot exceed total blocks allowed
				}
			}
			else{
				//there is no more space to add the entire block, so end here
				break;
			}
		}
	}

	public static synchronized int getBlockFromServerCache(int blockRequested){
		
		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(blockRequested);
		
		return serverCache.indexOf(allRequestingBlocks);
	}
}
