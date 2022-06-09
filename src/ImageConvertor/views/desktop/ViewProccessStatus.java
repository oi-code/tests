package ImageConvertor.views.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import ImageConvertor.core.Controller;

public class ViewProccessStatus extends JDialog implements Runnable {

	Controller controller;
	JTextComponent text;
	JScrollPane scrollPane;

	public ViewProccessStatus(Controller controller) {
		//super(View.getViewInstance(), true);
		this.controller = controller;
		setLayout(new BorderLayout());
		setTitle("Processing...");
		// text = (JTextComponent) getTextLabel();
		add(getTextLabel());
		add(getButtonAndLoadingImageLabel(), BorderLayout.PAGE_END);
		setSize(300, 200);
		setLocationRelativeTo(null);
		setFocusable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
	}

	@Override
	public void run() {
		setVisible(true);
		while (!Thread.currentThread().isInterrupted() && !controller.isProcessed()) {
			String nextText = "";
			try {
				nextText = controller.messageExchanger.poll();
				if (nextText == null || "null".equals(nextText)) {
					continue;
				}
				System.out.println(nextText);
			} catch (Exception e) {
				e.printStackTrace();
			}
			text.setText(text.getText() + "\n" + nextText);
			text.setCaretPosition(text.getText().length());
		}
		this.dispose();
		return;
	}

	JComponent getImageLabel() {
		ImageIcon icon = new ImageIcon("images/ezgif-4-3c6aceb748.gif");
		icon.setImage(icon.getImage().getScaledInstance(50, 50, Image.SCALE_FAST));
		JLabel label = new JLabel(icon);
		return label;
	}

	JComponent getButtonAndLoadingImageLabel() {
		JPanel label = new JPanel();
		label.setLayout(new GridLayout(1, 2));
		label.add(getImageLabel());
		label.add(getCancelButton());
		return label;
	}

	JComponent getTextLabel() {

		JTextArea text = new JTextArea();
		text.setSize(300, 50);
		text.setFocusable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);

		JScrollPane scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// scroll.setAutoscrolls(true);
		scroll.setViewportView(text);
		this.text = text;
		return scroll;
	}

	JComponent getCancelButton() {
		JButton button = new JButton();
		button.setText("Cancel task");
		button.setFocusable(false);
		button.setSelected(false);
		button.setEnabled(true);
		Color color = button.getBackground();
		button.addActionListener((e) -> {
			System.out.println("hi");
			button.setEnabled(false);
			button.setBackground(color);
		});
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (button.isEnabled())
					button.setBackground(Color.red);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (button.isEnabled())
					button.setBackground(color);
			}
		});
		return button;
	}

}
