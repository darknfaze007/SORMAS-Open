package de.symeda.sormas.ui.samples;

import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleFacade;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleController {

	private SampleFacade sf = FacadeProvider.getSampleFacade();

	public SampleController() { }

	public List<SampleIndexDto> getAllSamples() {
		UserDto user = LoginHelper.getCurrentUser();
		return FacadeProvider.getSampleFacade().getIndexList(user.getUuid());
	}

	public List<SampleIndexDto> getSamplesByCase(CaseReferenceDto caseRef) {
		return FacadeProvider.getSampleFacade().getIndexListByCase(caseRef);
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(SamplesView.VIEW_NAME, SamplesView.class);
		navigator.addView(SampleDataView.VIEW_NAME, SampleDataView.class);
	}

	public void navigateToData(String sampleUuid) {
		String navigationState = SampleDataView.VIEW_NAME + "/" + sampleUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void create(CaseReferenceDto caseRef, SampleGrid grid) {
		SampleCreateForm createForm = new SampleCreateForm();
		createForm.setValue(createNewSample(caseRef));
		final CommitDiscardWrapperComponent<SampleCreateForm> editView = new CommitDiscardWrapperComponent<SampleCreateForm>(createForm, createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if( !createForm.getFieldGroup().isModified()) {
					SampleDto dto = createForm.getValue();
					FacadeProvider.getSampleFacade().saveSample(dto);
					grid.reload();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, "Create new sample");
	}

	public void createReferral(SampleDto sample) {
		SampleCreateForm createForm = new SampleCreateForm();
		SampleDto referralSample = createNewSample(sample.getAssociatedCase());
		referralSample.setSampleDateTime(sample.getSampleDateTime());
		referralSample.setSampleCode(sample.getSampleCode());
		referralSample.setSampleMaterial(sample.getSampleMaterial());
		referralSample.setSuggestedTypeOfTest(sample.getSuggestedTypeOfTest());
		createForm.setValue(referralSample);
		final CommitDiscardWrapperComponent<SampleCreateForm> createView = new CommitDiscardWrapperComponent<SampleCreateForm>(createForm, createForm.getFieldGroup());

		createView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					SampleDto newSample = createForm.getValue();
					FacadeProvider.getSampleFacade().saveSample(newSample);
					sample.setReferredTo(FacadeProvider.getSampleFacade().getReferenceByUuid(newSample.getUuid()));
					FacadeProvider.getSampleFacade().saveSample(sample);
					navigateToData(newSample.getUuid());
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(createView, "Refer sample to another laboratory");
	}

	public CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(final String sampleUuid) {
		SampleEditForm form = new SampleEditForm();
		form.setWidth(form.getWidth() * 10/12, Unit.PIXELS);
		SampleDto dto = sf.getSampleByUuid(sampleUuid);
		form.setValue(dto);
		final CommitDiscardWrapperComponent<SampleEditForm> editView = new CommitDiscardWrapperComponent<SampleEditForm>(form, form.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					SampleDto dto = form.getValue();
					SampleDto originalDto = sf.getSampleByUuid(dto.getUuid());
					sf.saveSample(dto);
					navigateToData(dto.getUuid());

					if (dto.getSpecimenCondition() != originalDto.getSpecimenCondition() &&
							dto.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
						requestSampleCollectionTaskCreation(dto, form);
					} else {
						Notification.show("Sample data saved", Type.WARNING_MESSAGE);
					}
				}
			}
		});

		// Initialize 'Refer to another laboratory' button or link to referred sample
		Button referOrLinkToOtherLabButton = new Button();
		referOrLinkToOtherLabButton.addStyleName(ValoTheme.BUTTON_LINK);
		if (dto.getReferredTo() == null) {
			referOrLinkToOtherLabButton.setCaption("Refer to another laboratory");
			referOrLinkToOtherLabButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					createReferral(dto);
				}
			});
		} else {
			SampleDto referredDto = FacadeProvider.getSampleFacade().getSampleByUuid(dto.getReferredTo().getUuid());
			referOrLinkToOtherLabButton.setCaption("Referred to " + referredDto.getLab().toString());
			referOrLinkToOtherLabButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					navigateToData(dto.getReferredTo().getUuid());
				}
			});
		}

		editView.getButtonsPanel().addComponentAsFirst(referOrLinkToOtherLabButton);
		editView.getButtonsPanel().setComponentAlignment(referOrLinkToOtherLabButton, Alignment.BOTTOM_LEFT);

		return editView;
	}

	private void requestSampleCollectionTaskCreation(SampleDto dto, SampleEditForm form) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		ConfirmationComponent requestTaskComponent = buildRequestTaskComponent();

		Label description = new Label("You have set the specimen condition to not adequate.<br/>Do you want to create a new sample collection task?");
		description.setContentMode(ContentMode.HTML);
		description.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(description);
		layout.addComponent(requestTaskComponent);
		layout.setComponentAlignment(requestTaskComponent, Alignment.BOTTOM_RIGHT);
		layout.setSizeUndefined();
		layout.setSpacing(true);

		Window popupWindow = VaadinUiUtil.showPopupWindow(layout);
		popupWindow.setSizeUndefined();
		popupWindow.setCaption("Create new task?");
		requestTaskComponent.getConfirmButton().addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
				ControllerProvider.getTaskController().createSampleCollectionTask(TaskContext.CASE, dto.getAssociatedCase(), dto);
			}
		});
		requestTaskComponent.getCancelButton().addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				popupWindow.close();
			}
		});
	}

	private SampleDto createNewSample(CaseReferenceDto caseRef) {
		SampleDto sample = new SampleDto();
		sample.setUuid(DataHelper.createUuid());
		sample.setAssociatedCase(caseRef);
		sample.setReportingUser(LoginHelper.getCurrentUserAsReference());
		sample.setReportDateTime(new Date());

		return sample;

	}

	private ConfirmationComponent buildRequestTaskComponent() {
		ConfirmationComponent requestTaskComponent = new ConfirmationComponent(false) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
			}
			@Override
			protected void onCancel() {
			}
		};
		requestTaskComponent.getConfirmButton().setCaption("Yes");
		requestTaskComponent.getCancelButton().setCaption("No");
		return requestTaskComponent;
	}

}
