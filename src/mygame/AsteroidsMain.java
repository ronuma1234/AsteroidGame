package mygame;

import java.util.ArrayList;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.FlyByCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.collision.CollisionResults;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class AsteroidsMain extends SimpleApplication implements PhysicsCollisionListener {

    public static void main(String[] args) {
        AsteroidsMain app = new AsteroidsMain();
        app.start();
    }
    
    private Node spaceship;
    private Node spaceship2;
    private Node asteroid;
    private ArrayList<Node> allAsteroids = new ArrayList<Node>();
    //private Node bullet;
    private Node rotatePivote = new Node();
    private Node asteroids = new Node();
    private boolean isRunning = true;
    private float myTimer = 0;
    private BulletAppState bulletAppState;
    private int asteroidCount = 0;
    private int bounceFlag = 1;
    Geometry bulletGeom;
    Geometry wall1;
    Geometry wall2;
    Geometry wall3;
    Geometry wall4;
    Vector3f bulletVelocity;
    Boolean bulletFlag = false;
    

    @Override
    public void simpleInitApp() {
        spaceship = (Node) assetManager.loadModel("Models/SpaceshipImproved.j3o");
        spaceship.scale(0.1f);
        spaceship.setLocalTranslation(new Vector3f(0, 0, 0));
        
        asteroid = (Node) assetManager.loadModel("Models/Asteroid.j3o");
        asteroid.scale(0.5f);
        asteroid.setLocalTranslation(new Vector3f(4, 0, 0));
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        Box b1 = new Box(10f, 0.5f, 20f);
        wall1 = new Geometry("wall1", b1);
        wall1.setMaterial(mat);
        
        Box b2 = new Box(0.5f, 10f, 20f);
        wall2 = new Geometry("wall2", b2);
        wall2.setMaterial(mat);
        
        wall3 = wall1.clone();
        
        wall4 = wall2.clone();
        wall1.setLocalTranslation(0, -5, 0);
        wall2.setLocalTranslation(7, 0, 0);
        wall3.setLocalTranslation(0, 5, 0);
        wall4.setLocalTranslation(-7, 0, 0);
        
        Node asteroid2 = asteroid.clone(true);
        Node asteroid3 = asteroid.clone(true);
        
        asteroid2.setLocalTranslation(new Vector3f(-4,0,0));
        asteroid3.setLocalTranslation(new Vector3f(0,3,0));
        
        rootNode.attachChild(asteroid2);
        rootNode.attachChild(asteroid3);
        
        
        
        
       
        rootNode.attachChild(asteroid);
        rootNode.attachChild(asteroids);
        rootNode.attachChild(wall1);
        flyCam.setEnabled(false);
        
        float viewAngle = 45.0f;
        float minDistance = 20f;
        float maxDistance = 100f;
        Vector3f cameraLocation = new Vector3f(0,0,10);
        Vector3f lookatLocation = Vector3f.ZERO;
        Vector3f lookatDirection = Vector3f.UNIT_Z;
        
        //cam.setFrustumPerspective(viewAngle, (float)settings.getWidth(), (float)settings.getHeight(), maxDistance);
        cam.setLocation(cameraLocation);
        cam.lookAt(lookatLocation, lookatDirection);
        
        PointLight myLight = new PointLight();
        myLight.setColor(ColorRGBA.White);
        myLight.setPosition(cam.getLocation());
        myLight.setRadius(200);
        rootNode.addLight(myLight);
        
        //rotatePivote.attachChild(spaceship);
        rootNode.attachChild(spaceship);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
        RigidBodyControl shipControl = new RigidBodyControl(10f);
        spaceship.addControl(shipControl);
        bulletAppState.getPhysicsSpace().add(shipControl);
        shipControl.setKinematic(true);
        
        RigidBodyControl wallControl1 = new RigidBodyControl(100f);
        wall1.addControl(wallControl1);
        bulletAppState.getPhysicsSpace().add(wallControl1);
        wallControl1.setKinematic(true);
        wallControl1.setDamping(0, 0);
        wallControl1.setFriction(0);
        //wallControl1.clearForces();
        
        RigidBodyControl wallControl2 = new RigidBodyControl(100f);
        wall2.addControl(wallControl2);
        bulletAppState.getPhysicsSpace().add(wallControl2);
        wallControl2.setKinematic(true);
        wallControl2.setDamping(0, 0);
        wallControl2.setFriction(0);
        //wallControl2.clearForces();
        
        RigidBodyControl wallControl3 = new RigidBodyControl(100f);
        wall3.addControl(wallControl3);
        bulletAppState.getPhysicsSpace().add(wallControl3);
        wallControl3.setKinematic(true);
        wallControl3.setDamping(0, 0);
        wallControl3.setFriction(0);
        //wallControl3.clearForces();
        
        RigidBodyControl wallControl4 = new RigidBodyControl(100f);
        wall4.addControl(wallControl4);
        bulletAppState.getPhysicsSpace().add(wallControl4);
        wallControl4.setKinematic(true);
        wallControl4.setDamping(0, 0);
        wallControl4.setFriction(0);
        //wallControl4.clearForces();
        
        bulletAppState.getPhysicsSpace().setAccuracy(1/300f);
        initKeys();
        reset();
    }
    
    protected void reset() {
        //bulletVelocity = spaceship.getLocalTranslation();
        //bullet.setLocalTranslation(bulletVelocity);
    }
    
    private void initKeys() {
        inputManager.addMapping("Accelerate", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Decelerate", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Rightwards", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Leftwards", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addListener(actionListener, "Shoot");
        inputManager.addListener(analogListener, "Accelerate", "Decelerate", "Rightwards", "Leftwards");
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
           if (name.equals("Shoot") && !keyPressed) {
               if (bulletFlag == true) {
                   bulletFlag = false;
               }
               else {
                   bulletFlag = true;
               }
               shootBullet();
           }
        }
    };
    
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (isRunning) {
                
                value = 9;
                if(name.equals("Accelerate")) {
                    Vector3f forward = spaceship.getLocalRotation().getRotationColumn(1);
                    spaceship.move(forward.mult(tpf).mult(9));
                }
                if(name.equals("Decelerate")) {
                    Vector3f forward = spaceship.getLocalRotation().getRotationColumn(1);
                    spaceship.move(forward.mult(tpf).mult(-9));
                }
                if(name.equals("Rightwards")) {
                    spaceship.rotate(0, 0, -1*value*tpf);
                }
                if(name.equals("Leftwards")) {
                    spaceship.rotate(0, 0, value*tpf); 
                }
            }
        }
    };
    
    public void shootBullet() {
        Node bullet = (Node) assetManager.loadModel("Models/Bullet.j3o");
        bullet.scale(0.05f);
        bullet.setLocalTranslation(spaceship.getLocalTranslation());
        rootNode.attachChild(bullet);
        
        RigidBodyControl bulletPhy = new RigidBodyControl(0.1f);
        bullet.addControl(bulletPhy);
        bulletAppState.getPhysicsSpace().add(bulletPhy);
        bulletPhy.setCcdSweptSphereRadius(1f);
        bulletPhy.setCcdMotionThreshold(0.0001f);
        bulletPhy.setGravity(Vector3f.ZERO);
        
        bulletVelocity = spaceship.getLocalRotation().getRotationColumn(1);
        bulletPhy.setLinearVelocity(bulletVelocity.mult(20));
        
        for (Node eachAsteroid : allAsteroids) {
            CollisionResults results = new CollisionResults();
            bullet.collideWith(eachAsteroid.getWorldBound(), results);
            
            if (results.size() > 0) {
                Vector3f direction = new Vector3f(1, FastMath.nextRandomInt(0, 1),0);
                Node smallAsteroid1 = new Node();
                smallAsteroid1 = (Node) assetManager.loadModel("Models/Asteroid.j3o");
                smallAsteroid1.scale(0.2f);
                Node smallAsteroid2 = smallAsteroid1.clone(true);
                
                smallAsteroid1.setLocalTranslation(eachAsteroid.getLocalTranslation().add(0.5f, 0, 0));
                smallAsteroid2.setLocalTranslation(eachAsteroid.getLocalTranslation().add(-0.5f, 0, 0));
                
                eachAsteroid.detachChild(eachAsteroid);
                RigidBodyControl astControl = new RigidBodyControl(20f);
                smallAsteroid1.addControl(astControl);
                bulletAppState.getPhysicsSpace().add(astControl);
                astControl.setAngularVelocity(Vector3f.ZERO);
                astControl.setAngularFactor(0);
                astControl.setDamping(0, 0);
                astControl.setGravity(Vector3f.ZERO);
                astControl.setFriction(0);
                astControl.setLinearDamping(0);
                astControl.setRestitution(3);
                
                eachAsteroid.detachChild(eachAsteroid);
                RigidBodyControl astControl2 = new RigidBodyControl(20f);
                smallAsteroid1.addControl(astControl2);
                bulletAppState.getPhysicsSpace().add(astControl2);
                astControl2.setAngularVelocity(Vector3f.ZERO);
                astControl2.setAngularFactor(0);
                astControl2.setDamping(0, 0);
                astControl2.setGravity(Vector3f.ZERO);
                astControl2.setFriction(0);
                astControl2.setLinearDamping(0);
                astControl2.setRestitution(3);
                
            }
           
        }
    }
    
    
    public void spawnAsteroids() {
        
        Node bigAsteroid = new Node();
        bigAsteroid = (Node) assetManager.loadModel("Models/Asteroid.j3o");
        bigAsteroid.scale(0.5f);
        int position = FastMath.nextRandomInt(0, 3);
        System.out.println(position);
        switch (position) {
            case 0:
                bigAsteroid.setLocalTranslation(new Vector3f(6f,0,0));
                break;
            case 1:
                bigAsteroid.setLocalTranslation(new Vector3f(0, 4f, 0));
                break;
            case 2:
                bigAsteroid.setLocalTranslation(new Vector3f(-6f, 0, 0));
                break;
            case 3:
                bigAsteroid.setLocalTranslation(new Vector3f(0, -4f, 0));
        }
        //bigAsteroid.setLocalTranslation(new Vector3f(4, 0, 0));
        //rootNode.attachChild(bigAsteroid);
        asteroids.attachChild(bigAsteroid);
        RigidBodyControl astControl = new RigidBodyControl(20f);
        bigAsteroid.addControl(astControl);
        bulletAppState.getPhysicsSpace().add(astControl);
        //astControl.setKinematic(true);
        //astControl.setCcdSweptSphereRadius(1f);
        //astControl.setCcdMotionThreshold(0.0001f);
        astControl.setAngularVelocity(Vector3f.ZERO);
        astControl.setAngularFactor(0);
        astControl.setDamping(0, 0);
        astControl.setGravity(Vector3f.ZERO);
        astControl.setFriction(0);
        astControl.setLinearDamping(0);
        astControl.setRestitution(3);
        
        Vector3f astVector = new Vector3f(1, FastMath.nextRandomInt(0, 1),0);
        bigAsteroid.setLocalTranslation(astVector);
        
        astControl.setLinearVelocity(astVector.mult(5f));
        allAsteroids.add(bigAsteroid);
        asteroidCount++;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
      
        
        if(asteroidCount < 3 && ((int)myTimer)%20==0 && (int)myTimer!=0) {
            spawnAsteroids();
            myTimer*=0;
        }
        
        
 
        
        myTimer += tpf;
    }
    
    public void collision(PhysicsCollisionEvent event) {
        for (Node eachAsteroid : allAsteroids) {
            
        if ((event.getNodeA().getName().equals(eachAsteroid.getName()) && event.getNodeB().getName().equals("wall1"))||
                (event.getNodeB().getName().equals("wall1") && event.getNodeA().getName().equals(eachAsteroid.getName()))) {
            RigidBodyControl astControl = eachAsteroid.getControl(RigidBodyControl.class);
            Vector3f speed = astControl.getLinearVelocity();
            speed.setY(-speed.getY());
          
            astControl.setLinearVelocity(speed);
            
        }
        if ((event.getNodeA().getName().equals(eachAsteroid.getName()) && event.getNodeB().getName().equals("wall2"))||
                (event.getNodeB().getName().equals("wall2") && event.getNodeA().getName().equals(eachAsteroid.getName()))) {
            RigidBodyControl astControl = eachAsteroid.getControl(RigidBodyControl.class);
            Vector3f speed = astControl.getLinearVelocity();
            speed.setX(-speed.getX());
            astControl.setLinearVelocity(speed);
        }
        if ((event.getNodeA().getName().equals(eachAsteroid.getName()) && event.getNodeB().getName().equals("wall3"))||
                (event.getNodeB().getName().equals("wall3") && event.getNodeA().getName().equals(eachAsteroid.getName()))) {
            RigidBodyControl astControl = eachAsteroid.getControl(RigidBodyControl.class);
            Vector3f speed = astControl.getLinearVelocity();
            speed.setY(-speed.getY());
            astControl.setLinearVelocity(speed);
        }
        if ((event.getNodeA().getName().equals(eachAsteroid.getName()) && event.getNodeB().getName().equals("wall4"))||
                (event.getNodeB().getName().equals("wall4") && event.getNodeA().getName().equals(eachAsteroid.getName()))) {
            RigidBodyControl astControl = eachAsteroid.getControl(RigidBodyControl.class);
            Vector3f speed = astControl.getLinearVelocity();
            speed.setX(-speed.getX());
            astControl.setLinearVelocity(speed);
        }
        }
    }

}
