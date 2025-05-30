package com.example.WheaterApp.cities;

import com.example.WheaterApp.appuser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {
    List<City> findByUser(AppUser user);

    Optional<City> findByName(String name);

    Optional<City> findByNameAndUser(String name, AppUser user);
}
