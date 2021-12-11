package com.kontakt.sample.beacons;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class BeaconList {
    public Map<Integer, ArrayList<Integer>> beaconMap = new HashMap<Integer, ArrayList<Integer>>();
    public Map<Integer, String> beaconRooms = new HashMap<Integer, String>();

    public BeaconList(){
        beaconMap.put(20258, new ArrayList<>(Arrays.asList(6823)));
        beaconMap.put(6823, new ArrayList<>(Arrays.asList(20258, 15462)));
        beaconMap.put(15462, new ArrayList<>(Arrays.asList(6823, 11690, 12880)));
        beaconMap.put(11690, new ArrayList<>(Arrays.asList(15462)));
        beaconMap.put(12880, new ArrayList<>(Arrays.asList(15462)));

        beaconRooms.put(20258, "Pokój");
        beaconRooms.put(6823, "Przejście");
        beaconRooms.put(15462, "Łazienka");
        beaconRooms.put(11690, "Wyjście");
        beaconRooms.put(12880, "Kuchnia");
    }

    public Integer getKey(String roomName){
        for (Map.Entry<Integer, String> pair: beaconRooms.entrySet()) {
            if (roomName.equals(pair.getValue())){
                return pair.getKey();
            }
        }
        return 0;
    }


    public LinkedList findPath(int source, int destination) {
        Map<Integer, Integer> predecessor = new HashMap<Integer, Integer>();
        Map<Integer, Integer> distances = new HashMap<Integer, Integer>();

        if (breadthFirstSearch(predecessor, distances, source, destination) == false){
            return new LinkedList();
        }

        //store my path
        LinkedList<Integer> path = new LinkedList<Integer>();
        int current = destination;
        path.add(current);
        while (predecessor.get(current) != -1 ){
            path.add(predecessor.get(current));
            current = predecessor.get(current);
        }


        return path;
    }

    public Boolean breadthFirstSearch (Map<Integer, Integer> predecessor, Map<Integer, Integer> distances, int source, int destination ){
        Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        LinkedList<Integer> queue = new LinkedList<Integer>();

        for (Map.Entry<Integer, String> pair: beaconRooms.entrySet()) {
            visited.put(pair.getKey(),false);
            predecessor.put(pair.getKey(),-1);
            distances.put(pair.getKey(),Integer.MAX_VALUE);
        }

        visited.put(source, true);
        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()){
            int currentVertex = queue.remove();
            for (int i = 0; i < Objects.requireNonNull(beaconMap.get(currentVertex)).size(); i++) {
                int currentVertexNeighbours = beaconMap.get(currentVertex).get(i);
                if(!visited.get(currentVertexNeighbours)){
                    visited.put(currentVertexNeighbours, true);
                    distances.put(currentVertexNeighbours, distances.get(currentVertex) + 1);
                    predecessor.put(currentVertexNeighbours, currentVertex);
                    queue.add(currentVertexNeighbours);

                    //when we find path
                    if(currentVertexNeighbours == destination){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

