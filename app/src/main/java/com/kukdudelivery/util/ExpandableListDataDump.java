package com.kukdudelivery.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataDump {
    public static HashMap<String, List<String>> getData()
    {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("India");
        cricket.add("Pakistan");
        cricket.add("Australia");
        cricket.add("England");
        cricket.add("South Africa");

        List<String> football = new ArrayList<String>();
        football.add("Publisher 1");
        football.add("Publisher 2");
        football.add("Publisher 3");
        football.add("Publisher 4");

        List<String> basketball = new ArrayList<String>();
        basketball.add("Publisher 1");
        basketball.add("Publisher 2");
        basketball.add("Publisher 3");
        basketball.add("Publisher 4");

        expandableListDetail.put("Afghanistan Publisher", cricket);
        expandableListDetail.put("Iran Publisher", football);
        return expandableListDetail;
    }
}
