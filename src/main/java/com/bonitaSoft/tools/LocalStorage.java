package com.bonitaSoft.tools;

import java.util.HashMap;

/**
 * Created by Fabrice on 02/10/2014.
 */
public class LocalStorage {

    protected HashMap localStorage;

    public LocalStorage() {
        this.localStorage = new HashMap<String, Object>();
    }

    public void set(String key, Object value) {
       this.localStorage.put(key,value);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    public Object get(String key, Object defaultValue) {
        Object valReturn = this.localStorage.get(key);

        if((valReturn == null) && (defaultValue != null)){
            this.set(key, defaultValue);
            valReturn = defaultValue;
        }

        return valReturn;
    }
}
