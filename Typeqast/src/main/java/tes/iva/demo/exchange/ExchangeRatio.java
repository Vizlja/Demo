/**
 * 
 */
package tes.iva.demo.exchange;

/**
 * Input ratio object based on legacy format.
 * 
 * @author Vizlja
 */
public class ExchangeRatio {

	private String Month;
	private String Profile;
	private double Ratio;

	/**
	 * @return the month
	 */
	public String getMonth() {
		return Month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month) {
		Month = month;
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return Profile;
	}

	/**
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(String profile) {
		Profile = profile;
	}

	/**
	 * @return the ratio
	 */
	public double getRatio() {
		return Ratio;
	}

	/**
	 * @param ratio
	 *            the ratio to set
	 */
	public void setRatio(double ratio) {
		Ratio = ratio;
	}
}
