package jepamt;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.*;

public class Second {

    public static void main(String... args) {
	Second s = new Second();
	Method[] m = s.getClass().getDeclaredMethods();
	List<Method> arr = new ArrayList<Method>();
	for (Method mm : m) {
	    if (mm.toString().contains("public") && !mm.toString().contains("main")) {
		arr.add(mm);
	    }
	}
	Comparator<Method> comp = new Comparator<Method>() {
	    @Override
	    public int compare(Method o1, Method o2) {
		return o1.toString().compareTo(o2.toString());
	    }
	};
	Collections.sort(arr, comp);
	for (Method mm : arr) {
	    try {
		mm.invoke(s);
	    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		e.printStackTrace();
	    }
	}
    }

    public void om01() {
	int k = new Random().nextInt(101);
	int[] temp = ThreadLocalRandom.current().ints(1, 1001).limit(400).toArray();
	int result = 0;
	for (Integer i : temp) {
	    if (i % k == 0) {
		result += i;
	    }
	}
	System.out.println("om1:\t" + result);
    }

    public void om02() {
	int k = new Random().nextInt(101);
	int[] temp = ThreadLocalRandom.current().ints(1, 1001).limit(400).toArray();
	int result = 0;
	for (int i = 0; i < temp.length; i++) {
	    if (temp[i] > k) {
		temp[i] = k;
		result++;
	    }
	}
	System.out.println("om2:\t" + result);
    }

    public void om03() {
	int[] temp = ThreadLocalRandom.current().ints(-1001, 1001).limit(400).toArray();
	int min = 0, max = 0, zero = 0;
	for (Integer i : temp) {
	    if (i < 0)
		min++;
	    if (i > 0)
		max++;
	    if (i == 0)
		zero++;
	}
	System.out.println(String.format("om3:\tmin: %d, max: %d, zero: %d", min, max, zero));
    }

    public void om04() {
	int[] temp = ThreadLocalRandom.current().ints(-1001, 1001).limit(400).toArray();
	Arrays.sort(temp);
	int i = temp[0];
	int j = temp[temp.length - 1];
	temp[0] = j;
	temp[temp.length - 1] = i;
	System.out.println(
		"om4:\tmin: " + i + ", max: " + j + ", temp[0]: " + temp[0] + ", temp[max]: " + temp[temp.length - 1]);
    }

    public void om05() {
	// ?? what need to do here?
	int[] temp = ThreadLocalRandom.current().ints(-1001, 1001).limit(400).toArray();
	int count = 0;
	System.out.print("om5:\t");
	for (Integer i : temp) {
	    if (i > count) {
		System.out.print(i + " ");
	    }
	    count++;
	}
	System.out.print(" count: " + count + "\n");
    }

    public void om06() {
	int[] temp = ThreadLocalRandom.current().ints(1, 1001).limit(400).toArray();
	List<Integer> arr = new ArrayList<Integer>();
	for (int i = 0; i < temp.length - 1; i++) {
	    int count = 0;
	    int r = temp[i];
	    for (int j = r; j > -1; j--) {
		if (j != 0 && r % j == 0) {
		    count++;
		}
	    }
	    if (count == 2) {
		arr.add(r);
	    }
	    count = 0;
	}
	int result = arr.stream().mapToInt(e -> e).sum();
	System.out.println("om6:\t" + result);
    }

    public void om07() {
	// ??
    }

    public void om08() {
	List<Integer> arr = new Random().ints(-1001, 1001).limit(400).mapToObj(e -> e).collect(Collectors.toList());
	int min = Collections.min(arr);
	int freq = Collections.frequency(arr, min);
	arr.removeAll(List.of(Integer.valueOf(min)));
	System.out.println("om8:\t" + min + " " + (arr.contains(min)) + " freq " + freq);
    }

