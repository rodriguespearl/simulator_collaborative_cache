//package com.collabcache.setup;
import java.util.ArrayList;

public abstract class CacheReplacementAlgorithm {
	
	public static String infoLevel = "INFO";
	public static String severeLevel = "SEVERE";
	
	public abstract int initCache(int clientID, ArrayList<ArrayList<Integer>> cache);
	public abstract int swap(ArrayList<ArrayList<Integer>> cache, int curSize, int item);
	public abstract int remove(ArrayList<ArrayList<Integer>> cache, int curSize);
	public abstract int add(ArrayList<ArrayList<Integer>> cache, ArrayList<Integer> theBlocks, int curSize, int item);
	public abstract boolean checkForBlock(ArrayList<ArrayList<Integer>> cache, int item);

}
