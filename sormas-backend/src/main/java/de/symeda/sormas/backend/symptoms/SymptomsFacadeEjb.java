package de.symeda.sormas.backend.symptoms;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "SymptomsFacade")
public class SymptomsFacadeEjb implements SymptomsFacade {
	
	@EJB
	private SymptomsService service;
	@EJB
	private LocationFacadeEjbLocal locationFacade;

	public Symptoms fromDto(SymptomsDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Symptoms symptoms = service.getByUuid(dto.getUuid());
		if (symptoms == null) {
			symptoms = new Symptoms();
			symptoms.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				symptoms.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		} 
		
		Symptoms target = symptoms;
		SymptomsDto source = dto;
		DtoHelper.validateDto(source, target);
		
		target.setAbdominalPain(source.getAbdominalPain());
		target.setAlteredConsciousness(source.getAlteredConsciousness());
		target.setAnorexiaAppetiteLoss(source.getAnorexiaAppetiteLoss());
		target.setBleedingVagina(source.getBleedingVagina());
		target.setBloodInStool(source.getBloodInStool());
		target.setBloodUrine(source.getBloodUrine());
		target.setBloodyBlackStool(source.getBloodyBlackStool());
		target.setChestPain(source.getChestPain());
		target.setConfusedDisoriented(source.getConfusedDisoriented());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setCough(source.getCough());
		target.setCoughingBlood(source.getCoughingBlood());
		target.setDehydration(source.getDehydration());
		target.setDiarrhea(source.getDiarrhea());
		target.setDifficultyBreathing(source.getDifficultyBreathing());
		target.setDigestedBloodVomit(source.getDigestedBloodVomit());
		target.setEyePainLightSensitive(source.getEyePainLightSensitive());
		target.setFatigueWeakness(source.getFatigueWeakness());
		target.setFever(source.getFever());
		target.setGumsBleeding(source.getGumsBleeding());
		target.setHeadache(source.getHeadache());
		target.setHearingloss(source.getHearingloss());
		target.setHiccups(source.getHiccups());
		target.setInjectionSiteBleeding(source.getInjectionSiteBleeding());
		target.setJointPain(source.getJointPain());
		target.setKopliksSpots(source.getKopliksSpots());
		target.setMusclePain(source.getMusclePain());
		target.setNausea(source.getNausea());
		target.setNeckStiffness(source.getNeckStiffness());
		target.setNoseBleeding(source.getNoseBleeding());
		target.setOnsetDate(source.getOnsetDate());
		target.setOnsetSymptom(source.getOnsetSymptom());
		target.setOtherHemorrhagicSymptoms(source.getOtherHemorrhagicSymptoms());
		target.setOtherHemorrhagicSymptomsText(source.getOtherHemorrhagicSymptomsText());
		target.setOtherNonHemorrhagicSymptoms(source.getOtherNonHemorrhagicSymptoms());
		target.setOtherNonHemorrhagicSymptomsText(source.getOtherNonHemorrhagicSymptomsText());
		target.setOtitisMedia(source.getOtitisMedia());
		target.setRedBloodVomit(source.getRedBloodVomit());
		target.setRefusalFeedorDrink(source.getRefusalFeedorDrink());
		target.setRunnyNose(source.getRunnyNose());
		target.setSeizures(source.getSeizures());
		target.setShock(source.getShock());
		target.setSkinBruising(source.getSkinBruising());
		target.setSkinRash(source.getSkinRash());
		target.setSoreThroat(source.getSoreThroat());
		target.setSymptomatic(source.getSymptomatic());
		target.setSymptomsComments(source.getSymptomsComments());
		target.setTemperature(source.getTemperature());
		target.setTemperatureSource(source.getTemperatureSource());
		target.setThrobocytopenia(source.getThrobocytopenia());
		target.setUnexplainedBleeding(source.getUnexplainedBleeding());
		target.setVomiting(source.getVomiting());
		target.setIllLocation(locationFacade.fromDto(source.getIllLocation()));
		target.setIllLocationFrom(source.getIllLocationFrom());
		target.setIllLocationTo(source.getIllLocationTo());
		
		return symptoms;
	}
	
