package com.example.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.application.views.helloworld.HelloWorldView;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.storage.StorageProvider;

@Service
public class IlivalidatorService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HelloWorldView helloWorldView;

    @Job(name="Ilivalidator")
    public synchronized boolean validate(String inputFileName) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
        
//        helloWorldView.getButton().setText("SCHEEEEIIIISSEE");
        
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return true;
    }
}
