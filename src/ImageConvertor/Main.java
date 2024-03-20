package ImageConvertor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import javax.swing.UIManager;

import ImageConvertor.core.AntEdge;
import ImageConvertor.data.Chunk;
import ImageConvertor.views.desktop.View;

//import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
	public static void main(String... args) throws Throwable {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
		}
		try {
			View.getInstance();
			/*
			 * AtomicReference<Float> fl = new AtomicReference<>(0f);
			 * IntStream.range(0, 10).forEach(e -> {
			 * fl.set(fl.get() + (float) e);
			 * });
			 * System.out.println(fl.get());
			 */

			/*
			 * short a = 4;
			 * short b = 2;
			 * Chunk c1 = new Chunk(a, a);
			 * Chunk c2 = new Chunk(b, b);
			 * c1.index = 300_000;
			 * c2.index = 599_999;
			 * AntEdge ed = new AntEdge(c1, c2);
			 * AntEdge de = new AntEdge(c2, c1);
			 * System.out.println(ed.equals(de));
			 * System.out.println(ed.hashCode() + " " + de.hashCode());
			 * 
			 * 
			 * short c=52;
			 * short d=752;
			 * Chunk c3 = new Chunk(c, c);
			 * Chunk c4 = new Chunk(d, d);
			 * c3.index = 2;
			 * c4.index = 1;
			 * AntEdge es = new AntEdge(c1, c3);
			 * AntEdge ds = new AntEdge(c4, c2);
			 * System.out.println(es.equals(ds));
			 * System.out.println(es.hashCode() + " " + ds.hashCode());
			 * 
			 * System.out.println(es.getDistance());
			 * System.out.println(ds.getDistance());
			 * System.out.println(ed.getDistance());
			 * System.out.println(de.getDistance());
			 */

		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
