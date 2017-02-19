import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Input parameters: <IP> <port>");
			System.exit(-1);
		}
		System.out.println("okeeey, do dziela :3");
		try {
			String host = args[0];
			int port  = Integer.parseInt(args[1]);
			Socket socket = new Socket(host, port);
			InputStream in = socket.getInputStream();
			PrintWriter file = null;

			
			StringBuilder sb =new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String read, tittle;
			int i = 0;
			int len = 0;
			System.out.println("zainicjalizowane");
			while((read = br.readLine()) != null) {
				System.out.println("otrzymalem cos");
				if (i == 0){
					System.out.println("ooo! tytul");
					sb.append(read);
					i = 1;
					tittle = sb.toString();
					file = new PrintWriter(tittle);
					len = tittle.length();	
					sb.delete(0, tittle.length());
				}
				else {
					i = i + 1;
					sb.append(read);
					file.write(sb.toString());
					int lenTmp = (sb.toString()).length();
					file.write("\n");
					len = len + lenTmp; 
					sb.delete(0,lenTmp);
				}
				
			}
			
			//sb.append(read);
			//file.write(sb.toString());
			//len = len + (sb.toString()).length(); 
			br.close();
			
			System.out.println("received bytes: " + len); 
			
			file.close();
			socket.close();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
