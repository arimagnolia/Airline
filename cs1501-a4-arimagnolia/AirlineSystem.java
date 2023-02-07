import java.io.*;
import java.util.*;

final public class AirlineSystem implements AirlineInterface {
  private String [] cityNames = null;
  private Digraph G = null;
  private static final int INFINITY = Integer.MAX_VALUE;
	private int v = 0;
  
  public boolean loadRoutes(String fileName) {
    try {
			Scanner fileScan = new Scanner(new FileInputStream(fileName));
			v = Integer.parseInt(fileScan.nextLine());
			G = new Digraph(v);
			cityNames = new String[v];
			for (int i = 0; i < v; i++) {
				cityNames[i] = fileScan.nextLine();
			}
			//System.out.println("***CITYNAMES SUCCESSFULLY POPULATED****");
			while (fileScan.hasNext()) {
				int from = fileScan.nextInt();
				int to = fileScan.nextInt();
				int distance = fileScan.nextInt();
				double price = fileScan.nextDouble();
				//System.out.println("******READ IN LINE SUCCESSFULLY*****");
				String from1 = cityNames[from - 1];
				String to1 = cityNames[to - 1];
				//fileScan.nextLine();
				addRoute(from1, to1, distance, price);
			}
			fileScan.close();
			return true;
		} catch (Exception e) {
			return false;
		}
  }

