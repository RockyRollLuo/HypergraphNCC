package model;

import javafx.util.Pair;

import java.util.HashMap;

public class ResultPair {
    private HashMap<Integer, Pair<Integer,Integer>> coreVMap;
    private HashMap<Integer, Pair<Integer,Integer>> coreEMap;
    private Double takenTime;
    private String algorithmName;
    private String datasetName;
    private String type;

    /**
     * constructor
     */
    public ResultPair(HashMap<Integer, Pair<Integer, Integer>> coreVMap, HashMap<Integer, Pair<Integer, Integer>> coreEMap, Double takenTime, String algorithmName) {
        this.coreVMap = coreVMap;
        this.coreEMap = coreEMap;
        this.takenTime = takenTime;
        this.algorithmName = algorithmName;
    }

    /**
     * Getter() and Setter()
     */
    public HashMap<Integer, Pair<Integer, Integer>> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Pair<Integer, Integer>> coreVMap) {
        this.coreVMap = coreVMap;
    }

    public HashMap<Integer, Pair<Integer, Integer>> getCoreEMap() {
        return coreEMap;
    }

    public void setCoreEMap(HashMap<Integer, Pair<Integer, Integer>> coreEMap) {
        this.coreEMap = coreEMap;
    }

    public Double getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(Double takenTime) {
        this.takenTime = takenTime;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
