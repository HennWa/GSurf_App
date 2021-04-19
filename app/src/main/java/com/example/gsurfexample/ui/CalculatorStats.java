package com.example.gsurfexample.ui;

import com.example.gsurfexample.source.local.historic.ProcessedDataHistoric;

import java.util.List;

public class CalculatorStats {

    public static String calculatePeriod(List<ProcessedDataHistoric> processedData){

        long endTime = processedData.get(processedData.size()-1).getTimeStamp();
        long startTime = processedData.get(0).getTimeStamp();
        long elapsedTimeHr = (long)((endTime - startTime)/1e3/60/60);
        String elapsedTimeHrStr;
        if(elapsedTimeHr<10){
            elapsedTimeHrStr = "0"+elapsedTimeHr;
        }else{
            elapsedTimeHrStr = ""+elapsedTimeHr;
        }
        long elapsedTimeMin = (long)((endTime - startTime
                - elapsedTimeHr*60*60*1e9)/1e3/60);
        String elapsedTimeMinStr;
        if(elapsedTimeMin<10){
            elapsedTimeMinStr = "0"+elapsedTimeMin;
        }else{
            elapsedTimeMinStr = ""+elapsedTimeMin;
        }
        long elapsedTimeSec = (long)((endTime - startTime
                - elapsedTimeHr*60*60*1e3 -
                -elapsedTimeMin*1e3*60)/1e3%60);
        String elapsedTimeSecStr;
        if(elapsedTimeSec<10){
            elapsedTimeSecStr = "0"+elapsedTimeSec;
        }else{
            elapsedTimeSecStr = ""+elapsedTimeSec;
        }
        return elapsedTimeHrStr + ":" + elapsedTimeMinStr + ":" + elapsedTimeSecStr + " hr";
    }
}
