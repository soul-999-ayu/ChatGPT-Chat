import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import okhttp3.*;

public class MainBot extends JFrame implements ActionListener, KeyListener{
	
	private static final long serialVersionUID = 1L;
	JLabel label = new JLabel("<html>Your query will be visible here.</html>", SwingConstants.LEFT);
	JLabel label2 = new JLabel("<html>The response from ChatGPT will be shown here.</html>", SwingConstants.LEFT);
	JTextField field = new JTextField("Enter your query");
	JButton button[] = new JButton[2];
	String api;
	JScrollPane scroller = new JScrollPane(label2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789";
	OkHttpClient client = new OkHttpClient();
    String model = "gpt-3.5-turbo";
    String url = "https://api.openai.com/v1/chat/completions";
    String json;
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body;
    Request request;
	OkHttpClient.Builder builder = new OkHttpClient.Builder();
	
	public static String decrypt(String cipherText, int shiftKey){
		String plainText = "";
		for (int i = 0; i < cipherText.length(); i++){
			int charPosition = alphabet.indexOf(cipherText.charAt(i));
			int keyVal = (charPosition - shiftKey) % 63;
			if (keyVal < 0){
				keyVal = alphabet.length() + keyVal;
	        }
			char replaceVal = alphabet.charAt(keyVal);
			plainText += replaceVal;
	    }
		return plainText;
	}
	
	void internetCheck() {
		for(int i=0; i<2; i++) {
			try {
				URL url = new URL("https://www.google.com");
				URLConnection connection = url.openConnection();
				connection.connect();
			}	
			catch (Exception e) {
			JOptionPane.showMessageDialog(null, "No Internet Connection\nPlease connect to interent and try again.", "Information",JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	
	void getResponse(String prompt) {
		json = "{"
	            + "\"model\": \"" + model + "\","
	            + "\"messages\": ["
	            +   "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},"
	            +   "{\"role\": \"user\", \"content\": \"" + prompt + "\"}"
	            + "],"
	            + "\"max_tokens\": 500"
	            + "}";
		body = RequestBody.create(mediaType, json);
		request = new Request.Builder().url(url).post(body).addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + api).build();
		
		try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                responseBody = responseBody.substring(responseBody.indexOf("content")+10, responseBody.indexOf("finish_reason")-17);
                String words[]=responseBody.split("\\s");
                int length=210, count =0;
                for (int i = 0; i < words.length; i++)
                {
                if (words[i].contains("\\n"))
                    count++;
                if (words[i].contains("\n"))
                    count++;
                if(words[i].contains("\\n\\n"))
                	count++;
                }
                if(words.length>49 || count>5) {
                	if((words.length-49)%5==0 || (words.length-49)%5==1 || (words.length-49)%5==2 || (words.length-49)%5==3|| (words.length-49)%5==4) {
                		length = 210+(((words.length-49)/5)*21);
                	}
                	length += (count*12);
                	label2.setPreferredSize(new Dimension(250, length));
                }
                else {
                	label2.setPreferredSize(new Dimension(250, 175));
                }
                
                label2.setText("<html>" + responseBody.replaceAll("<","&lt;").replaceAll(">", "&gt;").replaceAll("\n", " <br> ").replaceAll("\\\\n", " <br> ").replaceAll("\\\\", "").replaceAll("    ", "&emsp;") + "</html>");
                System.out.println(label2.getText());
            } else {
            	String resp = response.body().string();
                if(resp.contains("You exceeded your current quota, please check your plan and billing details.")) {
                	Object[] options = {"Upgrade Plan", "Change API key"};
                	int choose = JOptionPane.showOptionDialog(null, "You exceeded your current quota, please choose an option.", "Insufficient Quota", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                	if (choose == JOptionPane.YES_OPTION){
                		try {
            		        Desktop d=Desktop.getDesktop();
            		        d.browse(new URI("https://platform.openai.com/account/billing/overview"));
            			} catch (Exception e1) {
            				e1.printStackTrace();
            			}
                    }
                	else {
                		this.dispose();
                		new GetKey("new");
                	}
                }
                else if(resp.contains("invalid_api_key")) {
                	Object[] options = {"Change API key"};
                	int choose = JOptionPane.showOptionDialog(null, "Your API key seems to be invalid, please change it.", "Invalid API key", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
                	if(choose == JOptionPane.YES_OPTION) {
                		this.dispose();
                		new GetKey("new");
                	}
                }
                
            }
        } catch (SocketTimeoutException e) {
        	label2.setText("<html>" +  "Timeout error, 60s has elapsed and the response is null." + "</html>");
			label2.setPreferredSize(new Dimension(250, 175));
			scroller.getViewport().setViewPosition(new Point(0,0));
			super.paintAll(getGraphics());
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void search() {
		internetCheck();
		label.setText("<html>" + field.getText().replaceAll("<","&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>");
		label2.setText("<html>" +  "Fetching ChatGPT's response to your query." + "</html>");
		label2.setPreferredSize(new Dimension(250, 175));
		scroller.getViewport().setViewPosition(new Point(0,0));
		super.paintAll(getGraphics());
		getResponse(field.getText());
		field.setText("");
	}
	
	MainBot(){
		internetCheck();
		builder.connectTimeout(60, TimeUnit.SECONDS);
		builder.readTimeout(60, TimeUnit.SECONDS);
		builder.writeTimeout(60, TimeUnit.SECONDS);
		client = builder.build();
		try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\openaikey.txt"))) {
			String line;
			while ((line = reader.readLine()) != null){
				if(!line.equals("")){
					api = decrypt(line, 5);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		TextBubbleBorder line = new TextBubbleBorder(Color.black, 2, 6, 0, true);
		
		label.setFont(new Font("MV Boli", Font.PLAIN, 15));
		label.setForeground(Color.RED);
		label.setOpaque(true);
		label.setBackground(Color.LIGHT_GRAY);
		label.setVisible(true);
		label.setBounds(15, 20, 285, 80);
		label.setBorder(line);
		this.add(label);
		
		label2.setFont(new Font("MV Boli", Font.PLAIN, 12));
		label2.setForeground(Color.BLACK);
		label2.setOpaque(true);
		label2.setBackground(Color.LIGHT_GRAY);
		label2.setVisible(true);
		label2.setPreferredSize(new Dimension(255, 175));
		scroller.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	StringBuffer line = new StringBuffer(label2.getText());
	            String resp = "" + line.substring(line.indexOf("<html>")+6, line.indexOf("</html>"));
	            StringSelection selection = new StringSelection(resp.replaceAll(" <br> ", "\n").replaceAll("<br>&emsp;", "\n    ").replaceAll("&emsp;", "    "));
	            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	            clipboard.setContents(selection, selection);
	            if(!label.getText().contains("(Response copied to clipboard)"))
	            	label.setText("<html>"+label.getText().replace("<html>", "").replace("</html>", "")+"(Response copied to clipboard)"+"</html>");
            }

        });
		scroller.setBounds(15, 110, 285, 180);
		scroller.setBorder(line);
		
		field.setFont(new Font("MV Boli", Font.PLAIN, 14));
		field.setForeground(Color.BLACK);
		field.setVisible(true);
		field.setBounds(15, 305, 238, 30);
		field.setBorder(line);
		this.add(field);
		
		String buttons[] = {"S", "Copy"};
		for(int i=0; i<2; i++) {
			button[i] = new JButton(buttons[i]);
			button[i].setForeground(Color.BLUE);
			button[i].setFont(new Font("MV Boli", Font.PLAIN, 13));
			button[i].setFocusable(false);
			button[i].setVisible(true);
			button[i].addActionListener(this);
			button[i].setBorder(line);
			this.add(button[i]);
			this.getContentPane().add(scroller);
		}
	
		button[0].setBounds(255, 305, 45, 30);;
		
        //FRAME
		this.setTitle("ChatBot");
		this.setIconImage(new ImageIcon("src/bot.jpg").getImage());
      	this.setLocationRelativeTo(null);
      	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      	this.setResizable(false);
      	this.setLayout(null);
      	this.setSize(new Dimension(335,400));
      	this.getContentPane().setBackground(Color.WHITE);
      	this.setVisible(true);
      	field.addKeyListener(this);
      	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		search();
	}
	
	@Override
	public void keyTyped(KeyEvent k) {
	}

	@Override
	public void keyPressed(KeyEvent k) {
		if(k.getKeyCode() == KeyEvent.VK_ENTER && !field.getText().equals("")) {
		      search();
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {	
	}
}
