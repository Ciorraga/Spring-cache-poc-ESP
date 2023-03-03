package com.pocsma.springcache.app.service;

import java.util.List;

import com.pocsma.springcache.app.model.City;

public interface CityService {

	List<City> getCity();
	
	City getCity(Integer id);
	
	City saveCity(City newCity);

	void deleteCity(Integer id);

	City updateCity(Integer id, City newCity);
	
}
