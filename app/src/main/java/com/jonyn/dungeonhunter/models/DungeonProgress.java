package com.jonyn.dungeonhunter.models;

import com.jonyn.dungeonhunter.DbUtils;

import java.util.List;

public class DungeonProgress {

    public enum StageType {
        BATTLE,
        REST_PLACE
    }

    private int floor;
    private int stagePos;
    private int run;
    private List<StageType> floorProgress;

    public DungeonProgress() {

    }

    public DungeonProgress(int floor, int stagePos, int run, List<StageType> floorProgress) {
        this.floor = floor;
        this.stagePos = stagePos;
        this.run = run;
        this.floorProgress = floorProgress;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getStagePos() {
        return stagePos;
    }

    public void setStagePos(int stagePos) {
        this.stagePos = stagePos;
    }

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public List<StageType> getFloorProgress() {
        if (floorProgress.isEmpty()){
            updateFloorProgress();
        }

        return floorProgress;
    }

    public void setFloorProgress(List<StageType> floorProgress) {
        this.floorProgress = floorProgress;
    }

    /*---*/

    public void runUp(){
        this.run++;
        this.floor = 1;
        this.stagePos = 1;
    }

    public void floorUp(){
        this.floor++;
    }

    public void stagePosUp(){
        if (stagePos < 5)
            this.stagePos++;
        else {
            stagePos = 1;
            floorUp();
        }
    }

    public void updateFloorProgress(){
        floorProgress.clear();
        do {
            if (!floorProgress.contains(StageType.REST_PLACE)){
                switch (DbUtils.randomNumber(1, 5)){
                    case 1: case 2: case 3: case 4:
                        floorProgress.add(StageType.BATTLE);
                        break;
                    case 5:
                        floorProgress.add(StageType.REST_PLACE);
                        break;
                }
            } else floorProgress.add(StageType.BATTLE);
        } while (floorProgress.size()<5);
    }
}
