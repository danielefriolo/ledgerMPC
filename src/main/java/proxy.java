import Blockchain.*;
import ethereum.EthereumBlockchain;
import fabcar.Hyperledger;

import java.io.*;
import java.net.*;
import java.net.StandardSocketOptions;

public class proxy implements Runnable {
 public static Socket clientSocket=null;
 //public static BufferedIReader in=null,stdIn=null;
 public static BufferedInputStream in=null;
 public static PrintWriter out=null;
 public static InetAddress addr;
 public static Socket socket=null;
 public static String str;
 public static byte[] str2;
 public static int read_counter=0,write_counter=0;
 public static Integer P1,P2;
 public static byte[] SID;
 public static Blockchain dof;
/* public static DataOnFile dof;
    public static  EthereumBlockchain dof;
 public static Hyperledger dof;*/

 static {
  System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
 }
public static void main(String[] args) throws IOException {

// Server
//
//
 System.out.println("args: " + args[0]+"v"+args[1]+ "v"+args[2]+"v"+args[3]+"v"+args[4]+"v"+args[5]+"v"+args[6]+"v"+args[7]);
SID=args[1].getBytes();
P1=Integer.valueOf(args[2]);
P2=Integer.valueOf(args[3]);

 if (args[0].equals("C")){

 System.out.println("Server starting..."+args[2]);
 try {
 ServerSocket serverSocket = new ServerSocket();
	 serverSocket.setOption(StandardSocketOptions.SO_REUSEPORT, true);
                serverSocket.bind(new InetSocketAddress(Integer.valueOf(args[4])));
 System.out.println("Server Socket: " + serverSocket+args[2]);
 clientSocket = serverSocket.accept();
 System.out.println("Connection accepted: "+ clientSocket+args[2]);
 }
 catch (IOException e) {
 System.err.println("Accept failed"+args[2]);
 System.exit(1);
 }
 }
 // end server
if (args[0].equals("S")) {
 System.out.println("Client starting...");
 addr = InetAddress.getByName(null);
 try {
 clientSocket = new Socket(addr, Integer.valueOf(args[4]));
System.out.println("Client Socket: "+ clientSocket);
 }
 catch (UnknownHostException e) {
 System.err.println("Don’t know about host "+ clientSocket);
 System.exit(1);
 } catch (IOException e) {
 System.err.println("Couldn’t get I/O for the connection to: " + clientSocket);
 System.exit(1);
 }
 }

//InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
DataInputStream isr = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

//in = new BufferedReader(isr);
in = new BufferedInputStream(isr);
//OutputStreamWriter osw = new OutputStreamWriter(clientSocket.getOutputStream());
DataOutputStream osw = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
//BufferedWriter bw = new BufferedWriter(osw);
BufferedOutputStream bw = new BufferedOutputStream(osw);
//out = new PrintWriter(bw, true);
switch (args[6]) {
    case "ETH":
        dof = new EthereumBlockchain();
        break;
    case "HLF":
        dof = new Hyperledger();
        break;
    case "DOF":
        dof = new DataOnFile();
        break;
    default:
        System.out.println("Wrong blockchain.");
}

try {
	dof.setUp(args[5], SID, P1, P2);
	System.out.println(SID.toString());
} catch (Exception e1) {
	e1.printStackTrace();
}

 proxy obj = new proxy();
 Thread thread = new Thread(obj);
 thread.start();

 //stdIn = new BufferedReader(new InputStreamReader(System.in));
while(true){ // read from blockchain and write to socket

read_counter++;
byte[] msg = null;
 //System.out.println("Debug before read from blockchain:\n "+args[2]+" " +str);
try {
	msg = dof.readData(read_counter, args[7].equals("1"));
	//byte[][] readData = readBroadcastData(read_counter, partyNum, false);
} catch (Exception e) {
	e.printStackTrace();
} 
//str=new String(msg);
 //System.out.println("Debug read from blockchain:\n "+args[2]+" " +str);
//la seguente riga legge da stdIn invece che da blockchain
//str=stdIn.readLine();
try {
 //System.out.println("Debug before out"+args[2]);
 //out.print(str);
 bw.write(msg,0,msg.length);
 //System.out.println("Debug before flush"+args[2]);
//out.flush();
bw.flush();
 //System.out.println("Debug after writing to Alice");
} catch (Exception e) {
	e.printStackTrace();
} 

msg=null;
}
}

 public void run() {
	     byte[] buffer;
	     int lenread,i;
	     buffer= new byte[8192*8];
 while (true){
 // read from socket and write to blockchain
 try{
// System.out.println("Debug before read from socket:\n " + P1+ " "+str2);
	lenread=in.read(buffer,0,8192*8);
	if (lenread==-1) System.exit(1);
	str2=new byte[lenread];
	for (i=0;i<lenread;i++) str2[i]= buffer[i];
 //System.out.println("Debug read from socket " + lenread+ "\n"+ P1+ " "+str2);

 write_counter++;
  dof.writeData(str2, write_counter);
 
//System.out.print("Write to blockchain: " + SID.toString()+" "+P1+" " +P2+ " " +str2);
 } catch (IOException e) {
 System.err.println("Couldn’t get I/O for the connection to (2): " + P1);
 System.exit(1);
 } catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
str2=null;
 }
  }
} 

