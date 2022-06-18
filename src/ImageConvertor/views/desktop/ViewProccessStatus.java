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
import java.util.ArrayList;
import java.util.List;
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
	float size = 0f;

	public ViewProccessStatus(Controller controller) {
		this.controller = controller;
		setLayout(new BorderLayout());
		setTitle(controller.getLocaleText("processing"));
		//setModal(true);
		Thread.currentThread().setName("process_window_thread_helper");
		// text = (JTextComponent) getTextLabel();
		add(getButtonAndLoadingImageLabel(), BorderLayout.PAGE_END);
		add(getTextLabel());
		setSize(500, 200);
		setLocationRelativeTo(null);
		setFocusable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	@Override
	public void run() {
		setVisible(true);
		while (!Thread.currentThread().isInterrupted() && !controller.isProcessed()) {
			String nextText = "";
			try {
				nextText = controller.pollMessage();
				if (nextText == null || "null".equals(nextText)) {
					continue;
				}
				// System.out.println(nextText);
			} catch (Exception e) {
				e.printStackTrace();
				this.dispose();
				return;
			}
			text.setText(text.getText() + "\n" + nextText);
			text.setCaretPosition(text.getText().length());
		}
		this.dispose();
		return;
	}

	JComponent getImageLabel() {
		ImageIcon icon = new ImageIcon("images/ezgif-4-66d70871c9.gif");
		icon.setImage(icon.getImage().getScaledInstance(140, 40, Image.SCALE_DEFAULT));
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
		text.setFont(text.getFont().deriveFont(size));
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
		/*
		 * ImageIcon icon = new ImageIcon("images/ezgif-4-66d70871c9.gif");
		 * icon.setImage(icon.getImage().getScaledInstance(140, 40, Image.SCALE_DEFAULT));
		 * JButton button = new JButton("cancel task", icon);
		 */
		JButton button = new JButton();
		size = button.getFont().getSize();
		button.setText(controller.getLocaleText("cancel_task"));
		button.setFocusable(false);
		button.setSelected(false);
		button.setEnabled(true);
		Color color = button.getBackground();
		button.addActionListener((e) -> {
			button.setEnabled(false);
			button.setBackground(color);
			controller.cancelTask();
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