    public void om09() {
	List<Integer> arr = ThreadLocalRandom.current().ints(-100, 100).limit(400).mapToObj(e -> e).collect(Collectors.toList());
	Map<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
	for (Integer i : arr) {
	    int count = Collections.frequency(arr, i);
	    freqMap.put(i, count);
	}
	int max = freqMap.values().stream().mapToInt(e -> e).max().getAsInt();
	int min = freqMap.entrySet().stream().filter(i -> i.getValue() == max).mapToInt(Map.Entry::getKey).min()
		.getAsInt();
	System.out.println("om9:\tmin: " + min + " " + freqMap.toString());
    }

    public void om10() {	
	int[] temp = ThreadLocalRandom.current().ints(-1001, 1001).limit(399).toArray();
	int left = 1, right, q;
	if (temp.length % 2 == 0) {
	    right = temp.length - 2;
	} else {
	    right = temp.length - 1;
	}
	q = right / 4;
	for (int i = 1; i <= temp.length - 1; i += 2) {
	    temp[i] = 0;
	}
	for (int i = q; i >= 0; i--) {
	    int temp1 = temp[right];
	    int temp2 = temp[left];
	    temp[left] = temp1;
	    temp[right] = temp2;
	    left += 2;
	    right -= 2;
	}
	System.out.println("om10:\t" + Arrays.toString(temp));
    }

    public void mm01() {
	int[][] matrix = new int[10][10];
	System.out.println("mm1:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = new Random().nextInt(101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	int length;
	if (matrix.length % 2 == 0) {
	    length = matrix.length;
	} else {
	    length = matrix.length - 1;
	}
	for (int i = 0; i < length; i++) {
	    if (i % 2 == 0) {
		continue;
	    }
	    if (matrix[0][i] > matrix[matrix.length - 1][i]) {
		for (int j = 0; j < matrix.length; j++) {
		    System.out.print("\t" + matrix[j][i] + " ");
		}
		System.out.println();
	    }
	}
	System.out.println();
    }

    public void mm02() {
	int[][] matrix = new int[10][10];
	System.out.println("mm2:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = new Random().nextInt(101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    System.out.print("\t" + matrix[i][i] + " ");
	}
	System.out.println();
    }

    public void mm03() {
	int k = 3, p = 5;
	int[][] matrix = new int[10][10];
	System.out.println("mm3:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = new Random().nextInt(101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    System.out.print("\t" + matrix[k][i] + " ");
	}
	System.out.println();
	for (int i = 0; i < matrix.length; i++) {
	    System.out.print("\t" + matrix[i][p] + " ");
	}
	System.out.println();
    }

    public void mm04() {
	int[][] matrix = new int[10][10];
	for (int i = 0; i < matrix.length; i++) {
	    int count;
	    if (i % 2 == 0) {
		count = 1;
	    } else {
		count = -matrix.length;
	    }
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = Math.abs(count++);
	    }

	}
	System.out.println("mm4:\n\tmatrix:");
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }

