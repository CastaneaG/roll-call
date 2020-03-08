package com.wangkangli.roll_call.tools.GSONTools;

import com.google.gson.Gson;

public class  GSONTool {
    static public <T> T parseJSONWithGSON(String jsonData,Class<T> type){
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData,type);
        return result;
    }
}
