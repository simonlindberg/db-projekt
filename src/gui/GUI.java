package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import se.datadosen.component.RiverLayout;
import database.Database;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public GUI() {
		setLayout(new RiverLayout());

		addComponents();

		setTitle("Krusty Kookies Sweden AB");
		setSize(700, 600);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void addComponents() {
		final JTextArea errorMessage = new JTextArea(10, 30);
		final JScrollPane jsp = new JScrollPane(errorMessage);

		add(new JLabel("Pallet produced:"));
		addProducePallet(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(new JLabel("Pallet blocking:"), RiverLayout.LINE_BREAK);
		addBlockPallet(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(new JLabel("Pallet search:"), RiverLayout.LINE_BREAK);
		addSearchBlock(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(jsp, RiverLayout.LINE_BREAK);
	}

	private void addSearchBlock(final JTextArea errorMessage) {
		final JTextField input = new JHintTextField(20, "enter the pallet id");
		final JCheckBox isBlocked = new JCheckBox("Show blocked pallets");
		final JLabel labelFrom = new JLabel("From:");
		final JTextField dateFrom = new JHintTextField(12, "yyyy-mm-dd HH:MM");
		final JLabel labelTo = new JLabel("To:");
		final JTextField dateTo = new JHintTextField(12, "yyyy-mm-dd HH:MM");

		final String[] recipes = Database.instance().getRecipes();
		final String[] productList = new String[recipes.length + 1];
		productList[0] = "All products";
		System.arraycopy(recipes, 0, productList, 1, recipes.length);

		final JComboBox<String> products = new JComboBox<String>(productList);
		final JCheckBox delivered = new JCheckBox("Show delivered pallets");

		final JButton button = new JButton("Search pallet");

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				Date from = null;
				Date to = null;
				try {
					if (!dateFrom.getText().equals("") && !dateTo.getText().equals("")) {
						from = parseDate(dateFrom.getText());
						to = parseDate(dateFrom.getText());
					}
				} catch (ParseException e1) {
					errorMessage.setText("Wrong date");
					e1.printStackTrace();
				}
				int id = 0;

				if (!input.getText().isEmpty()) {
					try {
						id = Integer.parseInt(input.getText());
					} catch (NumberFormatException ex) {
						errorMessage.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
					}
				}
				// final Location location =
				// Database.instance().searchPalletLocation(id);
				final String searchPallet = Database.instance().searchPallet(id, isBlocked.isSelected(), from, to, (String) products.getSelectedItem(), delivered.isSelected());
				errorMessage.setText(searchPallet);
			}
		});

		add(input, RiverLayout.LINE_BREAK);
		add(isBlocked, RiverLayout.LINE_BREAK);
		add(labelFrom, RiverLayout.LINE_BREAK);
		add(dateFrom);
		add(labelTo);
		add(dateTo);
		add(products, RiverLayout.LINE_BREAK);
		add(delivered, RiverLayout.LINE_BREAK);
		add(button, RiverLayout.LINE_BREAK);
	}

	private void addBlockPallet(final JTextArea errorMessage) {
		final JComboBox<String> products = new JComboBox<String>(Database.instance().getRecipes());

		final JButton button = new JButton("Block pallet");

		final JLabel from = new JLabel("From:");
		final JTextField dateFrom = new JHintTextField(12, "yyyy-mm-dd HH:MM");
		final JLabel to = new JLabel("To:");
		final JTextField dateTo = new JHintTextField(12, "yyyy-mm-dd HH:MM");

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = (String) products.getSelectedItem();

				try {
					final Date to = parseDate(dateTo.getText());
					final Date from = parseDate(dateFrom.getText());
					System.out.println("to: " + to.toString());
					System.out.println("from: " + from.toString());
					final int blockPallet = Database.instance().blockPallet(text, from, to);
					if (blockPallet > 0) {
						errorMessage.setText("Success: blocked a total of " + blockPallet + " pallets.");
					} else {
						errorMessage.setText("Ouch, No pallets were blocked");
					}
				} catch (ParseException e1) {
					errorMessage.setText("<html><font color='red'>Bad date. Must be in format ´yyyy-mm-dd HH:MM´.</font></html>");
				}
			}
		});

		add(products, RiverLayout.LINE_BREAK);
		add(from);
		add(dateFrom);
		add(to);
		add(dateTo);
		add(button);
	}

	private void addProducePallet(final JTextArea errorMessage) {
		final JTextField input = new JHintTextField(20, "enter the pallet id");
		final JButton button = new JButton("Add pallet");
		final JComboBox<String> products = new JComboBox<String>(Database.instance().getRecipes());

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();
				final String product = (String) products.getSelectedItem();
				try {
					final int id = Integer.parseInt(text);
					if (Database.instance().palletProduced(id, product)) {
						errorMessage.setText("Success: Pallet " + id + " is now registered.");
					} else {
						errorMessage
								.setText("<html><font color='red'>Ouch, some thing went wrong. Perhaps the id already exists.</font></html>");
					}
				} catch (NumberFormatException ex) {
					errorMessage.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
				}
			}
		});

		add(products, RiverLayout.LINE_BREAK);
		add(input);
		add(button);

	}

	private Date parseDate(final String dateString) throws ParseException {
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		final java.util.Date date = formatter.parse(dateString);
		return new Date(date.getTime());

	}

	public static void main(String[] args) {
		new GUI();
	}

	@SuppressWarnings("serial")
	private static class JHintTextField extends JTextField implements FocusListener {
		private static final Color hintColor = Color.GRAY;
		private static final Color normalColor = Color.BLACK;
		private final String hint;
		private boolean hinting;

		public JHintTextField(final int size, final String hint) {
			super(size);
			this.hint = hint;
			addFocusListener(this);
			focusLost(null);
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (getText().isEmpty()) {
				setForeground(normalColor);
				setText("");
				hinting = false;
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (getText().isEmpty()) {
				setForeground(hintColor);
				setText(hint);
				hinting = true;
			}
		}

		@Override
		public String getText() {
			return hinting ? "" : super.getText();
		}

	}
}