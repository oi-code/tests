package ImageConvertor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import javax.swing.UIManager;

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
			AtomicReference<Float> fl = new AtomicReference<>(0f);
			IntStream.range(0, 10).forEach(e -> {
				fl.set(fl.get() + (float) e);
			});
			System.out.println(fl.get());
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
