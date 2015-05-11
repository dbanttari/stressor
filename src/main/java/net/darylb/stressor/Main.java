package net.darylb.stressor;

import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	private static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		if(args.length != 3) {
			printHelp();
		}
		else {
			LinkedList<String> argsList = new LinkedList<String>();
			for(String arg : args) {
				argsList.add(arg);
			}
			Properties props = new Properties();
			Util.loadProperties(props);
			TestDefinition testRunner;
			try {
				if(props.containsKey("stressor.package")) {
					testRunner = (TestDefinition)Class.forName(props.getProperty("stressor.package") + "." + argsList.remove()).newInstance();
				}
				else { 
					testRunner = (TestDefinition)Class.forName(argsList.remove()).newInstance();
				}
			}
			catch(Exception e) {
				log.error("Could not instantiate TestRunner class", e);
				printHelp();
				return;
			}
			
			LoadTest loadTest = getTest(testRunner, argsList);
			
			if(loadTest==null) {
				printHelp();
				return;
			}
			else {
				TestResults results = loadTest.run();
				System.out.println(results.toString());
			}
	
		}
	}

	static LoadTest getTest(TestDefinition testRunner, LinkedList<String> args) {
		int threads;
		try {
			threads = Integer.parseInt(args.remove());
		}
		catch(NumberFormatException e) {
			printHelp();
			return null;
		}

		String testDuration = args.remove();
		
		// run static number of tests
		Pattern p = Pattern.compile("^([0-9]+)$");
		Matcher match = p.matcher(testDuration);
		if(match.matches()) {
			return new FixedLoadTest(testRunner, threads, Integer.parseInt(match.group()));
		}
		
		// run for a specified duration
		p = Pattern.compile("^([0-9]+)([dhms])$");
		match = p.matcher(testDuration);
		if(match.matches()) {
			return new TimedLoadTest(testRunner, threads, testDuration);
		}
		
		return null;
	}

	private static void printHelp() {
		System.out.println("Usage: java -jar {loadtest}.jar {testname} [threads] [duration]");
		System.out.println("Where [duration] is in one of the following formats:");
		System.out.println("   integer (eg 100) : each thread performs (eg)100 tests");
		System.out.println("   duration[s/m/h] (eg 30m) : all threads run for a minimum of 30 minutes then exit at the end of the currently running test.");
		//System.out.println("   H:mm (eg 15:30) : all threads run until the next occurance of the specified time (in 24-hr clock format; eg 3pm = 15:00.)");
		System.exit(1);
	}
}
