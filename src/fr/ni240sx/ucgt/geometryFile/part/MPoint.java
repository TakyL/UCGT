package fr.ni240sx.ucgt.geometryFile.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import fr.ni240sx.ucgt.binstuff.Hash;
import fr.ni240sx.ucgt.geometryFile.Geometry;
import fr.ni240sx.ucgt.geometryFile.Part;
import fr.ni240sx.ucgt.geometryFile.io.VertexData3;

public class MPoint {
	
//	public int key;
	
	public float[][] matrix = new float[3][3];

	public float positionX = 0;
	public float positionY = 0;
	public float positionZ = 0;
	public float positionW = 1; //scale or positionW ?

	public float scaleX = 1;
	public float scaleY = 1;
	public float scaleZ = 1;
	
	public Hash nameHash;
	public Part part;
//	public ArrayList<Part> parts = new ArrayList<Part>();
	
	public String uniqueName = "_";
	
	public String tempPartName; //used only when loading from file
	public ArrayList<String> tempPartNames = new ArrayList<>();
	public ArrayList<VertexData3> verts = new ArrayList<>();
	
	public static final int roundFactor = 1000;
	
	public MPoint() {
	}
	
	public MPoint(MPoint mp) {
//		this.key = mp.key;
		this.nameHash = mp.nameHash;
		this.matrix = mp.matrix;
		this.positionX = mp.positionX;
		this.positionY = mp.positionY;
		this.positionZ = mp.positionZ;
		this.positionW = mp.positionW;
		this.scaleX = mp.scaleX;
		this.scaleY = mp.scaleY;
		this.scaleZ = mp.scaleZ;
	}

	public void tryGuessName(Geometry geometry) {
		nameHash = Hash.guess(nameHash.binHash, geometry.hashlist, String.format("0x%08X", nameHash.binHash), "BIN");
	}
	
	public static float[] rotationMatrixToEulerAngles(float[][] R) {
	    double sy = -Math.asin(R[0][2]);
	    double cy = Math.cos(sy);

	    double x, y, z;
	    if (Math.abs(cy) > 1e-6) { // Avoid division by zero
	        x = Math.atan2(R[1][2], R[2][2]);
	        y = sy;
	        z = Math.atan2(R[0][1], R[0][0]);
	    } else { // Gimbal lock situation
	        x = 0.0; 
//	        z = 0.0;
//	    	System.out.println("Gymbal lock "+this.nameHash.label+" on "+this.part.name);
	        if (R[0][2] < 0) {
//	        	x = Math.atan2(R[1][0], R[2][0] );
	            y = Math.PI / 2;
	            z = Math.atan2(R[1][0], R[1][1]);
	        } else {
//	        	x = Math.atan2( -R[1][0], -R[2][0] );
	            y = -Math.PI / 2;
	            z = Math.atan2(-R[1][0], -R[1][1]);
	        }
	    }
	    return new float[]{(float) round(Math.toDegrees(x)), (float) round(Math.toDegrees(y)), (float) round(Math.toDegrees(z))};
	}
	
	public float[][] eulerAnglesToMatrix(double w, double v, double u) {
	    float[] inversion = rotationMatrixToEulerAngles(eulerAnglesToMatrixCalculation(u, v, w)); //doing it twice fixes issues with rotations
	    return eulerAnglesToMatrixCalculation(inversion[0], inversion[1], inversion[2]);
	}
	
	public float[][] eulerAnglesToMatrixCalculation(double u, double v, double w) {
	    u = Math.toRadians(u);
	    v = Math.toRadians(v);
	    w = Math.toRadians(w);

	    double cu = Math.cos(u);
	    double su = Math.sin(u);
	    double cv = Math.cos(v);
	    double sv = Math.sin(v);
	    double cw = Math.cos(w);
	    double sw = Math.sin(w);

	    float[][] R = new float[3][3];
	    R[0][0] = (float) (cv * cw)*scaleX;
	    R[0][1] = (float) (cv * sw)*scaleX;
	    R[0][2] = (float) -sv*scaleX;
	    R[1][0] = (float) (su * sv * cw - cu * sw)*scaleY;
	    R[1][1] = (float) (su * sv * sw + cu * cw)*scaleY;
	    R[1][2] = (float) (su * cv)*scaleY;
	    R[2][0] = (float) (cu * sv * cw + su * sw)*scaleZ;
	    R[2][1] = (float) (cu * sv * sw - su * cw)*scaleZ;
	    R[2][2] = (float) (cu * cv)*scaleZ;
	    return R;
	}
	
	
	
