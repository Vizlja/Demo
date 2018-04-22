/**
 * 
 */
package tes.iva.demo.dao;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tes.iva.demo.model.Reading;
import tes.iva.demo.repository.ReadingRepository;

/**
 * Implemeration of {@link ReadingsDao} interface.
 * 
 * @author Vizlja
 */
@Service
public class ReadingsDaoImpl implements ReadingsDao {

	private final ReadingRepository readingRepository;

	@Autowired
	ReadingsDaoImpl(ReadingRepository readingRepository) {
		this.readingRepository = readingRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.ReadingsDao#saveAll(java.util.Set)
	 */
	@Override
	@Transactional
	public void saveAll(Set<Reading> inputSet) {

		if (this.readingRepository.findByConnectionId(inputSet.iterator().next().getConnectionId()).isEmpty()) {

			this.readingRepository.saveAll(inputSet);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.ReadingsDao#findAll()
	 */
	@Override
	@Transactional
	public List<Reading> findAll() {

		return this.readingRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.ReadingsDao#findByConnection(java.lang.String)
	 */
	@Override
	@Transactional
	public List<Reading> findByConnection(String connectionId) {

		return this.readingRepository.findByConnectionId(connectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.ReadingsDao#deleteByConnection(java.lang.String)
	 */
	@Override
	@Transactional
	public int deleteByConnection(String connectionId) {

		return this.readingRepository.deleteByConnectionId(connectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.ReadingsDao#deleteAll()
	 */
	@Override
	@Transactional
	public void deleteAll() {

		this.readingRepository.deleteAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tes.iva.demo.dao.ReadingsDao#findLastReading(java.lang.String,java.lang.
	 * Integer,java.lang. String)
	 */
	@Override
	@Transactional
	public Reading findReading(String connectionId, int year, String month) {

		return this.readingRepository.findByConnectionIdAndYearAndMonth(connectionId, year, month);
	}
}
