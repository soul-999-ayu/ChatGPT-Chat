import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.border.Border;

public class GetKey extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789";
	String encrypt(String plainText, int shiftKey){
		    String cipherText = "";
		    for (int i = 0; i < plainText.length(); i++){
		        int charPosition = alphabet.indexOf(plainText.charAt(i));
		        int keyVal = (shiftKey + charPosition) % 63;
		        char replaceVal = alphabet.charAt(keyVal);
		        cipherText += replaceVal;
		    }
		    return cipherText;
	}
	
	void appendingText() {
		File file = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\openaikey.txt");
	    if (file.exists()) {
	    	file.delete();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true));
			writer.write(encrypt(field.getText(),5));
		   	} catch (Exception e) {}
		finally {
			try {
				writer.close();
			   	} catch (Exception e) {}
		 	}
	}
	
	JLabel label = new JLabel("Enter your OpenAI API Key:");
	JLabel label2 = new JLabel("Don't have any? Get it");
	JTextField field = new JTextField("sk-XXXXXXXXXXXXXXXXXX");
	JButton button[] = new JButton[2];
	GetKey(String work){
		
		label.setFont(new Font("MV Boli", Font.PLAIN, 17));
		label.setForeground(Color.RED);
		label.setVisible(true);
		label.setBounds(20, 20, 280, 30);
		this.add(label);
		
		label2.setFont(new Font("MV Boli", Font.PLAIN, 13));
		label2.setForeground(Color.BLACK);
		label2.setVisible(true);
		label2.setBounds(20, 82, 280, 30);
		this.add(label2);
		
		field.setFont(new Font("MV Boli", Font.PLAIN, 14));
		field.setForeground(Color.BLACK);
		field.setVisible(true);
		field.setBounds(20, 55, 260, 30);
		this.add(field);
		
		String buttons[] = {"here", "Continue"};
		
		for(int i=0; i<2; i++) {
				button[i] = new JButton(buttons[i]);
				button[i].setForeground(Color.BLUE);
				button[i].setFont(new Font("MV Boli", Font.PLAIN, 13));
				button[i].setFocusable(false);
				button[i].setVisible(true);
				button[i].addActionListener(this);
				this.add(button[i]);
		}
		
		Border emptyBorder = BorderFactory.createEmptyBorder();
		
		button[0].setBorder(emptyBorder);
		button[0].setBackground(Color.WHITE);
		button[0].setBounds(166, 87, 38, 20);
		
		button[1].setForeground(Color.BLACK);
		button[1].setFont(new Font("MV Boli", Font.PLAIN, 15));
		button[1].setBackground(new Color(123,100,255));
		button[1].setBounds(90, 117, 100, 30);
		
		//FRAME
		this.setTitle("API Key");
		this.setLocationRelativeTo(null);
		this.setIconImage(new ImageIcon("src/bot.jpg").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);
		this.setSize(new Dimension(300,200));
		this.getContentPane().setBackground(Color.WHITE);
		this.setVisible(true);
		
		if(work.equals("check")) {
			File file = new File("C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\openaikey.txt");
		    if (file.exists()) {
		    	this.dispose();
		    	new MainBot();
			}
		}
}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==button[0]) {
			try {
		        Desktop d=Desktop.getDesktop();
		        d.browse(new URI("https://youtu.be/XlSAO9Ff2Yk"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource()==button[1]) {
			this.dispose();
			appendingText();
			new MainBot();
		}
	}
	}
