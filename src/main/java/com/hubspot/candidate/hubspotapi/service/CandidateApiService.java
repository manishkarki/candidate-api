package com.hubspot.candidate.hubspotapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.candidate.hubspotapi.entity.Country;
import com.hubspot.candidate.hubspotapi.entity.Partner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface CandidateApiService {

    String getURL = "https://candidate.hubteam.com/candidateTest/v3/problem/dataset?userKey=fd87898e167586dc6c601a432d0d";

    String postURL = "https://candidate.hubteam.com/candidateTest/v3/problem/result?userKey=fd87898e167586dc6c601a432d0d";


    // method to get the list of partners from the api
    List<Partner> getAvailablePartners();

    // method to get the dates of the partner on which they can attend the conference
    List<Date> getAppropriateDate(Partner partner);

    Map<String, List<Partner>> buildCountryDates(List<Partner> partners);

    // method to get the final date, partner and country list
    Map<String, Map<Date, List<Partner>>> getInvitations(List<Partner> partnerList);

    // method to create the invitation list
    List<Country> getInvitees(List<Partner> partners);

    RestTemplate restTemplate = new RestTemplate();

    default String readUrl() {
        return restTemplate.getForObject(getURL, String.class);
    }

    default String makeRequest(String invitations) {

        try {
            //declare and set headers to the entity
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAcceptCharset(new ArrayList<>(Arrays.asList(StandardCharsets.UTF_8)));
            httpHeaders.set("Accept", "application/json");
            httpHeaders.set("Content-type", "application/json");
            httpHeaders.add("Authorization", ("userKey=fd87898e167586dc6c601a432d0d"));
            HttpEntity<String> entity1 = new HttpEntity<>(invitations, httpHeaders);

            // call the api
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(postURL, entity1, String.class);
            return responseEntity.getBody();

        } catch (Exception e) {
            //log some message here
            return e.toString();
        }
    }

    //as this will be common to all
    // it will act as the utility method to convert object's list to json in the required format
    default String stringToJSON(List<Country> inviteesList) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String arrayToJson = null;
        try {
            arrayToJson = objectMapper.writeValueAsString(inviteesList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        StringBuilder s = new StringBuilder();
        s.append("{ " + "\"" + "countries" + "\"" + ": " + arrayToJson + "}");

        return s.toString();
    }
}
