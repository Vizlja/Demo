/**
 * 
 */
package tes.iva.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tes.iva.demo.model.Reading;

/**
 * @author Vizlja
 *
 */
@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long>{
	
	List<Reading> findByConnectionId(String connectionId);
	
	Reading findByConnectionIdAndYearAndMonth(String connectionId, int year, String month);
	
	int deleteByConnectionId(String connectionId);
}
