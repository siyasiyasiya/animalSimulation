package com.example.demo;

import java.util.ArrayList;

public class Tiger {
    private int x;
    private int y;
    private int age;
    private int months;
    private int gender; //0: female 1: male
    private int hunger;
    private int thirst;
    private long startTime;
    private int sleep;
    private Tiger mother;
    private boolean hasBaby;
    private Deer prey;
    private boolean hunting;
    private Tiger targetMate;

    public Tiger(int x, int y, int a){
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
        prey = null;
        hunting = false;
    }

    public void changeLoc(int[][] gameGrid, ArrayList<Locations> water){
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
                if(age<3){
                    if(mother.getX() >=tempx-2 && mother.getX()<=tempx+2 &&
                            mother.getY() >=tempy-2 && mother.getY()<=tempy+2){
                        check=true;
                        gameGrid[tempx][tempy]= 1;
                        gameGrid[x][y]=0;
                        x=tempx;
                        y=tempy;
                    }
                    mother.hasBaby = true;
                } else {
                    //only moving to the spot if it is closer to the water then the current spot
                    if(thirst>=20){
                        if(this.placeDistance(water, tempx, tempy) < this.placeDistance(water, x, y)){
                            System.out.println("tiger thirsty bro");
                            check=true;
                            moved(gameGrid, tempx, tempy);
                        }
                    } else {
                        check=true;
                        moved(gameGrid, tempx, tempy);
                    }
                }
            }
        }
