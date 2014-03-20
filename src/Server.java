import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;

@SuppressWarnings("serial")
public class Server extends JFrame {
	
	int PORT = 7657;
	public static final String pathTowebsitefolders = "./";
	public static final String nameoftxt = "index.txt";

	static JTextPane textBox = new JTextPane();

	
	String wName = "";


	public static void main(String[] args) throws BadLocationException {
		new Server();
		
	}

	public static String getDocument(String name){
		FileSearch fileSearch = new FileSearch();

		fileSearch.searchDirectory(new File(pathTowebsitefolders + name), nameoftxt);

		int count = fileSearch.getResult().size();

		if(count == 0){

			return "Error: 404 | Page Not Found!\nSubmit a blank adress to see avaible pages.\n";

		}else if(count == 1){
			String content = FileSearch.readFile(pathTowebsitefolders + name + "/" + nameoftxt, Charset.defaultCharset());
			if(content == null) return "Error: 403 | Web page missing " + nameoftxt;
			return content;
		}else{
			String multiStr = "";
			multiStr = ("\nFound " + count + " pages!\n");
			for (String matched : fileSearch.getResult()){
				multiStr += (">  " + matched.replaceAll(pathTowebsitefolders/*new File(pathTowebsitefolders).getAbsolutePath()*/ + "/", "").replaceAll(nameoftxt, "").replaceAll("/", "") + "\n");
			}

			return multiStr;
		}
	}

	@SuppressWarnings("resource")
	public Server() throws BadLocationException {
		setLayout(new BorderLayout());
		textBox.setEditable(false);
		textBox.setBackground(Color.black);
		textBox.setForeground(Color.white);
		textBox.setFont(new Font("Courier",Font.PLAIN,16));

		//auto scroll down
		DefaultCaret caret = (DefaultCaret)textBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//----------------
		add(new JScrollPane(textBox), BorderLayout.CENTER);

		setTitle("Web server");
		setSize(1000,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			ServerSocket sSocket = new ServerSocket(PORT, 1000000, InetAddress.getByName("0.0.0.0"));
			

			appendString("Server started at: " + new Date() + "\n", textBox,"white");
			while(true) {
				Socket socket = sSocket.accept();
				ClientThread cT = new ClientThread(socket);
				
				new Thread(cT).start();
				
			}
		} catch(IOException exception) {
			System.out.println("Error: " + exception);
		}
	}

	class ClientThread implements Runnable {
		Socket threadSocket;

		public ClientThread(Socket socket) {
			threadSocket = socket;
		}


		public void run(){
			try {
				PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

				output.println("You have connected to the server at: " + new Date());

				//Gives the client the homepage on startup
				output.println(Server.getDocument("home"));

				appendString(threadSocket.getInetAddress().getHostAddress() + " has connected!"+" "+new Date()+"\n",textBox,"magneta"); 
				
				
				

			
				while(true) {
					String chatInput = input.readLine();
					
					

					
					String what = "is searching for";
					System.out.println(chatInput);

					

					
					if(chatInput == null){ 



						appendString(threadSocket.getInetAddress().getHostAddress() + " disconnected. "+new Date()+"\n",textBox,"yellow");
						return;


					}

					if(chatInput.isEmpty()){
						what = "is viewing a list of pages";
						
						
						
					}
					if(chatInput.startsWith("/websitename ")){
						System.out.println("Found /websitename command");
						
						

						
						
						wName = chatInput.replaceAll("/websitename ", "");
						
						
						createFolder(wName);
						
						output.println("ok");
						chatInput = "post";
						
						appendString(threadSocket.getInetAddress().getHostAddress() + " is publishing a post called: "+wName+" "+new Date()+"\n",textBox,"magneta");

						
						
						
						

					}
					
					
					
					else if(chatInput.startsWith("/website")){
						System.out.println("Found /website command");
						
						
						
						String webText = chatInput.replaceAll("/website", "");
						
						writeToFile(wName+"/"+"index.txt",webText);
						
						
						output.println("ok");
						
						chatInput = wName;
						
						
						
						
	
					}
					
					


					System.out.println("Reached end of loop)");
					output.println("/clear/");
					output.println(Server.getDocument(chatInput));

					appendString(threadSocket.getInetAddress().getHostAddress() +" "+what+" "+'"'+chatInput+'"'+" "+new Date()+"\n",textBox,"white");
					if (Server.getDocument(chatInput).contains("404") && !chatInput.contains("post")){

						appendString("[Could not find requested page] \n",textBox,"red");

					}
					else if (Server.getDocument(chatInput).contains("403")){

						appendString("[Page empty] \n",textBox,"red");
					}
					else if (chatInput.contains("post")){

						appendString("[Publishing post...] \n",textBox,"yellow");
					}else{

						appendString("[Page found] \n",textBox,"green");
					}


				}
			} catch(IOException | BadLocationException exception) {
				System.out.println("Error: " + exception);
			}
		}

	}
	
	public String ReadBigStringIn(BufferedReader buffIn) {
        StringBuilder b = new StringBuilder();
        

        try {
            String line = buffIn.readLine();
            while (line != null) {
                b.append(" "+line);
                line = buffIn.readLine();
            }
        }
        catch(IOException e){};

        return b.toString();
}

	public void appendString(String str,JTextPane pane,String color) throws BadLocationException
	{

		int i = 0;

		javax.swing.text.Style[] a = null;
		a = new javax.swing.text.Style[7];

		javax.swing.text.Style white = textBox.addStyle("white", null);
		StyleConstants.setForeground(white, Color.white);

		javax.swing.text.Style blue = textBox.addStyle("blue", null);
		StyleConstants.setForeground(blue, Color.blue);

		javax.swing.text.Style red = textBox.addStyle("red", null);
		StyleConstants.setForeground(red, Color.red);

		javax.swing.text.Style magneta = textBox.addStyle("magneta", null);
		StyleConstants.setForeground(magneta, Color.magenta);

		javax.swing.text.Style green = textBox.addStyle("green", null);
		StyleConstants.setForeground(green, Color.green);

		javax.swing.text.Style yellow = textBox.addStyle("yellow", null);
		StyleConstants.setForeground(yellow, Color.yellow);

		a[0] = white;
		a[2] = blue;
		a[3] = red;
		a[4] = magneta;
		a[5] = green;
		a[6] = yellow;

		if(color.equals("white")){i = 0;}
		else if(color.equals("blue")){i = 2;}
		else if(color.equals("red")){i = 3;}
		else if(color.equals("magneta")){i = 4;}
		else if(color.equals("green")){i = 5;}
		else if(color.equals("yellow")){i = 6;}
		else{i = 0;}



		StyledDocument document = (StyledDocument) pane.getDocument();
		document.insertString(document.getLength(), str, a[i]);

		// ^ or your style attribute  
	}
	
	public static void createFolder(String name){
		File dir = new File(name);
    	dir.mkdir();
	}
	
	public static void writeToFile(String fileName,String text) throws IOException{
		 
		
		
		
        try {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            	
        		
        		
            	
        		
        		
            	
            	
            	out.append(text);
                
            	
            
            out.close();
        } catch (IOException e) {}
    
}

}
