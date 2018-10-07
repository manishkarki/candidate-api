package com.hubspot.candidate.hubspotapi.service;

import com.hubspot.candidate.hubspotapi.entity.Country;
import com.hubspot.candidate.hubspotapi.entity.Partner;

import java.util.List;

public interface CandidateApiService {
    // method to get the list of partners from the api
    List<Partner> getAvailablePartners();

    // method to create the invitation list
    List<Country> getInvitees(List<Partner> partners);

    String makeRequest(String invitations, String postUrl);

    // method to convert object's list to json in the required format
    String stringToJSON(List<Country> inviteesList);
}
