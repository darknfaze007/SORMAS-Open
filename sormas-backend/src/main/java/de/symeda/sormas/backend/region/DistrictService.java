package de.symeda.sormas.backend.region;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;



@Stateless
@LocalBean
public class DistrictService extends AbstractAdoService<District> {
	
	public DistrictService() {
		super(District.class);
	}
	
	public List<District> getAllWithoutEpidCode() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(getElementClass());
		Root<District> from = cq.from(getElementClass());
		cq.where(cb.isNull(from.get(District.EPID_CODE)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}
	
	@Override
	protected Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<District, District> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
