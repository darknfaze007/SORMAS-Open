package de.symeda.sormas.ui.utils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends VerticalLayout implements View {

	private static final long serialVersionUID = -1L;
	
    @Override
    public abstract void enter(ViewChangeEvent event);
}
