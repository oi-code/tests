package ImageConvertor.views.desktop;

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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.InternationalFormatter;

import ImageConvertor.core.Controller;

@SuppressWarnings("serial")
public class AlgorithmSettingsView extends JDialog {

	Controller controller;
	List<Float> settings;
	int width = 600;
	int height = 300;

	AbstractFormatterFactory formatter = new AbstractFormatterFactory() {
		@Override
		public AbstractFormatter getFormatter(JFormattedTextField tf) {
			NumberFormat f = DecimalFormat.getInstance();
			f.setMinimumFractionDigits(1);
			f.setMaximumFractionDigits(3);
			f.setRoundingMode(RoundingMode.HALF_UP);
			InternationalFormatter iff = new InternationalFormatter(f);
			iff.setAllowsInvalid(false);
			iff.setMinimum(0f);
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
		setTitle(controller.getLocaleText("alset_title"));
		setModal(true);
		add(getTotalConnectedLimitPoints());
		add(getConnectedLimitPoints());
		add(getRangeRate());
		add(getWeightRate());
		add(getPathDivider());
		add(getMaxRange());
		add(getRangeDelimiter());
		add(getIterationns());
		add(getVaporizeRate());
		add(getButtons());
		int size = getContentPane().getComponents().length;
		setLayout(new GridLayout(size, 2));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (d.getWidth() / 2 - width / 2), (int) (d.getHeight() / 2 - height / 2));
		setSize(width, height);
		setResizable(false);
		setVisible(true);
	}

	JComponent getTotalConnectedLimitPoints() {

		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText(controller.getLocaleText("matrix_limit"));
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
		text.setText(controller.getLocaleText("conpo_limit"));
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
		text.setText(controller.getLocaleText("range_rate"));
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
		text.setText(controller.getLocaleText("path_divider"));
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
		text.setText(controller.getLocaleText("weight_rate"));
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
		text.setText(controller.getLocaleText("max_range"));
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
		text.setText(controller.getLocaleText("init_dist_delim"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(50f);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getIterationns() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText(controller.getLocaleText("iter_count"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(10f);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getVaporizeRate() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel();
		text.setText(controller.getLocaleText("iter_vapor_rate"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JFormattedTextField input = new JFormattedTextField(formatter);
		input.setValue(0.62f);

		container.add(text);
		container.add(input);

		return container;
	}

	JComponent getButtons() {
		JPanel container = new JPanel();

		JButton ok = new JButton(controller.getLocaleText("start"));
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
			settings = temp;
			/*for (float f : temp) {
				System.out.println(f);
			}*/
			dispose();
		});

		JButton cancel = new JButton(controller.getLocaleText("cancel"));
		cancel.addActionListener((e) -> {
			controller.setCanceled(true);
			this.dispose();
		});
		int wdth = (int) (width / 2 - width * 0.1);
		int hgt = height / getContentPane().getComponentCount()
				- (int) ((height / getContentPane().getComponentCount()) * 0.4);
		JButton questionMark = new JButton("?");
		questionMark.addActionListener((e) -> {
			JDialog d = new JDialog();
			d.setModal(true);
			d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			d.setSize(100,100);
			d.setLocationRelativeTo(null);
			d.setAlwaysOnTop(true);
			d.setVisible(true);
		});

		ok.setPreferredSize(new Dimension(wdth, hgt));
		cancel.setPreferredSize(new Dimension(wdth, hgt));
		questionMark.setPreferredSize(new Dimension((int) (width * 0.1), hgt));

		container.add(ok);
		container.add(cancel);
		container.add(questionMark);

		return container;
	}

	public List<Float> getSettings() {
		return settings;
	}

}
