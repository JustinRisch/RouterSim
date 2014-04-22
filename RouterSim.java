import java.util.*;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/* We will implement a system where there are two types of net devices-- routers and clients. Clients may only connect to one router, routers 
 * may connect to any number of any devices. 
 * */
public class RouterSim {
	public static ArrayList<device> Network = new ArrayList<device>(0);
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String input, from, to, name;
		Boolean success; 
		Double distance; 
		System.out.println("Welcome to RouterSim.\nPlease use the following commands:\nadd [device name] - Adds a device to the network. It must start with R or C to indicate type of device.");
		System.out.println("connect [device name] [device name] [distance]");
		System.out.println("djikstra");
		//assign the value of s.nextLine's lower case version. if it says "exit", stop executing. 
		while((input=s.nextLine().toLowerCase())!="exit"){
			success=false;
			//proper syntax to add a connection: "connect fromthisdevice tothisdevice distance" 
			if (input.startsWith("connect") && input.split(" ").length>3){
				from = input.split(" ")[1];
				to = input.split(" ")[2];
				int index1=0, index2=0, numfound=0; 
				distance = Double.parseDouble(input.split(" ")[3]);

				for (int i = 0; i<Network.size(); i++)
				{
					if (Network.get(i).name.equalsIgnoreCase(from)) {
						index1=i; 
						numfound++;
					} else if (Network.get(i).name.equalsIgnoreCase(to)) {
						index2=i; 
						numfound++;
					}
				}	
				if (numfound==2){
					success=(Network.get(index1).MakeConnection(Network.get(index2), distance) && Network.get(index2).MakeConnection(Network.get(index1), distance));
				} 

				//proper syntax to add a device: "add devicename" 
			} else if (input.startsWith("add") && input.split(" ").length==2){
				name = input.split(" ")[1].toLowerCase(); 

				//if it starts with an R, add a router. else if it starts with a C, add a client. 
				if (name.startsWith("r")) {
					Network.add(new Router(name));
					success=true;
				}else if (name.startsWith("c")){
					Network.add(new Client(name));
					success=true;
				}
				// shows each device, with it's connections listed below it in form of device - distance
			} else if (input.startsWith("showall")){
				if (Network.size()>0){
					for (device d : Network){
						System.out.println("------"+d.name+"------");
						System.out.println(d); 
					}
					success=null; 
				} else 
					success = false; 
			} else if (input.startsWith("djikstra") && input.split(" ").length>=3){
				String start = input.split(" ")[1];
				String finish = input.split(" ")[2];
				System.out.println(djikstra(start, finish, start, 0.0)); 
				success=true;
			}
			//if it was successful. Null to not display output. 
			if (success==null)
				continue; 
			else if (success)
				System.out.println("Success.");
			else if (!success)
				System.out.println("Last action failed. Check syntax of command. Be sure to add the device before attempting to connect to it or show them."); 
		}
		s.close();
		System.out.println("Simulation Ceased.");
	}
	public static String djikstra(String start, String target, String path, Double distance){
		device A=new Router(), B = new Router(); 
		int numfound = 0; 
		//this for loop finds our starting point and destination
		for (device x : Network)
			if (x.name.equals(start)){ 
				A = x; 
				numfound++;
			}else if (x.name.equals (target)) {
				B = x;
				numfound++;
			}
		if (numfound!=2 || (B.name == null || A.name==null))
			return "Error finding starting point and destination"; 
		
		
		return A.name + " "+ B.name;
	}
}
abstract class device  {
	public String name; 
	public ArrayList<Double> distances = new ArrayList<Double>(0);
	public ArrayList<device> connections = new ArrayList<device>(0);
	public boolean MakeConnection(device X, Double Y){ return false;}//to be overloaded. 

	//Simulates the sending of a packet. 
	public void sendPacket(int PacketSize, String message, device Target){
		byte[] bytes = message.getBytes(), temp = new byte[PacketSize]; 
		ArrayList<byte[]> packets = new ArrayList<byte[]>();	
		//packing packets
		for (int i = 0; i < bytes.length; i++)
		{	
			//wraps from 0->packetsize (mod function)
			temp[i%PacketSize]=bytes[i];
			//Every packetsize number of bytes, it will add that new "packet" of bytes to the arraylist. 
			if (i%PacketSize==0 || i ==(bytes.length-1)){
				packets.add(temp);
			}
		}
		//sending packets 
		for (int i = 0; i < packets.size(); i++)
		{
			Target.showPacket(packets.get(i));
		}

	}
	//Simulates the displaying of a packet
	public void showPacket(byte[] input){
		try {
			System.out.println(new String(input,"UTF-8"));
		} catch (Exception e) {
			System.out.println("Packet corrupted.");
		}
	}
	public String toString(){
		String re=""; 
		for (int i = 0; i < connections.size(); i++)
		{
			re +=connections.get(i).name + "-" +  distances.get(i)+"\n";
		}
		return re; 
	}
}
class Client extends device  {
	//constructors
	public Client() {}
	public Client(String X) {name = X; }
	//overloading methods 
	public boolean MakeConnection(device X, Double Y){
		//now we remove the previous connection as only one is allowed at a time. 
		if (connections.size()>0){
			distances.remove(0);
			connections.remove(0);
		}
		distances.add(Y);
		connections.add(X);
		return true; 
	}
} 
class Router extends device {
	//constructors
	public Router(){}
	public Router(String X){
		this.name = X;
	}
	//overloading methods
	public boolean MakeConnection(device X, Double Y){
		distances.add(Y);
		connections.add(X);
		return true; 
	}
	public boolean removeConnection(String name){
		boolean re = false; 
		for (int i = 0; i<connections.size(); i++)
			if (connections.get(i).equals(name)) {
				connections.remove(i);
				distances.remove(i);
				re = true; 
			}
		return re;
	}
}
