package fr.ni240sx.ucgt.geometryFile.part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import fr.ni240sx.ucgt.binstuff.Block;
import fr.ni240sx.ucgt.geometryFile.GeomBlock;
import fr.ni240sx.ucgt.geometryFile.part.mesh.*;
import javafx.util.Pair;

public class Mesh extends Block {

	public GeomBlock getBlockID() {return GeomBlock.Part_Mesh;}
	
	public Mesh_Info info;
	public Materials materials;
	public ShadersUsage shadersUsage;
	public ArrayList<Vertices> verticesBlocks = new ArrayList<Vertices>();
	public Triangles triangles;
	
	public Mesh(ByteBuffer in) {
		var blockLength = in.getInt();
		var blockStart = in.position();
		Block block;
		
		while(in.position() < blockStart+blockLength) {
			if ((block = Block.read(in)) != null) subBlocks.add(block);
		}
		// SUB-BLOCKS PRE-TREATMENT TO REFERENCE THEM ALL
		// if there's more than one block only the last one is taken into account
		for (var b : subBlocks) {
			switch (b.getBlockID()) {
			case Part_Mesh_Info:
				info = (Mesh_Info) b;
				break;
			case Part_Mesh_Materials:
				materials = (Materials) b;
				break;
			case Part_Mesh_ShadersUsage:
				shadersUsage = (ShadersUsage) b;
				break;
			case Part_Mesh_UNKNOWN:
				break;
			case Part_Mesh_Vertices:
				verticesBlocks.add((Vertices) b);
				break;
			case Part_Mesh_Triangles:
				triangles = (Triangles) b;
				break;
			default:
				break;}
		}
		
//		System.out.println(info.numMaterials+" materials, "+info.numTriangles+" triangles, "+info.numVertices+" vertices");
		
		for (int i=0; i<materials.materials.size(); i++) {
			verticesBlocks.get(i).material = materials.materials.get(i);
			materials.materials.get(i).verticesBlock = verticesBlocks.get(i);
			materials.materials.get(i).triangles = triangles.triangles.subList(materials.materials.get(i).fromVertID/3, (materials.materials.get(i).toVertID/3));
		}
		
		
	}

	@Override
	public byte[] save(int currentPosition) throws IOException, InterruptedException {
		var out = new ByteArrayOutputStream();

		var buf = ByteBuffer.wrap(new byte[8]);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(getBlockID().getKey());
		buf.putInt(-1); //length for later

		out.write(buf.array());
		
		for (var b : subBlocks) {
			out.write(b.save(currentPosition + out.size()));
		}

		buf = ByteBuffer.wrap(new byte[4]);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(out.size()-8);

//		out.write(buf.array(), 4, 4); //write correct size
		
		var arr = out.toByteArray();
		arr[4] = buf.array()[0];
		arr[5] = buf.array()[1];
		arr[6] = buf.array()[2];
		arr[7] = buf.array()[3]; //writes the correct size
		return arr;	
	}
}