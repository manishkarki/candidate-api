package com.hubspot.candidate.hubspotapi.controller;

import com.hubspot.candidate.hubspotapi.entity.Country;
import com.hubspot.candidate.hubspotapi.entity.Partner;
import com.hubspot.candidate.hubspotapi.service.CandidateApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CandidateApiController {

	@Autowired
	public CandidateApiService service;

	private List<Partner> partners;
	
	private List<Country> countries;

	@RequestMapping(method= RequestMethod.GET, value="/partners")
	public String sendInvitation() {
		
		partners = service.getAvailablePartners();
		
		if(partners == null) {
			//log or give response accordingly, avoided in this case
		}

		countries = service.getInvitees(partners);
		
		if(partners == null) {
			//log or give response accordingly, avoided currently in this case considering non-null situation
		}

		String invitationList = service.stringToJSON(countries);

		return service.makeRequest(invitationList);
	}
}
