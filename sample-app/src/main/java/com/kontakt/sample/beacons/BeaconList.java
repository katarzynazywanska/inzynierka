package com.kontakt.sample.beacons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class BeaconList  {

    //public Map<Integer, Map<Integer, String>> beaconNeighboursMap = new HashMap<Integer, Map<Integer, String>>();
    //public Map<Integer, Map<String, String>> beaconRooms = new HashMap<Integer, Map<String, String>>();
    public Map<Integer, Double> beaconPreviousDistance = new HashMap<Integer, Double>();
    public Map<Integer, Double> beaconCurrentDistance = new HashMap<Integer, Double>();
    //public Map<Integer, Map<Integer, String> > beaconDirections = new HashMap<Integer, Map<Integer, String> >();
    public HashMap<String, Map<String, String>> beaconNeighboursMapDataBase;
    public HashMap<String, Map<String,String>> beaconRoomsDataBase;


    //klucz baza danych
    public BeaconList(){
        readFromDataBase("beaconNeighboursMapDataBase1", "beaconRoomsDataBase1");

        /*// neighbours map with the relation of directions
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
        beaconRooms.put(11690, new HashMap<String,String>(){{put("Pokój 5", "E");put("Pokój 10", "W");}});*/
    }

    public void readFromDataBase(String neighboursKey, String roomsKey){
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nawigacja-w-budynku-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        myRef.child(neighboursKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "error getting data");
                } else {
                    beaconNeighboursMapDataBase = (HashMap<String, Map<String, String>>) task.getResult().getValue();
                    Log.d("firebase", "Done " );
                }
            }
        });

        myRef.child(roomsKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "error getting data");
                } else {
                    beaconRoomsDataBase = (HashMap<String, Map<String,String>>) task.getResult().getValue();
                    Log.d("firebase", "Done " );
                }
            }
        });

        /*//Write to database
        DatabaseReference myRef2 = database.getReference("message");
        myRef2.setValue("Hello, World!");*/
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

    //get minor value (key value of map) of beacon, knowing what room name is related to it
    public String getKey(String roomName){
        for (Map.Entry<String, Map<String,String>> keyAndValue: beaconRoomsDataBase.entrySet()) {

            for (Map.Entry<String,String> roomAndDir: keyAndValue.getValue().entrySet()) {
                if (roomName.equals(roomAndDir.getKey())) {
                    return keyAndValue.getKey();
                }
            }
        }
        return "0";
    }

    //find path from one vertex to another in the graph(V,E)
    public LinkedList findPath(String source, String destination) {
        LinkedList<String> path = new LinkedList<String>();
        if(source.equals(destination)){
            path.add(source);
            return path;
        }
        Map<String, String> predecessor = new HashMap<String, String>();
        Map<String, Integer> distances = new HashMap<String, Integer>();

        if (breadthFirstSearch(predecessor, distances, source, destination) == false){
            return new LinkedList();
        }
        //store my path

        String current = destination;
        path.add(current);
        while ( !predecessor.get(current).equals("finish") ){
            path.add(predecessor.get(current));
            current = predecessor.get(current);
        }
        Log.i("getKeyFunClass1", ""+path);
        return path;
    }

    public Boolean breadthFirstSearch (Map<String, String> predecessor, Map<String, Integer> distances, String source, String destination ){
        Map<String, Boolean> visited = new HashMap<String, Boolean>();
        LinkedList<String> queue = new LinkedList<String>();

        //for (Map.Entry<Integer, String> keyAndValue: beaconRooms.entrySet()) {
        //for (Map.Entry<Integer, Pair<String, String>> keyAndValue: beaconRooms.entrySet()) {
        for (Map.Entry<String, Map<String, String>> keyAndValue: beaconRoomsDataBase.entrySet()) {
            visited.put(keyAndValue.getKey(),false);
            predecessor.put(keyAndValue.getKey(),"finish");
            distances.put(keyAndValue.getKey(),Integer.MAX_VALUE);
        }

        visited.put(source, true);
        distances.put(source, 0);
        queue.add(source);

        List<String> keyList = new ArrayList<String>(beaconNeighboursMapDataBase.keySet());

        while (!queue.isEmpty()){
            String currentVertex = queue.remove();
            for (String key : Objects.requireNonNull(beaconNeighboursMapDataBase.get(currentVertex).keySet())) {
                String currentVertexNeighbours = key;

                if(visited.get(currentVertexNeighbours) == null){
                    return  false;
                }
                else if(!visited.get(currentVertexNeighbours)){
                    visited.put(currentVertexNeighbours, true);
                    distances.put(currentVertexNeighbours, distances.get(currentVertex) + 1);
                    predecessor.put(currentVertexNeighbours, currentVertex);
                    queue.add(currentVertexNeighbours);

                    //when we find path
                    if(currentVertexNeighbours.equals(destination)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

