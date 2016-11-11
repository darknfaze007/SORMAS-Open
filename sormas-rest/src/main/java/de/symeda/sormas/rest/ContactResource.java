package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/contacts")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
public class ContactResource {

	@GET
	@Path("/all/{user}/{since}")
	public List<ContactDto> getAllContacts(@PathParam("user") String userUuid, @PathParam("since") long since) {
		
		List<ContactDto> contacts = FacadeProvider.getContactFacade().getAllContactsAfter(new Date(since), userUuid);
		return contacts;
	}
	
	@POST 
	@Path("/push")
	public Integer postContacts(List<ContactDto> dtos) {
		
		ContactFacade contactFacade = FacadeProvider.getContactFacade();
		for (ContactDto dto : dtos) {
			contactFacade.saveContact(dto);
		}
		
		return dtos.size();
	}
}