package com.example.demo;

import java.util.ArrayList;

public class Deer {
    private int x;
    private int y;
    private int age;
    private int months;
    private int gender; //0: female 1: male
    private int hunger;
    private int thirst;
    private long startTime;
    private int sleep;
    private Deer mother;
    private boolean hasBaby;
    private int myFlock;
    private boolean hasFlock;
    private boolean noticedPredator;
    private Tiger predator;

    public Deer(int x, int y, int a, int f){
        this.x = x;
        this.y = y;
        age = a;
        months = 0;
        gender = (int)(Math.random() * 2);
        hunger = 2;
        thirst = 2;
        sleep = 0;
        startTime = System.nanoTime();
        mother = null;
        hasBaby = false;
        myFlock = f;
        hasFlock = true;
    }

    public void changeLoc(int[][] gameGrid, ArrayList<ArrayList<Deer>> allFlock, ArrayList<Locations> water, ArrayList<Locations> bush){
        int timeout=0;
        boolean check = false;
        while(!check && timeout<1000){
            timeout++;
            int tempx = x;
            int tempy = y;
            if(Math.random()>.5 && tempx < gameGrid.length-1){
                tempx++;
            }else if(tempx > 0){
                tempx--;
            }
            if(Math.random()>.5 && tempy < gameGrid[0].length-1){
                tempy++;
            }else if(tempy > 0) {
                tempy--;
            }
            if (gameGrid[tempx][tempy]==0 && tempx >= 0 && tempx <= gameGrid.length && tempy >= 0 && tempy <= gameGrid[0].length) {
                if(hasFlock && thirst < 20 && hunger < 15 && myFlock >= 0 && myFlock < allFlock.size() && allFlock.get(myFlock).size() > 0){
                    if(!this.equals(allFlock.get(myFlock).get(0))) {
                        if (allFlock.get(myFlock).get(0).getX() >= tempx - 3 && allFlock.get(myFlock).get(0).getX() <= tempx + 3 &&
                                allFlock.get(myFlock).get(0).getY() >= tempy - 3 && allFlock.get(myFlock).get(0).getY() <= tempy + 3) {
                            check = true;
                            if (age <= 1) {
                                gameGrid[tempx][tempy] = 4;
                            } else {
                                gameGrid[tempx][tempy] = gender + 5;
                            }
                            gameGrid[x][y] = 0;
                            x = tempx;
                            y = tempy;
                        }
                    } else {
                        check = true;
                        if (age <= 1) {
                            gameGrid[tempx][tempy] = 4;
                        } else {
                            gameGrid[tempx][tempy] = gender + 5;
                        }
                        gameGrid[x][y] = 0;
                        x = tempx;
                        y = tempy;
                    }
//                    mother.hasBaby = true;
                } else {
                    if (hunger >= 15) {
                        if (this.placeDistance(bush, tempx, tempy) < this.placeDistance(bush, x, y)) {
                            check = true;
                            gameGrid[tempx][tempy] = gender + 5;
                            gameGrid[x][y] = 0;
                            x = tempx;
                            y = tempy;
                        }
                    }else if (thirst >= 20) {
                        if (this.placeDistance(water, tempx, tempy) < this.placeDistance(water, x, y)) {
                            check = true;
                            gameGrid[tempx][tempy] = gender + 5;
                            gameGrid[x][y] = 0;
                            x = tempx;
                            y = tempy;
                        }
                    } else{
                        check = true;
                        gameGrid[tempx][tempy] = gender + 5;
                        gameGrid[x][y] = 0;
                        x = tempx;
                        y = tempy;
                    }
                }
            }
        }
//          System.out.println("x: " + x);
    }

