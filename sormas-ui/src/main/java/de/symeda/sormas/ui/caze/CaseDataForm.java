package de.symeda.sormas.ui.caze;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {

	private static final String MEDICAL_INFORMATION_LOC = "medicalInformationLoc";
	private static final String REPORT_INFO_LOC = "reportInfoLoc";
	
    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
			
			LayoutUtil.div( 
					LayoutUtil.fluidRowLocs(CaseDataDto.CASE_CLASSIFICATION) +
					LayoutUtil.fluidRowLocs(CaseDataDto.INVESTIGATION_STATUS) +
		    		LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
		    				LayoutUtil.fluidRowLocs(CaseDataDto.UUID, REPORT_INFO_LOC) +
		    				LayoutUtil.fluidRowLocs(CaseDataDto.EPID_NUMBER, CaseDataDto.DISEASE) +
		    				LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
				    		LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
		    				LayoutUtil.fluidRowLocs("", CaseDataDto.HEALTH_FACILITY_DETAILS) +
		    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, ""))
		    )+
			LayoutUtil.loc(MEDICAL_INFORMATION_LOC) +
			LayoutUtil.fluidRow(
					LayoutUtil.fluidRowLocs(CaseDataDto.PREGNANT, "") +
					LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION, CaseDataDto.MEASLES_DOSES) +
					LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, "")
			);
    	

    private final PersonDto person;
    private final Disease disease;
	private Label reportInfoLabel;

    public CaseDataForm(PersonDto person, Disease disease) {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
        this.person = person;
        this.disease = disease;
        addFields();
    }

    @Override
	protected void addFields() {
    	if (person == null || disease == null) {
    		return;
    	}
    	
    	addField(CaseDataDto.UUID, TextField.class);
    	
    	TextField epidField = addField(CaseDataDto.EPID_NUMBER, TextField.class);
    	epidField.addValidator(new RegexpValidator(DataHelper.getEpidNumberRegexp(), true, 
    			"The EPID number does not match the required pattern. You may still save the case and enter the correct number later."));
    	epidField.addValidator(new StringLengthValidator("An EPID number has to be provided. You may still save the case and enter the correct number later.", 1, null, false));
    	epidField.setInvalidCommitted(true);
    	
    	addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
    	addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
    	NativeSelect diseaseField = addField(CaseDataDto.DISEASE, NativeSelect.class);
    	TextField healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
    	
    	ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
    	ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
    	ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});
    	district.addValueChangeListener(e -> {
    		community.removeAllItems();
    		DistrictReferenceDto districtReferenceDto = (DistrictReferenceDto)e.getProperty().getValue();
    		if (districtReferenceDto != null) {
    			community.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtReferenceDto.getUuid()));
    			if (!epidField.isValid()) {
	    			RegionDto regionDto = FacadeProvider.getRegionFacade().getRegionByUuid(((RegionReferenceDto) region.getValue()).getUuid());
	    			DistrictDto districtDto = FacadeProvider.getDistrictFacade().getDistrictByUuid(districtReferenceDto.getUuid());
		    		Calendar calendar = Calendar.getInstance();
		    		String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
	    			epidField.setValue(CaseDataDto.COUNTRY_EPID_CODE + "-" + regionDto.getEpidCode() + "-" + districtDto.getEpidCode() + "-" + year + "-");
    			}
    		}
    	});
    	community.addValueChangeListener(e -> {
    		facility.removeAllItems();
    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
    		if (communityDto != null) {
    			facility.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		ComboBox surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);
		
		district.addValueChangeListener(e -> {
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict((DistrictReferenceDto) district.getValue(), false, UserRole.SURVEILLANCE_OFFICER);
			
			surveillanceOfficerField.removeAllItems();
			surveillanceOfficerField.select(0);
			surveillanceOfficerField.addItems(assignableSurveillanceOfficers);
			surveillanceOfficerField.select(0);
		});
    	
		addField(CaseDataDto.PREGNANT, OptionGroup.class);
		addField(CaseDataDto.MEASLES_VACCINATION, ComboBox.class);
		addField(CaseDataDto.MEASLES_DOSES, TextField.class);
		addField(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, ComboBox.class);
    	
    	setRequired(true, CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

    	setReadOnly(true, CaseDataDto.UUID, CaseDataDto.INVESTIGATION_STATUS);
    	if (!UserRole.isSupervisor(LoginHelper.getCurrentUserRoles())) {
    		setReadOnly(true, CaseDataDto.DISEASE);
    	}
    	
    	Sex personSex = person.getSex();
    	if (personSex != Sex.FEMALE) {
    		setVisible(false, CaseDataDto.PREGNANT);
    	}
    	
    	boolean visible = DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, CaseDataDto.MEASLES_VACCINATION, disease);
		setVisible(visible, CaseDataDto.MEASLES_VACCINATION);
		
		if (visible) {
			FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.MEASLES_DOSES, CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE), 
					CaseDataDto.MEASLES_VACCINATION, Arrays.asList(Vaccination.VACCINATED), true);
		} else {
			getField(CaseDataDto.MEASLES_VACCINATION).setValue(null);
			setVisible(false, CaseDataDto.MEASLES_DOSES, CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE);
		}
		
		List<String> medicalInformationFields = Arrays.asList(CaseDataDto.PREGNANT, CaseDataDto.MEASLES_VACCINATION);
		
		for (String medicalInformationField : medicalInformationFields) {
			if (getFieldGroup().getField(medicalInformationField).isVisible()) {
				String medicalInformationCaptionLayout = LayoutUtil.h3(CssStyles.VSPACE3, "Additional medical information");
				Label medicalInformationCaptionLabel = new Label(medicalInformationCaptionLayout);
				medicalInformationCaptionLabel.setContentMode(ContentMode.HTML);
				getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
				break;
			}
		}
		
		reportInfoLabel = new Label();
		reportInfoLabel.setContentMode(ContentMode.HTML);
		reportInfoLabel.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, REPORT_INFO_LOC));
		getContent().addComponent(reportInfoLabel, REPORT_INFO_LOC);
		
		addValueChangeListener(e -> {
			updateReportInfo();
			diseaseField.addValueChangeListener(new DiseaseChangeListener(diseaseField, getValue().getDisease()));
		});
		
		facility.addValueChangeListener(e -> {
			if (facility.getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) facility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;
				
				healthFacilityDetails.setVisible(visibleAndRequired);
				healthFacilityDetails.setRequired(visibleAndRequired);

				if (otherHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					healthFacilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
				}
				if (!visibleAndRequired) {
					healthFacilityDetails.clear();
				}
			} else {
				healthFacilityDetails.setVisible(false);
				healthFacilityDetails.setRequired(false);
				healthFacilityDetails.clear();
			}			
		});
	}
    
    private void updateReportInfo() {
		CaseDataDto caseDto = getValue();
		StringBuilder sb = new StringBuilder();
		sb.append(DateHelper.formatShortDateTime(caseDto.getReportDate()) + ", ");
		sb.append(caseDto.getReportingUser().toString());
		reportInfoLabel.setValue(sb.toString());
	}
    
	@Override 
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
	
	private static class DiseaseChangeListener implements ValueChangeListener {
		
		private NativeSelect diseaseField;
		private Disease currentDisease;
		
		DiseaseChangeListener(NativeSelect diseaseField, Disease currentDisease) {
			this.diseaseField = diseaseField;
			this.currentDisease = currentDisease;
		}
		
		@Override
		public void valueChange(Property.ValueChangeEvent e) {
			
			if (diseaseField.getValue() != currentDisease) {
				ConfirmationComponent confirmDiseaseChangeComponent = new ConfirmationComponent(false) {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onConfirm() {
						diseaseField.removeValueChangeListener(DiseaseChangeListener.this);
					}
					@Override
					protected void onCancel() {
						diseaseField.setValue(currentDisease);
					}
				};
				confirmDiseaseChangeComponent.getConfirmButton().setCaption("Really change case disease?");
				confirmDiseaseChangeComponent.getCancelButton().setCaption("Cancel");
				confirmDiseaseChangeComponent.setMargin(true);
				
				Window popupWindow = VaadinUiUtil.showPopupWindow(confirmDiseaseChangeComponent);
				CloseListener closeListener = new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						diseaseField.setValue(currentDisease);
					}
				};
				popupWindow.addCloseListener(closeListener);
				confirmDiseaseChangeComponent.addDoneListener(new DoneListener() {
					public void onDone() {
						popupWindow.removeCloseListener(closeListener);
						popupWindow.close();
					}
				});
				popupWindow.setCaption("Change case disease");       
			}
		}
	}
}
