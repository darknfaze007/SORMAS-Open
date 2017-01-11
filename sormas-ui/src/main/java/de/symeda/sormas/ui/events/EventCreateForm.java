package de.symeda.sormas.ui.events;

import java.util.Arrays;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.location.LocationForm;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class EventCreateForm extends AbstractEditForm<EventDto> {
	
	private static final String HTML_LAYOUT =
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(EventDto.EVENT_TYPE, EventDto.DISEASE),
					LayoutUtil.fluidRowLocs(EventDto.EVENT_DATE, EventDto.TYPE_OF_PLACE),
					LayoutUtil.fluidRowLocs("", EventDto.TYPE_OF_PLACE_TEXT),
					LayoutUtil.div(
						LayoutUtil.fluidRowLocsCss(CssStyles.VSPACE4, EventDto.EVENT_LOCATION)
					) +
					LayoutUtil.fluidRowLocs(EventDto.EVENT_DESC, EventDto.SURVEILLANCE_OFFICER)
			);
	
	public EventCreateForm() {
		super(EventDto.class, EventDto.I18N_PREFIX);
	}
	
	@Override
	protected void addFields() {
		addField(EventDto.EVENT_TYPE, NativeSelect.class);
		addField(EventDto.EVENT_DATE);
		addField(EventDto.TYPE_OF_PLACE, NativeSelect.class);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);
		addField(EventDto.DISEASE, NativeSelect.class);
		addField(EventDto.EVENT_LOCATION, LocationForm.class).setCaption(null);
		addField(EventDto.EVENT_DESC, TextArea.class).setRows(2);
		
		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
		addField(EventDto.SURVEILLANCE_OFFICER, ComboBox.class).addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.SURVEILLANCE_OFFICER));
		
		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE, Arrays.asList(TypeOfPlace.OTHER), true, true);
		
		setRequired(true, EventDto.EVENT_TYPE, EventDto.EVENT_DATE, EventDto.TYPE_OF_PLACE, EventDto.EVENT_DESC);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}