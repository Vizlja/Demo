/**
 * 
 */
package tes.iva.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tes.iva.demo.dao.RatiosDao;
import tes.iva.demo.dao.ReadingsDao;
import tes.iva.demo.exception.AppException;
import tes.iva.demo.exception.BadRequestException;
import tes.iva.demo.exchange.ExchangeReading;
import tes.iva.demo.model.Ratio;
import tes.iva.demo.model.Reading;

/**
 * Rest Controller for all Reading actions.
 * 
 * @author Vizlja
 */
@RestController
@RequestMapping("/readings")
public class ReadingsController {

	private static final Logger LOG = LoggerFactory.getLogger(ReadingsController.class);
	private static final Map<Integer, String> VALID_MONTHS = new HashMap<>();
	@Autowired
	private ReadingsDao readingsDao;
	@Autowired
	private RatiosDao ratiosDao;

	/**
	 * Init method.
	 */
	@PostConstruct
	private void init() {

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

	/**
	 * Inserts readings.
	 * 
	 * @param request
	 * @param year
	 * @param bindingResult
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/insert/{year}", consumes = MediaType.APPLICATION_JSON_VALUE)
	void insertReadings(@RequestBody List<ExchangeReading> request, @PathVariable int year,
			BindingResult bindingResult) {

		LOG.info("Saving readings...");
		String errorMessage = "";

		try {

			Assert.isTrue(!bindingResult.hasErrors(), bindingResult.getAllErrors().toString());
			Assert.notEmpty(request, "List of readings is empty!");
		} catch (final Exception e) {

			throw new BadRequestException(e.getMessage());
		}
		final Set<String> distinctConnections = new HashSet<>();

		for (ExchangeReading exchangeReading : request) {

			distinctConnections.add(exchangeReading.getConnectionId());
		}

		for (final String connection : distinctConnections) {

			final List<ExchangeReading> tempConnList = new ArrayList<>();

			for (ExchangeReading exchangeReading : request) {

				if (connection.equals(exchangeReading.getConnectionId())) {

					tempConnList.add(exchangeReading);
				}
			}

			try {

				final Set<Reading> inputSet = this.validateConnectionReadings(tempConnList, year);
				this.readingsDao.saveAll(inputSet);
				LOG.info(String.format("Saved readings for connection %s!", connection));
			} catch (final Exception e) {

				errorMessage = errorMessage.concat(String.format(
						"Invalid data for connection %s! Connection data will be discarded! Error message: %s\n",
						connection, e.getMessage()));
				LOG.error(String.format(
						"Invalid data for connection %s! Connection data will be discarded! Error message: %s",
						connection, e.getMessage()));
			}
		}

		if (!errorMessage.isEmpty()) {

			throw new BadRequestException(errorMessage);
		}
	}

	/**
	 * Validates readings for single connectionId.
	 * 
	 * @param request
	 * @param year
	 * @return Set<Reading>
	 */
	private Set<Reading> validateConnectionReadings(List<ExchangeReading> request, int year) {

		Assert.isTrue(request.size() == 12, "There is no 12 readings but " + request.size());
		final List<Ratio> profileRatios = this.ratiosDao.findByProfile(request.get(0).getProfile());
		Assert.isTrue(profileRatios.size() == 12, "There are no ratios for profile " + request.get(0).getProfile());

		String profileCheck = null;

		for (ExchangeReading er : request) {

			Assert.isTrue(VALID_MONTHS.containsValue(er.getMonth()), "Invalid month: " + er.getMonth());

			if (profileCheck == null) {

				profileCheck = er.getProfile();
			} else {

				Assert.isTrue(profileCheck.equals(er.getProfile()),
						String.format("Readings for connection %s contain multiple profiles!", er.getConnectionId()));
			}
		}
		// input validation done, now check reading values
		// for evaluation of readings and tolerances we need readings for 13
		// months
		// if reading is done first day of a month it is impossible to calculate
		// consumption for entire year
		//
		// For demo purposes I will implement according to next assumptions...
		// assumption: reading is done last day of a month
		// assumption: if there's no reading from December previous year we
		// start from 0

		Reading lastReading = this.readingsDao.findReading(request.get(0).getConnectionId(), year - 1, "DEC");

		int initialReading = 0;

		if (lastReading != null) {

			initialReading = lastReading.getReading();
		}
		ExchangeReading previousReading = null;
		ExchangeReading currentReading = null;
		int totalConsumption = 0;
		Map<String, Integer> consumptionsPerMonth = new HashMap<>();
		Map<String, Double> ratiosPerMonth = new HashMap<>();
		Map<String, ExchangeReading> readingsPerMonth = new HashMap<>();

		for (int i = 1; i <= 12; i++) {

			for (ExchangeReading er : request) {

				if (er.getMonth().equals(VALID_MONTHS.get(i))) {

					currentReading = er;
					readingsPerMonth.put(VALID_MONTHS.get(i), er);
					break;
				}
			}

			for (Ratio ratio : profileRatios) {

				if (ratio.getProfileMonth().split("_")[1].equals(VALID_MONTHS.get(i))) {

					ratiosPerMonth.put(VALID_MONTHS.get(i), ratio.getRatio());
					break;
				}
			}

			if (previousReading == null) {

				previousReading = currentReading;
				Assert.isTrue(initialReading <= currentReading.getReading(),
						"Reading for JAN is lower than reading for DEC last year!");
				consumptionsPerMonth.put(currentReading.getMonth(), currentReading.getReading() - initialReading);
			} else {

				Assert.isTrue(previousReading.getReading() <= currentReading.getReading(),
						String.format("Reading for %s is lower than reading for %s!", currentReading.getMonth(),
								previousReading.getMonth()));
				consumptionsPerMonth.put(currentReading.getMonth(),
						currentReading.getReading() - previousReading.getReading());
			}

			if (i == 12) {

				totalConsumption = currentReading.getReading() - initialReading;
			}
			previousReading = currentReading;
		}

		final Set<Reading> inputSet = this.checkTolerance(totalConsumption, consumptionsPerMonth, ratiosPerMonth,
				readingsPerMonth, year);

		return inputSet;
	}

	/**
	 * Checks consumptions according to profile tolerances and maps input
	 * readings to {@link Reading} objects.
	 * 
	 * @param totalConsumption
	 * @param consumptionsPerMonth
	 * @param ratiosPerMonth
	 * @param exchangeMap
	 * @param year
	 * @return Set<Reading>
	 */
	private Set<Reading> checkTolerance(int totalConsumption, Map<String, Integer> consumptionsPerMonth,
			Map<String, Double> ratiosPerMonth, Map<String, ExchangeReading> exchangeMap, int year) {

		final Set<Reading> inputSet = new HashSet<>();

		for (int i = 1; i <= 12; i++) {

			String currentMonth = VALID_MONTHS.get(i);
			double currentRatio = ratiosPerMonth.get(currentMonth);
			double minConsumption = (totalConsumption * currentRatio) * 0.75;
			double maxConsumption = (totalConsumption * currentRatio) * 1.25;
			double currentConsumption = (double) consumptionsPerMonth.get(currentMonth);

			Assert.isTrue(minConsumption < currentConsumption,
					String.format("Consumption for %s is lower then alowed! ", currentMonth));
			Assert.isTrue(maxConsumption > currentConsumption,
					String.format("Consumption for %s is higher then alowed! ", currentMonth));

			Reading reading = new Reading();
			ExchangeReading exReading = exchangeMap.get(currentMonth);
			reading.setConnectionId(exReading.getConnectionId());
			reading.setMonth(currentMonth);
			reading.setProfile(exReading.getProfile());
			reading.setReading(exReading.getReading());
			reading.setYear(year);
			inputSet.add(reading);
		}

		return inputSet;
	}

	/**
	 * Gets all readings.
	 * 
	 * @return List<Reading>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getall", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Reading> getAllReadings() {

		LOG.info("Getting all readings...");
		List<Reading> resultList = null;

		try {

			resultList = this.readingsDao.findAll();
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}

		return resultList;
	}

	/**
	 * Gets readings denoted by given connectionId.
	 * 
	 * @param connectionId
	 * @return List<Reading>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getbyconn/{connectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Reading> getConnectionReadings(@PathVariable String connectionId) {

		LOG.info(String.format("Getting all readings for connection %s...", connectionId));
		List<Reading> resultList = null;

		try {

			resultList = this.readingsDao.findByConnection(connectionId);
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}

		return resultList;
	}

	/**
	 * Gets single reading denoted by given connectionId, year and month.
	 * 
	 * @param connectionId
	 * @param year
	 * @param month
	 * @return List<Reading>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getone/{connectionId}/{year}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
	Reading getOneReading(@PathVariable String connectionId, @PathVariable int year, @PathVariable String month) {

		LOG.info(String.format("Getting single reading for connection %s, year %s and month %s...", connectionId, year,
				month));
		Reading result = null;

		try {

			result = this.readingsDao.findReading(connectionId, year, month.toUpperCase());
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}

		return result;
	}

	/**
	 * Deletes readings denoted by given connectionId.
	 * 
	 * @param connectionId
	 * @return number of deleted rows
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/deletebyconn/{connectionId}")
	int deleteConnectionReadings(@PathVariable String connectionId) {

		LOG.info(String.format("Deleting all readings for connection %s...", connectionId));
		int deletedRows = 0;

		try {

			deletedRows = this.readingsDao.deleteByConnection(connectionId);
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}

		return deletedRows;
	}

	/**
	 * Deletes all readings.
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteall")
	void deleteAllReadings() {

		LOG.info("Deleting all readings...");

		try {

			this.readingsDao.deleteAll();
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}
	}
}
