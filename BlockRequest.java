import java.util.ArrayList;

public class BlockRequest extends Thread{

	Client client;
	ArrayList<Integer> blocksNeeded;
	ArrayList<Integer> blocksReceived;
	ArrayList<Double> startTimes;
	ArrayList<Double> endTimes;
	int clientID;
	int currentBlockIndex;
	boolean gotBlocks;
	Tick tickObj;

	public void init(Client client, ArrayList<Integer> blockList, ArrayList<Integer> blocksReceived, 
			int clientID, Tick tickObj, ArrayList<Double> startTimes, ArrayList<Double> endTimes){
		this.client = client;
		currentBlockIndex = 0;
		this.clientID = clientID;
		this.blocksNeeded = blockList;
		this.blocksReceived = blocksReceived;
		this.gotBlocks = false;
		this.tickObj = tickObj;
		this.startTimes = startTimes;
		this.endTimes = endTimes;
	}

	public void getBlock(int currentBlockIndex){
		
		int blockNeeded = blocksNeeded.get(currentBlockIndex);
		retrieveBlock(blockNeeded);

	}

	public void retrieveBlock(int blockNeeded){

		try{
			
			int request = client.checkForBlockInOwnCache(blockNeeded); //first check own cache
			
			if(request == -1)
			{
				//first checking in other clients
				request = Master.getBlockFromClientCache(clientID, blockNeeded);
				tickObj.increaseTickerBy(Constants.tick_increase_other_client);

				if ( request == -1 ){ // didn't find in another client
					tickObj.increaseTickerBy(Constants.tick_increase_server_cache);

					request = Master.checkForBlockFromServerCache(clientID, blockNeeded);

					if(request == -1)// didn't find in server cache
					{
						tickObj.increaseTickerBy(Constants.tick_increase_server_disk);
					}
				}
			}
			
			
			tickObj.increaseTickerBy(Constants.tick_increase_block_retrieve);
			Thread.sleep(Constants.tick_increase_block_retrieve);
			blocksReceived.add(blockNeeded);
		}
		catch(InterruptedException e){
			SimulatorLogger.writeLog("Block Request", e.getMessage(), "SEVERE");
		}
	}

	public void run(){
		while(blocksReceived.size() < blocksNeeded.size()){
			this.startTimes.add(tickObj.getTicker());
			getBlock(currentBlockIndex);
			this.endTimes.add(tickObj.getTicker());
			currentBlockIndex++;
		}
		gotBlocks = true;
	}

}
