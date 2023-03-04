package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user=userRepository2.findById(userId).get();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        String country_Name=countryName.toUpperCase();
        String origCountry=user.getOriginalCountry().getCode();
//        String[] s=origCountry.split("\\.");

        if(origCountry.equals(CountryName.valueOf(country_Name).toCode())){
            return user;
        }
        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        if(serviceProviderList.size()==0){
            throw new Exception("Unable to connect");
        }

        ServiceProvider serviceProviderRRR=null;
        int count=Integer.MAX_VALUE;
        String remoteCode=CountryName.valueOf(country_Name).toCode();
        for(ServiceProvider sp:serviceProviderList){
            List<Country> countryList=sp.getCountryList();
            for(Country country:countryList){
                if(country.getCode().equals(remoteCode)){
                    if(count>sp.getId()){
                        serviceProviderRRR=sp;
                        count=sp.getId();

                    }
                }
            }
        }
        if(serviceProviderRRR==null){
            throw new Exception("Unable to connect");
        }
        user.setConnected(true);

        Connection connection=new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProviderRRR);
//        connectionRepository2.save(connection);

        serviceProviderRRR.getConnectionList().add(connection);

        user.getConnectionList().add(connection);

        user.setMaskedIp(remoteCode+"."+serviceProviderRRR.getId()+"."+user.getId());


        userRepository2.save(user);
        serviceProviderRepository2.save(serviceProviderRRR);
        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user=userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();
        if(receiver.getConnected()){
            String[] resStr=receiver.getMaskedIp().split("\\.");
            String resCode=resStr[0];

//            String[] senStr=sender.getOriginalIp().split("\\.");
            String senCode=sender.getOriginalCountry().getCode();
            if(resCode.equals(senCode)){
//                sender.setMaskedIp(null);
//                sender.setConnected(false);
//                userRepository2.save(sender);
                return sender;
            }

            List<ServiceProvider> serviceProviderList=sender.getServiceProviderList();
            ServiceProvider serviceProviderRRR=null;
            int count=Integer.MAX_VALUE;
//            String remoteCode=CountryName.valueOf(country_Name).toCode();
            for(ServiceProvider sp:serviceProviderList){
                List<Country> countryList=sp.getCountryList();
                for(Country country:countryList){
                    if(country.getCode().equals(resCode)){
                        if(count>sp.getId()){
                            serviceProviderRRR=sp;
                            count=sp.getId();

                        }
                    }
                }
            }
            if(serviceProviderRRR==null){
                throw new Exception("Cannot establish communication");
            }
            sender.setConnected(true);
            sender.setMaskedIp(resCode+"."+serviceProviderRRR.getId()+"."+sender.getId());
            userRepository2.save(sender);
            return sender;
        }
//        String[] resStr=receiver.getOriginalIp().split("\\.");
        String resCode=receiver.getOriginalCountry().getCode();

//        String[] senStr=sender.getOriginalIp().split("\\.");
        String senCode=sender.getOriginalCountry().getCode();
        if(resCode.equals(senCode)){
//            sender.setMaskedIp(null);
//            sender.setConnected(false);
//            userRepository2.save(sender);
            return sender;
        }

        List<ServiceProvider> serviceProviderList=sender.getServiceProviderList();
        ServiceProvider serviceProviderRRR=null;
        int count=Integer.MAX_VALUE;
//            String remoteCode=CountryName.valueOf(country_Name).toCode();
        for(ServiceProvider sp:serviceProviderList){
            List<Country> countryList=sp.getCountryList();
            for(Country country:countryList){
                if(country.getCode().equals(resCode)){
                    if(count>sp.getId()){
                        serviceProviderRRR=sp;
                        count=sp.getId();

                    }
                }
            }
        }
        if(serviceProviderRRR==null){
            throw new Exception("Cannot establish communication");
        }
        sender.setConnected(true);
        sender.setMaskedIp(resCode+"."+serviceProviderRRR.getId()+"."+sender.getId());
        userRepository2.save(sender);
        return sender;
    }
}