    public boolean checkingOpposite(ArrayList<Deer> tempGrid){
        //check how many squares away.
        for (int i = 0; i < tempGrid.size(); i++) {
            if(Math.abs(tempGrid.get(i).getX() - x) == 1 && Math.abs(tempGrid.get(i).getY() - y) == 1 && gender!=tempGrid.get(i).getGender() && tempGrid.get(i).getAge() >= 1 && age >= 1 && !hasBaby){
                if(tempGrid.get(i).getMother() != null && !tempGrid.get(i).getMother().equals(this)){
                    return true;
                } else if (tempGrid.get(i).getMother() == null){
                    return true;
                }
                System.out.println("smth aint right");
                return false;
            }
        }
//        for (int r = x-1; r <= x+1 && r < tempGrid.size() && r>0; r++) {
//            for (int c = y-1; c <= y+1 && c < tempGrid[r].length && c>0; c++) {
//                if(tempGrid[r][c] != 0 && (r!=x && c!=y)){
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public void checkFlock(ArrayList<ArrayList<Deer>> allFlock, ArrayList<Deer> tempGrid){
        if(myFlock >= 0 && myFlock < allFlock.size() && allFlock.get(myFlock).size() > 0){
            //checks if deer is atleast 5 away from flock gorup leader
            if(allFlock.get(myFlock).get(0).getX() >=x-5 && allFlock.get(myFlock).get(0).getX()<=x+5 &&
                    allFlock.get(myFlock).get(0).getY() >=y-5 && allFlock.get(myFlock).get(0).getY()<=y+5){
                hasFlock = true;
            } else {
                System.out.println("lost the fam bro");
                hasFlock = false;
                //remove deer from flock array
                allFlock.get(myFlock).remove(this);

                if(allFlock.get(myFlock).size() == 0){
                    allFlock.remove(myFlock);
                    System.out.println("removed flock: " + myFlock);
                    for (int j = 0; j < tempGrid.size(); j++) {
                        if(tempGrid.get(j).isHasFlock() && tempGrid.get(j).getMyFlock()>myFlock){
                            tempGrid.get(j).setMyFlock(tempGrid.get(j).getMyFlock()-1);
                        }
                    }
                }

                myFlock = -1;
            }
        } else {
            hasFlock = false;
            for (int i = 0; i < allFlock.size(); i++) {
                if(allFlock.get(i).size() == 0){
                    allFlock.remove(i);
                    System.out.println("removed flock: " + i);
                    for (int j = 0; j < tempGrid.size(); j++) {
                        if(tempGrid.get(j).isHasFlock() && tempGrid.get(j).getMyFlock()>i){
                            tempGrid.get(j).setMyFlock(tempGrid.get(j).getMyFlock()-1);
                        }
                    }
                }
            }
            //if lone deer encounters new flock it will join as long as there is enough space
            for (int i = 0; i < allFlock.size(); i++) {
                if(allFlock.get(i).get(0).getX() >=x-5 && allFlock.get(i).get(0).getX()<=x+5 &&
                        allFlock.get(i).get(0).getY() >=y-5 && allFlock.get(i).get(0).getY()<=y+5 && allFlock.get(i).size() <=7) {
                    System.out.println("found anotha flock besties");
                    hasFlock = true;
                    myFlock = i;
                }
            }

            //if there are two lone deer they create their own lone flock
            for (int i = 0; i < tempGrid.size(); i++) {
                if(tempGrid.get(i).getX() >=x-5 && tempGrid.get(0).getX()<=x+5 &&
                        tempGrid.get(0).getY() >=y-5 && tempGrid.get(0).getY()<=y+5 && tempGrid.get(i).getMyFlock() < 0 && myFlock < 0){
                    allFlock.add(new ArrayList<>());
                    allFlock.get(allFlock.size()-1).add(tempGrid.get(i));
                    tempGrid.get(i).setMyFlock(allFlock.size()-1);
                    tempGrid.get(i).setHasFlock(true);
                    allFlock.get(allFlock.size()-1).add(this);
                    hasFlock = true;
                    myFlock = allFlock.size()-1;
                    System.out.println("CREATED A FLOCK");
                }
            }
        }
    }

    public void checkPredator(ArrayList<Tiger> tempGrid){
        for (Tiger e: tempGrid) {
            if(e.getX()>=x-3 && e.getX()<=x+3 && e.getY() >=y-3 && e.getY()<=y+3 && e.getAge() >= 3){
                if(e.isHunting()){
                    if(e.getPrey().equals(this) && Math.random() <= .1){
                        noticedPredator = true;
                        predator = e;
                    } else if(!e.getPrey().equals(this) && Math.random() <= .4){
                        noticedPredator = true;
                        predator = e;
                    }
                } else if (Math.random() <= .6){
                    noticedPredator = true;
                    predator = e;
                }
            }
        }
    }

    public void runningAway(int[][] gameGrid, ArrayList<Tiger> allTiger){
        ArrayList<Locations> tiger = new ArrayList<>();
        ArrayList<Locations> tempLocs = new ArrayList<>();
        double distance;
        Locations which = new Locations(x, y);

        //adding every location around the tiger to an array
        if(allTiger.contains(predator) && predator.getX()>=x-6 && predator.getX()<=x+6 && predator.getY() >=y-6 && predator.getY()<=y+6){
            tiger.add(new Locations(predator.getX(), predator.getY()));
            distance = this.placeDistance(tiger, x, y);
            for (int r = x-1; r <= x+1 && r < gameGrid.length && r > 0; r++) {
                for (int c = y-1; c <= y+1 && c < gameGrid[0].length && c > 0; c++) {
                    if(checkEmptyAround(r, c, gameGrid)){
                        tempLocs.add(new Locations(r, c));
                    }
                }
            }

            //looking through every location around the tiger and determining which is the closest to the target
            //if there is no closest location the tiger remains in the same spot.
            for(Locations e: tempLocs){
                if(placeDistance(tiger, e.getX(), e.getY()) > distance){
                    distance = placeDistance(tiger, e.getX(), e.getY());
                    which.setX(e.getX());
                    which.setY(e.getY());
                }
            }
        } else {
            predator = null;
            noticedPredator = false;
        }
        if(age<=1){
            gameGrid[which.getX()][which.getY()]= 4;
        } else {
            gameGrid[which.getX()][which.getY()]= gender + 5;
        }
        gameGrid[x][y]=0;
        x= which.getX();
        y= which.getY();
    }

    public boolean willDie(){
        if(hunger >= 15 && thirst >= 20){
            if(age < 2 && Math.random() >= age * .3){
                System.out.println("shawty so young she couldnt handle the pain bro");
                return true;
            } else if (Math.random() < age * .1){
                System.out.println("this was kinda random bro");
                return true;
            }
        } else if (thirst >= 40){
            System.out.println("shawty dehydrated bro");
            return true;
        } else if (hunger >= 30){
            System.out.println("shawty starved bro");
            return true;
        } else if (age >= 15 && Math.random() > .5){
            System.out.println("shawty old asf");
            return true;
        }
        return false;
    }

    public double placeDistance(ArrayList<Locations> place, int x, int y){
        double distance = 100;
        for(Locations e: place){
            double temp = Math.hypot(x-e.getX(), y-e.getY());
            if(temp < distance){
                distance = temp;
            }
        }
        return distance;
    }

    public boolean checkEmptyAround(int i, int j, int[][] gameGrid){
        return gameGrid[i][j] == 0;
    }

    public boolean inWater(int[][] envGrid){
        return envGrid[x][y] == 1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender;
    }

    public int getHunger() {
        return hunger;
    }

    public int getThirst() {
        return thirst;
    }

    public int getSleep() {
        return sleep;
    }

    public Deer getMother() {
        return mother;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getMonths() {
        return months;
    }

    public boolean isHasBaby() {
        return hasBaby;
    }

    public int getMyFlock() {
        return myFlock;
    }

    public boolean isHasFlock() {
        return hasFlock;
    }

    public boolean isNoticedPredator() {
        return noticedPredator;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public void resetStartTime() {
        this.startTime = System.nanoTime();
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public void setMother(Deer mother) {
        this.mother = mother;
    }

    public void setMyFlock(int myFlock) {
        this.myFlock = myFlock;
    }

    public void setHasFlock(boolean hasFlock) {
        this.hasFlock = hasFlock;
    }

    public void setHasBaby(boolean hasBaby) {
        this.hasBaby = hasBaby;
    }

    public void setNoticedPredator(boolean noticedPredator) {
        this.noticedPredator = noticedPredator;
    }

    public void increaseMonths(){
        months += 2;
        if(months >= 12){
            age ++;
            months = 0;
        }
        if (age >= 1 && mother != null) {
            mother.hasBaby = false;
        }
    }
}
