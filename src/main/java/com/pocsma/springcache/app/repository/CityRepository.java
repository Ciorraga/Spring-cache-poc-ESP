package com.pocsma.springcache.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pocsma.springcache.app.model.City;

public interface CityRepository extends JpaRepository<City, Integer>{

}

