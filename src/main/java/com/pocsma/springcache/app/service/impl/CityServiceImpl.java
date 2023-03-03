package com.pocsma.springcache.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.pocsma.springcache.app.model.City;
import com.pocsma.springcache.app.repository.CityRepository;
import com.pocsma.springcache.app.service.CityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@CacheConfig(cacheNames = "cities")
public class CityServiceImpl implements CityService {

	@Autowired
	private CityRepository CityRepository;
	
	@Override
	@Cacheable()
	public List<City> getCity() {		
		try {
            Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            List<City> City = CityRepository.findAll();
    		
    		return City;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}

	@Override
	@Cacheable(key="#id")
	public City getCity(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return CityRepository.findById(id)
            		.orElse(null);

        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}

	@Override
	@Caching(evict = {
	    @CacheEvict(cacheNames = "cities", allEntries = true), 
	    @CacheEvict(cacheNames = "stores", allEntries = true)})
	public City updateCity(Integer id, City newCity) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return CityRepository.save(newCity);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
	
	@Override
	@Caching(evict = {
	    @CacheEvict(cacheNames = "cities", allEntries = true), 
	    @CacheEvict(cacheNames = "stores", allEntries = true)})
	public City saveCity(City newCity) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return CityRepository.save(newCity);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }			
	}
	

	@Override
	@Caching(evict = {
	    @CacheEvict(cacheNames = "cities", allEntries = true), 
	    @CacheEvict(cacheNames = "stores", allEntries = true)})
	public void deleteCity(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            CityRepository.deleteById(id);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
	
}
