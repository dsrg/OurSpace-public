package org.dsrg.ourSpace.wea.domLogic;

import java.util.Date;

/**
 * Factory used to create java.util.Date's that have only the precision for this
 * application (namely rounded to the nearest second).
 * 
 * @author Patrice Chalin
 * 
 */
public class DateFactory {

	private DateFactory() {
	}

	private static long truncMilli(long x) {
		return (x / 1000) * 1000;
	}

	/**
	 * @return date & time now, but rounded to the nearest second.
	 */
	public static Date newDate() {
		long now = System.currentTimeMillis();
		return newDate(now);
	}

	/**
	 * @param t
	 * @return date & time represented by t, but rounded to the nearest second.
	 */
	public static Date newDate(long t) {
		return new Date(truncMilli(t));
	}

}
