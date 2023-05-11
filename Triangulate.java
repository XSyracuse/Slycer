import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Collections;

class Vertex {

  float x;
  float y;
  float z;

  public Vertex(float x, float y, float z) {

    this.x = x;
    this.y = y;
    this.z = z;

  }

  public String toString() {
    return x + "," + y + "," + z;
  }

}
class Line {
  Vertex v1;
  Vertex v2;
  
  public Line(Vertex v1, Vertex v2) {

    this.v1 = v1;
    this.v2 = v2;

  }
  public Vertex getVertex1() {
    return v1;
  }
  public Vertex getVertex2() {
    return v2;
  }
  public Line reverseLine() {
    return new Line(v2, v1);
  }
  public double distanceVertex1(Line l) {
    double dx = l.v1.x - v1.x;
    double dy = l.v1.y - v1.y;
    double d = dx * dx + dy * dy;
    return d;
  }
  public double distanceVertex2(Line l) {
    double dx = l.v2.x - v1.x;
    double dy = l.v2.y - v1.y;
    double d = dx * dx + dy * dy;
    return d;
  }
  public String toString() {
    return "Line: " + v1 + " to " + v2;
  }

}

class Tri {

  Vertex v1;
  Vertex v2;
  Vertex v3;

  public Tri(Vertex v1, Vertex v2, Vertex v3) {
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
  }
  public boolean isBetween(float z,float z1,float z2) {
    float temp;
    if(z1 > z2) {
      temp = z1;
      z1 = z2;
      z2 = temp;
    } 
    if(z >= z1 && z <= z2) return true;

    return false;

  }

  public boolean zIntersects(float z) {

    if( isBetween(z, v1.z, v2.z) ) return true;
    if( isBetween(z, v2.z, v3.z) ) return true;
    if( isBetween(z, v3.z, v1.z) ) return true;
    return false;

  }

  public static Vertex getInter(float z, Vertex v1, Vertex v2) {
    float mxz;
    
    float myz;
    float intercept;

    mxz = (v2.x - v1.x)/(v2.z - v1.z);
    intercept = v1.x - v1.z*mxz;
   
    float x = mxz*(z) + intercept;
    
    myz = (v2.y - v1.y)/(v2.z - v1.z);
    intercept = v1.y - v1.z*myz;    

    float y = myz*(z) + intercept;
    //System.out.println(v1);
    //System.out.println(v2);
    //System.out.println("mxz: " + mxz);
    //System.out.println("myz: " + myz);
    
    return new Vertex(x,y,z);
   
  }

  public Line getIntersects(float z) {

    int i = 0;
    Vertex[] verts = new Vertex[2];

    if( isBetween(z, v1.z, v2.z) ) {

      Vertex v = getInter(z,v1,v2);
      
      verts[i] = v;
      ++i;

      System.out.println("E12: " + v);

    }
    if( isBetween(z, v2.z, v3.z) ) {

      Vertex v = getInter(z,v2,v3);
      verts[i] = v;
      ++i;
      System.out.println("E23: " + v);

    }
    if( isBetween(z, v3.z, v1.z) ) {

      Vertex v = getInter(z,v3,v1);
      verts[i] = v;
      ++i; 
      System.out.println("E31: " + v);

    }

   
    return new Line(verts[0],verts[1]);

  }

}
class DistanceLine implements Comparator<DistanceLine> {
  static double distanceMaxError = 0.01; 
  Line line;
  double distance;
  double reverseDistance;
  double dx;
  double dy;

  double rdx;
  double rdy;

  public DistanceLine(Line line, Line compare) {

    this.line = line;

    dx = compare.getVertex2().x - line.getVertex1().x;
    dy = compare.getVertex2().y - line.getVertex1().y;
    
    rdx = compare.getVertex2().x - line.getVertex2().x;
    rdy = compare.getVertex2().y - line.getVertex2().y;
  }
  public DistanceLine(Line line, double distance, double reversedDistance) {
    this.line = line;
    this.distance = distance;
    this.reverseDistance = reverseDistance;
    
  }

  public Line getLine() {
    return line;
  }
  public boolean endsMatch() {
    // absolute value
    if(dx<0) dx=-dx;
    if(dy<0) dy=-dy;
    
    if(dx < distanceMaxError && dy < distanceMaxError) {
      return true;
    }
    return false;
  }

  public boolean endsReverseMatch() {
    // absolute value
    if(rdx<0) rdx=-rdx;
    if(rdy<0) rdy=-rdy;
    
    if(rdx < distanceMaxError && rdy < distanceMaxError) {
      return true;
    }
    return false;
  }
  @Override
  public int compare(DistanceLine l1, DistanceLine l2) {
    double d = l1.distance - l2.distance;
    int r=0;
    if(d==0) r = 0;
    if(d>0) r = 1;
    if(d<0) r = -1;
    return r;
  }
  
  public String toString() {
    String r = line.toString() + "  " + distance + "  " + reverseDistance;
    return r;
  }
}

