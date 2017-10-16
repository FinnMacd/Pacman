import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Pathfinding {
	
	private ArrayList closedList = new ArrayList();
	private SortedList openList = new SortedList();
	
	private Node[][] nodes;
	private int[] map;
	
	private Pacman pac;
	
	public Pathfinding(int[] m,Pacman p){
		pac = p;
		map = m;
		nodes = new Node[32][25];
		for(int x = 0; x < 32; x++){
			for(int y = 0; y < 25; y++){
				nodes[x][y] = new Node(x,y);
			}
		}
		
	}
	
	public Path getDir(Point s,Point e){
		
		nodes[s.x][s.y].G = 0;
		closedList.clear();
		openList.clear();
		openList.add(nodes[s.x][s.y]);
		
		nodes[e.x][e.y].parent = null;
		
		main:
		while(openList.size() >= 1){
			
			Node current = (Node)openList.getNext();
			
			openList.remove(current);
			closedList.add(current);
			
			for(int x = -1; x < 2; x++){
				for(int y = -1; y < 2; y++){
					
					if(x!=0 && y != 0 || x == 0 && y == 0)continue;
					
					int xp = x + current.x;
					int yp = y + current.y;
					
					if(xp < 0 || yp < 0 || xp > 31 || yp > 31)continue;
					if(map[xp + yp * 32] == 1)continue;
					if(xp == s.x && yp == s.y)continue;
					
					if(openList.contains(nodes[xp][yp])||closedList.contains(nodes[xp][yp])){
						if(nodes[xp][yp].G >= current.G + 1){
							nodes[xp][yp].parent = current;
							nodes[xp][yp].G = current.G + 1;
						}
					}else{
						nodes[xp][yp].G = current.G + 1;
						nodes[xp][yp].setH(e);
						nodes[xp][yp].parent = current;
						openList.add(nodes[xp][yp]);
						if(xp == e.x && yp == e.y)break main;
					}
					
				}
			}
			
		}
		
		if(nodes[e.x][e.y].parent == null)return new Path();
		
		Node target = nodes[e.x][e.y];
		Path path = new Path();
		
		while(target != nodes[s.x][s.y]){
			if(target.x < target.parent.x)path.addDir(3);
			else if(target.x > target.parent.x)path.addDir(1);
			else if(target.y < target.parent.y)path.addDir(0);
			else if(target.y > target.parent.y)path.addDir(2);
			target = target.parent;
		}
		System.out.println("path found");
		return path;
	}
	
 	private class SortedList{
		
		ArrayList list = new ArrayList();
		
		public void add(Object o){
			list.add(o);
			Collections.sort(list);
		}
		
		public Object getNext(){
			return list.get(0);
		}
		
		public void remove(Node n){
			list.remove(n);
		}
		
		public void clear(){
			list.clear();
		}
		
		public boolean contains(Object o){
			return list.contains(o);
		}
		
		public int size(){
			return list.size();
		}
		
	}
	
	private class Node implements Comparable{
		
		public int x,y,G,H;
		public Node parent;
		
		private Random r = new Random();
		
		public Node(int x,int y){
			this.x = x;
			this.y = y;
		}
		
		public void setH(Point e){
			int xdiff = e.x - x;
			int ydiff = e.y - y;
			if(ydiff < 0)ydiff *= -1;
			if(xdiff < 0)xdiff *= -1;
			
			H = xdiff + ydiff;
		}
		
		public int compareTo(Object other){
			Node o = (Node)other;
			
			int f = H + G;
			int of = o.H + o.G;
			
			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}
}
