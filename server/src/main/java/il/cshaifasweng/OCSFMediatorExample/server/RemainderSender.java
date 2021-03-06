package il.cshaifasweng.OCSFMediatorExample.server;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import il.cshaifasweng.OCSFMediatorExample.entities.HomeLinkTicket;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static java.time.temporal.ChronoUnit.MINUTES;

public class RemainderSender implements  Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("executing.....");
        ArrayList<HomeLinkTicket> hometickets= DBase.getHomeLinkTicket();
        for(HomeLinkTicket hlt:hometickets){
            System.out.println(hlt.getBuyerName());
            LocalTime lt= LocalTime.now();
            System.out.println(lt);
            Long remainingtime=lt.until(hlt.getStartingTime(),MINUTES);
            System.out.println(remainingtime);
            System.out.println("link starting time "+hlt.getStartingTime());
            if(remainingtime<=60&& hlt.isSent()==false){
                EmailUtil.sendEmailRemainder(hlt);
                hlt.setSent(true);
                System.out.println("remainder has been sent");
                DBase.UpdateHometicket(hlt);
            }
        }
    }
}
