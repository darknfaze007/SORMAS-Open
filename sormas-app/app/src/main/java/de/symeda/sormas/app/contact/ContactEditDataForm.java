package de.symeda.sormas.app.contact;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CaseNewActivity;
import de.symeda.sormas.app.component.FieldHelper;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.databinding.ContactDataFragmentLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.validation.ContactValidator;

/**
 * Created by Stefan Szczesny on 02.11.2016.
 */
public class ContactEditDataForm extends FormTab {

    private ContactDataFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.contact_data_fragment_layout, container, false);

        final String contactUuid = (String) getArguments().getString(Contact.UUID);
        final ContactDao contactDao = DatabaseHelper.getContactDao();
        Contact contact = contactDao.queryUuid(contactUuid);
        binding.setContact(contact);

        binding.contactLastContactDate.initialize(this);
        binding.contactContactProximity.initialize(ContactProximity.class);

        FieldHelper.initSpinnerField(binding.contactRelationToCase, ContactRelation.class);

        final Case associatedCase = findAssociatedCase(binding.getContact().getPerson(), binding.getContact().getCaze().getDisease());
        if (associatedCase == null) {
            Button createCaseButton = binding.contactCreateCase;
            createCaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CaseNewActivity.class);
                    intent.putExtra(CaseNewActivity.CONTACT, contactUuid);
                    startActivity(intent);
                }
            });
            binding.contactLayoutAssociatedCase.setVisibility(View.GONE);
        } else {
            LabelField associatedCaseLabel = binding.contactAssociatedCase;
            associatedCaseLabel.setValue(DataHelper.getShortUuid(associatedCase.getUuid()));
            associatedCaseLabel.makeLink(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCaseEditView(associatedCase);
                }
            });
            binding.contactCreateCase.setVisibility(View.GONE);
        }

        ContactValidator.setRequiredHintsForContactData(binding);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.contactCazeUuid.makeLink(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.getContact().getCaze() != null) {
                    final CaseDao caseDao = DatabaseHelper.getCaseDao();
                    final Case caze = caseDao.queryUuid(binding.getContact().getCaze().getUuid());
                    showCaseEditView(caze);
                }
            }
        });
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(getActivity(), CaseEditActivity.class);
        intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
        startActivity(intent);
    }

    private Case findAssociatedCase(Person person, Disease disease) {
        if(person == null || disease == null) {
            return null;
        }

        Case caze = DatabaseHelper.getCaseDao().getByPersonAndDisease(person, disease);
        if (caze != null) {
            return caze;
        } else {
            return null;
        }
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getContact();
    }

    public ContactDataFragmentLayoutBinding getBinding() {
        return binding;
    }

}