/**
 * 
 */
package tes.iva.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tes.iva.demo.model.Ratio;

/**
 * @author Vizlja
 *
 */
@Repository
public interface RatioRepository extends JpaRepository<Ratio, Long> {

	Ratio findByProfileMonth(String profileMonth);
}
