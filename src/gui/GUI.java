package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		setTitle("Krusty Kookies Sweden AB");
		setSize(700, 500);

		final JLabel errorMessage = new JLabel();

		addProducePallet(errorMessage);
		addBlockPallet(errorMessage);
		addSearchBlock(errorMessage);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(errorMessage, RiverLayout.LINE_BREAK);
		setVisible(true);
	}

	private void addSearchBlock(final JLabel errorMessage) {
		final JTextField input = new JTextField(20);
		final JButton button = new JButton("Search pallet");

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();
				try {
					final int id = Integer.parseInt(text);
					final Location location = Database.instance()
							.searchPalletLocation(id);
					errorMessage.setText("Pallet " + id + " is "
							+ location.getLocationName());
				} catch (NumberFormatException ex) {
					errorMessage
							.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
				}
			}
		});

		add(input, RiverLayout.LINE_BREAK);
		add(button);
	}

	private void addBlockPallet(final JLabel errorMessage) {
		final JComboBox<String> products = new JComboBox<String>(Database
				.instance().getRecipes());

		final JButton button = new JButton("Block pallet");

		final JLabel from = new JLabel("From:");
		final JTextField dateFrom = new JTextField(10);
		final JLabel to = new JLabel("To:");
		final JTextField dateTo = new JTextField(10);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = (String) products.getSelectedItem();

				final Date from = parseDate(dateFrom.getText());
				final Date to = parseDate(dateTo.getText());

				if (from != null && to != null) {
					int blockPallet = Database.instance().blockPallet(text,
							from, to);
					if (blockPallet > 0) {
						errorMessage.setText("Success: blocked a total of "
								+ blockPallet + " pallets.");
					} else {
						errorMessage.setText("Ouch, No pallets were blocked");
					}
				} else {
					errorMessage
							.setText("<html><font color='red'>Bad date. Must be in format yyyy-mm-dd</font></html>");
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
		final JTextField input = new JTextField(20);
		final JButton button = new JButton("Add pallet");
		final JComboBox<String> products = new JComboBox<String>(Database
				.instance().getRecipes());

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();
				final String product = (String) products.getSelectedItem();
				try {
					final int id = Integer.parseInt(text);
					if (Database.instance().palletProduced(id, product)) {
						errorMessage.setText("Success: Pallet " + id
								+ " is now registered.");
					} else {
						errorMessage
								.setText("<html><font color='red'>Ouch, some thing went wrong. Perhaps the id already exists.</font></html>");
					}
				} catch (NumberFormatException ex) {
					errorMessage
							.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
				}
			}
		});

		add(products);
		add(input);
		add(button);

	}

	private Date parseDate(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

		try {

			java.util.Date date = formatter.parse(dateString);
			return new Date(date.getTime());

		} catch (ParseException e) {
			return null;
		}
	}

	public static void main(String[] args) {
		new GUI();
	}

}