package ImageConvertor.views.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

import ImageConvertor.core.Controller;

public class AlgorithmSettingsView extends JDialog {

	Controller controller;
	List<Float>settings;

	AbstractFormatterFactory formatter = new AbstractFormatterFactory() {
		@Override
		public AbstractFormatter getFormatter(JFormattedTextField tf) {
			NumberFormat f = DecimalFormat.getInstance();
			f.setMinimumFractionDigits(1);
			f.setMaximumFractionDigits(2);
			f.setRoundingMode(RoundingMode.HALF_UP);
			InternationalFormatter iff = new InternationalFormatter(f);
			iff.setAllowsInvalid(false);
			iff.setMinimum(1f);
			iff.setMaximum(Float.MAX_VALUE);
			return iff;
		}
	};

	public AlgorithmSettingsView(Controller c) {
		super(View.getInstance(), true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosed(e);
				controller.setCanceled(true);
			}

		});
		this.controller = c;
		setTitle("alg settings");
		setLayout(new GridLayout(8, 2));
		add(getTotalConnectedLimitPoints());
		add(getConnectedLimitPoints());
		add(getRangeRate());
		add(getWeightRate());
		add(getPathDivider());
		add(getMaxRange());
		add(getRangeDelimiter());
		add(getOkandCancelButton());
		// setLocationRelativeTo(null);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)(d.getWidth()/2-150), (int)(d.getHeight()/2-150));
		setSize(300, 300);
		setVisible(true);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	JComponent getTotalConnectedLimitPoints() {

		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("matrix point limit");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(2500);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getConnectedLimitPoints() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("connected point limit");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(80);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getRangeRate() {

		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("range rate");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(20);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getPathDivider() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("path divider");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(9);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getWeightRate() {

		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("weight rate");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(1);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getMaxRange() {

		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("max range");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(5);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getRangeDelimiter() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText("max delimiter range");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(50f);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getOkandCancelButton() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JButton ok = new JButton("start");
		ok.addActionListener((e) -> {
			List<Float> temp = new ArrayList<>();
			for (Component c : getContentPane().getComponents()) {
				if (c instanceof JPanel jp) {
					for (Component cc : jp.getComponents()) {
						if (cc instanceof JFormattedTextField jtf) {
							// System.out.println(jtf.getValue());
							temp.add(Float.valueOf(jtf.getValue().toString()));
						}
					}
				}
			}
			settings=temp;
			for (float f : temp) {
				System.out.println(f);
			}
			dispose();
		});

		JButton cancel = new JButton("cancel");
		cancel.addActionListener((e) -> {
			controller.setCanceled(true);
			this.dispose();
		});

		container.add(ok);
		container.add(cancel);

		return container;
	}

	public List<Float> getSettings() {
		return settings;
	}

}