//          System.out.println("x: " + x);
    }

    public void moved(int[][] gameGrid, int tempx, int tempy){
        gameGrid[tempx][tempy]= gender + 2;
        gameGrid[x][y]=0;
        x=tempx;
        y=tempy;
    }

    public boolean checkingOpposite(ArrayList<Tiger> tempGrid){
        //check how many squares away.
        for (int i = 0; i < tempGrid.size(); i++) {
            if(Math.abs(tempGrid.get(i).getX() - x) == 1 && Math.abs(tempGrid.get(i).getY() - y) == 1 && gender!=tempGrid.get(i).getGender() && tempGrid.get(i).getAge() >= 3 && age >= 3 && !hasBaby){
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

    public void findPrey(ArrayList<Deer> deer, int[] wind) {
        for (Deer d : deer) {
            //if deer is 15 blocks away from tiger than tiger will chase deer
            if (d.getX() >= x - 15 + wind[0] && d.getX() <= x + 15 + wind[0] &&
                    d.getY() >= y - 15 + wind[1] && d.getY() <= y + 15 + wind[1] && prey == null) {
                prey = d;
                hunting = true;
                //if there is another deer that is less than 2 blcoks away and the deer that is currently
                //being chased is further away than the deer will switch targets.
            } else if(hunting && d.getX() >= x - 2 + wind[0] && d.getX() <= x + 2 + wind[0] &&
                    d.getY() >= y - 2 + wind[0] && d.getY() <= y + 2 + wind[0]){
                if(prey.getX() <= x - 2 + wind[0] && prey.getX() >= x + 2 + wind[0] &&
                        prey.getY() <= y - 2 + wind[0] && prey.getY() >= y + 2 + wind[0]){
                    prey = d;
                    System.out.println("tiger changed prey");
                }
            }
        }
    }

    public void chasingPrey(int[][] gameGrid, ArrayList<Deer> allDeer){
        ArrayList<Locations> deer = new ArrayList<>();
        ArrayList<Locations> tempLocs = new ArrayList<>();
        double distance;
        Locations which = new Locations(x, y);

        //adding every location around the tiger to an array
        if(allDeer.contains(prey)){
            deer.add(new Locations(prey.getX(), prey.getY()));
            distance = this.placeDistance(deer, x, y);
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
                if(placeDistance(deer, e.getX(), e.getY()) < distance){
                    distance = placeDistance(deer, e.getX(), e.getY());
                    which.setX(e.getX());
                    which.setY(e.getY());
                }
            }
        } else {
            prey = null;
            hunting = false;
        }
        moved(gameGrid, which.getX(), which.getY());
    }


    public void foundPrey(ArrayList<Deer> deer, int[][] tempGrid, ArrayList<ArrayList<Deer>> allFlock, ArrayList<Tiger> tigers, Deer prey){
        if(prey.getX() >= x - 1 && prey.getX() <= x + 1 &&
                prey.getY() >= y - 1 && prey.getY() <= y + 1){
            tempGrid[prey.getX()][prey.getY()] = 0;
            if(prey.isHasFlock()){
                allFlock.get(prey.getMyFlock()).remove(prey);
            }
            deer.remove(prey);
            hunger = 0;
            thirst -= 1;
            hunting = false;
            this.prey = null;
            for (Tiger a: tigers) {
                if(a.getAge() < 3 && a.getMother() == this){
                    hunger = 0;
                    thirst -= 1;
                }
            }
            System.out.println("she munching bro");
        }
    }

    public void chasingMate(int[][] gameGrid, ArrayList<Tiger> allTiger){
        ArrayList<Locations> mate = new ArrayList<>();
        ArrayList<Locations> tempLocs = new ArrayList<>();
        double distance;
        Locations which = new Locations(x, y);

        //adding every location around the tiger to an array
        if(allTiger.contains(targetMate)){
            mate.add(new Locations(targetMate.getX(), targetMate.getY()));
            distance = this.placeDistance(mate, x, y);
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
                if(placeDistance(mate, e.getX(), e.getY()) < distance){
                    System.out.println("THE TIGERS ARE MATING AND CHASING EACH OTHER");
                    distance = placeDistance(mate, e.getX(), e.getY());
                    which.setX(e.getX());
                    which.setY(e.getY());
                }
            }
        } else {
            targetMate = null;
        }
        moved(gameGrid, which.getX(), which.getY());
    }

    public boolean willDie(){
        if(hunger >= 20 && thirst >= 25){
            if(age < 3 && Math.random() >= age * .3){
                System.out.println("deer so young it died");
                return true;
            } else if (Math.random() < age * .1){
                System.out.println("this was kinda random bro");
                return true;
            }
        } else if (thirst >= 45){
            System.out.println("shawty dehydrated bro");
            return true;
        } else if (hunger >= 35){
            System.out.println("shawty starved bro");
            return true;
        } else if (age >= 15 && Math.random() > .5){
            System.out.println("deer very old");
            return true;
        }
        return false;
    }

    public boolean checkDistance(Tiger check, int dist){
        return (check.getX() >= x - dist && check.getX() <= x + dist &&
                check.getY() >= y - dist && check.getY() <= y + dist);
    }

    public double placeDistance(ArrayList<Locations> places, int x, int y){
        double distance = 100;
        for(Locations e: places){
            double temp = Math.hypot(x-e.getX(), y-e.getY());
            if(temp < distance){
                distance = temp;
            }
        }
        return distance;
    }

    public boolean inWater(int[][] envGrid){
        return envGrid[x][y] == 1;
    }

    public boolean checkEmptyAround(int i, int j, int[][] gameGrid){
        return gameGrid[i][j] == 0;
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

    public Tiger getMother() {
        return mother;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getMonths() {
        return months;
    }

    public Deer getPrey() {
        return prey;
    }

    public boolean isHasBaby() {
        return hasBaby;
    }

    public boolean isHunting() {
        return hunting;
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

    public void setMother(Tiger mother) {
        this.mother = mother;
    }

//    public void setMonths{int m}{
//        this.months = m;
//    }


    public void setHasBaby(boolean hasBaby) {
        this.hasBaby = hasBaby;
    }

    public void increaseMonths(){
        months += 2;
        if(months >= 12){
            age ++;
            months = 0;
        }
        if (age >= 3 && mother != null) {
            mother.hasBaby = false;
        }
    }

    public Tiger getTargetMate() {
        return targetMate;
    }

    public void setTargetMate(Tiger targetMate) {
        this.targetMate = targetMate;
    }
}
