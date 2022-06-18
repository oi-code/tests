package ImageConvertor;

import java.util.Arrays;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import ImageConvertor.views.desktop.AlgorithmSettingsView;
import ImageConvertor.views.desktop.View;

//import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
	public static void main(String... args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
		}
		try {
			View.getInstance();
			//new AlgorithmSettingsView();			
		} catch (Throwable e) {
			e.printStackTrace();
			//System.exit(0);
		}
	}
}
