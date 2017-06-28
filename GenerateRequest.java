//package com.collabcache.actors;

/*import com.collabcache.setup.*;
import com.collabcache.algos.*;
import com.collabcache.actors.*;
import com.collabcache.tick.*;*/

import java.util.ArrayList;
import java.util.Random;

public class GenerateRequest {

	ArrayList<ArrayList<ArrayList<Integer>>> allBlocks;
	ArrayList<Integer> blocksPresent;
	Random random;
	final int blockSizeIndex = 0;

	public GenerateRequest(){
		allBlocks = ReadTraceFile.buildBlockList();
		blocksPresent = ReadTraceFile.blocksPresent;
		random = new Random();
	}

	public int makeRequest(){
		
		int blockRequested = blocksPresent.get(random.nextInt(blocksPresent.size()));
		return blockRequested;
	}

	public int getBlockSize(int blockNum) {
		for( int index = 0; index < allBlocks.size(); index++ ){
			if ( index >= 1) { // first index is size
				for( ArrayList<Integer> blockList: allBlocks.get(index)){
					for( int val : blockList ){
						if ( val == blockNum ){
							return allBlocks.get(index).get(blockSizeIndex).get(blockSizeIndex); // it is the first element's first entry
						}
					}
				}
			}
		}
		return (Integer) null;
	}

	public ArrayList<Integer> getCorrespondingBlocks(int blockNum){
		//SimulatorLogger.writeLog("~~~~~~~GENERATE REQUEST~~~~~~~", "Generated request for " + blockNum, "INFO");
		int ctr;
		for( int index = 0; index < allBlocks.size(); index++ ){
			ctr = 0;
			for( ArrayList<Integer> blockList: allBlocks.get(index)){
				if( ctr == 0 ){ // first element will be block size
					ctr++;
				}
				else
				{
					for( int val : blockList ){
						if ( val == blockNum ){
							return blockList; 
						}
					}
				}

			}
		}

		//SimulatorLogger.writeLog("~~~~~~~GENERATE REQUEST~~~~~~~", "Returning NULL for " + blockNum, "INFO");
		return null;
	}


}