  public Set<String> retrieveCityNames() {
    Set<String> set = new HashSet<String>();
		for (int i = 0; i < cityNames.length; i++) {
			if(cityNames[i] == null) {
				return set;
			}
			set.add(cityNames[i]);
		}
		return set;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException {
    Set<Route> route = new HashSet<Route>();
		int cityIndex = -1;
		//Route r;
		for (int i = 0; i < cityNames.length; i++) {
			if (cityNames[i] != null && cityNames[i].equalsIgnoreCase(city)) {
				cityIndex = i; 
				for (Route e : G.adj(cityIndex)) { //G.adj(i);
					route.add(e);
				}
			}
		}
		return route;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException {
    ArrayList<String> hops = new ArrayList<String>();
		Set<ArrayList<String>> setHops = new HashSet<>();
		int sourceIndex = -1;
		int destinationIndex = -1;
		for (int i = 0; i < cityNames.length; i++) {
			if (cityNames[i].equalsIgnoreCase(source)) { 
				sourceIndex = i;
			}
		}
		for (int j = 0; j < cityNames.length; j++) {
			if (cityNames[j].equalsIgnoreCase(destination)) { 
				destinationIndex = j;
			}
		}
		//if either are -1, throw a new CNFE(some exception [should be a string])
		if(sourceIndex == -1 || destinationIndex == -1)  { throw new CityNotFoundException("City not found"); }
		G.bfs(sourceIndex);
		if (!G.marked[destinationIndex]) {
			return setHops; 
		} else {
			Stack<Integer> path = new Stack<>();
			for (int x = destinationIndex; x != sourceIndex; x = G.edgeTo[x]) {
				path.push(x);
			}
			path.push(sourceIndex);
			while (!path.empty()) {
				hops.add(cityNames[path.pop()]);
			}
			setHops.add(hops);
			return setHops;
		}    
 
  // }
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {
    ArrayList<Route> shortDistance = new ArrayList<Route>();
		Set<ArrayList<Route>> setOfDistances = new HashSet<>();
		int sourceIndex = -1;
		int destinationIndex = -1;
		for (int i = 0; i < cityNames.length; i++) {
			if (cityNames[i].equalsIgnoreCase(source))
				sourceIndex = i;
		}
		for (int j = 0; j < cityNames.length; j++) {
			if (cityNames[j].equalsIgnoreCase(destination))
				destinationIndex = j;
		}
		if(sourceIndex == -1 || destinationIndex == -1)  { throw new CityNotFoundException(""); }
		G.dijkstras(sourceIndex);
		if (!G.marked[destinationIndex]) {
			return setOfDistances;
		} else {
			Stack<Integer> path = new Stack<>();
			for (int x = destinationIndex; x != sourceIndex; x = G.edgeTo[x]) {
				path.push(x);
			}
			path.push(sourceIndex);
			int g = path.pop();
			while (!path.empty()) {
				//pop from stack, then construct a new route?
				int v = path.pop();
				Route s = G.findEdge(g, v);
				shortDistance.add(s);
				g = v;
				}
			}
		setOfDistances.add(shortDistance);
		return setOfDistances;
    
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException {
    ArrayList<Route> transitList = new ArrayList<Route>();
		Set<ArrayList<Route>> setOfTransits = new HashSet<>();
		int sourceIndex = -1;
		int destinationIndex = -1;
		int transitIndex = -1;
		for (int i = 0; i < cityNames.length; i++) {
			if (cityNames[i].equalsIgnoreCase(source))
				sourceIndex = i;
		}
		for (int j = 0; j < cityNames.length; j++) {
			if (cityNames[j].equalsIgnoreCase(destination))
				destinationIndex = j;
		}
		for (int k = 0; k < cityNames.length; k++) {
			if(cityNames[k].equalsIgnoreCase(transit))
			transitIndex = k;
		}
		//I need to throw an exception before calling dijkstras
		if(sourceIndex == -1 || destinationIndex == -1 || transitIndex == -1) { throw new CityNotFoundException(""); }
		G.dijkstras(sourceIndex);
		if (!G.marked[transitIndex]) {
			return setOfTransits;
		} else {
		Stack<Integer> path = new Stack<>();
			for (int x = transitIndex; x != sourceIndex; x = G.edgeTo[x]) {
				path.push(x);
			}
			path.push(sourceIndex);
			int g = path.pop();
			while (!path.empty()) {
				//pop from stack, then construct a new route?
				int v = path.pop();
				Route s = G.findEdge(g, v);
				transitList.add(s);
				g = v;
				}
			}
			G.dijkstras(transitIndex);
			if (!G.marked[destinationIndex]) {
				return setOfTransits;
			} else {
			Stack<Integer> path2 = new Stack<>();
			for (int y = destinationIndex; y != transitIndex; y = G.edgeTo[y]) {
				path2.push(y);
			}
			path2.push(transitIndex);
			int g = path2.pop(); //remove
			while (!path2.empty()) {
				int v = path2.pop();
				Route s = G.findEdge(g, v);
				transitList.add(s);
				g = v;
			}
			}
		//}
		setOfTransits.add(transitList);
		return setOfTransits;
  }

  public boolean addCity(String city){
    cityNames = Arrays.copyOf(cityNames, cityNames.length + 1);
		cityNames[cityNames.length-1] = city;
		v = cityNames.length;
		G.add();
		if(city.equals(cityNames[cityNames.length - 1])) {
				return true;
			} 
		return false;
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
    G.addEdge(new Route(source, destination, distance, price));
		G.addEdge(new Route(destination, source, distance, price));
		return true;
  }

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
    int sourceIndex = -1;
		int destinationIndex = -1;
		for(int i = 0; i < cityNames.length; i++) {
			if(cityNames[i].equalsIgnoreCase(source)) {
				sourceIndex = i;
			}
		}
		for(int j = 0; j < cityNames.length; j++) {
			if(cityNames[j].equalsIgnoreCase(destination)){
				destinationIndex = j;
			}
		}
		if(sourceIndex == -1 || destinationIndex == -1) {
			throw new CityNotFoundException("Program ending");
		}
		for(Route e : G.adj(sourceIndex)) {
			if(e.destination.equalsIgnoreCase(destination)) {
				e.distance = distance;
				e.price = price;
				break;
			}
		}
		for(Route g : G.adj(destinationIndex)) {
			if(g.destination.equalsIgnoreCase(source)) {
				g.distance = distance;
				g.price = price;
				break;
			}
		} 
		return true;
  }

 private class Digraph {
		private int v;
		private int e;
		private LinkedList<Route>[] adj; // 
		private boolean[] marked; // marked[v] = is there an s-v path
		private int[] edgeTo; // edgeTo[v] = previous edge on shortest s-v path
		private int[] distTo; // distTo[v] = number of edges shortest s-v path
		/**
		 * Create an empty digraph with v vertices.
		 */
		public Digraph(int v) {
			if (v < 0)
				throw new RuntimeException("Number of vertices must be nonnegative");
			this.v = v;
			this.e = 0;
			@SuppressWarnings("unchecked")
			LinkedList<Route>[] temp = (LinkedList<Route>[]) new LinkedList[v];
			adj = temp;
			for (int i = 0; i < v; i++)
				adj[i] = new LinkedList<Route>();
		}

		public void add(){
			adj = Arrays.copyOf(adj, adj.length+1);
			adj[adj.length - 1] = new LinkedList<Route>();
			v++;
			//copy the LinkedList array and expand it
			//then initialize it at length - 1;
			//inside of addCity(above), call as G.addCity
			
		}

		/**
		 * Add the edge e to this digraph.
		 */
		public void addEdge(Route edge) { //change to Route
			//int from = edge.to();
			//adj[from].add(edge);
			
			String from = edge.source;
			//iterate through adjacency list
			for (int i = 0; i < cityNames.length; i++) {
				if (cityNames[i] != null && cityNames[i].equals(from)) {
					adj[i].add(edge);
					e++;
				}
			}
		}

		public void removeEdge(Route edge) {
			String from = edge.source;
			for(int i = 0; i < cityNames.length; i++) {
				if(cityNames[i] != null && cityNames[i].equals(from)){
					adj[i].remove(edge);
					e--;
				}
			}
		} 

		public Route findEdge(int source, int destination) {
			for(Route b : adj(source)) {
				if(b.destination.equals(cityNames[destination])) {
					return b;
				}
			}
			return null;
		}
		//will compare source and dest only

		/**
		 * Return the edges leaving vertex v as an Iterable. To iterate over the edges
		 * leaving vertex v, use foreach notation:
		 * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
		 */
		public Iterable<Route> adj(int v) { //DirectedEdge
			return adj[v];
		}

		public void bfs(int source) {
			marked = new boolean[this.v];
			distTo = new int[this.v];
			edgeTo = new int[this.v]; 

			Queue<Integer> q = new LinkedList<Integer>();
			for (int i = 0; i < v; i++) {
				distTo[i] = INFINITY;
				marked[i] = false;
			}
			distTo[source] = 0;
			marked[source] = true;
			q.add(source);

			while (!q.isEmpty()) {
				int v = q.remove(); 
				for (Route w : this.adj(v)) { 
					for(int i = 0; i < cityNames.length; i++) {
						if(cityNames[i].equalsIgnoreCase(w.destination)) { //.equals
							if(!marked[i]) {
								edgeTo[i] = v; 
								distTo[i] = distTo[v] + 1;
								marked[i] = true;
								q.add(i);
							}
						}
					}
				}
			}
		} 
 
		public void dijkstras(int source) {
			
			marked = new boolean[this.v];
			distTo = new int[this.v];
			edgeTo = new int[this.v];

			for (int i = 0; i < v; i++) {
				distTo[i] = INFINITY;
				marked[i] = false;
			}
			distTo[source] = 0;
			marked[source] = true;
			int nMarked = 1;

			int current = source;
			while (nMarked < this.v) {
				for (Route w : adj(current)) {
					for(int i = 0; i < cityNames.length; i++) {
						if(cityNames[i].equalsIgnoreCase(w.destination)) {
							if (distTo[current] + w.distance < distTo[i]) { 
						// TODO:update edgeTo and distTo
								edgeTo[i] = current;
								distTo[i] = distTo[current] + w.distance; 
							}
						}
					}
				}
				// Find the vertex with minimim path distance
				// This can be done more efficently using a priority queue!
				int min = INFINITY;
				current = -1;

				for (int i = 0; i < distTo.length; i++) {
					if (marked[i])
						continue;
					if (distTo[i] < min) {
						min = distTo[i];
						current = i;
					}
				}

				// TODO: Update marked[] and nMarked. Check for disconnected graph.
				if (current >= 0) {
					marked[current] = true;
					nMarked++;
				} else {
					break;
				}
			}
		}
	
	
	}
}
