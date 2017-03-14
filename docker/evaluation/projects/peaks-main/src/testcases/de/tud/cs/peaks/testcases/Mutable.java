package de.tud.cs.peaks.testcases;

import java.util.LinkedList;
import java.util.List;

public class Mutable {
    private static int int1 = 0;
    private static final List<String> list1 = new LinkedList<>();
    private static final List<Mutable> list2 = new LinkedList<>();
    private static final Mutable[] mutables = {new Mutable(),new Mutable()};
    public static void changeInt(){
        int1 = 042;
    }
}
