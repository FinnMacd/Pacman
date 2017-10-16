public class Path{
		
		public int[] path = new int[4];
		
		public Path(){
			for(int i = 0; i < path.length; i++){
				path[i] = -1;
			}
		}
		
		public void addDir(int dir){
			path[3] = path[2];
			path[2] = path[1];
			path[1] = path[0];
			path[0] = dir;
		}
		
		public int getDir(){
			int dir = path[0];
			path[0] = path[1];
			path[1] = path[2];
			path[2] = path[3];
			path[3] = -1;
			System.out.println(dir);
			return dir;
		}
		
		public int seeNext(){
			return path[0];
		}
		
	}