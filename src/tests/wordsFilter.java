package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class wordsFilter {
    public static void main(String... args) throws IOException {
	StringBuilder sb = new StringBuilder();
	Files.readAllLines(Paths.get("<path_to_file>")).stream().forEach(e -> {
	    sb.append(e + " ");
	});
	sb.append("wen enw ");
	new wordsFilter().checkWords(sb.toString());
    }

    void checkWords(String args) {

	Map<String, Set<String>> resultMap = new HashMap<>();

	int[] temp = new int[256];

	StringBuilder sb = new StringBuilder();

	Arrays.stream(args.split(" ")).forEach(e -> {

	    Arrays.stream(e.split("")).forEach(q -> {
		temp[q.hashCode()]++;
	    });

	    for (int w = 0; w < temp.length; w++) {
		if (temp[w] != 0) {
		    sb.append(temp[w]);
		    sb.append((char) w);
		}
	    }

	    Set<String> tempList;
	    String tempString = sb.toString();

	    if (resultMap.containsKey(tempString)) {
		tempList = resultMap.get(tempString);
		tempList.add(e);
	    } else {
		tempList = new HashSet<String>();
		tempList.add(e);
	    }

	    resultMap.put(tempString, tempList);

	    sb.setLength(0);
	    Arrays.fill(temp, 0);
	});

	resultMap.values().stream().filter(e -> e.size() > 1).forEach(System.out::println);
	System.out.println();
	// resultMap.keySet().stream().forEach(System.out::println);
    }
}
