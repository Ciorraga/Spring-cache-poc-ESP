package com.pocsma.springcache.app.service;

import java.util.List;

import com.pocsma.springcache.app.model.Store;

public interface StoreService {

	List<Store> getStores();
	
	Store getStore(Integer id);
	
	Store saveStore(Store newStore);

	void deleteStore(Integer id);

	Store updateStore(Integer id, Store newStore);
	
}