public class Triangulate {
    static List<Line> p = new ArrayList<>();

    public static List<Line> findPath(List<Line> lines, int pathIndex) {
  
      Line seed = p.get(pathIndex);
      //lines.remove(0);

      Line foundLine = null;
      boolean reverse = false;
      for(Line l : lines) {
        

        DistanceLine dl = new DistanceLine(l,seed);
        if(dl.endsMatch()) {
          System.out.println("hit: "+l);
          p.add(l);
          foundLine = l;
          break;
        }
        else if (dl.endsReverseMatch()) {
          System.out.println("rhit: "+l);
          p.add(l.reverseLine());
          foundLine = l;
          reverse = true;
          break;
        }
        
        
      }//for
      
      lines.remove(foundLine);
     
      System.out.println("\npath is now: ");
      for(Line l : p) {
        System.out.println(l); 
      }

      System.out.println("\nlines is now (size = " + lines.size() + ") : ");
      for(Line l : lines) {
        System.out.println(l);
      }

      return lines;
    }
    public static List<Line> buildPath(List<Line> lines) {

            //seed the start
            
            p.add(lines.get(0));
            lines.remove(0);
            int s = lines.size();

            for(int j=0;j<s;j++) {

              lines = findPath(lines,p.size()-1);

            }

            return lines;
    }
    public static void convertToGcode(List<Line> pathIn) {
      List<Line> path = new ArrayList<>(pathIn);
      StringBuilder sb = new StringBuilder();
      // move to first
      float x0 =  path.get(0).getVertex1().x;
      float y0 =  path.get(0).getVertex1().y;
      
      String s = "G0 X" + x0 + " Y" + y0 + "\n";
      sb.append(s);
      path.remove(0);

      for(Line l : path) {
         s = "G1 X" + l.getVertex1().x + " Y" + l.getVertex1().y + "\n";
         sb.append(s);
      }

      s = "G1 X" + x0 + " Y" + y0 + "\n";
      sb.append(s);

      System.out.println(sb);

    }
    public static void main(String[] args) {

      // get vertex
      // then triangles
      
      List<Vertex> verts = new ArrayList<>();
      Set<Tri> tris = new HashSet<>();
      List<Line> lines = new ArrayList<>();
      List<Line> filteredLines = new ArrayList<>();

      Scanner scanner = new Scanner(System.in);
      
      int no_verts = scanner.nextInt();
      int no_tris = scanner.nextInt();
      scanner.nextLine();

      for(int i=0;i<no_verts;i++) {
        
        //int sides = scanner.nextInt();
        float x = scanner.nextFloat();
        float y = scanner.nextFloat();
        float z = scanner.nextFloat();
        scanner.nextLine();
        verts.add(new Vertex(x,y,z));
        
      }
      System.out.println(verts);

      for(int i=0;i<no_tris;i++) {
        int s = scanner.nextInt();
        int vt1 = scanner.nextInt();
        int vt2 = scanner.nextInt();
        int vt3 = scanner.nextInt();
        scanner.nextLine();

        System.out.println(verts.get(vt1));
        System.out.println(verts.get(vt2));
        System.out.println(verts.get(vt3));
        System.out.println("");

        Tri tri = new Tri(verts.get(vt1),verts.get(vt2),verts.get(vt3));
        tris.add(tri);
      }
      float z = 0.4f; 
      int i=0;
      for (Tri tr : tris) {
        System.out.print("\n" + i + ": ");
        Line line = tr.getIntersects(z);
        lines.add(line);
        ++i; 
      }
      scanner.close();  
      for(Line l : lines) {
          if(l.toString().equals("Line: null to null")) {

          } else {
              filteredLines.add(l);
              System.out.println(l);
          }
      }

      System.out.println(filteredLines);
      System.out.println(filteredLines.size());
      

      List<List<Line>> paths = new ArrayList<>();
      
      boolean newcode = true;

      if(newcode) {
      
        while(filteredLines.size()>2) {
          filteredLines = buildPath(filteredLines);
          paths.add(p);
          p = new ArrayList<>();
        }

        System.out.println(paths);

      }
      else {
      //seed the start
      p.add(filteredLines.get(0));
      filteredLines.remove(0);
      int s= filteredLines.size(); 
      for(int j=0;j<s;j++) { 
          filteredLines = findPath(filteredLines,p.size()-1);
      }
      paths.add(p);
      p = new ArrayList<>();
 
      if(filteredLines.size() > 1) {
         p.add(filteredLines.get(0));
         filteredLines.remove(0);
         //System.out.println(p);
         //System.out.println(filteredLines);
         int size = filteredLines.size();

         for(int j=0;j<size;j++) {
           filteredLines = findPath(filteredLines,p.size()-1);
         }
         paths.add(p);
      }
      
      System.out.println(paths); 
      }//else 
      
      for(List<Line> path : paths) {
        convertToGcode(path);
      }
    }//main
    


}
