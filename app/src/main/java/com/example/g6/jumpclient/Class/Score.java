package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 14-Mar-18.
 */

import static java.lang.Math.sqrt;


public class Score{
    public static double wilsonRating(double upvotes, double downvotes) {
        return (5*(((upvotes + 1.9208) / (upvotes + downvotes) - 1.96 * sqrt(((upvotes * downvotes) / (upvotes + downvotes)) + 0.9604) / (upvotes + downvotes)) / (1 + 3.8416 / (upvotes + downvotes))));
    }
    public static double wilsonRating(int one, int two, int three, int four, int five) {
        double upvotes = (double) (one*1 +two*2 + three*3 +four*4 + five*5);
        double downvotes = (double) ( one*4 +two*3 + three*2 +four*1);
        return (5*(((upvotes + 1.9208) / (upvotes + downvotes) - 1.96 * sqrt(((upvotes * downvotes) / (upvotes + downvotes)) + 0.9604) / (upvotes + downvotes)) / (1 + 3.8416 / (upvotes + downvotes))));
    }
}

