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

				//if it contains an R, add a router. else if it contains a C, add a client. 
				if (name.contains("r")) {
					Network.add(new Router(name));
					success=true;
				}else if (name.contains("c")){
					Network.add(new Client(name));
					success=true;
				}
			}
			//if it was successful. 
			if (success)
				System.out.println("Success.");
			else 
				System.out.println("Last action failed. Check syntax of command."); 
		}
		s.close();
		System.out.println("Simulation Ceased.");
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
}
class Client extends device  {
	//constructors
	public Client() {}
	public Client(String X) {
		name = X; 
	}
	//overloading methods 
	public boolean MakeConnection(String X, Double Y){
		distances.add(Y);
		connections.add(X);
		//now we remove the previous connection as only one is allowed at a time. 
		distances.remove(0);
		connections.remove(0);
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

}
