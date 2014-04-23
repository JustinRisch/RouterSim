import java.util.*;
import java.io.PrintWriter; 

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
		System.out.println("reconnect [device name] [device name] [distance]");

		System.out.println("djikstra [from] [to] - finds the shortest route to a device from a device.");
		System.out.println("routetable - Prints out a Route Table for all devices.");
		//assign the value of s.nextLine's lower case version. if it says "exit", stop executing. 
		while((input=s.nextLine().toLowerCase())!="exit"){
			success=false;
			//proper syntax to add a connection: "connect fromthisdevice tothisdevice distance" 
			if (input.startsWith("reconnect") && input.split(" ").length>3){
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
					success=(Network.get(index1).reconnect(Network.get(index2), distance) && Network.get(index2).reconnect(Network.get(index1), distance));
				} 
			} 
			else if (input.startsWith("connect") && input.split(" ").length>3){
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
			} else if (input.startsWith("send") && input.split(" ").length==5){ 
				String[] temp = input.split(" "); 
				String route = FindDistance(lookUpDevice(temp[3]), lookUpDevice(temp[4]));
				if (route.contains("Infinity")){
					System.out.println("There exists no path to this device.");
					success=null;
				} else {
					System.out.println(route);
					device.sendPacket(Integer.parseInt(temp[1]), temp[2], lookUpDevice(temp[3]), lookUpDevice(temp[4])); 
				}
			} else if (input.startsWith("routetable")){ 
				try { 
					PrintWriter writer = new PrintWriter("routertable.txt", "UTF-8");
					for (device d : Network){
						System.out.println("------"+d.name+"------");
						writer.println("------"+d.name+"------");
						for (device e : Network) {
							System.out.println(FindDistance(d,e));
							writer.println(FindDistance(d,e));
						}
					}
					writer.close();
					success=null; 
				}catch(Exception e) {
					System.out.print("Failed to write route table to file.");
				}
			} else if (input.startsWith("add") && input.split(" ").length>=2){
				for (int i =1; i<input.split(" ").length;i++){
					name = input.split(" ")[i].toLowerCase(); 
					//if it starts with an R, add a router. else if it starts with a C, add a client. 
					if (name.startsWith("r")) {
						Network.add(new Router(name));
						Network.get(Network.size()-1).index=Network.size()-1;
						System.out.println(Network.get(Network.size()-1));
						success=true;
					}else if (name.startsWith("c")){
						Network.add(new Client(name));
						Network.get(Network.size()-1).index=Network.size()-1;
						System.out.println(Network.get(Network.size()-1));
						success=true;
					}else {
						success=false;
						break;
					}
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

				System.out.println(FindDistance(lookUpDevice(start), lookUpDevice(finish))); 
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
	
	public static int lookUpIndex(String A){
		int i = -1; 
		for (device x : Network)
			if (x.name.equals(A))
				i = x.index;
		return i; 
	}
	public static device lookUpDevice(int A){

		device i = null;
		for (device x : Network)
			if (x.index==A)
				i = x;
		return i; 
	}
	public static int lookUpIndex(device A){
		int i = -1; 
		for (device x : Network)
			if (x.equals(A))
				i = x.index;
		return i; 
	}
	public static device lookUpDevice(String A){

		device i = null;
		for (device x : Network)
			if (x.name.equals(A))
				i = x;
		return i; 
	}
	public static Double[][] convert(ArrayList<device> Network) {
		//creating a square array where each variable 
		Double[][] temp = new Double[Network.size()][Network.size()];  
		for (int i = 0; i< Network.size(); i++) 						//Setting defaults
			for (int j =0; j<Network.size(); j++)
				temp[i][j]=Double.POSITIVE_INFINITY; 

		for (int i = 0; i< temp.length; i++) 				// for every device in the network
			for (int j =0; j<Network.get(i).connections.size(); j++){	// for every device that device is connected to 
				temp[i][Network.get(i).connections.get(j).index]=Network.get(i).distances.get(j);  // store the distance between them 
			}
		return temp; 
	}

	public static String FindDistance(device start, device target){
		if (start.equals(target))
			return "Self-0";
		int i1 = lookUpIndex(start), i2 = lookUpIndex(target);

		String[][] path = new String[Network.size()][Network.size()];
		for(int j=0; j<path.length; j++) 
			for(int x=0; x<path[1].length; x++) 
				path[j][x]=lookUpDevice(j).name;
		Double[][] net = convert(Network);		

		for(int j=0; j<net.length; j++) {
			for(int x=0; x<net.length; x++) {
				double testnum=net[i1][j]+net[j][x];
				if (net[i1][j] > 0 && net[j][x] > 0){
					if (testnum< net[i1][x] || net[i1][x]<0) {
						net[i1][x]=testnum;
						net[x][i1]=testnum;
						path[i1][x]=path[i1][j]+"-"+net[i1][j]+" " +path[j][x]+"-"+net[j][x];	
					}
				}
			}
		}
		return path[i1][i2]+" "+target.name+":"+net[i1][i2];
	}
}
abstract class device  {
	public String name; 
	public int index; 
	public ArrayList<Double> distances = new ArrayList<Double>(0);
	public ArrayList<device> connections = new ArrayList<device>(0);
	public boolean MakeConnection(device X, Double Y){ return false;}//to be overloaded. 
	public boolean reconnect(device X, Double Y){return false;}
	
	//Simulates the sending of a packet. 
	public static void sendPacket(int PacketSize, String message, device source, device Target){
		byte[] bytes = message.getBytes(); 
		ArrayList<byte[]> packets = new ArrayList<byte[]>();	
		//packing packets
		for (int i = 0; i < bytes.length; i+=PacketSize)
		{	
			packets.add(message.substring(i, Math.min(i+PacketSize, message.length())).getBytes());
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
		String re="_"+name+":"+index+"_"; 
		for (int i = 0; i < connections.size(); i++)
		{
			re +="\n"+connections.get(i).name + "-" +  distances.get(i);
		}
		return re; 
	}
}
class Client extends device  {
	//constructors
	public Client() {}
	public Client(String X) {name = X; }
	//overloading methods 
	public boolean reconnect(device X, Double Y){return MakeConnection(X,Y);}

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
	public boolean reconnect(device X, Double Y){
		this.removeConnection(X.name);
		this.MakeConnection(X, Y);
		return true; 
	}
	public boolean removeConnection(String name){
		boolean re = false; 
		device a = RouterSim.lookUpDevice(name);
		for (int i = 0; i<connections.size(); i++)
			if (connections.get(i).equals(a)) {
				connections.remove(i);
				distances.remove(i);
				re = true; 
			}
		return re;
	}
}
