package com.example.app0.ui;

public class CheckForms2025 implements CheckForms {

    @Override
    public String display(int level) {
        if (level >= 10 && level < 20){
            return "Sapling";
        }
        else if (level >= 20){
            return "Tree";
        }
        return "Seedling";
    }

    @Override
    public int maxXP(int level) {
        if (level >= 10 && level < 20){
            return 100000;
        }
        else if (level >= 20){
            return 1000000;
        }
        return 1000;
    }

    @Override
    public double[] checkLevel(double XP, int level) {
        if (level >= 20){
            while (XP >= 1000000){
                level += 1;
                XP -= 1000000;
            }
            return new double[]{XP, level};
        }

        if (level >=10 && level < 20){
            while (XP >=100000){
                if (level < 20){
                    level += 1;
                    XP -= 100000;
                }
                else {}
            }
            return new double[]{XP, level};
        }

        else {
            while (XP >= 1000) {
                if (level < 10){
                    level += 1;
                    XP -= 1000;
                }
            }
            return new double[]{XP, level};
        }
    }
}
