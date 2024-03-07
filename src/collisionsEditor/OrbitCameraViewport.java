package collisionsEditor;

import javafx.beans.NamedArg;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class OrbitCameraViewport extends SubScene {

	Group viewportGroup;
    public PerspectiveCamera camera = new PerspectiveCamera(true);
    Rotate rotationX = new Rotate(0, 0, 0, 10, Rotate.X_AXIS);
    Rotate rotationY = new Rotate(0, 0, 0, 10, Rotate.Y_AXIS);
    Rotate rotationZ = new Rotate(0, 0, 0, 10, Rotate.Z_AXIS);
    Translate translation = new Translate();
	
	public OrbitCameraViewport(Group viewportGroup, @NamedArg(value="width") double width, @NamedArg(value="height") double height) {
		super(viewportGroup, width, height, true, SceneAntialiasing.BALANCED);

		this.viewportGroup = viewportGroup;
		this.setFill(Color.SLATEGREY);
        this.buildAxes();
//        if (CollisionsEditor.debug) this.viewportGroup.getChildren().add(new Box(0.1,0.1,0.1));
        
        
        camera.setTranslateZ(-10);
        camera.setRotate(180);
        camera.setNearClip(0.01);
        camera.setFarClip(10000);
        camera.getTransforms().addAll(rotationX, rotationY, rotationZ, translation);

        this.setCamera(camera);
        
        this.setOnScroll((final ScrollEvent e) -> {
            rotationY.setAngle(rotationY.getAngle() - e.getDeltaX() / 10);
            rotationX.setAngle(rotationX.getAngle() + e.getDeltaY() / 10);

//            if (rotationY.getAngle() <= -180) rotationY.setAngle(rotationY.getAngle()+360);
//            if (rotationY.getAngle() > 180) rotationY.setAngle(rotationY.getAngle()-360);

//            rotationX.setAngle(rotationX.getAngle() - e.getDeltaY() / 10 * (Math.abs(rotationY.getAngle() % 180) - 90) / 90);
//            System.out.println((Math.abs(rotationY.getAngle() % 180) - 90) / 90);
//            rotationZ.setAngle(rotationZ.getAngle() - e.getDeltaY() / 10 * (Math.abs(rotationY.getAngle() % 180)) / 90);
//            System.out.println((Math.abs((rotationY.getAngle() + 180) % 180) - 90) / 90);

//            setTranslateX(getTranslateX() + e.getDeltaX());
//            setTranslateY(getTranslateY() + e.getDeltaY());
        });
        
        handleMouse();
	}
	
	public void buildAxes() {

		final Box xAxis = new Box(10, 0.01, 0.01);
        final Box yAxis = new Box(0.01, 10, 0.01);
        final Box zAxis = new Box(0.01, 0.01, 10);
        xAxis.setMaterial(new PhongMaterial(Color.RED));
        yAxis.setMaterial(new PhongMaterial(Color.GREEN));
        zAxis.setMaterial(new PhongMaterial(Color.BLUE));
        Group axisGroup = new Group();
        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);

        this.viewportGroup.getChildren().add(axisGroup);
	}
	
	
	
	
	
	private static final double CONTROL_MULTIPLIER = 0.1;    
	private static final double SHIFT_MULTIPLIER = 10.0;    
	private static final double MOUSE_SPEED = 0.1;    
	private static final double ROTATION_SPEED = 2.0;    
	private static final double TRACK_SPEED = 0.3;
	
	 
	private static final double modifierFactor = 0.1;    
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
	
	
	private void handleMouse() {
		 
        this.setOnMousePressed(me -> {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
        });
        this.setOnMouseDragged(me -> {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY);

               double modifier = 1.0;

               if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                } 
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }     
                if (me.isPrimaryButtonDown()) {
                    rotationY.setAngle(rotationY.getAngle() -
                       mouseDeltaX*modifierFactor*modifier*ROTATION_SPEED);  // 
                    rotationX.setAngle(rotationX.getAngle() +
                       mouseDeltaY*modifierFactor*modifier*ROTATION_SPEED);  // -
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                	translation.setX(translation.getX() + 
                      mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                	translation.setY(translation.getY() + 
                      mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                }
       }); // setOnMouseDragged
   } //handleMouse
}
