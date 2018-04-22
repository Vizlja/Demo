/**
 * 
 */
package tes.iva.demo.dao;

import java.util.List;
import java.util.Set;

import tes.iva.demo.model.Reading;

/**
 * Interface for {@link Reading} database access.
 * 
 * @author Vizlja
 */
public interface ReadingsDao {

	/**
	 * Saves or updates given readings.
	 * 
	 * @param inputSet
	 */
	public void saveAll(Set<Reading> inputSet);

	/**
	 * Finds all readings.
	 * 
	 * @return List<Reading>
	 */
	public List<Reading> findAll();

	/**
	 * Finds readings denoted by given connectionId.
	 * 
	 * @param connectionId
	 * @return List<Reading>
	 */
	public List<Reading> findByConnection(String connectionId);

	/**
	 * Deletes readings denoted by given connectionId.
	 * 
	 * @param connection
	 * @return number of deleted rows
	 */
	public int deleteByConnection(String connection);

	/**
	 * Deletes all readings.
	 */
	public void deleteAll();

	/**
	 * Finds single reading denoted by given connectionId, year and month.
	 * 
	 * @param connectionId
	 * @param year
	 * @param month
	 * @return {@link Reading}
	 */
	public Reading findReading(String connectionId, int year, String month);
}
