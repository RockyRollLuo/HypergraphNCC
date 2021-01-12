package algorithm;

import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DecompositionNeighbor {
    private static final Logger LOGGER = Logger.getLogger(DecompositionNeighbor.class);

    private final Hypergraph hypergraph;
    private HashMap<Integer, Integer> coreVMap;

    public DecompositionNeighbor(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }


    /**
     * travel graph by nodeToEdgesMap
     * this method is very slow, due to the comparing edges
     *
     * @return
     */
    public Result run() {
        long startTime = System.nanoTime();

        //tempdata of hypergraph
        ArrayList<Integer> tempNodeList = new ArrayList<>(hypergraph.getNodeList());
        HashMap<Integer, ArrayList<Integer>> tempEdgeMap = new HashMap<>(hypergraph.getEdgeMap());
        HashMap<Integer, ArrayList<Integer>> tempNodeToEdgesMap = new HashMap<>(hypergraph.getNodeToEdgesMap());

        //temp data
        HashMap<Integer, Integer> tempCoreVMap = new HashMap<>();

        //compute neighborNumMap
        HashMap<Integer, HashMap<Integer, Integer>> neighborTimesMap = hypergraph.getNeighborTimesMap(); //the number of occurrences of each node's neighbor

        //compute core number of node
        for (int k = 0; ; k++) {
            if (tempNodeList.size() == 0) {
                break;
            }

            //delete nodes
            HashSet<Integer> deleteNodes = new HashSet<>();
            for (Integer v : tempNodeList) {
                if (neighborTimesMap.get(v).size() <= k) {
                    deleteNodes.add(v);
                }
            }

            //new delete nodes
            while (!deleteNodes.isEmpty()) {
                HashSet<Integer> newDeleteNodes = new HashSet<>();

                for (Integer v : deleteNodes) {
                    if (!tempNodeList.contains(v)) continue;

                    for (Integer eId : tempNodeToEdgesMap.get(v)) {
                        if (!tempEdgeMap.containsKey(eId)) continue;

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
                                if (uNeiTimes == 0) {
                                    uNeighborTimesMap.remove(uNei);
                                }
                            }

                            if (uNeighborTimesMap.size() <= k && tempNodeList.contains(u)) {
                                newDeleteNodes.add(u); //update newly deleted node
                            }
                        }
                        tempEdgeMap.remove(eId);
                    }
                    tempCoreVMap.put(v, k); //core number of node

                    //update
                    tempNodeToEdgesMap.remove(v); //edges contain v need to update(one edge contain many nodes), very troublesome,
                    tempNodeList.remove(v);
                }
                deleteNodes = newDeleteNodes;
            }
        }

        this.coreVMap = tempCoreVMap;
        long endTime = System.nanoTime();
        double takenTime = (endTime - startTime) / 1.0E9D;
        LOGGER.info(takenTime);

        return new Result(coreVMap, takenTime, "DecompositionNeighbor");
    }

    /**
     * getter and setter
     */

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public HashMap<Integer, Integer> getCoreVMap() {
        return coreVMap;
    }

    public void setCoreVMap(HashMap<Integer, Integer> coreVMap) {
        this.coreVMap = coreVMap;
    }

}
