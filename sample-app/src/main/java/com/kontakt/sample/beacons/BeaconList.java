package com.kontakt.sample.beacons;

import android.util.Log;
import android.util.Pair;

import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import kotlin.collections.IntIterator;

public class BeaconList {
    public Map<Integer, Map<Integer, String>> beaconNeighboursMap = new HashMap<Integer, Map<Integer, String>>();
    public Map<Integer, Map<String, String>> beaconRooms = new HashMap<Integer, Map<String, String>>();
    public Map<Integer, Double> beaconPreviousDistance = new HashMap<Integer, Double>();
    public Map<Integer, Double> beaconCurrentDistance = new HashMap<Integer, Double>();
    public Map<Integer, Map<Integer, String> > beaconDirections = new HashMap<Integer, Map<Integer, String> >();


    public BeaconList(){
        // neighbours map with the relation of directions
        beaconNeighboursMap.put(20258, new HashMap<Integer,String>(){{put(6823,"S");}});
        beaconNeighboursMap.put(6823, new HashMap<Integer,String>(){{put(20258,"N");put(15462,"S");}});
        beaconNeighboursMap.put(15462, new HashMap<Integer,String>(){{put(6823,"N"); put(12880,"S");}});
        beaconNeighboursMap.put(12880, new HashMap<Integer,String>(){{put(15462,"N");put(11690,"S");}});
        beaconNeighboursMap.put(11690, new HashMap<Integer,String>(){{put(12880,"N");}});

        // which room is associated with which beacon and with what direction
        beaconRooms.put(20258, new HashMap<String,String>(){{put("Pokój 1", "E");put("Pokój 6", "W");}});
        beaconRooms.put(6823, new HashMap<String,String>(){{put("Pokój 2", "E");put("Pokój 7", "W");}});
        beaconRooms.put(15462, new HashMap<String,String>(){{put("Pokój 3", "E");put("Pokój 8", "W");}});
        beaconRooms.put(12880, new HashMap<String,String>(){{put("Pokój 4", "E");put("Pokój 9", "W");}});
        beaconRooms.put(11690, new HashMap<String,String>(){{put("Pokój 5", "E");put("Pokój 10", "W");}});

        //help check if user is far or close to beacon
        beaconPreviousDistance.put(20258, 0.0);
        beaconPreviousDistance.put(6823, 0.0);
        beaconPreviousDistance.put(15462,0.0);
        beaconPreviousDistance.put(11690, 0.0);
        beaconPreviousDistance.put(12880, 0.0);

        beaconCurrentDistance.put(20258, 0.0);
        beaconCurrentDistance.put(6823, 0.0);
        beaconCurrentDistance.put(15462,0.0);
        beaconCurrentDistance.put(11690, 0.0);
        beaconCurrentDistance.put(12880, 0.0);
    }

    public String giveDirections(String fromDirection, String toDirection){
        String direction = "";

        switch(fromDirection){
            case "N":
                switch (toDirection){
                    case "E":
                        direction = "prawo";
                        break;
                    case "S":
                        direction = "prosto";
                        break;
                    case "W":
                        direction = "lewo";
                        break;
                }
                break;
            case "E":
                switch (toDirection){
                    case "S":
                        direction = "prawo";
                        break;
                    case "W":
                        direction = "prosto";
                        break;
                    case "N":
                        direction = "lewo";
                        break;
                }
                break;
            case "S":
                switch (toDirection){
                    case "E":
                        direction = "lewo";
                        break;
                    case "W":
                        direction = "prawo";
                        break;
                    case "N":
                        direction = "prosto";
                        break;
                }
                break;
            case "W":
                switch (toDirection){
                    case "E":
                        direction = "prosto";
                        break;
                    case "S":
                        direction = "lewo";
                        break;
                    case "N":
                        direction = "prawo";
                        break;
                }
                break;
        }
        return direction;
    }

    // check if user is closer to specific beacon
    public Boolean whetherUserApproached(Integer key){
        double firstPosition = beaconPreviousDistance.get(key);
        double secondPosition = beaconCurrentDistance.get(key);
        return firstPosition > secondPosition;
    }

    //get minor value (key value of map) of beacon, knowing what room name is related to it
    public Integer getKey(String roomName){
        for (Map.Entry<Integer, Map<String,String>> keyAndValue: beaconRooms.entrySet()) {

            for (Map.Entry<String,String> roomAndDir: keyAndValue.getValue().entrySet()) {
                if (roomName.equals(roomAndDir.getKey())) {
                    return keyAndValue.getKey();
                }
            }
        }
        return 0;
    }

    //find path from one vertex to another in the graph(V,E)
    public LinkedList findPath(int source, int destination) {
        LinkedList<Integer> path = new LinkedList<Integer>();
        if(source==destination){
            path.add(source);
            return path;
        }
        Map<Integer, Integer> predecessor = new HashMap<Integer, Integer>();
        Map<Integer, Integer> distances = new HashMap<Integer, Integer>();

        if (breadthFirstSearch(predecessor, distances, source, destination) == false){
            return new LinkedList();
        }
        //store my path

        int current = destination;
        path.add(current);
        while (predecessor.get(current) != -1 ){
            path.add(predecessor.get(current));
            current = predecessor.get(current);
        }
        Log.i("getKeyFunClass1", ""+path);
        return path;
    }

    public Boolean breadthFirstSearch (Map<Integer, Integer> predecessor, Map<Integer, Integer> distances, int source, int destination ){
        Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        LinkedList<Integer> queue = new LinkedList<Integer>();

        //for (Map.Entry<Integer, String> keyAndValue: beaconRooms.entrySet()) {
        //for (Map.Entry<Integer, Pair<String, String>> keyAndValue: beaconRooms.entrySet()) {
        for (Map.Entry<Integer, Map<String, String>> keyAndValue: beaconRooms.entrySet()) {
            visited.put(keyAndValue.getKey(),false);
            predecessor.put(keyAndValue.getKey(),-1);
            distances.put(keyAndValue.getKey(),Integer.MAX_VALUE);
        }

        visited.put(source, true);
        distances.put(source, 0);
        queue.add(source);

        List<Integer> keyList = new ArrayList<Integer>(beaconNeighboursMap.keySet());

        while (!queue.isEmpty()){
            int currentVertex = queue.remove();
            for (int key : Objects.requireNonNull(beaconNeighboursMap.get(currentVertex).keySet())) {
                int currentVertexNeighbours = key;
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

