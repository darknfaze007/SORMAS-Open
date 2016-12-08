package de.symeda.sormas.app.person;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.component.DateField;
import de.symeda.sormas.app.component.PropertyField;
import de.symeda.sormas.app.component.SpinnerField;
import de.symeda.sormas.app.component.TextField;
import de.symeda.sormas.app.databinding.PersonEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.Item;


/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class PersonEditTab extends FormTab {

    private PersonEditFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.person_edit_fragment_layout, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        final String personUuid = getArguments().getString(Person.UUID);
        PersonDao personDao = DatabaseHelper.getPersonDao();
        Person person = personDao.queryUuid(personUuid);
        binding.setPerson(person);

        // date of birth
        addSpinnerField(R.id.person_birthdateDD, toItems(DateHelper.getDaysInMonth(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        addSpinnerField(R.id.person_birthdateMM, toItems(DateHelper.getMonthsInYear(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        addSpinnerField(R.id.person_birthdateYYYY, toItems(DateHelper.getYearsToNow(),true), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateApproximateAgeField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // age type
        addSpinnerField(R.id.person_approximateAgeType, ApproximateAgeType.class);

        // gender
        addSpinnerField(R.id.person_sex, Sex.class);

        // date of death
        addDateField(R.id.person_deathDate).addValueChangedListener(new PropertyField.ValueChangeListener() {
            @Override
            public void onChange(PropertyField field) {
                updateApproximateAgeField();
            }
        });


        // status of patient
        addSpinnerField(R.id.person_presentCondition, PresentCondition.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDateOfDeathField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateDateOfDeathField();
            }
        });

        // evaluate the model and set the ui
        updateDateOfDeathField();
        updateApproximateAgeField();

        // ================ Address ================
        addLocationField(person, R.id.person_address, R.id.form_cp_btn_address);

        // ================ Occupation ================

        final LinearLayout occupationDetailsLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_details_view);
        final TextField occupationDetails = (TextField) getView().findViewById(R.id.person_occupationDetails);
        final LinearLayout occupationFacilityLayout = (LinearLayout) getView().findViewById(R.id.form_cp_occupation_facility_view);
        addSpinnerField(R.id.person_occupationType1, OccupationType.class, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item)parent.getItemAtPosition(position);
                updateVisibilityOccupationFields(item, occupationDetailsLayout, occupationFacilityLayout);
                updateHeadlineOccupationDetailsFields(item, occupationDetails);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deactivateField(occupationDetailsLayout);
            }
        });

        addFacilitySpinnerField(R.id.person_occupationFacility);

        // @TODO: Workaround, find a better solution. Remove autofocus on first field.
        getView().requestFocus();
    }

    private void updateHeadlineOccupationDetailsFields(Item item, TextField occupationDetails) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
                case BUSINESSMAN_WOMAN:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_business));
                    break;
                case TRANSPORTER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_transport));
                    break;
                case OTHER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_other));
                    break;
                case HEALTHCARE_WORKER:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details_healthcare));
                    break;
                default:
                    occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details));
                    break;
            }
        }
        else {
            occupationDetails.updateCaption(getResources().getString(R.string.headline_form_cp_occupation_details));
        }
    }

    private void updateVisibilityOccupationFields(Item item, LinearLayout occupationDetailsLayout, LinearLayout occupationFacilityLayout) {
        if(item.getValue()!=null) {
            switch ((OccupationType) item.getValue()) {
                case BUSINESSMAN_WOMAN:
                case TRANSPORTER:
                case OTHER:
                    occupationDetailsLayout.setVisibility(View.VISIBLE);
                    occupationFacilityLayout.setVisibility(View.INVISIBLE);
                    break;
                case HEALTHCARE_WORKER:
                    occupationDetailsLayout.setVisibility(View.VISIBLE);
                    occupationFacilityLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    occupationDetailsLayout.setVisibility(View.INVISIBLE);
                    occupationFacilityLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        }
        else {
            occupationDetailsLayout.setVisibility(View.INVISIBLE);
            occupationFacilityLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDateOfDeathField() {
        PresentCondition condition = (PresentCondition)((SpinnerField)getView().findViewById(R.id.person_presentCondition)).getValue();
        setFieldVisible(getView().findViewById(R.id.cp_date_of_death_layout), condition != null
                && (PresentCondition.DEAD.equals(condition) || PresentCondition.BURIED.equals(condition)));
    }

    private void updateApproximateAgeField() {
        Integer birthyear = (Integer)((SpinnerField)getView().findViewById(R.id.person_birthdateYYYY)).getValue();
        TextField approximateAgeTextField = (TextField) getView().findViewById(R.id.person_approximateAge1);
        SpinnerField approximateAgeTypeField = (SpinnerField) getView().findViewById(R.id.person_approximateAgeType);


        if(birthyear!=null) {
            deactivateField(approximateAgeTextField);
            deactivateField(approximateAgeTypeField);

            Integer birthday = (Integer)((SpinnerField)getView().findViewById(R.id.person_birthdateDD)).getValue();
            Integer birthmonth = (Integer)((SpinnerField)getView().findViewById(R.id.person_birthdateMM)).getValue();

            Calendar birthDate = new GregorianCalendar();
            birthDate.set(birthyear, birthmonth!=null?birthmonth-1:0, birthday!=null?birthday:1);

            Date to = new Date();
            if(((DateField)getView().findViewById(R.id.person_deathDate)).getValue() != null) {
                to = ((DateField)getView().findViewById(R.id.person_deathDate)).getValue();
            }
            DataHelper.Pair<Integer, ApproximateAgeType> approximateAge = ApproximateAgeHelper.getApproximateAge(birthDate.getTime(),to);
            ApproximateAgeType ageType = approximateAge.getElement1();
            approximateAgeTextField.setValue(String.valueOf(approximateAge.getElement0()));
            for (int i=0; i<approximateAgeTypeField.getCount(); i++) {
                Item item = (Item)approximateAgeTypeField.getItemAtPosition(i);
                if (item != null && item.getValue() != null && item.getValue().equals(ageType)) {
                    approximateAgeTypeField.setValue(i);
                    break;
                }
            }
        }
        else {
            approximateAgeTextField.setEnabled(true);
            approximateAgeTypeField.setEnabled(true);
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getPerson();
    }

    /**
     * Commit all values from model to ado.
     * @param ado
     * @return
     */
    @Override
    protected AbstractDomainObject commit(AbstractDomainObject ado) {
        return null;
    }
}