package algorithm;

import javafx.util.Pair;
import model.Hypergraph;
import model.ResultPair;
import org.apache.log4j.Logger;
import util.ComputeUtils;
import util.GetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DecompositionConstraintCore {
    private static final Logger LOGGER = Logger.getLogger(DecompositionConstraintCore.class);

    private final Hypergraph hypergraph;
    private HashMap<Integer, Pair<Integer, Integer>> coreVMap;
    private HashMap<Integer, Pair<Integer, Integer>> coreEMap;

    public DecompositionConstraintCore(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    /**
     * travel graph by nodeToEdgesMap
     * this method is very slow, due to the comparing edges
     *
     * @return result
     */
    public ResultPair run() {
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<Integer> tempNodeList = new ArrayList<>(hypergraph.getNodeList());
        HashMap<Integer, ArrayList<Integer>> tempEdgeMap = new HashMap<>(hypergraph.getEdgeMap());
        HashMap<Integer, ArrayList<Integer>> tempNodeToEdgesMap = new HashMap<>(hypergraph.getNodeToEdgesMap());

        //temp data
        HashMap<Integer, Pair<Integer, Integer>> tempCoreVMap = new HashMap<>();

        //compute neighorsMap and degreeMap
        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        HashMap<Integer, HashMap<Integer, Integer>> neighborTimesMap = hypergraph.getNeighborTimesMap(); //the number of occurrences of each node's neighbor

        //compute core number of node
        for (int k = 1; ; k++) {
            if (tempNodeList.size() == 0) {
                break;
            }
            int c = k;

            //delete nodes
            ArrayList<Integer> deleteNodes = new ArrayList<>();
            for (Integer v : tempNodeList) {
                if (degreeMap.get(v) < k) {
                    deleteNodes.add(v);
                }
            }

            //new delete nodes
            while (!deleteNodes.isEmpty()) {
                ArrayList<Integer> newDeleteNodes = new ArrayList<>();

                for (Integer v : deleteNodes) {
                    if (!tempNodeList.contains(v)) continue;

                    for (Integer eId : tempNodeToEdgesMap.get(v)) {
                        if (!tempEdgeMap.containsKey(eId)) continue;

                        //update degree
                        for (Integer u : tempEdgeMap.get(eId)) {
                            if (!tempNodeList.contains(u)) continue;

                            //update degreeMap
                            int uDegree = degreeMap.get(u) - 1;
                            degreeMap.put(u, uDegree);
                            if (uDegree < k) {
                                newDeleteNodes.add(u);
                            }
                        }


                        //update neighbor
                        ArrayList<Integer> nodesInE = new ArrayList<>(tempEdgeMap.get(eId));
                        nodesInE.retainAll(tempNodeList);
                        nodesInE.remove(v);

                        for (Integer u : nodesInE) {
                            HashMap<Integer, Integer> uNeighborTimesMap = neighborTimesMap.get(u);
                            uNeighborTimesMap.remove(v);

                            for (Integer uNei : nodesInE) {
                                if (uNei.equals(u)) continue;
                                if (!uNeighborTimesMap.containsKey(uNei)) continue;

                                int uNeiTimes = uNeighborTimesMap.get(uNei) - 1;
                                uNeighborTimesMap.put(uNei, uNeiTimes);
                                if (uNeiTimes == 0) {
                                    uNeighborTimesMap.remove(uNei);
                                }
                            }

                            neighborTimesMap.put(u, uNeighborTimesMap);
                        }


                        tempEdgeMap.remove(eId);
                    }

                    //update
                    tempNodeToEdgesMap.remove(v); //edges contain v need to update(one edge contain many nodes), very troublesome,
                    tempNodeList.remove(v);

                    neighborTimesMap.remove(v);

                    c = GetUtils.getMinSizeOfMap(neighborTimesMap) ;

                    tempCoreVMap.put(v, new Pair<>(k, c)); //core number of node
                }

                deleteNodes = newDeleteNodes;
            }
        }

        //compute core number of edge

        this.coreVMap = tempCoreVMap;
        this.coreEMap = ComputeUtils.computeCoreEMapByCoreVMapPair(tempEdgeMap, tempCoreVMap);
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info("TakenTime:" + takenTime);

        return new ResultPair(coreVMap, coreEMap, takenTime, "DecompositionConstraint");
    }

    /**
     * naive (k,c)-core computation
     *
     * @param k degree constraint
     * @param c neighbor constraint
     * @return a sub-hypergraph
     */
    public Hypergraph run(int k, int c) {
        long startTime = System.nanoTime();

        //private properties of hypergraph
        ArrayList<Integer> tempNodeList = new ArrayList<>(hypergraph.getNodeList());
        HashMap<Integer, ArrayList<Integer>> tempEdgeMap = new HashMap<>(hypergraph.getEdgeMap());
        HashMap<Integer, ArrayList<Integer>> tempNodeToEdgesMap = new HashMap<>(hypergraph.getNodeToEdgesMap());

        //compute neighorsMap and degreeMap
        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        HashMap<Integer, HashMap<Integer, Integer>> neighborTimesMap = hypergraph.getNeighborTimesMap(); //the number of occurrences of each node's neighbor

        //delete nodes
        HashSet<Integer> deleteNodes = new HashSet<>();
        for (Integer v : tempNodeList) {
            if (degreeMap.get(v) < k || neighborTimesMap.get(v).size() < c) {
                deleteNodes.add(v);
            }
        }

        //new delete nodes
        while (!deleteNodes.isEmpty()) {
            HashSet<Integer> newDeleteNodes = new HashSet<>();

            for (Integer v : deleteNodes) {
                if (!tempNodeList.contains(v)) continue;
                if (!tempNodeToEdgesMap.containsKey(v)) continue;

                for (Integer eId : tempNodeToEdgesMap.get(v)) {
                    if (!tempEdgeMap.containsKey(eId)) continue;

                    //update neighbor
                    ArrayList<Integer> nodesInE = new ArrayList<>(tempEdgeMap.get(eId));
                    nodesInE.retainAll(tempNodeList);
                    nodesInE.remove(v);

                    for (Integer u : nodesInE) {
                        HashMap<Integer, Integer> uNeighborTimesMap = neighborTimesMap.get(u);
                        uNeighborTimesMap.remove(v);

                        for (Integer uNei : nodesInE) {
                            if (uNei.equals(u)) continue;
                            if (!uNeighborTimesMap.containsKey(uNei)) continue;

                            int uNeiTimes = uNeighborTimesMap.get(uNei) - 1;
                            uNeighborTimesMap.put(uNei, uNeiTimes);
                            if (uNeiTimes == 0) {
                                uNeighborTimesMap.remove(uNei);
                            }
                        }

                        if (uNeighborTimesMap.size() < c && tempNodeList.contains(u)) {
                            newDeleteNodes.add(u); //update newly deleted node
                        }
                    }

                    //update degree
                    for (Integer u : tempEdgeMap.get(eId)) {
                        if (!tempNodeList.contains(u)) continue;

                        //update degreeMap
                        int uDegree = degreeMap.get(u) - 1;
                        degreeMap.put(u, uDegree);
                        if (uDegree < k) {
                            newDeleteNodes.add(u);
                        }
                    }

                    //delete edge
                    tempEdgeMap.remove(eId);
                }

                //update
                tempNodeToEdgesMap.remove(v); //edges contain v need to update(one edge contain many nodes), very troublesome,
                tempNodeList.remove(v);
            }
            deleteNodes = newDeleteNodes;
        }

        //compute core number of edge
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info("TakenTime:" + takenTime);

        return new Hypergraph(tempNodeList, tempEdgeMap, false);
    }

    /**
     * getter and setter
     */

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

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
}
