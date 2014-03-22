package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import database.Database;
import se.datadosen.component.RiverLayout;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GUI() {
		setLayout(new RiverLayout());
		setTitle("Krusty Kookies Sweden AB");
		setSize(700, 500);


		final JLabel errorMessage = new JLabel();
		
		final JTextField palletInput = new JTextField(20);
		final JButton palletButton = new JButton("Add pallet");

		palletInput = new JTextField(20);
		blockPallet = new JTextField(20);
		
		final JButton palletButton = new JButton("Add pallet");
		final JButton blockPalletButton = new JButton("Block pallet");
		
		palletButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final String text = palletInput.getText();

				try {
					final int id = Integer.parseInt(text);
					Database.instance().palletProduced(id, "");
				} catch (NumberFormatException ex) {
					errorMessage.setText("incorrect input, id must be an integer.");
				}
			}
		});

		blockPalletButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("yay2");
			}
		});
		
		add(palletInput);
		add(palletButton);
		
		add(blockPallet, RiverLayout.LINE_BREAK);
		add(blockPalletButton);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true); // "this" Frame shows
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
	}

}
