package ua.edu.sumdu.j2se.chepiha.tasks.service;

import java.time.LocalDateTime;

public class VerifyingData {

    public static String isVerifyTask(String taskName, LocalDateTime dtStart){
        StringBuilder msgError = new StringBuilder();

        if( taskName == null || taskName.trim().length()==0){
            msgError.append("wrong name; ");
        }

        if( dtStart == null ){
            msgError.append("wrong start time; ");
        }
        return msgError.toString();
    }

    public static String isVerifyTask(String taskName, LocalDateTime dtStart, LocalDateTime dtEnd, String interval){
        StringBuilder msgError = new StringBuilder();

        if( taskName == null || taskName.trim().length()==0){
            msgError.append("wrong name; ");
        }

        if( dtStart == null ){
            msgError.append("wrong start time; ");
        }

        if( dtEnd == null ){
            msgError.append("wrong end time; ");
        }

        if( interval == null || interval.trim().length()==0){
            msgError.append("wrong interval; ");
        }

        if(dtStart != null && dtEnd != null){
            if(dtStart.isAfter(dtEnd)){
                msgError.append("start time must be before end time; ");
            }
        }

        return msgError.toString();
    }

    public static String verifyCalendar(LocalDateTime dStartCalendar, LocalDateTime dEndCalendar){
        StringBuilder msg = new StringBuilder();

        if(dStartCalendar == null){
            msg.append("wrong start time calendar;");
        }

        if(dEndCalendar == null){
            msg.append("wrong end time calendar;");
        }

        if(dStartCalendar != null && dEndCalendar != null){
            if(dStartCalendar.isAfter(dEndCalendar)){
                msg.append("start time calendar must be before end time calendar;");
            }
        }
        return msg.toString();
    }

}
