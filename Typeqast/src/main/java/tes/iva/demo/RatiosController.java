/**
 * 
 */
package tes.iva.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import tes.iva.demo.exception.AppException;
import tes.iva.demo.exception.BadRequestException;
import tes.iva.demo.exchange.ExchangeRatio;
import tes.iva.demo.model.Ratio;

/**
 * Rest Controller for all ratio actions.
 * 
 * @author Vizlja
 */
@RestController
@RequestMapping("/ratios")
public class RatiosController {

	private static final Logger LOG = LoggerFactory.getLogger(RatiosController.class);
	private static final Set<String> VALID_MONTHS = new HashSet<>(
			(Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")));

	@Autowired
	private RatiosDao ratiosDao;

	/**
	 * Inserts ratios.
	 * 
	 * @param request
	 * @param bindingResult
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
	void insertRatios(@RequestBody List<ExchangeRatio> request, BindingResult bindingResult) {

		LOG.info(String.format("Saving ratios..."));
		String errorMessage = "";

		try {

			Assert.isTrue(!bindingResult.hasErrors(), bindingResult.getAllErrors().toString());
			Assert.notEmpty(request, "List of ratios is empty!");
		} catch (final Exception e) {

			throw new BadRequestException(e.getMessage());
		}
		Set<String> distinctProfiles = new HashSet<>();

		for (ExchangeRatio exchangeRatio : request) {

			distinctProfiles.add(exchangeRatio.getProfile());
		}

		for (final String profile : distinctProfiles) {

			final List<ExchangeRatio> tempProfileList = new ArrayList<>();

			for (ExchangeRatio exchangeRatio : request) {

				if (profile.equals(exchangeRatio.getProfile())) {

					tempProfileList.add(exchangeRatio);
				}
			}

			try {

				final Set<Ratio> inputSet = this.validateProfileRatios(tempProfileList);
				this.ratiosDao.saveOrUpdateRatios(inputSet);
				LOG.info(String.format("Saved ratios for profile %s!", profile));
			} catch (final Exception e) {

				errorMessage = errorMessage.concat(String.format(
						"Invalid data for profile %s! Profile data will be discarded! Error message: %s\n", profile,
						e.getMessage()));
				LOG.error(
						String.format("Invalid data for profile %s! Profile data will be discarded! Error message: %s",
								profile, e.getMessage()));
			}
		}

		if (!errorMessage.isEmpty()) {

			throw new BadRequestException(errorMessage);
		}
	}

	/**
	 * Validates input ratios for single profile and maps it to {@link Ratio}
	 * objects.
	 * 
	 * @param profileRatios
	 * @return Set<Ratio>
	 */
	private Set<Ratio> validateProfileRatios(List<ExchangeRatio> profileRatios) {

		Assert.isTrue(profileRatios.size() == 12, "There is no 12 ratios but " + profileRatios.size());
		BigDecimal sumOfRations = BigDecimal.valueOf(0.0D);
		String profileCheck = null;
		final Set<Ratio> inputSet = new HashSet<>();

		for (ExchangeRatio exchangeRatio : profileRatios) {

			Assert.isTrue(VALID_MONTHS.contains(exchangeRatio.getMonth()),
					"Invalid month: " + exchangeRatio.getMonth());
			Ratio ratio = new Ratio();
			sumOfRations = sumOfRations.add(BigDecimal.valueOf(exchangeRatio.getRatio()));
			ratio.setRatio(exchangeRatio.getRatio());
			ratio.setProfileMonth(exchangeRatio.getProfile().concat("_").concat(exchangeRatio.getMonth()));

			if (profileCheck == null) {

				profileCheck = exchangeRatio.getProfile();
			} else {

				Assert.isTrue(profileCheck.equals(exchangeRatio.getProfile()), "Ratios for different profiles sent!");
			}
			inputSet.add(ratio);
		}
		Assert.isTrue(sumOfRations.doubleValue() == 1.0D, "Sum of ratios is not 1!");

		return inputSet;
	}

	/**
	 * Gets all ratios.
	 * 
	 * @return List<ExchangeRatio>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getall", produces = MediaType.APPLICATION_JSON_VALUE)
	List<ExchangeRatio> getAllRatios() {

		LOG.info(String.format("Getting all ratios..."));
		List<Ratio> resultList = null;

		try {

			resultList = this.ratiosDao.findAll();
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}
		List<ExchangeRatio> exchangeRatioList = new ArrayList<>();

		if (resultList != null && !resultList.isEmpty()) {

			for (Ratio ratio : resultList) {

				ExchangeRatio exRatio = new ExchangeRatio();
				exRatio.setRatio(ratio.getRatio());
				String[] profAndMon = ratio.getProfileMonth().split("_");
				exRatio.setProfile(profAndMon[0]);
				exRatio.setMonth(profAndMon[1]);
				exchangeRatioList.add(exRatio);
			}
		}

		return exchangeRatioList;
	}

	/**
	 * Gets ratios denoted by given profile.
	 * 
	 * @param profile
	 * @return List<ExchangeRatio>
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getbyprofile/{profile}", produces = MediaType.APPLICATION_JSON_VALUE)
	List<ExchangeRatio> getProfileRatios(@PathVariable String profile) {

		LOG.info(String.format("Getting ratios for profile %s...", profile));
		List<Ratio> resultList = null;

		try {

			resultList = this.ratiosDao.findByProfile(profile);
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}
		List<ExchangeRatio> exchangeRatioList = new ArrayList<>();

		if (resultList != null && !resultList.isEmpty()) {

			for (Ratio ratio : resultList) {

				ExchangeRatio exRatio = new ExchangeRatio();
				exRatio.setRatio(ratio.getRatio());
				String[] profAndMon = ratio.getProfileMonth().split("_");
				exRatio.setProfile(profAndMon[0]);
				exRatio.setMonth(profAndMon[1]);
				exchangeRatioList.add(exRatio);
			}

		}

		return exchangeRatioList;
	}

	/**
	 * Deletes ratios denoted by given profile.
	 * 
	 * @param profile
	 * @return number of deleted rows
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/deletebyprofile/{profile}")
	int deleteProfileRatios(@PathVariable String profile) {

		LOG.info(String.format("Deleting ratios for profile %s...", profile));
		int deletedRows = 0;

		try {

			deletedRows = this.ratiosDao.deleteByProfile(profile);
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}

		return deletedRows;
	}

	/**
	 * Deletes all ratios.
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteall")
	void deleteAllRatios() {

		LOG.info(String.format("Deleting all ratios..."));

		try {

			this.ratiosDao.deleteAll();
		} catch (final Exception e) {

			throw new AppException(e.getMessage());
		}
	}
}
