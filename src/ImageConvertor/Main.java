package ImageConvertor;

import java.util.Arrays;
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
		// String gs=new BufferedReader(new InputStreamReader(System.in)).readLine();
		// byte[][]arr=new byte[1000*1000][1000*1000];

		/*
		 * int entryH = 11;
		 * int entryW = 4;
		 * int[][] matrix = new int[20][20];
		 * 
		 * int startW = 4;
		 * int startH = startW;
		 * 
		 * int width = entryW - startW;
		 * int widthLength = entryW + startW;
		 * 
		 * int height = entryH - startH;
		 * int heightLength = entryH + startH;
		 * 
		 * do {
		 * for (; width < widthLength; width++) {
		 * matrix[height][width] = 1;
		 * }
		 * for (; height < heightLength; height++) {
		 * matrix[height][width] = 1;
		 * }
		 * for (; width > entryW - startW; width--) {
		 * matrix[height][width] = 1;
		 * }
		 * for (; height > entryH - startH; height--) {
		 * matrix[height][width] = 1;
		 * 
		 * }
		 * startW--;
		 * startH--;
		 * width = entryW - startW;
		 * widthLength = entryW + startW;
		 * 
		 * height = entryH - startH;
		 * heightLength = entryH + startH;
		 * } while (startW > 0);
		 * for (int[] i : matrix) {
		 * for (int w : i) {
		 * if (w == 1) {
		 * System.out.print("*\s");
		 * } else {
		 * System.out.print(".\s");
		 * }
		 * }
		 * System.out.println();
		 * }
		 */
		

	}
}
