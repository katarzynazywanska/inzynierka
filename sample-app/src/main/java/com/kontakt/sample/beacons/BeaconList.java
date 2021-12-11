package com.kontakt.sample.beacons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
}

