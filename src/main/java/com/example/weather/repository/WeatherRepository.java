package com.example.weather.repository;

import com.example.weather.entity.Weather;
import com.example.weather.entity.WeatherId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, WeatherId> {

}
