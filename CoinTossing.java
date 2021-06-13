import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Arrays;
public class CoinTossing {
	 public static Socket []socket;
 public static ServerSocket serverSocket;
	 public static InetAddress addr;
public static MessageDigest com;
private static String digits = "0123456789abcdef";

public static String toHex(byte[] data, int length)
{
StringBuffer buf = new StringBuffer();
for (int i = 0; i != length; i++)
{
int v = data[i] & 0xff;
buf.append(digits.charAt(v >> 4));
buf.append(digits.charAt(v & 0xf));
}
return buf.toString();
}

public static String toHex(byte[] data){
	return toHex(data, data.length);
	}

    public static void main (String [] args) {
  int flag=0;
	    System.out.println(args[0]+args[1]+args[2]+args[3]);

	    socket=new Socket[3+1];
	    for(int i=1;i<=3;i++){
if (i==Integer.parseInt(args[0])) continue;
if (i>Integer.parseInt(args[0])){
   	    System.out.println("Client starting...");
    	 try {
        	 addr = InetAddress.getByName("127.0.0.1");
    	 socket[i] = new Socket(addr, Integer.parseInt(args[i]));
    	System.out.println("Client Socket: "+ socket[i]);
    	 }
    	 catch (UnknownHostException e) {
    	 System.err.println("Don't know about host "+ socket[i]);
    	 System.exit(1);
    	 } catch (IOException e) {
    	 System.err.println("Couldn't get I/O for the connection to: " + socket[i]);
    	 System.exit(1);
    	 }
} else {
    	 System.out.println("Server starting...");
    	 try {
		 if (flag==0){
  serverSocket = new ServerSocket();
   serverSocket.bind(new InetSocketAddress(Integer.parseInt(args[Integer.parseInt(args[0])])));
 System.out.println("Server Socket: " + serverSocket+args[Integer.parseInt(args[0])]);
		 flag=1;
		 }
 socket[i] = serverSocket.accept();
 System.out.println("Connection accepted: "+ socket[i]+args[Integer.parseInt(args[0])]);
    	 } catch (IOException e) {
 e.printStackTrace();
    	 System.exit(1);
    	 }
}
	    }

 try {
byte []hashsent=new byte[32];
byte [][]hashrecv=new byte[32][3+1];
    	ObjectOutputStream []outputStream;
    	ObjectInputStream []objectIn;
	outputStream=new ObjectOutputStream[3+1];
	objectIn=new ObjectInputStream[3+1];
byte []preimage=new byte[16];
byte [][]preimagerecv=new byte[16][3+1];
SecureRandom r=new SecureRandom();
			 r.nextBytes(preimage);
 com = MessageDigest.getInstance("SHA1");
hashsent=com.digest(preimage);   
for (int i=1;i<=3;i++){
	if (i==Integer.parseInt(args[0])) continue;
    outputStream[i] = new ObjectOutputStream(socket[i].getOutputStream());
    outputStream[i].writeObject(hashsent);
System.out.println("hashsent to "+i+" "+toHex(hashsent));	
}
for (int i=1;i<=3;i++){
	if (i==Integer.parseInt(args[0])) continue;
objectIn[i] = new ObjectInputStream(socket[i].getInputStream());
hashrecv[i]= (byte[]) objectIn[i].readObject();
System.out.println("hashrecv from " + i+ " "+toHex(hashrecv[i]));	 
}
 //preimage[15]=0x03;   
for (int i=1;i<=3;i++){
	if (i==Integer.parseInt(args[0])) continue;
outputStream[i].writeObject(preimage);
System.out.println("preimage sent: "+toHex(preimage));	 
}


for (int i=1;i<=3;i++){
	if (i==Integer.parseInt(args[0])) 
	{
		preimagerecv[i]=preimage.clone();
		continue;
	}
preimagerecv[i]= (byte[]) objectIn[i].readObject();
System.out.println("preimage recv from "+i+" "+toHex(preimagerecv[i]));	 
System.out.println("hash with preimage recv from "+i+" "+toHex(com.digest(preimagerecv[i])));	 
System.out.println(Arrays.equals(com.digest(preimagerecv[i]),hashrecv[i]));   
}

byte []tmp=new byte[16];
for (int j=0;j<16;j++)tmp[j]=0;

for (int i=1;i<=3;i++){
	for (int j=0;j<16;j++) tmp[j]=(byte) (tmp[j]^preimagerecv[i][j]);
}
System.out.println("Shared String: "+toHex(tmp));	 

 }catch (Exception e) {
		 e.printStackTrace();
	 }

    }
}
