package fr.ni240sx.ucgt.geometryFile.geometry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import fr.ni240sx.ucgt.binstuff.Block;
import fr.ni240sx.ucgt.geometryFile.GeomBlock;
import fr.ni240sx.ucgt.geometryFile.Part;

public class PartsOffsets extends Block {

	public GeomBlock getBlockID() {return GeomBlock.Geom_PartsOffsets;}
	
	public ArrayList<PartOffset> partOffsets = new ArrayList<PartOffset>();
//	public HashMap<Integer, PartOffset> partOffsets = new HashMap<Integer, PartOffset>(); //this caused the part offsets to be mismatched
	
	public PartsOffsets(ByteBuffer in) {
		var blockLength = in.getInt();
//		var blockStart = in.position();
		
		for(int i=0; i<blockLength/24; i++) {
			var po = new PartOffset(in.getInt(), in.getInt(), in.getInt(), in.getInt());
			partOffsets.add(po);
//			partOffsets.put(po.partKey, po);
			in.getInt(); //512
			in.getInt(); //0
		}
	}

	@Override
	public byte[] save(int currentPosition) throws IOException {
		var buf = ByteBuffer.wrap(new byte[partOffsets.size()*24 + 8]); 
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(getBlockID().getKey()); 
		buf.putInt(partOffsets.size()*24); //length
		for (var p : partOffsets) { //.values()
			buf.putInt(p.partKey);
			buf.putInt(p.offset);
			buf.putInt(p.sizeCompressed);
			buf.putInt(p.sizeDecompressed);
			buf.putInt(p.unknown);
			buf.putInt(0);
		}
		return buf.array();
	}

	public void refresh(ArrayList<Part> parts) {
		partOffsets.clear();
		for (var p : parts) {
			var po = new PartOffset(p.partKey, 0, p.compressedLength, p.decompressedLength);
			partOffsets.add(po);
//			partOffsets.put(po.partKey, po);
		}
	}

	public void setOffset(Part p, int offset) {
//		partOffsets.get(p.partKey).offset = offset;
		
		for (var o : partOffsets) { //.values()
			if (o.partKey == p.partKey) {
				o.offset = offset;
				break;
			}
		}
	}
}