//package com.collabcache.setup;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTraceFile {

	static final String FILENAME = Constants.traceFile;

	public static ArrayList<Integer> blocksPresent;

	public static void setBlocksPresent(){
		blocksPresent = new ArrayList<>();
	}

	public static ArrayList<ArrayList<ArrayList<Integer>>> buildBlockList(){
		BufferedReader br = null;
		FileReader fr = null;

		ArrayList<ArrayList<Integer>> blocks = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<Integer>>> allBlocks = new ArrayList<>();

		setBlocksPresent();

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.equals("")){
					blocks = new ArrayList<>();
					String[] withoutBlockSize = sCurrentLine.split("\\-"); // separating block size from the block instances
					ArrayList<Integer> block = new ArrayList<>();
					block.add(Integer.parseInt(withoutBlockSize[0]));
					blocks.add(block);
					String[] splitLine = withoutBlockSize[1].split("\\| ");
					for( String s: splitLine ){
						String eachBlock[] = s.split(",");
						ArrayList<Integer> blockNumbers = new ArrayList<>();
						for( String blockNum: eachBlock ){
							blockNum = blockNum.trim(); // getting rid of leading and trailing white spaces
							blockNumbers.add(Integer.parseInt(blockNum));
							blocksPresent.add(Integer.parseInt(blockNum));
						}
						blocks.add(blockNumbers);
					}
					allBlocks.add(blocks);
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return allBlocks;
	}
}
