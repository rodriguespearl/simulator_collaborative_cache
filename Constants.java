//package com.collabcache.setup;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
	
	public static String infoLevel = "INFO";
	public static String severeLevel = "SEVERE";
	public static String name = "CONSTANTS";
	
	public static int endClient;
	public static int increaseBy;
	public static int clientCacheSize;
	public static int serverCachSize;
	public static String algo = new String();
	public static int tick_periodic_increase;
	public static int tick_increase_own_cache;
	public static int tick_increase_other_client;
	public static int tick_increase_server_cache;
	public static int tick_increase_server_disk;
	public static String traceFile = new String();
	public static int tick_increase_processing_time;
	public static int tick_increase_waiting_for_block;
	public static int tick_increase_block_retrieve;
	
	public static void setConstants(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			endClient = Integer.parseInt(prop.getProperty("client_end_id"));
			increaseBy = Integer.parseInt(prop.getProperty("increase_client_size_by"));
			clientCacheSize = Integer.parseInt(prop.getProperty("client_cache_size"));
			serverCachSize = Integer.parseInt(prop.getProperty("server_cache_size"));
			algo = prop.getProperty("cache_algo");
			tick_periodic_increase = Integer.parseInt(prop.getProperty("tick_periodic_increase"));
			tick_increase_own_cache = Integer.parseInt(prop.getProperty("tick_increase_owncache"));
			tick_increase_other_client = Integer.parseInt(prop.getProperty("tick_increase_otherclientcache"));
			tick_increase_server_cache = Integer.parseInt(prop.getProperty("tick_increase_servercache"));
			tick_increase_server_disk = Integer.parseInt(prop.getProperty("tick_increase_serverdisk"));
			tick_increase_processing_time = Integer.parseInt(prop.getProperty("tick_increase_processing_time"));
			tick_increase_waiting_for_block = Integer.parseInt(prop.getProperty("tick_increase_waiting_for_block"));
			tick_increase_block_retrieve = Integer.parseInt(prop.getProperty("tick_increase_block_retrieve"));
			traceFile = prop.getProperty("trace_file");

			
			//System.out.println("CONSTANTS HAVE BEEN READ AND SET");
			SimulatorLogger.writeLog(name, "CONSTANTS HAVE BEEN READ AND SET", infoLevel);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
