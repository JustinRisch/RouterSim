import java.util.*;

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
		//assign the value of s.nextLine's lower case version. if it says "exit", stop executing. 
		while((input=s.nextLine().toLowerCase())!="exit"){
			success=false;
			//proper syntax to add a connection: "connect fromthisdevice tothisdevice distance" 
			if (input.startsWith("connect") && input.split(" ").length>3){
				from = input.split(" ")[1];
				to = input.split(" ")[2];
				distance = Double.parseDouble(input.split(" ")[3]);
				for (device N : Network)
					if (N.name.equals(from))
						success=N.MakeConnection(to, distance);
					else if (N.name.equals(to))
						success=N.MakeConnection(from, distance);
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
		//finding the starting device
		int i=0; 
		for (i = 0; i < Network.size(); i++)
			if (Network.get(i).name.equals(start))
				break;

		//searching all of it's connections, if it has any. 
		if (Network.get(i).connections.size()>0){			
			for (int j = 0; j < Network.get(i).connections.size(); j++){
				if (Network.get(i).name == target) 
					return path +" "+ distance+"\n"; 
				else 
					return djikstra(Network.get(i).name, target, path+Network.get(i).name, distance+Network.get(i).distances.get(j));
			} 
		}
		return ""; 


	}
}
abstract class device  {
	public String name = "Default Name"; 
	public ArrayList<Double> distances = new ArrayList<Double>(0);
	public ArrayList<String> connections = new ArrayList<String>(0);
	public boolean MakeConnection(String X, Double Y){ return false;}//to be overloaded. 
	public String findRoute(String X){ 
		return "Dijstra's here.";
	}
	public String toString(){
		String re=""; 
		for (int i = 0; i < connections.size(); i++)
		{
			re +=connections.get(i) + "-" +  distances.get(i)+"\n";
		}
		return re; 
	}
}
class Client extends device  {
	//constructors
	public Client() {}
	public Client(String X) {
		name = X; 
	}
	//overloading methods 
	public boolean MakeConnection(String X, Double Y){
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
	public boolean MakeConnection(String X, Double Y){
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
