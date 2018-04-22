/**
 * 
 */
package tes.iva.demo.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tes.iva.demo.model.Ratio;
import tes.iva.demo.repository.RatioRepository;

/**
 * Implemeration of {@link RatiosDao} interface.
 * 
 * @author Vizlja
 */
@Service
public class RatiosDaoImpl implements RatiosDao {

	@PersistenceContext
	private EntityManager entityManager;
	private final RatioRepository ratioRepository;

	@Autowired
	RatiosDaoImpl(RatioRepository ratioRepository) {
		this.ratioRepository = ratioRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.RatiosDao#saveOrUpdateRatios(java.util.Set)
	 */
	@Override
	@Transactional
	public void saveOrUpdateRatios(Set<Ratio> inputSet) {

		if (this.ratioRepository.findByProfileMonth(inputSet.iterator().next().getProfileMonth()) != null) {

			for (Ratio ratio : inputSet) {

				CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
				CriteriaUpdate<Ratio> update = cb.createCriteriaUpdate(Ratio.class);
				Root<Ratio> e = update.from(Ratio.class);
				update.set("ratio", ratio.getRatio());
				update.where(cb.equal(e.get("profileMonth"), ratio.getProfileMonth()));

				this.entityManager.createQuery(update).executeUpdate();
			}
		} else {

			this.ratioRepository.saveAll(inputSet);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.RatiosDao#findAll()
	 */
	@Override
	@Transactional
	public List<Ratio> findAll() {

		return this.ratioRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.RatiosDao#findByProfile(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Ratio> findByProfile(String profile) {

		Query q = this.entityManager.createQuery("select r from Ratio r where r.profileMonth like :profile");
		q.setParameter("profile", profile + "_%");

		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.RatiosDao#deleteByProfile(java.lang.String)
	 */
	@Override
	@Transactional
	public int deleteByProfile(String profile) {

		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaDelete<Ratio> delete = cb.createCriteriaDelete(Ratio.class);
		Root<Ratio> e = delete.from(Ratio.class);
		delete.where(cb.like(e.get("profileMonth"), profile + "_%"));

		return this.entityManager.createQuery(delete).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tes.iva.demo.dao.RatiosDao#deleteAll()
	 */
	@Override
	@Transactional
	public void deleteAll() {

		this.ratioRepository.deleteAll();
	}
}
