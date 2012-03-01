package com.commTimeCal;

import java.util.LinkedList;
import java.util.List;

public class TimeCal {
	/*
	 * 
	 * Time table for a day is represented by 2 dimensional array
	 * timeslot[24][12]
	 * 	 0 1 2 ... 23
	 * 0 . . . 
	 * 1 . . . 
	 * 2 . . .
	 * .
	 * .
	 * 11. . .
	 * 
	 * if timeslot[0][5] is non zero, it means 00:30 to 00:35 is filled by event
	 * 
	 * 
	 * */
	

	
	
	// input array of time, e.g. args = {"10:23","12:23",...} 
	// first time period is first two strings, second time period is second two strings, ...
	// assume all time in args are in the same day for one user, no overlaping time period
	public static int[][] getTimeslot(String[] args){
        
		int timeslot[][] = new int[24][12];
		
		for(int i = 0;i<24;i++){
        	for(int j = 0;j<12;j++){
        		timeslot[i][j]=0;
        	}
        }
		
        for(int i = 0;i<args.length;i+=2){
        	String start[] = args[i].split(":");
        	String end[] = args[i+1].split(":");
        	int startHour = Integer.parseInt(start[0]);
        	int endHour = Integer.parseInt(end[0]);
        	int startMin = Integer.parseInt(start[1]);
        	int endMin = Integer.parseInt(end[1]);
        	
        	int trickstart =startHour*100+startMin;
        	int trickend =endHour*100+endMin;
        	
        	while(trickstart!=trickend){
        		timeslot[startHour][startMin/5]=1;
        		if(startMin==55) startHour++;
        		startMin=(startMin+5)%60;
        		trickstart=startHour*100+startMin;
        	}
        	
        }
        
        return timeslot;
		
	}
	
	// get common timeslot for two users
	public static int[][] getUsersTimeslot(int[][] u1, int[][] u2){
		int output [][] = new int[24][12];
		for(int i = 0;i<24;i++){
			for(int j = 0;j<12;j++){
				output[i][j]=u1[i][j]+u2[i][j];
			}
		}
		return output;
	}
	
	// output free timeslot
	// e.g. if 10:00-11:00 is busy time, then it output {"00:00","10:00","11:00","00:00"}
	public static String[] getFreeTime(final int[][] timeslot){

		LinkedList<String> output = new LinkedList<String>();
		
		boolean isstart=true;
		for(int i = 0;i<24 ;i++ ){
			for(int j = 0;j<12;j++){
				if(isstart && timeslot[i][j]==0){
					output.add(String.format("%02d", i)+":"+String.format("%02d", j*5));
					isstart=false;
				}
				if(!isstart && timeslot[i][j]!=0){
					output.add(String.format("%02d", i)+":"+String.format("%02d", j*5));
					isstart=true;
				}
			}
		}
		if(!isstart){
			output.add("00:00");
		}
		
		
		return output.toArray(new String[output.size()]);
	}
	
	// print timeslot array tfor testing
	public static void printTimeslot(int[][] x){
		for(int i = 0;i<12;i++){
			String output="";
			for(int j = 0;j<24;j++){
				output+=x[j][i]+" ";
			}
			System.out.println(output);
		}
	}
	
}