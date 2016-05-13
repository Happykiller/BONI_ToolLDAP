package com.bonitaSoft.tools;

import java.util.HashMap;

/**
 * Created by Fabrice on 02/10/2014.
 */
public class LocalStorage {

    protected HashMap localStorage;

    public LocalStorage() {
        this.localStorage = new HashMap<String, String>();
    }

    public void set(String key, String value) {
       this.localStorage.put(key,value);
    }

    public String get(String key) {
        return this.get(key, "");
    }

    public String get(String key, String defaultValue) {
        String valReturn = (String) this.localStorage.get(key);

        if((valReturn == null) && (defaultValue != null)){
            this.set(key, defaultValue);
            valReturn = defaultValue;
        }

        return valReturn;
    }
}
