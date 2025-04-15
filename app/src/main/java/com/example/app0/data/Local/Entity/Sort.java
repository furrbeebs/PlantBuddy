package com.example.app0.data.Local.Entity;

import java.util.ArrayList;

public class Sort {
    public static void nameAscending (ArrayList<GoalInstance> arr) {
        for (int i = 0; i < arr.size() - 1; i++ ) {
            boolean swapped = false;
            for (int j = 0; j< arr.size() - i - 1; j++) {
                if (arr.get(j).getTitle().compareTo(arr.get(j+1).getTitle()) > 0) {
                    GoalInstance temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public static void nameDescending (ArrayList<GoalInstance> arr) {
        for (int i = 0; i < arr.size() - 1; i++ ) {
            boolean swapped = false;
            for (int j = 0; j< arr.size() - i - 1; j++) {
                if (arr.get(j).getTitle().compareTo(arr.get(j+1).getTitle()) < 0) {
                    GoalInstance temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public static void pointsDescending (ArrayList<GoalInstance> arr) {
        for (int i = 0; i < arr.size() - 1; i++ ) {
            boolean swapped = false;
            for (int j = 0; j< arr.size() - i - 1; j++) {
                if (arr.get(j).getDifficulty().incPoints() < (arr.get(j+1).getDifficulty().incPoints())) {
                    GoalInstance temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
    public static void pointsAscending (ArrayList<GoalInstance> arr) {
        for (int i = 0; i < arr.size() - 1; i++ ) {
            boolean swapped = false;
            for (int j = 0; j< arr.size() - i - 1; j++) {
                if (arr.get(j).getDifficulty().incPoints() > (arr.get(j+1).getDifficulty().incPoints())) {
                    GoalInstance temp = arr.get(j);
                    arr.set(j,arr.get(j+1));
                    arr.set(j+1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
