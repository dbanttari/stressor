package net.darylb.stressor;

import java.util.List;
import java.util.Properties;

import net.darylb.stressor.actions.Props;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	private static Logger log = LoggerFactory.getLogger(Main.class);
	static Options options = new Options();

	public static CommandLine parseOptions(String[] args) throws ParseException {
		options.addOption("threads", true, "number of concurrent threads to test with, default 1");
		options.addOption("count", true, "number of story iterations to test. Either count or duration must be specified");
		options.addOption("duration", true, "duration of test, [n][t] where n is a number and t is a time increment: s,m, or h. Either count or duration must be specified");
		options.addOption("limit", true, "set rate limit, [n][t] where n is a number and t is a time increment: s,m, or h");
		options.addOption("help", false, "print this help text");
		options.addOption("?", false, "print this help text");
		CommandLineParser parser = new DefaultParser();
		return parser.parse(options, args);
	}
	
	public static void main(String[] args) throws ParseException {
		CommandLine cmd = parseOptions(args);
		List<String> argsList = cmd.getArgList();
		if(argsList.size() != 1) {
			printHelp();
		}
		else {
			String className = argsList.get(0);
			Properties props = new Properties();
			Util.loadProperties(props);
			LoadTestDefinition testRunner;
			try {
				if(props.containsKey("stressor.package")) {
					testRunner = (LoadTestDefinition)Class.forName(props.getProperty(Props.STRESSOR_PACKAGE) + "." + className).newInstance();
				}
				else { 
					testRunner = (LoadTestDefinition)Class.forName(className).newInstance();
				}
			}
			catch(Exception e) {
				log.error("Could not instantiate TestRunner class", e);
				printHelp();
				return;
			}
			
			LoadTest loadTest = getTest(testRunner, cmd);
			
			if(loadTest==null) {
				printHelp();
				System.exit(1);
			}
			else {
				LoadTestResults results = loadTest.run();
				System.out.println(results.toString());
			}
	
		}
	}

	static LoadTest getTest(LoadTestDefinition testRunner, CommandLine cmd) {
		int threads;
		try {
			threads = Integer.parseInt(cmd.getOptionValue("threads", "1"));
		}
		catch(NumberFormatException e) {
			printHelp();
			return null;
		}

		LoadTest ret;
		if(cmd.hasOption("count")) {
			// run static number of tests
			LoadTestContext cx = testRunner.getLoadTestContext();
			cx.setRateLimiter(testRunner.getRateLimiter());
			ret = new FixedLoadTest(cx, testRunner.getStoryFactory(cx), threads, Integer.parseInt(cmd.getOptionValue("count")));
		}
		else if (cmd.hasOption("duration")) {
			// run for a specified duration
			LoadTestContext cx = testRunner.getLoadTestContext();
			cx.setRateLimiter(testRunner.getRateLimiter());
			ret = new TimedLoadTest(cx, testRunner.getStoryFactory(cx), threads, cmd.getOptionValue("duration"));
		}
		else {
			return null;
		}
		if(cmd.hasOption("limit")) {
			String option = cmd.getOptionValue("limit");
			int num = Integer.parseInt(option.substring(0, option.length()-1));
			Interval interval = Interval.getInterval(option.charAt(option.length()-1));
			RateLimiterImpl rateLimiter = new RateLimiterImpl(num, interval);
			ret.getLoadTestContext().setRateLimiter(rateLimiter);
		}

		return ret;
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java -jar stressor*.jar [options] testname", options );
	}
}
