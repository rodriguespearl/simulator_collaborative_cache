//package com.collabcache.algos;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/

import java.util.ArrayList;
import java.util.Random;

public class MRU extends CacheReplacementAlgorithm{
	static int cacheSize;
	ArrayList<Integer> accessTimes;
	Random random;
	final int default_access_time = 0;
	final String name = "MRU";
	GenerateRequest generateObj;
	
	public MRU(int cacheSize, GenerateRequest generateObj){
		this.cacheSize = cacheSize;
		random = new Random();
		this.generateObj = generateObj;
	}
	
	@Override
	public int initCache(int clientID, ArrayList<ArrayList<Integer>> cache){
		int block;
		int curSize = 0;
		while(curSize < cacheSize ){
			block = generateObj.makeRequest(); //random.nextInt(upperBoundOnBlockGeneration)+1;
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

		accessTimes = new ArrayList<>();
		for(int index = 0; index < cache.size(); index++ ){ // adding zeros in the access times of all elements
			accessTimes.add(default_access_time);
		}

		return curSize;
	}
	
	@Override
	public boolean checkForBlock(ArrayList<ArrayList<Integer>> cache, int item){

		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(item); // get the entire set of blocks

		if (cache.contains(allRequestingBlocks)){ 
			int position = cache.indexOf(allRequestingBlocks);
			int newAccessTime = accessTimes.get(position) + 1;
			accessTimes.set(position, newAccessTime);
			return true;
		}
		return false;

	}

	@Override
	public int swap(ArrayList<ArrayList<Integer>> cache, int curSize, int item) {
		// using MRU, so the element with highest time must be removed
		ArrayList<Integer> allRequestingBlocks = generateObj.getCorrespondingBlocks(item); 
		//SimulatorLogger.writeLog(name, "SWAPPING OUT because of item " + item, infoLevel);
		while ((curSize + allRequestingBlocks.size()) > cacheSize ){
			//System.out.println("CACHE ALGO: Cache is full, need to remove some items");
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
			accessTimes.add(default_access_time);
			curSize += theBlocks.size();
		}
		else{
			SimulatorLogger.writeLog(name, "Something went wrong before add because cache is full "
					+ "and we are trying to add to it", severeLevel);
			System.err.println("Something went wrong before add because cache is full and we are trying to add to it");
		}
		
		return curSize;
	}

	@Override
	public int remove(ArrayList<ArrayList<Integer>> cache, int curSize) {
		//System.out.println("CACHE ALGO: Removing most recently used element");
		//SimulatorLogger.writeLog(name, "Removing most recently used element", infoLevel);
		
		// Finding most recently used item
		int max = Integer.MIN_VALUE;
		int maxPos = -1;
		for ( int index = 0; index < accessTimes.size(); index++ ){
			if(accessTimes.get(index) > max ){
				max = accessTimes.get(index);
				maxPos = index;
			}
		}
		
		ArrayList<Integer> items = cache.remove(maxPos);
		int timeRemoved = accessTimes.remove(maxPos);
		curSize -= items.size();
		return curSize;
		//System.out.println("CACHE ALGORITHM: Removed " + item + " with time " + timeRemoved);
		//SimulatorLogger.writeLog(name, "Removed " + item + " with time " + timeRemoved, infoLevel);
	}

}
