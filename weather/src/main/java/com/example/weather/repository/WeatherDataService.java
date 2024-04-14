package com.example.weather.repository;


import com.example.weather.model.weatherdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherDataService {
	@Autowired
    private JdbcTemplate jdbcTemplate;

    public List<weatherdata> getAllData() {
        String sql = "SELECT `humi`, `temp`, `gettime` FROM `th`";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            weatherdata data = new weatherdata();
            data.setHumi(rs.getDouble("humi"));
            data.setTemp(rs.getDouble("temp"));
            data.setGettime(rs.getTimestamp("gettime"));
            return data;
        });
    }
	

}
