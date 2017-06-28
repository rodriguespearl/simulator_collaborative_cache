//package com.collabcache.tick;

public class Tick extends Thread{

	double ticker = 0;
	static boolean runTicker = true;

	public Tick(){
		//runTicker = true;
		ticker = 0;
	}
	
	public void increaseTickerBy(int num){
		ticker += num;
	}

	public double getTicker(){
		return ticker;
	}

	public void setTickerFlag(){
		runTicker = true;
	}

	public void stopTickerFlag(){
		runTicker = false;
	}

	public void run(){
		while(runTicker){
			try{
				Thread.sleep(Constants.tick_periodic_increase);
				ticker+=Constants.tick_periodic_increase;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
