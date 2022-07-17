package com.example.demo;

import javafx.fxml.FXML;


//import com.sun.prism.paint.Color;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class HelloController {
    int x = 44;
    int y = 59;
    Button[][] btn = new Button[x][y];
    int[][] gameGrid = new int[x][y];
    int[][] environmentGrid = new int[x][y];
    int[] wind = new int[2];
    long gameStart = System.nanoTime();
    boolean matingSeason = false;
    int salinityLvl = 0;
    int riverWidth = 6;
    ArrayList<Locations> water = new ArrayList<>();
    ArrayList<Locations> bush = new ArrayList<>();
    ArrayList<Tiger> allTigers = new ArrayList<>();
    ArrayList<ArrayList<Deer>> allFlocks = new ArrayList<>();
    ArrayList<Deer> allDeer = new ArrayList<>();
    //GridPane gPane = new GridPane();
//    Image k = new Image("resources/Koala.jpg");
    @FXML
    private AnchorPane aPane, keyPane;
    @FXML
    private GridPane gPane;
    @FXML
    private LineChart lChart;
    @FXML
    private Button startButton, tigerButton, deerButton;
    @FXML
    private CheckBox matingCheck;
    @FXML
    private Slider climateBar, humanBar;
    @FXML
    private Label climate, human;

    ArrayList<Integer> monthlyDeer = new ArrayList<>();
    ArrayList<Integer> monthlyTiger = new ArrayList<>();
    XYChart.Series deerPop = new XYChart.Series();
    XYChart.Series tigerPop = new XYChart.Series();

    @FXML
    private void handleStart(ActionEvent event) {
        startButton.setVisible(false);
        tigerButton.setVisible(true);
        deerButton.setVisible(true);
        matingCheck.setVisible(true);
        climateBar.setVisible(true);
        humanBar.setVisible(true);
        climate.setVisible(true);
        human.setVisible(true);
        keyPane.setVisible(true);
        lChart.setVisible(true);
        lChart.getYAxis().setLabel("POPULATION");
        lChart.getXAxis().setLabel("MONTHS");
        //after adding the grdipane in scene
        // builder, modify the fxml manually to eliminate
        // rows and columns


//        gPane.setMinSize(0,0);
        //gPane.setPadding(new Insets(btn[i][j]));
        gPane.setHgap(0);
        gPane.setVgap(0);
        //gPane.setGridLinesVisible(true);
        //gPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn[0].length; j++) {

                //Initializing 2D buttons with values i,j
                btn[i][j] = new Button();
                btn[i][j].setStyle("-fx-background-color:#d3d3d3");

                btn[i][j].setMinWidth(20);
                btn[i][j].setMinHeight(20);

                btn[i][j].setMaxWidth(20);
                btn[i][j].setMaxHeight(20);


//                btn[i][j].setPrefSize(25, 5);
                //Paramters:  object, columns, rows
                gPane.add(btn[i][j], j, i);
                gameGrid[i][j] = 0;


            }
        }

        gPane.setGridLinesVisible(true);

        gPane.setVisible(true);

        for (int i = 0; i < environmentGrid.length; i++) {
            for (int j = 0; j < environmentGrid[0].length; j++) {
                environmentGrid[i][j]=2;
            }
        }

        for (int i = 0; i < 10; i++) {
            int x = (int)(Math.random()*(environmentGrid.length - 10)) + 3;
            int y = (int)(Math.random()*(environmentGrid[0].length - 10)) + 3;
            boolean tooClose = true;

            //making sure that the grass aren't too close to each other
            while(tooClose){
                tooClose = false;
                for (int r = x-10; r <= x+10 && r < gameGrid.length && r > 0; r++) {
                    for (int c = y - 10; c <= y + 10 && c < gameGrid[0].length && c > 0; c++) {
                        if(environmentGrid[r][c] == 3){
                            tooClose = true;
                        }
                    }
                }
                if(tooClose){
                    x = (int)(Math.random()*(environmentGrid.length - 10)) + 3;
                    y = (int)(Math.random()*(environmentGrid[0].length - 10)) + 3;
                }
            }

            //coloring the blcoks in
            for (int j = 0; j < (int)(Math.random()*4)+3; j++) {
                environmentGrid[x-1][y+j] = 3;
                environmentGrid[x][y+j] = 3;
                environmentGrid[x+1][y+j] = 3;
            }
        }

        setRiver(riverWidth);
        start();
        updateScreen();
    }

    public void setRiver(int width){
        water.clear();
        bush.clear();
        //setting certain spots on the grid to water
        int j = (int)((Math.random() * 5) + 23);
        for(int i = 0; i < environmentGrid.length; i++){
            environmentGrid[i][j]=1;
            for (int k = j; k < j+width; k++) {
                environmentGrid[i][k]=1;
            }
            int chance = (int)(Math.random() * 5);
            if(chance == 4){
                j--;
            } else if (chance == 3) {
                j++;
            }
        }
        int a = (int)((Math.random() * 5) + 16);
        for (int i = 0; i < environmentGrid[0].length; i++) {
            environmentGrid[a][i]=1;
            for (int k = a; k < a+width; k++) {
                environmentGrid[k][i]=1;
            }
            int chance = (int)(Math.random() * 5);
            if(chance == 4){
                a--;
            } else if (chance == 3) {
                a++;
            }
        }

        //adding which locations on the grid have water
        for (int r = 0; r < environmentGrid.length; r++) {
            for (int c = 0; c < environmentGrid[0].length; c++) {
                if(environmentGrid[r][c] == 1){
                    water.add(new Locations(r,c));
                }
            }
        }

        //adding the bush locations
        for (int i = 0; i < environmentGrid.length; i++) {
            for (int h = 0; h < environmentGrid[0].length; h++) {
                if(environmentGrid[i][h] == 3){
                    bush.add(new Locations(i, h));
                }
            }
        }
    }

    public void updateScreen() {
        //environment grid first, then overide with objects
        //1=river, 2 = grass
        for (int i = 0; i < btn.length; i++) {
            for (int j = 0; j < btn[0].length; j++) {
                if (environmentGrid[i][j] == 0) {
                    btn[i][j].setStyle("-fx-background-color:#d3d3d3");
                } else if (environmentGrid[i][j] == 1) {
                    btn[i][j].setStyle("-fx-background-color: #ADD8E6");
                } else if (environmentGrid[i][j] == 2) {
                    btn[i][j].setStyle("-fx-background-color:#86c892");
                } else if (environmentGrid[i][j] == 3) {
                    btn[i][j].setStyle("-fx-background-color:#5d8861");
                }
            }
        }

        // 0 = empty(already set in environment,
        // 1 = tiger child, 2 = tiger female 3 = tiger male
        // 4 = deer child, 5 = deer female, 6 = deer male
        for(int i=0; i<btn.length; i++) {
            for (int j = 0; j < btn[0].length; j++) {
                if (gameGrid[i][j]==1){
                    btn[i][j].setStyle("-fx-background-color:#fa8128");
                } else if (gameGrid[i][j]==2){
                    btn[i][j].setStyle("-fx-background-color:#dd571c");
                } else if (gameGrid[i][j]==3){
                    btn[i][j].setStyle("-fx-background-color:#8a3008");
                } else if (gameGrid[i][j]==4){
                    btn[i][j].setStyle("-fx-background-color:#d4b37f");
                } else if (gameGrid[i][j]==5){
                    btn[i][j].setStyle("-fx-background-color:#bd9168");
                } else if (gameGrid[i][j]==6){
                    btn[i][j].setStyle("-fx-background-color:#a3866a");
                }
            }
        }
    }

    @FXML
    private void addTiger(ActionEvent event) {
        int x = (int)(Math.random()*(environmentGrid.length));
        int y = (int)(Math.random()*(environmentGrid[0].length));
        allTigers.add(new Tiger(x,y, 3));
        //setting the game grid to a certain number depending on the age and gender of the tiger
        if(allTigers.get(allTigers.size()-1).getAge() < 3){
            gameGrid[allTigers.get(allTigers.size()-1).getX()][allTigers.get(allTigers.size()-1).getY()] = 1;
        } else {

            if(allTigers.get(allTigers.size()-1).getGender() == 0){
                gameGrid[allTigers.get(allTigers.size()-1).getX()][allTigers.get(allTigers.size()-1).getY()] = 2;
            } else {
                gameGrid[allTigers.get(allTigers.size()-1).getX()][allTigers.get(allTigers.size()-1).getY()] = 3;
            }

        }
        updateScreen();
    }

    @FXML
    private void addFlock(ActionEvent event){
        allFlocks.add(new ArrayList<>());
        int x = (int)(Math.random()*(environmentGrid.length - 12)) + 6;
        int y = (int)(Math.random()*(environmentGrid[0].length - 12)) + 6;

        //creates the leader of the flock and then every other deer in the flock will be surrounded around the leader
        allFlocks.get(allFlocks.size()-1).add(new Deer(x, y, 2, allFlocks.size()-1));
        allFlocks.get(allFlocks.size()-1).get(0).setGender(1);

        for (int j = 0; j < (int)(Math.random()*3) + 2; j++) {
            int r = (int)(Math.random()*7) + (x-3);
            int c = (int)(Math.random()*7) + (y-3);
            allFlocks.get(allFlocks.size()-1).add(new Deer(r, c, 2, allFlocks.size()-1));
        }

        for(int i = 0; i < allFlocks.get(allFlocks.size()-1).size(); i++){
            gameGrid[allFlocks.get(allFlocks.size()-1).get(i).getX()][allFlocks.get(allFlocks.size()-1).get(i).getY()] = allFlocks.get(allFlocks.size()-1).get(i).getGender() + 5;
        }

        allDeer.addAll(allFlocks.get(allFlocks.size() - 1));
        System.out.println(allFlocks.get(allFlocks.size() - 1).size());
        updateScreen();
    }

    @FXML
    private void handleMating(){
        if(matingCheck.isSelected()){
            matingSeason = true;
        } else {
            matingSeason = false;
        }
    }

    public void start() {
        lChart.getData().add(deerPop);
        lChart.getData().add(tigerPop);
        new AnimationTimer() {
            @Override
            public void handle(long now) {
//                System.out.println(now);
                if(now - gameStart > 2000000000.0){
//                    lChart.getData().clear();
                    monthlyDeer.add(allDeer.size());
                    monthlyTiger.add(allTigers.size());
                    deerPop.setName("Deer Population");
                    tigerPop.setName("Tiger Population");
                    deerPop.getData().clear();
                    tigerPop.getData().clear();
                    for (int i = 0; i < monthlyTiger.size(); i++) {
                        deerPop.getData().add(new XYChart.Data(i, monthlyDeer.get(i)));
                        tigerPop.getData().add(new XYChart.Data(i, monthlyTiger.get(i)));
                    }
                    gameStart = System.nanoTime();
                    humanInteraction();
                    climateChange();
                }
                if (allTigers.size() > 0) {
                    for (int i = 0; i < allTigers.size(); i++) {
                        if(allTigers.get(i).isHunting()){
                            //checking if tiger is next to prey
                            allTigers.get(i).foundPrey(allDeer, gameGrid, allFlocks, allTigers, allTigers.get(i).getPrey());
                            //checking if tiger is next to any other deer
                            for (int j = 0; j < allDeer.size(); j++) {
                                allTigers.get(i).foundPrey(allDeer, gameGrid, allFlocks, allTigers, allDeer.get(j));
                            }
                        }

                        if(Math.random() <= 0.2){
                            windy();
                        }

                        if (now - allTigers.get(i).getStartTime() > 1000000000.0) {

                            if(allTigers.get(i).checkingOpposite(allTigers) && allTigers.get(i).getGender()==0 && (!allTigers.get(i).inWater(environmentGrid))){
                                System.out.println("female tiger found a male tiger");
                                for (int j = 0; j < (int)((Math.random()*3) + 1); j++) {
                                    System.out.println("female reproduced");
                                    reproduceTiger(i);
                                }
                            }

                            if(allTigers.get(i).isHunting()){
                                allTigers.get(i).chasingPrey(gameGrid, allDeer);
                            } else if(matingSeason && allTigers.get(i).getThirst() <= 15 && allTigers.get(i).getAge() < 3 && !allTigers.get(i).isHasBaby()) {
                                //running through all tigers array and checking which one is close, diff gender and doesn't already have a target mate
                                for (int j = 0; j < allTigers.size() && allTigers.get(i).getTargetMate() == null; j++) {
                                    if(allTigers.get(i).checkDistance(allTigers.get(j), 10) && (allTigers.get(j).getGender() != allTigers.get(i).getGender()) && allTigers.get(j).getTargetMate() == null && allTigers.get(j).getAge() < 3 && !allTigers.get(j).isHasBaby()){
                                        allTigers.get(i).setTargetMate(allTigers.get(j));
                                        allTigers.get(j).setTargetMate(allTigers.get(i));
                                    }
                                }

                                if(allTigers.get(i).getTargetMate() != null){
                                    allTigers.get(i).chasingMate(gameGrid, allTigers);
                                }

                            } else {
                                allTigers.get(i).changeLoc(gameGrid, water);
                            }

                            //if the tiger consumes food or water the stats are reset to 0.
                            if(allTigers.get(i).getHunger() >= 15){
                                allTigers.get(i).findPrey(allDeer, wind);
                                System.out.println("tiger found prey: " + allTigers.get(i).isHunting());
                            }

                            if(allTigers.get(i).inWater(environmentGrid)){
                                if(salinityLvl >= 10){
                                    allTigers.get(i).setThirst(salinityLvl);
                                }else{
                                    allTigers.get(i).setThirst(0);
                                }
                                System.out.println("tiger found water");
                            }

                            allTigers.get(i).increaseMonths();
                            allTigers.get(i).setHunger(allTigers.get(i).getHunger()+1);
                            allTigers.get(i).resetStartTime();

                            //there is a randomized probability of the thirst increasing in order to contrast the need of water for deers and tigers
                            if(Math.random()>.3 && allTigers.get(i).getAge() >= 3){
                                allTigers.get(i).setThirst(allTigers.get(i).getThirst()+1);
                            }

                            //if the target mates are over 20 blocks away from each other than they will not be target mates no more
                            if(allTigers.get(i).getTargetMate() != null){
                                if(!allTigers.get(i).checkDistance(allTigers.get(i).getTargetMate(), 20)){
                                    allTigers.get(i).getTargetMate().setTargetMate(null);
                                    allTigers.get(i).setTargetMate(null);
                                }
                            }

                            //if the baby is too far awar from the mom it will die because it has no support
                            if(allTigers.get(i).getAge() < 3 && !allTigers.get(i).checkDistance(allTigers.get(i).getMother(), 4)){
                                gameGrid[allTigers.get(i).getX()][allTigers.get(i).getY()] = 0;
                                allTigers.remove(i);
                                System.out.println("the baby tiger lost the mother");
                            } else if(allTigers.get(i).willDie()){
                                //based on the health and age the probabilities of death are increased
                                gameGrid[allTigers.get(i).getX()][allTigers.get(i).getY()] = 0;
                                if(allTigers.get(i).getTargetMate() != null){
                                    allTigers.get(i).getTargetMate().setTargetMate(null);
                                }
                                allTigers.remove(i);
                                System.out.println("tiger died bro");
                            }
                        }
                    }
                }

                if(allDeer.size() > 0){
                    for(int i = 0; i < allDeer.size(); i++){
                        if (now - allDeer.get(i).getStartTime() > 1000000000.0) {
                            if(allDeer.get(i).getAge() <= 1){
                                allDeer.get(i).getMother().setHasBaby(true);
                            }
                            if(allDeer.get(i).checkingOpposite(allDeer) && allDeer.get(i).getGender()==0 && (!allDeer.get(i).inWater(environmentGrid))){
                                System.out.println("female deer found a male deer");
                                reproduceDeer(i);
                            }

                            allDeer.get(i).checkPredator(allTigers);

                            //if noticed predator uses special algorithim to run away else just uses randomized change location
                            if(allDeer.get(i).isNoticedPredator()){
                                allDeer.get(i).runningAway(gameGrid, allTigers);
                                System.out.println("deer noticed hungry tiger");
                            } else {
                                allDeer.get(i).changeLoc(gameGrid, allFlocks, water, bush);
                            }

                            //setting hunger and thirst to 0 if in respective places
                            if(allDeer.get(i).inWater(environmentGrid)){
                                if(salinityLvl >= 7){
                                    allDeer.get(i).setThirst(salinityLvl);
                                } else {
                                    allDeer.get(i).setThirst(0);
                                }
                            }
                            if(environmentGrid[allDeer.get(i).getX()][allDeer.get(i).getY()]==3){
                                allDeer.get(i).setHunger(0);
                            }

                            allDeer.get(i).checkFlock(allFlocks, allDeer);
                            allDeer.get(i).increaseMonths();
                            allDeer.get(i).resetStartTime();
                            allDeer.get(i).setHunger(allDeer.get(i).getHunger()+1);

                            if(Math.random()>.5){
                                allDeer.get(i).setThirst(allDeer.get(i).getThirst()+1);
                            }

                            if(allDeer.get(i).getAge() < 1 && !allDeer.get(i).isHasFlock()){
                                gameGrid[allDeer.get(i).getX()][allDeer.get(i).getY()] = 0;
                                allDeer.remove(i);
                                System.out.println("baby deer died bro");
                            } else if(allDeer.get(i).willDie()){
                                gameGrid[allDeer.get(i).getX()][allDeer.get(i).getY()] = 0;
                                if(allDeer.get(i).isHasFlock()) {
                                    allFlocks.get(allDeer.get(i).getMyFlock()).remove(allDeer.get(i));
                                    if (allFlocks.get(allDeer.get(i).getMyFlock()).size() == 0) {
                                        allFlocks.remove(allDeer.get(i).getMyFlock());
                                        System.out.println("removed flock: " + i);
                                        for (int j = 0; j < allDeer.size(); j++) {
                                            if (allDeer.get(j).isHasFlock() && allDeer.get(j).getMyFlock() > allDeer.get(i).getMyFlock()) {
                                                allDeer.get(j).setMyFlock(allDeer.get(j).getMyFlock() - 1);
                                            }
                                        }
                                    }
                                    allDeer.remove(i);
                                    System.out.println("deer died bro");
                                }
                            }
                        }
                    }
                }
                updateScreen();
            }
        }.start();
    }

    ArrayList<Locations> tempLocs = new ArrayList<>();
    public void reproduceTiger(int which){
        tempLocs.clear();
        double prob;
        if(matingSeason){
            prob = 1;
        } else {
            prob = 0.7;
        }
        //this code is running through every location around the ant.
        for (int r = allTigers.get(which).getX()-1; r <= allTigers.get(which).getX()+1 && r < gameGrid.length && r > 0; r++) {
            for (int c = allTigers.get(which).getY()-1; c <= allTigers.get(which).getY()+1 && c < gameGrid[0].length && c > 0; c++) {
                if(checkEmptyAround(r, c) && !(environmentGrid[r][c] == 1)){
                    tempLocs.add(new Locations(r, c));
                }
            }
        }
        if(tempLocs.size()>0 && allTigers.get(which).getAge() >= 3 && Math.random() <= prob){
            int num = (int)(Math.random() * tempLocs.size());
            allTigers.add(new Tiger(tempLocs.get(num).getX(), tempLocs.get(num).getY(), 0));
            gameGrid[tempLocs.get(num).getX()][tempLocs.get(num).getY()] = 1;
            allTigers.get(allTigers.size()-1).setMother(allTigers.get(which));
            System.out.println("new baby tiger");
        } else {
            System.out.println("no space for baby tiger :(");

        }
    }

    ArrayList<Locations> tempLocs2 = new ArrayList<>();
    public void reproduceDeer(int which) {
        tempLocs2.clear();
        double prob;
        if(matingSeason){
            prob = 1;
        } else {
            prob = 0.7;
        }
        //this code is running through every location around the ant.
        for (int r = allDeer.get(which).getX()-1; r <= allDeer.get(which).getX()+1 && r < gameGrid.length && r > 0; r++) {
            for (int c = allDeer.get(which).getY()-1; c <= allDeer.get(which).getY()+1 && c < gameGrid[0].length && c > 0; c++) {
                if(checkEmptyAround(r, c) && !(environmentGrid[r][c] == 1)){
                    tempLocs.add(new Locations(r, c));
                }
            }
        }
        if(tempLocs.size()>0 && allDeer.get(which).getAge() >= 1 && Math.random() <= prob){
            int num = (int)(Math.random() * tempLocs.size());
            allDeer.add(new Deer(tempLocs.get(num).getX(), tempLocs.get(num).getY(), 0, allDeer.get(which).getMyFlock()));
            gameGrid[tempLocs.get(num).getX()][tempLocs.get(num).getY()] = 4;
            allDeer.get(allDeer.size()-1).setMother(allDeer.get(which));
            System.out.println("new baby deer");
        } else {
            System.out.println("no space for baby deer :(");

        }
    }

    int count = 0;
    public void humanInteraction(){
        int rate = (int)(humanBar.getValue()/10);
        count++;
        if(count>=7 && rate > 0 && bush.size()>0){
            for (int i = 0; i < rate; i++) {
                int random = (int)(Math.random()* bush.size());
                environmentGrid[bush.get(random).getX()][bush.get(random).getY()] = 2;
                bush.remove(random);
            }
            count = 0;
            updateScreen();
        }

    }

    int count2 = 0;
    public void climateChange(){
        int rate = (int)(climateBar.getValue()/10);
        count2++;
        System.out.println(count2);
        if(count2 >= 13-rate && rate > 0){
            salinityLvl += 1;
            System.out.println("salinity: " + salinityLvl);
            riverWidth ++;
            setRiver(riverWidth);
            count2 = 0;
            updateScreen();
        }
    }

    public void windy(){
        int chance = (int)(Math.random() * 4);
        if(chance == 2){
            wind[0] = (int)(Math.random() * 3) + 1;
        } else if (chance == 3){
            wind[0] = -1 * (int)(Math.random() * 3) + 1;
        }
        chance = (int)(Math.random() * 4);
        if(chance == 2){
            wind[1] = (int)(Math.random() * 3) + 1;
        } else if (chance == 3){
            wind[1] = -1 * (int)(Math.random() * 3) + 1;
        }
    }

    public boolean checkEmptyAround(int i, int j){
        return gameGrid[i][j] == 0;
    }
}

