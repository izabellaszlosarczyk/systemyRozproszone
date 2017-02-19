import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Client {

	public static void main(String[] args) {

		if (args.length != 4) {
			System.out.println("Input parameters: <IP> <port> <number>");
			System.exit(-1);
		}
		int rec = 0;
		try {
			System.out.println("Inicjalizuje");
			String host = args[0];
			int port  = Integer.parseInt(args[1]);
			String number  = args[2];
			Socket socket = new Socket(host, port);
			
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			byte sendline[] = null;
			if (number.equals("1")){
				Byte b = new Byte(args[3]);
				ByteBuffer byteBuffer = ByteBuffer.allocate(1);
				byteBuffer.put(b);
				sendline = byteBuffer.array();
			} 
			else if (number.equals("2") ){
				Short s = new Short(args[3]);
				System.out.println("> " + s);
				System.out.println("> " + Integer.toBinaryString(s));
				ByteBuffer byteBuffer = ByteBuffer.allocate(2);
				byteBuffer.putShort(s);
				sendline = byteBuffer.array();
			}
			else if (number.equals("4") ){
				Integer i = new Integer(args[3]);
				ByteBuffer byteBuffer = ByteBuffer.allocate(4);
				byteBuffer.putInt(i);
				sendline = byteBuffer.array();
			}
			else if (number.equals("8") ){
				Long l = new Long(args[3]);
				ByteBuffer byteBuffer = ByteBuffer.allocate(8);
				byteBuffer.putLong(l);
				sendline = byteBuffer.array();
			}
			System.out.println("Wyslane");
			System.out.println("sent bytes: " + sendline.length );
			System.out.println("sent: " + sendline);
			out.write(sendline);
			
			StringBuilder sb =new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			int len = 0;
			String tmp = "", read;
			while (rec == 0){
				while ((read = br.readLine()) != null){
					System.out.println("otrzymalem cos");
					sb.append(read);
					tmp = tmp + sb.toString();
					int lenTmp = (sb.toString()).length();
					len = len + lenTmp; 
					sb.delete(0,lenTmp);
					rec = 1;
				}
			}
			System.out.println("received bytes: " + tmp.length()); 
			System.out.println("received: " + tmp); 
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