    public void mm05() {
	int[][] matrix = new int[10][10];
	int count = matrix.length - 1;
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		if (j <= count) {
		    matrix[i][j] = i + 1;
		} else {
		    matrix[i][j] = 0;
		}
	    }
	    count--;
	}
	System.out.println("mm5:\n\tmatrix:");
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }

    public void mm06() {
	int[][] matrix = new int[10][10];
	boolean check = false;
	int count = matrix.length;
	for (int i = 0; i < matrix.length; i++) {
	    int skip = matrix.length - count;
	    for (int j = 0; j < count; j++) {
		if (j < skip) {
		    continue;
		}
		matrix[i][j] = 1;
	    }
	    if (skip == matrix.length / 2 - 1) {
		check = true;
		i++;
		matrix[i][i - 1] = 1;
		matrix[i][i] = 1;
	    }
	    if (!check) {
		count--;
	    } else {
		count++;
	    }
	}
	System.out.println("mm6:\n\tmatrix:");
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }

    public void mm07() {
	// ???
    }

    public void mm08() {
	int[][] matrix = new int[10][10];
	System.out.println("mm8:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = new Random().nextInt(101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	int first = 0, second = 9;
	/*
	 * try (BufferedReader in = new BufferedReader(new
	 * InputStreamReader(System.in))) {
	 * System.err.println("input col number for swap, each in a new line");
	 * first = Integer.valueOf(in.readLine());
	 * second = Integer.valueOf(in.readLine());
	 * if (first < 0 || second < 0 || first >= matrix.length || second >=
	 * matrix.length) {
	 * throw new IOException();
	 * }
	 * } catch (IOException | NumberFormatException e) {
	 * System.err.println("ERRARRRRRRR");
	 * return;
	 * }
	 */

	for (int i = 0; i < matrix.length; i++) {
	    int ftemp = matrix[i][first];
	    int stemp = matrix[i][second];
	    matrix[i][first] = stemp;
	    matrix[i][second] = ftemp;
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }

    public void mm09() {
	int[][] matrix = new int[10][10];
	System.out.println("mm9:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = new Random().nextInt(101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	Map<Integer, Integer>treeMap=new TreeMap<Integer, Integer>();
	for (int i = 0; i < matrix.length; i++) {
	    int sum=0;
	    for (int j = 0; j < matrix.length; j++) {
		sum+=matrix[j][i];
	    }
	    treeMap.put(i, sum);
	}
	int result=treeMap.values().stream().mapToInt(e->e).max().getAsInt();
	int col=treeMap.entrySet().stream().filter(e->e.getValue()==result).mapToInt(e->e.getKey()).max().getAsInt();
	System.out.println("\t"+treeMap);
	System.out.format("\tresult: %d, %d\n", result, col);
    }
    
    public void mm10() {
	int[][] matrix = new int[10][10];
	System.out.println("mm10:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = ThreadLocalRandom.current().nextInt(-100, 101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    if(matrix[i][i]>0) {
		System.out.print("\t"+matrix[i][i]);
	    }
	}
	System.out.println();
    }
    
    public void mm11() {
	int[][] matrix = new int[10][10];
	System.out.println("mm11:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = ThreadLocalRandom.current().nextInt(0, 16);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	StringBuilder sb=new StringBuilder();
	for (int i = 0; i < matrix.length; i++) {
	    int count=0;
	    sb.append(String.format("\tmatrix row: %d:\t", i));
	    for (int j = 0; j < matrix.length; j++) {
		sb.append(matrix[i][j]+" ");
		if(matrix[i][j]==5) {
		    count++;
		}
	    }
	    if(count>=3) {
		System.out.println(sb.toString());
	    }
	    sb.setLength(0);
	}
    }
    
    public void mm12() {
	int[][] matrix = new int[10][10];
	System.out.println("mm12:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = ThreadLocalRandom.current().nextInt(-100, 101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    Arrays.sort(matrix[i]);
	}
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }
    
    public void mm13() {
	int[][] matrix = new int[10][10];
	System.out.println("mm13:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = ThreadLocalRandom.current().nextInt(-100, 101);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}	
	for (int i = 0; i < matrix.length; i++) {
	    int[]copy=new int[matrix.length];
	    for (int j = 0; j < copy.length; j++) {
		copy[j]=matrix[j][i];
	    }
	    Arrays.sort(copy);	    
	    for (int j = 0; j < copy.length; j++) {
		matrix[j][i]=copy[j];
	    }
	}
	System.out.println("\tresult:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	System.out.println();
    }
    
    public void mm14() {
	// m x n (3 dimensions) or M x N?
    }
    
    public void mm15() {
	int[][] matrix = new int[10][10];
	System.out.println("mm15:\n\tmatrix:");
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++) {
		matrix[i][j] = ThreadLocalRandom.current().nextInt(-50, 1000);
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}
	int max=Integer.MIN_VALUE;
	for (int i = 0; i < matrix.length; i++) {
	    int now=Arrays.stream(matrix[i]).max().getAsInt();
	    if(now>=max) {
		max=now;
	    }
	}
	System.out.println("\tresult:\tmax="+max);
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		if(matrix[i][j]%2!=0) {
		    matrix[i][j]=max;
		}
		System.out.print("\t" + matrix[i][j] + " ");
	    }
	    System.out.println();
	}	
    }
    
    public void mm16() {
	//too hard
    }
    
    public void oms01() {
	//and next - too boring
    }
}