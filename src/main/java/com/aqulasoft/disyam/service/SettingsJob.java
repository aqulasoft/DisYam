package com.aqulasoft.disyam.service;

import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;

import java.util.concurrent.TimeUnit;

@SimpleTrigger(repeatInterval = 10, timeUnit = TimeUnit.SECONDS)

public class SettingsJob extends org.knowm.sundial.Job {

    @Override
    public void doRun() throws JobInterruptException {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}
