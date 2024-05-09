package fr.ni240sx.ucgt.geometryFile.geometry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import fr.ni240sx.ucgt.binstuff.Block;
import fr.ni240sx.ucgt.geometryFile.GeomBlock;

public class Info extends Block {

	public GeomBlock getBlockID() {return GeomBlock.Geom_Info;}
	public static final int usualLength = 144;
	
	public int const01=0, const02=3, const03=30, partsCount=0;
	public String filename="Compiled with UCGT | NI240SX 2024", blockname="DEFAULT";
	public int const1=128, const21=0, const22=0, const23=0, const24=0;
	
	
	public Info(ByteBuffer in) {
		var blockLength = in.getInt();
		if (blockLength != usualLength) System.out.println("[WARN] Unexpected info block length : "+blockLength+" instead of 144.");
		var blockStart = in.position();
		
		const01 = in.getInt();
		const02 = in.getInt();
		const03 = in.getInt();
		partsCount = in.getInt();
		
		filename = Block.readString(in);
		in.position(blockStart+72);
		blockname = Block.readString(in);
		in.position(blockStart+blockLength-32);
		const1 = in.getInt();
		in.getInt();
		in.getInt();
		in.getInt();
		const21 = in.getInt();
		const22 = in.getInt();
		const23 = in.getInt();
		const24 = in.getInt();
	}

	@Override
	public byte[] save() throws IOException {
		var out = new ByteArrayOutputStream();

		var buf = ByteBuffer.wrap(new byte[usualLength+8]); //TODO usual size used, not dynamic
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(getBlockID().getKey()); 
		buf.putInt(usualLength); //length for later
		
		buf.putInt(const01);
		buf.putInt(const02);
		buf.putInt(const03);
		buf.putInt(partsCount);
		
		Block.putString(buf, filename, 55);
		buf.position(80); //go to second string
		Block.putString(buf, blockname, 39);
		buf.position(120);

		buf.putInt(const1);
		buf.putInt(0);
		buf.putInt(0);
		buf.putInt(0);

		buf.putInt(const21);
		buf.putInt(const22);
		buf.putInt(const23);
		buf.putInt(const24);
		
		out.write(buf.array());
		
		return out.toByteArray();	
	}

}
