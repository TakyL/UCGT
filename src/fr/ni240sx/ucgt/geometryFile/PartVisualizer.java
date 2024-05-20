package fr.ni240sx.ucgt.geometryFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import fr.ni240sx.ucgt.binstuff.Hash;
import fr.ni240sx.ucgt.collisionsEditor.OrbitCameraViewport;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.stage.Stage;

public class PartVisualizer extends Application{

	public static Group viewportGroup = new Group();
	public static OrbitCameraViewport viewport;
	
	public static ArrayList<MeshView> partMeshView = new ArrayList<MeshView>();
	
	public static List<Part> partsList = new ArrayList<Part>();
		
	@Override
	public void start(Stage primaryStage) throws Exception {

		viewport = new OrbitCameraViewport(viewportGroup, 1024, 600);
		for (Part part : partsList) {
			for (var m : part.mesh.materials.materials) {
				TriangleMesh matMesh = new TriangleMesh();
				
				matMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
				
				for (var v : m.verticesBlock.vertices) {
					matMesh.getPoints().addAll(v.posX, v.posY, v.posZ);
					matMesh.getNormals().addAll(v.normX, v.normY, v.normZ);
					matMesh.getTexCoords().addAll(v.texU, v.texV);
				}
				
				for (var tr : m.triangles) {
					matMesh.getFaces().addAll(
							// points normals   texcoords
							tr.vert1, tr.vert1, tr.vert1, //v1
							tr.vert2, tr.vert2, tr.vert2, //v2
							tr.vert3, tr.vert3, tr.vert3);//v3
				}
				
				var mv = new MeshView(matMesh);
	
				double colorR = Math.random();
				double colorG = Math.random();
				double colorB = Math.random();
				
				mv.setMaterial(new PhongMaterial(Color.color(colorR, colorG, colorB, 1)));
	
	//			mv.setMaterial(new PhongMaterial() {{
	//				setDiffuseMap(new Image("C:\\jeux\\UCE 1.0.1.18\\CARS\\AUD_RS4_STK_08\\textures\\AUD_RS4_STK_08_INTERIOR.png"));
	//				setDiffuseMap(new Image("C:\\jeux\\UCE 1.0.1.18\\CARS\\AUD_RS4_STK_08\\textures\\AUD_RS4_STK_08_ENGINE.png"));
	//				setBumpMap(new Image("C:\\jeux\\UCE 1.0.1.18\\CARS\\AUD_RS4_STK_08\\textures\\AUD_RS4_STK_08_ENGINE_N.png"));
	//			}});
				
				partMeshView.add(mv);
			}
		}
		updateRender();
		
		BorderPane root = new BorderPane();
        root.setCenter(viewport);

		viewport.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable evt) {
				updateRender();
			}
		});
        viewport.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable evt) {
				updateRender();
			}
		});
		viewport.widthProperty().bind(root.widthProperty());
		viewport.heightProperty().bind(root.heightProperty());
		
        
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
	}
	
	
    public static void updateRender() {
    	viewport.viewportGroup.getChildren().clear();
    	viewport.buildAxes();
    	viewport.viewportGroup.getChildren().addAll(partMeshView);
    }

    public static void setParts(String geomFile, String parts) {
    	partsList.clear();
    	addParts(geomFile, parts);
    }
    
    public static void addParts(String geomFile, String parts) {
		try {
			long t = System.currentTimeMillis();
			
			var geom = Geometry.load(new File(geomFile));
			
			System.out.println("File loaded in "+(System.currentTimeMillis()-t)+" ms.");
			t = System.currentTimeMillis();
	
			for (Part p : geom.parts) {
				if (parts.contains(p.header.partName.replace(geom.carname+"_", ""))) partsList.add(p);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void run() {
    	launch();
    }
    public static void run(String[] args) {
    	launch(args);
    }
	
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}