package com.bitcoinminers.messageapp;

import org.json.JSONObject;

public interface Saveable {
    /**
     * Convert contents of this object to a JSON object.
     * @return JSON object with all key attributes.
     */
    public JSONObject toJson();

    /**
     * Copy the state of a JSON object to this object.
     * @param obj JSON object with key attributes of this object.
     */
    public void fromJson(JSONObject obj);
}
