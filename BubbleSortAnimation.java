package com.ggl.testing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class BubbleSortAnimation implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new BubbleSortAnimation());
	}
	
	private ColorDigits colorDigits;
	
	private DrawingPanel drawingPanel;
	
	private JTextField integerArrayField;
	
	public BubbleSortAnimation() {
		int[] array = { 8, 5, 26, 4, 9, 3, 2, 7, 1 };
		this.colorDigits = new ColorDigits(array);
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Bubble Sort Animation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		drawingPanel = new DrawingPanel(colorDigits);
		frame.add(createEntryPanel(), BorderLayout.BEFORE_FIRST_LINE);
		frame.add(drawingPanel, BorderLayout.CENTER);
		frame.add(createButtonPanel(), BorderLayout.AFTER_LAST_LINE);
		
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	private JPanel createEntryPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel label = new JLabel("Integers to sort:");
		panel.add(label);
		
		integerArrayField = new JTextField(30);
		panel.add(integerArrayField);
		
		JButton button = new JButton("Submit");
		button.addActionListener(new ArrayListener(this, colorDigits));
		panel.add(button);
		
		return panel;
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JButton button = new JButton("Sort Integers");
		button.addActionListener(new SortButtonListener(this, colorDigits));
		panel.add(button);
		
		return panel;
	}

	public JTextField getIntegerArrayField() {
		return integerArrayField;
	}
	
	public void repaint() {
		drawingPanel.repaint();
	}

	public class DrawingPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private int blockWidth;
		private int margin;
		
		private final ColorDigits model;

		public DrawingPanel(ColorDigits model) {
			this.model = model;
			
			this.blockWidth = 96;
			this.margin = 20;
			
			int width = model.getColorDigits().length * blockWidth +  2 * margin;
			int height = blockWidth + 2 * margin;
			this.setBackground(Color.WHITE);
			this.setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			ColorDigit[] colorDigits = model.getColorDigits();
			int x = margin;
			int y = margin;
			
			for (int index = 0; index < colorDigits.length; index++) {
				paintDigit(g, colorDigits[index].getDigit(), 
						colorDigits[index].getColor(), x, y);
				x += blockWidth;
			}
		}
		
		private void paintDigit(Graphics g, int digit, Color color, int x, int y) {
			String text = Integer.toString(digit);

			g.setColor(color);
			g.fillRect(x, y, blockWidth, blockWidth);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5f));
			g2d.setColor(Color.BLACK);
			g2d.drawRect(x, y, blockWidth, blockWidth);

			Font font = getFont().deriveFont(Font.BOLD, 54f);
			g2d.setFont(font);
			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(text, g2d);
			int a = (blockWidth - (int) r.getWidth()) / 2 + x;
			int b = (blockWidth - (int) r.getHeight()) / 2 + fm.getAscent() + y;
			g2d.drawString(text, a, b);
		}
	}
	
	public class ArrayListener implements ActionListener {
		
		private final BubbleSortAnimation frame;
		
		private final ColorDigits model;

		public ArrayListener(BubbleSortAnimation frame, ColorDigits model) {
			this.frame = frame;
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			String text = frame.getIntegerArrayField().getText().trim();
			String[] parts = text.split("\\s+");
			
			int[] array = new int[parts.length];
			for (int index = 0; index < parts.length; index++) {
				try {
					array[index] = Integer.valueOf(parts[index]);
				} catch (NumberFormatException e) {
					return;
				}
			}
			
			model.setColorDigits(createColorDigitArray(array, Color.WHITE));
			frame.getIntegerArrayField().setText("");
			frame.repaint();
		}
		
		private ColorDigit[] createColorDigitArray(int[] array, Color color) {
			ColorDigit[] colorDigitArray = new ColorDigit[array.length];
			
			for (int index = 0; index < array.length; index++) {
				colorDigitArray[index] = new ColorDigit(array[index], color);
			}
			
			return colorDigitArray;
		}
		
	}
	
	public class SortButtonListener implements ActionListener {
		
		private final BubbleSortAnimation frame;
		
		private final ColorDigits model;

		public SortButtonListener(BubbleSortAnimation frame, ColorDigits model) {
			this.frame = frame;
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			SortListener listener = new SortListener(frame, model);
			Timer timer = new Timer(2000, listener);
			listener.setTimer(timer);
			timer.setInitialDelay(0);
			timer.start();
		}
		
	}
	
	public class SortListener implements ActionListener {
		
		private int currentIndex;
		private int endIndex;
		private int stage;
		
		private final BubbleSortAnimation frame;
		
		private ColorDigit[] colorDigits;
		
		private Timer timer;

		public SortListener(BubbleSortAnimation frame, ColorDigits model) {
			this.frame = frame;
			
			this.colorDigits = model.getColorDigits();
			this.currentIndex = 0;
			this.endIndex = colorDigits.length - 2;
			this.stage = 1;
		}

		public void setTimer(Timer timer) {
			this.timer = timer;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			updateColorArray(Color.WHITE, Color.WHITE);
			
			if (endIndex < 0) {
				updateColorArray(Color.GREEN, Color.GREEN);
				frame.repaint();
				timer.stop();
				return;
			}
			
			switch (stage) {
			case 1:
				updateColorArray(Color.YELLOW, Color.WHITE);
				int digit1 = colorDigits[currentIndex].getDigit();
				int digit2 = colorDigits[currentIndex + 1].getDigit();
				if (digit1 > digit2) {
					stage = 2;
				} else {
					currentIndex++;
				}
				break;
			case 2:
				updateColorArray(Color.RED, Color.WHITE);
				stage = 3;
				break;
			case 3:
				ColorDigit temp = colorDigits[currentIndex].copy();
				colorDigits[currentIndex] = colorDigits[currentIndex + 1].copy();
				colorDigits[currentIndex + 1] = temp;
				
				stage = 1;
				currentIndex++;
				break;
			}
			
			frame.repaint();
			
			if (currentIndex > endIndex) {
				currentIndex = 0;
				endIndex--;
			} 
		}
		
		private void updateColorArray(Color color, Color baseColor) {
			for (int index = 0; index < colorDigits.length; index++) {
				colorDigits[index].setColor(baseColor);
			}
			
			colorDigits[currentIndex].setColor(color);
			colorDigits[currentIndex + 1].setColor(color);
		}
		
	}
	
	public class ColorDigits {
		
		private ColorDigit[] colorDigits;
		
		public ColorDigits(int[] array) {
			this.colorDigits = new ColorDigit[array.length];
			
			for (int index = 0; index < array.length; index++) {
				colorDigits[index] = new ColorDigit(array[index], Color.WHITE);
			}
		}

		public ColorDigit[] getColorDigits() {
			return colorDigits;
		}

		public void setColorDigits(ColorDigit[] colorDigits) {
			this.colorDigits = colorDigits;
		}
		
	}
	
	public class ColorDigit {
		
		private final int digit;
		
		private Color color;

		public ColorDigit(int digit, Color color) {
			this.digit = digit;
			this.color = color;
		}
		
		public ColorDigit copy() {
			return new ColorDigit(digit, color);
		}

		public int getDigit() {
			return digit;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}
		
	}

}
