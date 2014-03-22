package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		add(errorMessage, RiverLayout.LINE_BREAK);
		setVisible(true);
	}

	private void addBlockPallet(final JLabel errorMessage) {
		final JLabel inputId = new JLabel("ID");
		final JTextField input = new JTextField(5);
		final JButton button = new JButton("Block pallet");

		final JLabel from = new JLabel("Från");
		final JTextField dateFrom = new JTextField(10);
		final JLabel to = new JLabel("Till");
		final JTextField dateTo = new JTextField(10);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();

				try {
					final int id = Integer.parseInt(text);
					if (Database.instance().palletProduced(id, "")) {
						errorMessage.setText("Success: Pallet " + id
								+ " is now blocked!");
					} else {
						errorMessage
								.setText("<html><font color='red'>Ouch, some thing went wrong. Perhaps the id doesn't exists.</font></html>");
					}
				} catch (NumberFormatException ex) {
					errorMessage
							.setText("<html><font color='red'>Incorrect input, id must be an integer.</font></html>");
				}
			}
		});

		add(from, RiverLayout.LINE_BREAK);
		add(dateFrom);
		add(to);
		add(dateTo);
		add(inputId);
		add(input);
		add(button);
	}

	private void addProducePallet(final JLabel errorMessage) {
		final JTextField input = new JTextField(20);
		final JButton button = new JButton("Add pallet");
		final JComboBox products = new JComboBox(Database.instance().getRecipes());

		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = input.getText();

				try {
					final int id = Integer.parseInt(text);
					if (Database.instance().palletProduced(id, "")) {
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

	public static void main(String[] args) {
		GUI gui = new GUI();
	}

}