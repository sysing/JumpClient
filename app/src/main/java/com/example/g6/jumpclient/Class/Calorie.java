package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 15-Mar-18.
 */

public class Calorie {
    public static boolean MALE = true, FEMALE = false;

    //The Mifflin St Jeor Equation:
    public static double getBMR (double weight, double height,double age , boolean gender ){
        double s;
        if (gender == MALE){
             s = 5;
        }else{
             s = -161;
        }
        return (10*weight + 6.25* height + 5*age + s);
    }

    public static double getMealIntake(double BMR, double weekTarget){
        return (BMR + (weekTarget*1100)) /3 ;
    }

    public static double getDailyIntake(double BMR, double weekTarget){
        return (BMR + (weekTarget*1100)) ;
    }

}
