package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import se.datadosen.component.RiverLayout;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField palletInput;

	public GUI() {
		setLayout(new RiverLayout());
		setTitle("Krusty Kookies Sweden AB");
		setSize(700, 500);

		palletInput = new JTextField(20);
		JButton palletButton = new JButton("Add pallet");

		palletButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("yay");
			}
		});

		add(palletInput);
		add(palletButton);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true); // "this" Frame shows
	}

	public static void main(String[] args) {
		GUI gui = new GUI();
	}

}
