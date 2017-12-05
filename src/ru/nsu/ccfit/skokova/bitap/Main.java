package ru.nsu.ccfit.skokova.bitap;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        BitapSearcher bitapSearcher = new BitapSearcher();
        List<Long> integerList = bitapSearcher.search("./temp", "friend", 2);
        System.out.println(integerList);
    }


}
