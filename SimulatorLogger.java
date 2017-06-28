//package com.collabcache.setup;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SimulatorLogger {
	static Logger logger = Logger.getLogger("MyLog");  
	static FileHandler fh;  
	
	public static void startLogger(){
		try {  
			// This block configure the logger with handler and formatter  
			fh = new FileHandler("SimulatorLogFile.log");  
			logger.setUseParentHandlers(false); // to ensure it does not print on the console
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

			// the following statement is used to log any messages  
			logger.info("Starting Logs");  

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}
	}
	
	public static void writeLog(String from, String message, String level){
		switch(level){
		case "INFO":
			logger.info(from.toUpperCase() + " : "+ message);
			break;
		case "SEVERE":
			logger.severe(from.toUpperCase() + " : "+ message);
			break;
		}
	}

}
