package com.aqulasoft.disyam.service;

public class SettingsThread extends Thread{
    public SettingsThread(String threadName){
        super(threadName);
    }
    public void run(){
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            try {
                SettingsThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
