package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
		final JTextField input = new JTextField(20);
		final JButton button = new JButton("Block pallet");

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

		add(input, RiverLayout.LINE_BREAK);
		add(button);
	}

	private void addProducePallet(final JLabel errorMessage) {
		final JTextField input = new JTextField(20);
		final JButton button = new JButton("Add pallet");

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

		add(input);
		add(button);

	}

	public static void main(String[] args) {
		GUI gui = new GUI();
	}

}