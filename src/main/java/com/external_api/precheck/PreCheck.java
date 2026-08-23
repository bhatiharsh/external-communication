package com.external_api.precheck;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PreCheck {

    private final Set<Integer> integerSet = new HashSet<>();


    public boolean addId(int id) {
        return integerSet.add(id);
    }

    public boolean isPresent(int id) {
        return integerSet.contains(id);

    }

    public int getSize() {
        return integerSet.size();
    }

    public void remove(int id) {
        integerSet.remove(id);
    }
}
