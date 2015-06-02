package net.darylb.stressor;

public enum Interval {
	SECOND(1000L),
	MINUTE(1000L * 60L),
	HOUR(1000L * 60L * 60L),
	DAY(1000L * 60L * 60L * 24L);
	long ms;
	Interval(long ms) {
		this.ms = ms;
	}
	public long getIntervalMs() {
		return ms;
	}
	public static Interval getInterval(String s) {
		return getInterval(s.charAt(0));
	}
	public static Interval getInterval(char s) {
		if(s == 's') {
			return SECOND;
		}
		else if (s == 'm') {
			return MINUTE;
		}
		else if (s == 'h') {
			return HOUR;
		}
		else if (s == 'd') {
			return DAY;
		}
		return null;
	}
}