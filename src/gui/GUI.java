package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import location.Location;
import se.datadosen.component.RiverLayout;
import database.Database;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public GUI() {
		setLayout(new RiverLayout());

		addComponents();

		setTitle("Krusty Kookies Sweden AB");
		setSize(700, 500);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void addComponents() {
		final JLabel errorMessage = new JLabel();

		add(new JLabel("Pallet produced:"));
		addProducePallet(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(new JLabel("Pallet blocking:"), RiverLayout.LINE_BREAK);
		addBlockPallet(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(new JLabel("Pallet search:"), RiverLayout.LINE_BREAK);
		addSearchBlock(errorMessage);

		add(new JLabel(" "), RiverLayout.LINE_BREAK);
		add(errorMessage, RiverLayout.LINE_BREAK);
	}

	private void addSearchBlock(final JLabel errorMessage) {
		final JTextField input = new JHintTextField(20, "enter the pallet id");
		final JButton button = new JButton("Search pallet");

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();
				try {
					final int id = Integer.parseInt(text);
					final Location location = Database.instance().searchPalletLocation(id);
					errorMessage.setText("Pallet " + id + " is " + location.getLocationName());
				} catch (NumberFormatException ex) {
					errorMessage.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
				}
			}
		});

		add(input, RiverLayout.LINE_BREAK);
		add(button);
	}

	private void addBlockPallet(final JLabel errorMessage) {
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

	private void addProducePallet(final JLabel errorMessage) {
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
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:MM");

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