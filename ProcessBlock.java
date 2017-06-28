import java.util.ArrayList;

public class ProcessBlock extends Thread {

	ArrayList<Integer> blocksNeeded;
	ArrayList<Integer> blocksReceived;
	int currentProcessingBlock;
	boolean processingDone;
	ArrayList<Double> startTimes;
	ArrayList<Double> endTimes;
	Tick tickObj;
	
	static String infoLevel = "INFO";
	static String severeLevel = "SEVERE";

	public void init(ArrayList<Integer> blocksNeeded, ArrayList<Integer> blocksReceived, Tick tickObj, 
			ArrayList<Double> startTimes, ArrayList<Double> endTimes){
		this.blocksNeeded = blocksNeeded;
		this.blocksReceived = blocksReceived;
		currentProcessingBlock = 0;
		processingDone = false;
		this.startTimes = startTimes;
		this.endTimes = endTimes;
		this.tickObj = tickObj;
	}

	public void processNextBlock(){
		/*

				//System.out.println("I have this block, and I can process it");
				//SimulatorLogger.writeLog("Processed Block " + currentProcessingBlock, "Done", "INFO");

				//tickObj.increaseTickerBy(Constants.tick_increase_processing_time);

				//System.out.println("I need to wait for my block to get here");
				//SimulatorLogger.writeLog("Processed Block waiting", "Done", "INFO");

				//tickObj.increaseTickerBy(Constants.tick_increase_waiting_for_block);
		 */
		boolean doneProcessingCurrentBlock = false;
		while(!doneProcessingCurrentBlock){
			try{
				if(currentProcessingBlock < blocksReceived.size()){
					//Process the block
					Thread.sleep(Constants.tick_increase_processing_time);
					tickObj.increaseTickerBy(Constants.tick_increase_processing_time);
					currentProcessingBlock++; //move to the next block
					doneProcessingCurrentBlock = true;
				}
				else{

					//waiting for block
					Thread.sleep(Constants.tick_increase_waiting_for_block);
					tickObj.increaseTickerBy(Constants.tick_increase_waiting_for_block);
				}

			}
			catch(InterruptedException e){
				SimulatorLogger.writeLog("Process Block", e.getMessage(), severeLevel);
			}
		}



	}

	public void run(){
		while(currentProcessingBlock < blocksNeeded.size()){
			this.startTimes.add(tickObj.getTicker());
			processNextBlock();
			this.endTimes.add(tickObj.getTicker());
		}
		processingDone = true;
		//SimulatorLogger.writeLog("Process Block", "Done", "INFO");
	}

}
