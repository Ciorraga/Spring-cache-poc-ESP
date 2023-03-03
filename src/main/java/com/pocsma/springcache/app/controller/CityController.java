package com.pocsma.springcache.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pocsma.springcache.app.model.City;
import com.pocsma.springcache.app.service.CityService;

@RestController
public class CityController {

	@Autowired
	private CityService cityService;

	@GetMapping("/cities")
	public ResponseEntity<List<City>> getAllCities(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
		
		List<City> cities = cityService.getCity();
		
		return !cities.isEmpty() 
				? new ResponseEntity<>(cities, HttpStatus.OK) 
				: new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/cities/{id}")
    public City getCity(@PathVariable("id") Integer id) {		
		return cityService.getCity(id);
    }
	
	@PostMapping(value = "/cities")
	public ResponseEntity<Object> saveCity(@RequestBody City city){
		
		City newCity = cityService.saveCity(city);
		
		return ResponseEntity.ok(newCity);
	}
	
	@PutMapping(value = "/cities/{id}")
	public ResponseEntity<Object> updateCity(@PathVariable("id") Integer id, 
			@RequestBody City city){
		
		City newCity = cityService.updateCity(id, city);
		
		return ResponseEntity.ok(newCity);
	}
	
	@DeleteMapping(value = "/cities/{id}")
	public ResponseEntity<Object> deleteCity(@PathVariable("id") Integer id){
		
		if(getCity(id) != null) {
			cityService.deleteCity(id);
			return ResponseEntity.ok("City object deleted with id: " + id);
		}
		
		return ResponseEntity.ok("City not found with id: " + id);
		
	}
	
	@GetMapping("cities/clearcache")
	@Caching(evict = {
		    @CacheEvict(cacheNames = "cities", allEntries = true), 
		    @CacheEvict(cacheNames = "stores", allEntries = true)})
    public ResponseEntity<Object> clearCache() {
        return ResponseEntity.ok("Cache cleaned");
    }
	
	
}
