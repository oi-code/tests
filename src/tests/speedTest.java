package tests;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
/*
 * https://www.youtube.com/watch?v=3PcIJKd1PKU
 */
public class speedTest {
    public static void main(String... args) {
	int n=0;
	StopWatch s=new StopWatch();
	s.start();
	for(int i=2;i<250_001;i++) {
	    n+=calculate(i);
	}
	s.stop();
	System.out.printf("%d.5", s.getTime(TimeUnit.MILLISECONDS)/1000l);
    }
    
    static int calculate(int n) {
	for(int i=2;i<=n/2;i++){
	    if(n%i==0) {
		return 0;
	    }	    
	}
	return 1;
    }
}
