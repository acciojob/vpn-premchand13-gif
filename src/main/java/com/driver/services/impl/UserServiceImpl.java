package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);
        user.setMaskedIp(null);

        List<String> stringList=new ArrayList<>();
        //ind, aus, usa, chi, jpn
        stringList.add("IND");
        stringList.add("AUS");
        stringList.add("USA");
        stringList.add("CHI");
        stringList.add("JPN");
        countryName=countryName.toUpperCase();

        if(!stringList.contains(countryName)){
            throw new Exception("Country not found");
        }
        Country country=new Country();
        country.setCountryName(CountryName.valueOf(countryName));
        country.setCode(CountryName.valueOf(countryName).toCode());

        country.setUser(user);
        user.setCountry(country);
        user.setOriginalIp(CountryName.valueOf(countryName).toCode()+"."+user.getId());
        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {

        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();
        User user=userRepository3.findById(userId).get();



        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        user.setServiceProviderList(serviceProviderList);

        List<User> userList=serviceProvider.getUsers();
        userList.add(user);
        serviceProvider.setUsers(userList);

        userRepository3.save(user);
        serviceProviderRepository3.save(serviceProvider);

        return user;

    }
}
