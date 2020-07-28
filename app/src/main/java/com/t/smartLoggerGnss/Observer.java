package com.t.smartLoggerGnss;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Observer {

    public Observer() {
        observerList = new LinkedList<IObserver>();
    }

    /**
     * Informs all observers and calls the notifyGnss() function
     */
    public void notifyGnssObserver() {
        Iterator<IObserver> it = observerList.iterator();
        while (it.hasNext()) {
            IObserver obs = it.next();
            obs.notifyGnss();
        }
    }

    /**
     * Informs all observers and calls the notifyDisable() function
     */
    public void notifyDisableObserver() {
        Iterator<IObserver> it = observerList.iterator();
        while (it.hasNext()) {
            IObserver obs = it.next();
            obs.notifyDisable();
        }
    }

    /**
     * Informs all observers and calls the notifyNavigationMessage() function
     */
    public void notifyNavigationMessageObserver() {
        Iterator<IObserver> it = observerList.iterator();
        while (it.hasNext()) {
            IObserver obs = it.next();
            obs.notifyNavigationMessage();
        }
    }

    /**
     * Informs all observers and calls the notifyLocation() function
     */
    public void notifyLocationObserver() {
        Iterator<IObserver> it = observerList.iterator();
        while (it.hasNext()) {
            IObserver obs = it.next();
            obs.notifyLocation();
        }
    }

    /**
     * Informs all observers and calls the notifySensor() function
     */
    public void notifySensorObserver() {
        Iterator<IObserver> it = observerList.iterator();
        while (it.hasNext()) {
            IObserver obs = it.next();
            obs.notifySensor();
        }
    }

    /**
     * Adds a observer to the list
     * @param observer
     */
    void addObserver(IObserver observer) {
        observerList.add(observer);
    }

    private List<IObserver> observerList;
}



