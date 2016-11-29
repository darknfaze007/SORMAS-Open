package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "VisitFacade")
public class VisitFacadeEjb implements VisitFacade {
	
	@EJB
	private VisitService service;	
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;

	
	@Override
	public List<VisitDto> getAllVisitsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public VisitDto getVisitByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public VisitReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}
	
	@Override
	public VisitDto saveVisit(VisitDto dto) {
		Visit entity = fromDto(dto);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	public Visit fromDto(@NotNull VisitDto source) {
		
		Visit target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new Visit();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(userService.getByReferenceDto(source.getVisitUser()));
		
		return target;
	}
	
	public VisitReferenceDto toReferenceDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitReferenceDto target = new VisitReferenceDto();
		DtoHelper.fillReferenceDto(target, source);
		return target;
	}	
	
	public static VisitDto toDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitDto target = new VisitDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(UserFacadeEjb.toReferenceDto(source.getVisitUser()));
		
		return target;
	}
}