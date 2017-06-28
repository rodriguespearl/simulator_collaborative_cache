//package com.collabcache.algos;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/

import java.util.ArrayList;
import java.util.Random;

public class FIFO extends CacheReplacementAlgorithm{

	static int cacheSize;
	final int FIRST_INDEX = 0;
	Random random;
	final String name = "FIFO";
	GenerateRequest generateObj;

	public FIFO(int cacheSize, GenerateRequest generateObj){
		this.cacheSize = cacheSize;
		random = new Random();
		this.generateObj = generateObj;
	}

	// method to initially fill the cache with random blocks
	@Override
	public int initCache(int clientID, ArrayList<ArrayList<Integer>> cache){
		int block;
		int curSize = 0;
		while(curSize < cacheSize ){
			block = generateObj.makeRequest();
			ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(block); // get the entire set of blocks

			if ( (allRequestingBlocks.size()+curSize) <= cacheSize ){
				if (!cache.contains(allRequestingBlocks)){ // if not already present, add it in
					cache.add(allRequestingBlocks);
					curSize += allRequestingBlocks.size(); // even though we have array list of blocks, we cannot exceed total blocks allowed
				}
			}
			else{
				//there is no more space to add the entire block, so end here
				break;
			}
		}
		return curSize;
	}

	@Override
	public boolean checkForBlock(ArrayList<ArrayList<Integer>> cache, int item){
		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(item); // get the entire set of blocks

		if (cache.contains(allRequestingBlocks)){ 
			return true;
		}
		return false;
	}

	@Override
	public int swap(ArrayList<ArrayList<Integer>> cache, int curSize, int item) {
		
		//ADD what if block size > total cache size
		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(item); 
		//SimulatorLogger.writeLog(name, "SWAPPING OUT because of item " + item, infoLevel);
		//SimulatorLogger.writeLog(name, "SWAPPING OUT because of item " + item, infoLevel);
		//SimulatorLogger.writeLog(name, "Block size " + allRequestingBlocks.size(), infoLevel);
		
		if(cache.indexOf(allRequestingBlocks) != -1){
			return curSize;
		}
		
		while ((curSize + allRequestingBlocks.size()) > cacheSize ){
			//SimulatorLogger.writeLog(name, "Cache is full, need to remove some items", infoLevel);
			curSize = remove(cache, curSize);
		}

		curSize = add(cache, allRequestingBlocks, curSize, item); // add either after one remove, if that was needed, else directly add
		return curSize;
	}

	@Override
	public int add(ArrayList<ArrayList<Integer>> cache, ArrayList<Integer> theBlocks, int curSize, int item) {
		
		
		if ((curSize + theBlocks.size()) <= cacheSize ){
			cache.add(theBlocks);
			curSize += theBlocks.size();
		}
		else{
			SimulatorLogger.writeLog(name, "Something went wrong before add because cache is full "
					+ "and we are trying to add to it", severeLevel);
			System.err.println("CACHE ALGO: Something went wrong before add because cache is full and we are trying to add to it");
		}
		
		return curSize;

	}

	@Override
	public int remove(ArrayList<ArrayList<Integer>> cache, int curSize) {
		ArrayList<Integer> items = cache.remove(FIRST_INDEX);
		curSize -= items.size();
		//SimulatorLogger.writeLog(name, "Removed " + items, infoLevel);
		return curSize;
	}

}
