package net.darylb.stressor;

import java.text.NumberFormat;

public class Average {

	private int count = 0;
	double total = 0.0;
	
	public int add(double value) {
		total += value;
		count++;
		return getCount();
	}
	
	NumberFormat nf = NumberFormat.getNumberInstance();
	
	public String toString() {
		nf.setMinimumFractionDigits(2);
		return nf.format(getAverage());
	}

	public int getCount() {
		return count;
	}

	public double getAverage() {
		return total / (double)getCount();
	}

	public double getTotal() {
		return total;
	}

}
