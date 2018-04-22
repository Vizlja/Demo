/**
 * 
 */
package tes.iva.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Ratio entity.
 * 
 * @author Vizlja
 */
@Entity
public class Ratio {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "ratio", nullable = false)
	private Double ratio;

	@Column(name = "profileMonth", nullable = false, unique = true)
	private String profileMonth;

	/**
	 * @return the ratio
	 */
	public Double getRatio() {
		return ratio;
	}

	/**
	 * @param ratio
	 *            the ratio to set
	 */
	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the profileMonth
	 */
	public String getProfileMonth() {
		return profileMonth;
	}

	/**
	 * @param profileMonth
	 *            the profileMonth to set
	 */
	public void setProfileMonth(String profileMonth) {
		this.profileMonth = profileMonth;
	}
}