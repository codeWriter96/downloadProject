package com.wang.downloadProject.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class WriteJsonUntil {
    //将boolean值解析为json串
    public static String printJsonFlag(boolean flag){

        Map<String,Boolean> map = new HashMap<String,Boolean>();
        map.put("success",flag);

        ObjectMapper om = new ObjectMapper();
        String json =null;
        try {
            //{"success":true}
            json = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    //将对象解析为json串
    public static String printJsonObj(Object obj){

        /*
         *
         * Person p
         * 	id name age
         * {"id":"?","name":"?","age":?}
         *
         * List<Person> pList
         * [{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?}...]
         *
         * Map
         * 	key value
         * {key:value}
         *
         *
         */

        ObjectMapper om = new ObjectMapper();
        String json =null;
        try {
            json = om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
