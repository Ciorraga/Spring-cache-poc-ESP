package com.pocsma.springcache.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pocsma.springcache.app.model.Store;
import com.pocsma.springcache.app.repository.StoreRepository;
import com.pocsma.springcache.app.service.StoreService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	private StoreRepository storeRepository;
	
	@Override
	@Cacheable(cacheNames = "stores")
	public List<Store> getStores() {		
		try {
            Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            List<Store> store = storeRepository.findAll();
    		
    		return store;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}

	@Override
	@Cacheable(cacheNames = "stores", key="#id")
	public Store getStore(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.findById(id)
            		.orElse(null);

        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }
	}

	@Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public Store updateStore(Integer id, Store newStore) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.save(newStore);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
	
	@Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public Store saveStore(Store newStore) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            return storeRepository.save(newStore);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }			
	}
	

	@Override
	@CacheEvict(cacheNames = "stores", allEntries = true)
	public void deleteStore(Integer id) {
		try {
        	Thread.sleep(3000);
            log.info("return value after {} seconds", 3);
            
            storeRepository.deleteById(id);
        } catch (Exception e) {
        	log.error(e.getMessage());
            throw new RuntimeException(e);
        }		
	}
		
	@CacheEvict(cacheNames = "stores", allEntries = true)
	@Scheduled(fixedRateString = "600000")
	public void emptyCache() {
	    log.info("emptying cache");
	}

}
