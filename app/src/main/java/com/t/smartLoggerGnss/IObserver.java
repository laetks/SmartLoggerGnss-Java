package com.t.smartLoggerGnss;


public interface IObserver {
    void notifyGnss() ;
    void notifyNavigationMessage();
    void notifyLocation();
    void notifyDisable();
    void notifySensor();

}
