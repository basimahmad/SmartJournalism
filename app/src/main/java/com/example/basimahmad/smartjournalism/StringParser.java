package com.example.basimahmad.smartjournalism;

import org.json.JSONException;
import org.json.JSONObject;

import Tempelate.Template;

/**
 * Created by Basim AHmad on 14-Nov-17.
 */
public class StringParser {


    public static String getCode(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return String.valueOf(jsonObject.getInt(Template.Query.KEY_CODE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMessage(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString(Template.Query.KEY_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}