	public String toConfig() {
		String s = "";
		var m = rotationMatrixToEulerAngles(matrix);
//		for (var p : parts) {
//			s += "MARKER	"
//			+uniqueName+"	"
//			+nameHash.label +"	"
//			+p.name+"	";
////			+positionX+"	"+positionY+"	"+positionZ+"	"
////			s += m[0] + "	" + m[1] + "	" + m[2] + "\n";
//			// inverted axes and using - to have the same angles as ctk
//			if ((int)m[0] == m[0]) s+=(int)m[0]; else s+=m[0];
//			s += "	";
//			if ((int)m[1] == m[1]) s+=(int)m[1]; else s+=m[1];
//			s += "	";
//			if ((int)m[2] == m[2]) s+=(int)m[2]; else s+=m[2];
//			s += "\n";
//		}
		
		s += "MARKER	"
		+uniqueName+"	"
		+nameHash.label +"	"
		+part.name+"	";
//			+positionX+"	"+positionY+"	"+positionZ+"	"
//			s += m[0] + "	" + m[1] + "	" + m[2] + "\n";
		if ((int)m[0] == m[0]) s+=(int)m[0]; else s+=m[0];
		s += "	";		
		if ((int)m[1] == m[1]) s+=(int)m[1]; else s+=m[1];
		s += "	";
		if ((int)m[2] == m[2]) s+=(int)m[2]; else s+=m[2];
		if (scaleX == scaleY && scaleX == scaleZ) {
			if (scaleX != 1) {
				s += "	";
				if ((int)scaleX == scaleX) s+=(int)scaleX; else s+=scaleX;
			}
		} else {
			s += "	";
			if ((int)scaleX == scaleX) s+=(int)scaleX; else s+=scaleX;
			s += "	";
			if ((int)scaleY == scaleY) s+=(int)scaleY; else s+=scaleY;
			s += "	";
			if ((int)scaleZ == scaleZ) s+=(int)scaleZ; else s+=scaleZ;
		}
		s += "\n";
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(matrix);
		result = prime * result + Objects.hash(nameHash, positionX, positionY, positionZ, positionW, uniqueName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() == MPointPositionCube.class) {
			MPointPositionCube other = (MPointPositionCube) obj;
			return MPoint.floatEquals(other.x, positionX) && MPoint.floatEquals(other.y, positionY) && MPoint.floatEquals(other.z, positionZ);
		}
		if (getClass() != obj.getClass())
			return false;
		MPoint other = (MPoint) obj;
		return floatEquals(matrix[0][0], other.matrix[0][0]) && floatEquals(matrix[0][1], other.matrix[0][1]) && floatEquals(matrix[0][2], other.matrix[0][2]) &&
				floatEquals(matrix[1][0], other.matrix[1][0]) && floatEquals(matrix[1][1], other.matrix[1][1]) && floatEquals(matrix[1][2], other.matrix[1][2]) &&
				floatEquals(matrix[2][0], other.matrix[2][0]) && floatEquals(matrix[2][1], other.matrix[2][1]) && floatEquals(matrix[2][2], other.matrix[2][2]) &&
				nameHash.binHash == other.nameHash.binHash &&
				floatEquals(positionX, other.positionX) && floatEquals(positionY, other.positionY) && floatEquals(positionZ, other.positionZ) && 
				floatEquals(positionW, other.positionW) && uniqueName.equals(other.uniqueName);
	}

	public static boolean floatEquals(float a, float b) {
		//takes in account floating point errors, we don't need more than millimeter precision
		return (int)(a*roundFactor) == (int)(b*roundFactor);
	}
	
	public static double round(double d) {
		return (double)(Math.round(d*roundFactor))/roundFactor;
	}
	public static float round(float d) {
		return (float)(Math.round(d*roundFactor))/roundFactor;
	}
	
//	public static void printMat(float[][] mat) {
//		for (var arr : mat) printArr(arr);
//	}
//	
//	public static void printArr(float[] arr) {
//		System.out.print("[");
//		if (arr.length>0) System.out.print(arr[0]);
//		for (int i=1;i<arr.length;i++) System.out.print(", "+arr[i]);
//		System.out.println("]");
//	}
//	
//	public static void main(String[] args) {
//		
//
//		var mp = new MPoint();
//
//		//rotations test
//		float[][] matrix0 = {{-0.34202015f,	0, 0.9396926f},
//							{0.9396926f, -1.763115E-16f, 0.34202015f},
//							{1.8000426E-16f, 1.0f, 0}};
//		printMat(matrix0);
//
//		System.out.println(); //simulates first extraction
////		float[] euler0 = {180, -70, 90};
//		var euler0 = mp.rotationMatrixToEulerAngles(matrix0);
//		printArr(euler0);
//		
//		System.out.println(); //simulates first compiling
//		var matrix1 = mp.eulerAnglesToMatrix(euler0[0], euler0[1], euler0[2]);
//		printMat(matrix1);
//
//		System.out.println(); //simulates second extraction
//		var euler1 = mp.rotationMatrixToEulerAngles(matrix1);
//		printArr(euler1);
//		
//		System.out.println(); //simulates second compiling
//		var matrix2 = mp.eulerAnglesToMatrix(euler1[0], euler1[1], euler1[2]);
//		printMat(matrix2);
//
//		System.out.println(); //simulates third extraction
//		var euler2 = mp.rotationMatrixToEulerAngles(matrix2);
//		printArr(euler2);
//	}
}
