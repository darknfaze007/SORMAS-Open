package de.symeda.sormas.app.validation;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.databinding.CaseDataFragmentLayoutBinding;
import de.symeda.sormas.app.databinding.CaseNewFragmentLayoutBinding;

/**
 * Created by Mate Strysewske on 20.07.2017.
 */
public final class CaseValidator {

    /**
     * Validates whether the Case Data entered are valid. Fields should be processed from bottom to top according to
     * their arrangement in the layout to make sure that the error message popup is displayed for the first invalid field.
     */
    public static boolean validateCaseData(Case caze, CaseDataFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Health facility & description
        if (caze.getHealthFacility() == null) {
            binding.caseDataHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        } else {
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_health_facility_details));
                    success = false;
                }
            }
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_none_health_facility_details));
                    success = false;
                }
            }
        }

        // Community/Ward
        if (caze.getCommunity() == null) {
            binding.caseDataCommunity.setError(resources.getString(R.string.validation_community));
            success = false;
        }

        // District/LGA
        if (caze.getDistrict() == null) {
            binding.caseDataDistrict.setError(resources.getString(R.string.validation_district));
            success = false;
        }

        // Region/State
        if (caze.getRegion() == null) {
            binding.caseDataRegion.setError(resources.getString(R.string.validation_region));
            success = false;
        }

        return success;
    }

    public static boolean validateNewCase(Case caze, CaseNewFragmentLayoutBinding binding) {
        Resources resources = DatabaseHelper.getContext().getResources();

        boolean success = true;

        // Health facility & description
        if (caze.getHealthFacility() == null) {
            binding.caseDataHealthFacility.setError(resources.getString(R.string.validation_health_facility));
            success = false;
        } else {
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_health_facility_details));
                    success = false;
                }
            }
            if (caze.getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID)) {
                if (caze.getHealthFacilityDetails() == null || caze.getHealthFacilityDetails().trim().isEmpty()) {
                    binding.caseDataFacilityDetails.setError(resources.getString(R.string.validation_none_health_facility_details));
                    success = false;
                }
            }
        }

        // Community/Ward
        if (caze.getCommunity() == null) {
            binding.caseDataCommunity.setError(resources.getString(R.string.validation_community));
            success = false;
        }

        // District/LGA
        if (caze.getDistrict() == null) {
            binding.caseDataDistrict.setError(resources.getString(R.string.validation_district));
            success = false;
        }

        // Region/State
        if (caze.getRegion() == null) {
            binding.caseDataRegion.setError(resources.getString(R.string.validation_region));
            success = false;
        }

        // Disease
        if (caze.getDisease() == null) {
            binding.caseDataDisease.setError(resources.getString(R.string.validation_case_disease));
            success = false;
        }

        // Last name
        if (caze.getPerson().getLastName() == null || caze.getPerson().getLastName().trim().isEmpty()) {
            binding.caseDataLastName.setError(resources.getString(R.string.validation_person_last_name));
            success = false;
        }

        // First name
        if (caze.getPerson().getFirstName() == null || caze.getPerson().getFirstName().trim().isEmpty()) {
            binding.caseDataFirstName.setError(resources.getString(R.string.validation_person_first_name));
            success = false;
        }

        return success;
    }

    public static void clearErrorsForCaseData(CaseDataFragmentLayoutBinding binding) {
        for (PropertyField field : getCaseDataFields(binding)) {
            field.clearError();
        }
    }

    public static void clearErrorsForNewCase(CaseNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewCaseFields(binding)) {
            field.clearError();
        }
    }

    public static void setRequiredHintsForCaseData(CaseDataFragmentLayoutBinding binding) {
        for (PropertyField field : getCaseDataFields(binding)) {
            if (field != binding.caseDataDisease) {
                field.setRequiredHint(true);
            }
        }
    }

    public static void setRequiredHintsForNewCase(CaseNewFragmentLayoutBinding binding) {
        for (PropertyField field : getNewCaseFields(binding)) {
            field.setRequiredHint(true);
        }
    }

    private static final List<PropertyField<?>> getCaseDataFields(CaseDataFragmentLayoutBinding binding) {
        return Arrays.asList(binding.caseDataRegion, binding.caseDataDistrict, binding.caseDataCommunity,
                binding.caseDataHealthFacility, binding.caseDataFacilityDetails);
    }

    private static final List<PropertyField<?>> getNewCaseFields(CaseNewFragmentLayoutBinding binding) {
        return Arrays.asList(binding.caseDataFirstName, binding.caseDataLastName, binding.caseDataDisease,
                binding.caseDataRegion, binding.caseDataDistrict, binding.caseDataCommunity,
                binding.caseDataHealthFacility, binding.caseDataFacilityDetails);
    }

}
