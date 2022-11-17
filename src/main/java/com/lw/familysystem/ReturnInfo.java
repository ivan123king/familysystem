package com.lw.familysystem;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lw
 * @date 2022/10/28 0028
 * @description  返回信息
 */
@Data
@Slf4j
public class ReturnInfo {

    private String result_code = "0";
    private String msg;

    private Object data;

    public static ReturnInfo returnSuccessInfo(){
        ReturnInfo returnInfo = new ReturnInfo();
        return returnInfo;
    }

    public static ReturnInfo returnSuccessInfo(Object o){
        ReturnInfo returnInfo = new ReturnInfo();
        returnInfo.data = o;
        return returnInfo;
    }
    public static ReturnInfo returnErrorInfo(String msg){
        ReturnInfo returnInfo = new ReturnInfo();
        returnInfo.result_code = "500";
        return returnInfo;
    }
}
