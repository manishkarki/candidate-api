package com.hubspot.candidate.hubspotapi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hubspot.candidate.hubspotapi.entity.Country;
import com.hubspot.candidate.hubspotapi.entity.Partner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class CandidateApiServiceImpl implements CandidateApiService {

	@Override
	public List<Partner> getAvailablePartners() {

		try {
			List<Partner> partners = new ArrayList<>();
			//get the json data retrieved by calling the api
			String jsonData = readUrl();
			JSONParser parser = new JSONParser();
			Object object = parser.parse(jsonData);

			JSONObject json = (JSONObject) object;
			//from the json, get param
			JSONArray result = (JSONArray) json.get("partners");

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

			for (int i = 0; i < result.size(); i++) {

				JSONObject jsonPartner = (JSONObject) result.get(i);

				Partner p = gson.fromJson(jsonPartner.toJSONString(), Partner.class);

				partners.add(p);

			}

			return partners;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Date> getAppropriateDate(Partner partner)
	{

		Collections.sort(partner.getAvailableDates());

		Map<Date, Integer> startDates = new TreeMap<>();

		List<Date> sortedDates = partner.getAvailableDates();

		startDates.put(sortedDates.get(0), 0);

		for (int i = 1; i < sortedDates.size() ; i++)
		{			
			Date prevDate = sortedDates.get(i-1);
			Date curDate = sortedDates.get(i);

			long diff = Math.abs(curDate.getTime() - prevDate.getTime());
			long diffDays = diff / (24 * 60 * 60 * 1000);

			if(diffDays == 1) {					
				int count  = startDates.get(prevDate);
				startDates.put(prevDate, count+1);
				startDates.put(curDate, 1);
			}
			startDates.put(curDate, 0);
		}

		return startDates.entrySet().stream()
				.filter(startDate -> startDate.getValue() > 0)
				.map(startDate -> startDate.getKey())
				.collect(Collectors.toList());
	}

	public Map<String, List<Partner>> buildCountryDates(List<Partner> partners) {
		Map<String, List<Partner>> countryDates = new HashMap<>();

		for(Partner p : partners) {

			List<Partner> partnerList = new ArrayList<>();

			if(countryDates.containsKey(p.getCountry())) {
				partnerList = countryDates.get(p.getCountry());
				partnerList.add(p);
			}
			else {
				partnerList.add(p);
				countryDates.put(p.getCountry(), partnerList);
			}
		}

		return countryDates;
	}

	public Map<String, Map<Date, List<Partner>>> getInvitations(List<Partner> partnerList) {

		Map<String, List<Partner>> countryMap = buildCountryDates(partnerList);

		Map<String, Map<Date, List<Partner>>> invitatonMap = new HashMap<>();

		for(Entry<String, List<Partner>> countryEntry : countryMap.entrySet()) {

			List<Partner> partners = countryEntry.getValue();

			Set<Date> dates = new TreeSet<>();

			for(Partner p : partners) {
				dates.addAll(getAppropriateDate(p));
			}

			Map<Date, List<Partner>> partnerMap = new TreeMap<>();

			for(Partner p : partners) {
				List<Date> partnerDate = getAppropriateDate(p);

				for(Date d : partnerDate) {
					if(dates.contains(d)) {
						List<Partner> par = new ArrayList<>();
						if(partnerMap.containsKey(d)) {
							par = partnerMap.get(d);
						}
						par.add(p);
						partnerMap.put(d, par);
					}
				}
			}

			invitatonMap.put(countryEntry.getKey(), partnerMap);

		}

		return invitatonMap;
	}

	public List<Country> getInvitees(List<Partner> partners) {

		List<Country> invites = new ArrayList<>();

		Map<String, Map<Date, List<Partner>>> invitationMap = getInvitations(partners);

		for(Entry<String, Map<Date, List<Partner>>> invitationEntry : invitationMap.entrySet()) {

			Map<Date, List<Partner>> dateMap = invitationEntry.getValue();

			int max = -1;
			Date date  = null;
			List<Partner> availablePartners = null;

			for (Entry<Date, List<Partner>> entry : dateMap.entrySet()) {

				List<Partner> partnerList = entry.getValue();

				if(partnerList.size() > max) {
					max = partnerList.size();
					date = entry.getKey();
					availablePartners = partnerList;
				}
			}	
			
			Country country = new Country();
			
			country.setAttendeeCount(availablePartners.size());

			List<String> emails  = new ArrayList<>();
			for(Partner partner : availablePartners) {
				emails.add(partner.getEmail());
			}
			country.setAttendees(emails);

			country.setName(invitationEntry.getKey());
			country.setStartDate(date);
			invites.add(country);
		}

		return invites;
	}
	
}
