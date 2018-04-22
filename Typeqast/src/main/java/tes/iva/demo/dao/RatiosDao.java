/**
 * 
 */
package tes.iva.demo.dao;

import java.util.List;
import java.util.Set;

import tes.iva.demo.model.Ratio;

/**
 * Interface for {@link Ratio} database access.
 * 
 * @author Vizlja
 */
public interface RatiosDao {

	/**
	 * Saves or updates given ratios.
	 * 
	 * @param inputSet
	 */
	public void saveOrUpdateRatios(Set<Ratio> inputSet);

	/**
	 * Finds all ratios.
	 * 
	 * @return List<Ratio>
	 */
	public List<Ratio> findAll();

	/**
	 * Finds ratios denoted by given profile.
	 * 
	 * @param profile
	 * @return List<Ratio>
	 */
	public List<Ratio> findByProfile(String profile);

	/**
	 * Deletes ratios denoted by given profile.
	 * 
	 * @param profile
	 * @return number of deleted rows
	 */
	public int deleteByProfile(String profile);

	/**
	 * Deletes all ratios.
	 */
	public void deleteAll();
}