	public static SymptomsDto toDto(Symptoms symptoms) {
		
		if (symptoms == null) {
			return null;
		}

		SymptomsDto target = new SymptomsDto();
		Symptoms source = symptoms;
		
		target.setCreationDate(source.getCreationDate());
		target.setChangeDate(source.getChangeDate());
		target.setUuid(source.getUuid());
		
		target.setAbdominalPain(source.getAbdominalPain());
		target.setAlteredConsciousness(source.getAlteredConsciousness());
		target.setAnorexiaAppetiteLoss(source.getAnorexiaAppetiteLoss());
		target.setBleedingVagina(source.getBleedingVagina());
		target.setBloodInStool(source.getBloodInStool());
		target.setBloodUrine(source.getBloodUrine());
		target.setBloodyBlackStool(source.getBloodyBlackStool());
		target.setChestPain(source.getChestPain());
		target.setConfusedDisoriented(source.getConfusedDisoriented());
		target.setConjunctivitis(source.getConjunctivitis());
		target.setCough(source.getCough());
		target.setCoughingBlood(source.getCoughingBlood());
		target.setDehydration(source.getDehydration());
		target.setDiarrhea(source.getDiarrhea());
		target.setDifficultyBreathing(source.getDifficultyBreathing());
		target.setDigestedBloodVomit(source.getDigestedBloodVomit());
		target.setEyePainLightSensitive(source.getEyePainLightSensitive());
		target.setFatigueWeakness(source.getFatigueWeakness());
		target.setFever(source.getFever());
		target.setGumsBleeding(source.getGumsBleeding());
		target.setHeadache(source.getHeadache());
		target.setHearingloss(source.getHearingloss());
		target.setHiccups(source.getHiccups());
		target.setInjectionSiteBleeding(source.getInjectionSiteBleeding());
		target.setJointPain(source.getJointPain());
		target.setKopliksSpots(source.getKopliksSpots());
		target.setMusclePain(source.getMusclePain());
		target.setNausea(source.getNausea());
		target.setNeckStiffness(source.getNeckStiffness());
		target.setNoseBleeding(source.getNoseBleeding());
		target.setOnsetDate(source.getOnsetDate());
		target.setOnsetSymptom(source.getOnsetSymptom());
		target.setOtherHemorrhagicSymptoms(source.getOtherHemorrhagicSymptoms());
		target.setOtherHemorrhagicSymptomsText(source.getOtherHemorrhagicSymptomsText());
		target.setOtherNonHemorrhagicSymptoms(source.getOtherNonHemorrhagicSymptoms());
		target.setOtherNonHemorrhagicSymptomsText(source.getOtherNonHemorrhagicSymptomsText());
		target.setOtitisMedia(source.getOtitisMedia());
		target.setRedBloodVomit(source.getRedBloodVomit());
		target.setRefusalFeedorDrink(source.getRefusalFeedorDrink());
		target.setRunnyNose(source.getRunnyNose());
		target.setSeizures(source.getSeizures());
		target.setShock(source.getShock());
		target.setSkinBruising(source.getSkinBruising());
		target.setSkinRash(source.getSkinRash());
		target.setSoreThroat(source.getSoreThroat());
		target.setSymptomatic(source.getSymptomatic());
		target.setSymptomsComments(source.getSymptomsComments());
		target.setTemperature(source.getTemperature());
		target.setTemperatureSource(source.getTemperatureSource());
		target.setThrobocytopenia(source.getThrobocytopenia());
		target.setUnexplainedBleeding(source.getUnexplainedBleeding());
		target.setVomiting(source.getVomiting());
		target.setIllLocation(LocationFacadeEjb.toDto(source.getIllLocation()));
		target.setIllLocationFrom(source.getIllLocationFrom());
		target.setIllLocationTo(source.getIllLocationTo());
		
		return target;
	}

	@Override
	public SymptomsDto saveSymptoms(SymptomsDto dto) {
		SymptomsHelper.updateIsSymptomatic(dto);
		Symptoms ado = fromDto(dto);
		service.ensurePersisted(ado);
		return toDto(ado);	
	}
	
	@LocalBean
	@Stateless
	public static class SymptomsFacadeEjbLocal extends SymptomsFacadeEjb {
	}
}

