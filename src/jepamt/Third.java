package jepamt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;
import java.util.regex.*;

public class Third {
    public static void main(String... args) {
	Third s = new Third();
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

    public void shm01() {
	String[] cm = { "haHa", "beBe", "coCo" };
	String[] res = new String[cm.length];
	System.out.println("shm01:\n\tin: " + Arrays.toString(cm));
	for (int i = 0; i < cm.length; i++) {
	    char[] c = cm[i].toCharArray();
	    String cmRes = "";
	    for (int j = 0; j < c.length; j++) {
		if (c[j] > 65 && c[j] < 97) {
		    cmRes += "_" + (char) (c[j] + 32);
		    continue;
		}
		cmRes += c[j];
	    }
	    res[i] = cmRes;
	}
	System.out.println("\tout: " + Arrays.toString(res));
    }

    public void shm02() {
	String str = "word fword 12word wordword";
	System.out.println("shm02:\n\tbefore: " + str);
	str = str.replaceAll("word", "letter");
	System.out.println("\tresult: " + str);
    }

    public void shm03() {
	String str = "12 123 1 2 dfa0 0f 2 4 4.21 2.22";
	Matcher m = Pattern.compile("\\b\\d\\.*\\d*\\b").matcher(str);
	int count = 0;
	while (m.find()) {
	    count++;
	}
	System.out.println("shm03:\n\tstr: " + str + "\n\tresult: " + count);
    }

    public void shm04() {
	System.out.print("shm04:\n\t");
	shm03();
    }

    public void shm05() {
	String str = "asdf    adf  hrh4w 4ahw4   aw4h         a4hwa haw4   awh4     haw4h";
	String result = str.replaceAll("\s{2,}", "\s");
	System.out.println("shm05:\n\tstr: " + str + "\n\tresult: " + result);

    }

    public void slsb01() {
	String check = "\s".repeat(22);
	String str = "a  s     s    g         g   g         g       gh" + check + ".";
	char[] c = str.toCharArray();
	int min = 0;
	int max = 0;
	for (int i = 0; i < c.length; i++) {
	    if (c[i] == '\s') {
		min++;
		continue;
	    }
	    if (max <= min) {
		max = min;
	    }
	    min = 0;
	}
	System.out.println("slsb01:\n\tstr: " + str + "\n\tresult: " + max + "\n\tcheck: " + check.length());
    }

    public void slsb02() {
	String str = "auuae99ae8fawe.wafwa./aef+f==a-wafga-gaegawepg";
	String result = "";
	for (int i = 0; i < str.length(); i++) {
	    result += str.charAt(i);
	    if (str.charAt(i) == 'a') {
		result += 'b';
	    }
	}
	System.out.println("slsb02:\n\tstr:\t" + str + "\n\tresult:\t" + result);
    }

    public void slsb03() {
	String[] str = { "abcba", "asdf", "asdew", "abccba" };
	String result = "";
	for (int i = 0; i < str.length; i++) {
	    int count = 0;
	    for (int q = 0, w = str[i].length() - 1; q < w / 2; q++, w--) {
		char a = str[i].charAt(q);
		char b = str[i].charAt(w);
		if (b == a) {
		    count++;
		}
	    }
	    if (count > 0 && count == str[i].length() / 2 - 1) {
		result += "\t\t" + str[i] + ": " + true + "\n";
	    } else {
		result += "\t\t" + str[i] + ": " + false + "\n";
	    }
	    count = 0;
	}
	System.out.println("slsb03:\t" + "\n\tresult:\n" + result.substring(0, result.length() - 1));
    }

    public void slsb04() {
	// ???
    }

    public void slsb05() {
	String str = "b" + "a".repeat(20) + "c" + "a".repeat(14);
	int count = 0;
	for (int i = 0; i < str.length(); i++) {
	    if (str.charAt(i) == 'a') {
		count++;
	    }
	}
	System.out.println("slsb05:\n\tstr: " + str + "\n\tcount: " + count);
    }

    public void slsb06() {
	String str = "qwertyuiop";
	String result = "";
	for (int i = 0; i < str.length(); i++) {
	    result += String.valueOf(str.charAt(i)).repeat(2);
	}
	System.out.println("slsb06:\n\tstr: " + str + "\n\tresult: " + result);
    }

    public void slsb07() {
	String str = "abc cde def fgh hjy yik kmlabc";
	String result = str.replaceAll("\s", "");
	StringBuilder sb = new StringBuilder();
	/*
	 * LinkedHashMap<String, String>lhm=new LinkedHashMap<String, String>();
	 * for(Character c:result.toCharArray()) {
	 * lhm.put(String.valueOf(c), "");
	 * }
	 * lhm.keySet().stream().map(e->e).forEach(e->sb.append(e));
	 */
	for (int i = 0; i < result.length(); i++) {
	    if (sb.toString().contains(String.valueOf(result.charAt(i)))) {
		continue;
	    }
	    sb.append(result.charAt(i));
	}
	System.out.println("slsb07:\n\tstr: " + str + "\n\tresult: " + sb.toString());
    }

    public void slsb08() {
	String str = "asd asdfasdf fasdfasdfasd thiswordisthelongestinthesequence  asdgasgdasgsadg dd gasdgawega dagasdg ga";
	String[] arr = str.split("\s");
	int max = 0, arrPos = -1;
	for (int i = 0; i < arr.length; i++) {
	    if (arr[i].length() >= max) {
		max = arr[i].length();
		arrPos = i;
	    }
	}
	System.out.println("slsb08:\n\tstr: " + str + "\n\tresult: " + max + "\n\tword: " + arr[arrPos]);
    }

    public void slsb09() {
	String str = "a".repeat(15) + "B".repeat(12);
	int low = 0, up = 0;
	for (int i = 0; i < str.length(); i++) {
	    if (str.charAt(i) > 96 && str.charAt(i) < 123) {
		low++;
	    } else if (str.charAt(i) > 64 && str.charAt(i) < 91) {
		up++;
	    }
	}
	System.out.println("slsb09:\n\tstr: " + str + "\n\tresult:\n\t\tlower: " + low + "\n\t\tupper: " + up);
    }

    public void slsb10() {
	String str = "ff! ss? aa. ff! gg. sd? as? ga!";
	//if string contains delimiter type '\s', solution is str.split("\s").length
	List<String>arr=new ArrayList<String>();
	String res="";
	for(int i=0;i<str.length();i++) {
	    res+=str.charAt(i);
	    if(str.charAt(i)=='!'||str.charAt(i)=='?'||str.charAt(i)=='.'){
		arr.add(res);
		res="";
		continue;
	    }
	}
	System.out.println("slsb10:\n\tstr: " + str+"\n\tresult: "+arr.size());
    }
    
    //for resolve last 2 tasks, need relatively long time. I skipped this.
}
