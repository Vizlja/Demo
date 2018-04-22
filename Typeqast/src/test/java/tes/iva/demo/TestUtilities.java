/**
 * 
 */
package tes.iva.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tes.iva.demo.exchange.ExchangeRatio;
import tes.iva.demo.exchange.ExchangeReading;

/**
 * @author Vizlja
 *
 */
public final class TestUtilities {

	public static final Map<Integer, String> VALID_MONTHS = new HashMap<>();

	public static void init() {

		VALID_MONTHS.put(1, "JAN");
		VALID_MONTHS.put(2, "FEB");
		VALID_MONTHS.put(3, "MAR");
		VALID_MONTHS.put(4, "APR");
		VALID_MONTHS.put(5, "MAY");
		VALID_MONTHS.put(6, "JUN");
		VALID_MONTHS.put(7, "JUL");
		VALID_MONTHS.put(8, "AUG");
		VALID_MONTHS.put(9, "SEP");
		VALID_MONTHS.put(10, "OCT");
		VALID_MONTHS.put(11, "NOV");
		VALID_MONTHS.put(12, "DEC");
	}

	public static List<ExchangeRatio> generateValidRatios() {

		List<ExchangeRatio> input = new ArrayList<>();

		for (int i = 1; i <= 12; i++) {

			ExchangeRatio er = new ExchangeRatio();
			er.setProfile("A");
			er.setMonth(VALID_MONTHS.get(i));
			if (i > 10) {

				er.setRatio(0.05);
			} else {

				er.setRatio(0.09);
			}
			input.add(er);
		}

		return input;
	}

	public static List<ExchangeReading> generateValidReadings() {

		List<ExchangeReading> input = new ArrayList<>();
		int reading = 0;

		for (int i = 1; i <= 12; i++) {

			ExchangeReading er = new ExchangeReading();
			er.setProfile("A");
			er.setConnectionId("0001");
			er.setMonth(VALID_MONTHS.get(i));
			if (i > 10) {
				
				reading += 5;
				er.setReading(reading);
			} else {

				reading += 9;
				er.setReading(reading);
			}
			input.add(er);
		}

		return input;
	}
}
