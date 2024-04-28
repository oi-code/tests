package ImageConvertor.views.desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ImageConvertor.core.Controller;

@SuppressWarnings("serial")
public class GCodeGeneratorView extends JDialog {

	private List<Component> components = new ArrayList<>();
	private List<String> settings;
	private int size;
	private Controller controller;
	int width = 500;
	int height = 200;

	public GCodeGeneratorView(Controller c) {
		controller = c;
		getPenUpCommand();
		getPenDownCommand();
		getDelayCommand();
		getScaleCommand();
		getFeedrateCommand();
		getSheetWidthAndHeight();
		getButtons();
		setTitle(controller.getLocaleText("create_gcode"));
		setModal(true);
		setResizable(false);
		// setLocationRelativeTo(null);
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) (screenDimension.getWidth() / 2 - width / 2),
				(int) (screenDimension.getHeight() / 2 - height / 2));
		setLayout(new GridLayout(components.size(), 1));
		components.stream().forEach(e -> add(e));
		setSize(width, height);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void getPenUpCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("pen_up"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		size = text.getFont().getSize();

		JTextField input = new JTextField();
		input.setAlignmentX(CENTER_ALIGNMENT);
		input.setAlignmentY(CENTER_ALIGNMENT);
		input.setText("M5");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);

		components.add(container);
	}

	private void getPenDownCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("pen_down"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("M3 S40");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getDelayCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("delay"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("G4 P0.2");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getScaleCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel(controller.getLocaleText("scale"));
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("0.955");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getFeedrateCommand() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		// JLabel text = new JLabel(controller.getLocaleText("feedrate"));
		JLabel text = new JLabel("feedrate");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		JTextField input = new JTextField();
		input.setText("F10000");
		input.setFont(text.getFont().deriveFont(size));

		container.add(text);
		container.add(input);
		components.add(container);
	}

	private void getSheetWidthAndHeight() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JLabel text = new JLabel("width/height");
		text.setHorizontalAlignment((int) CENTER_ALIGNMENT);

		JPanel sizeContainer = new JPanel();
		sizeContainer.setLayout(new GridLayout(1, 2));

		JTextField _width = new JTextField();
		_width.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		_width.setText("210");
		_width.setFont(text.getFont().deriveFont(size));

		JTextField _height = new JTextField();
		_height.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		_height.setText("297");
		_height.setFont(text.getFont().deriveFont(size));

		sizeContainer.add(_width);
		sizeContainer.add(_height);

		container.add(text);
		container.add(sizeContainer);
		components.add(container);
	}

	private void getButtons() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(1, 2));

		JButton okButton = new JButton(controller.getLocaleText("ok"));
		okButton.addActionListener((e) -> {
			settings = new ArrayList<>();
			for (Component c : components) {
				if (c instanceof JPanel panel) {
					for (Component jp : panel.getComponents()) {
						if (jp instanceof JTextField text) {
							settings.add(text.getText());
						}
					}
				}
			}
			/*
			 * get width and height from component {@link #getSheetWidthAndHeight()}
			 */
			Component c = components.get(5);
			JPanel p = (JPanel) c;
			p = (JPanel) Arrays.stream(p.getComponents()).filter(element -> element instanceof JPanel).findFirst()
					.get();
			Arrays.stream(p.getComponents()).filter(element -> element instanceof JTextField)
					.forEach(element -> settings.add(((JTextField) element).getText()));
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});

		JButton cancelButton = new JButton(controller.getLocaleText("cancel"));
		cancelButton.addActionListener(e -> {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		});

		container.add(okButton);
		container.add(cancelButton);

		components.add(container);
	}

	public List<String> getSettings() {
		return settings;
	}

}
