package ig3d.tp;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.picking.behaviors.PickZoomBehavior;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


/*----------------*/
/* Classe SceneSU */
/*----------------*/
/**
 * Scène 3D construire sur un SimpleUniverse.
 *
 * @author	<a href="mailto:berro@univ-tlse1.fr">Alain Berro</a>
 */
public class ApplicationTP5 extends JFrame implements KeyListener {
    /*---------*/
    /* Données */
    /*---------*/

    /**
     * Données relatives à la fenêtre.
     */
    private int largeur;	// Taille
    private int hauteur;
    private int posx;		// Position
    private int posy;
    private Light lumiereDirectionnel;
    private Light lumierePonctuelle;

    /**
     * Objets composants la structure principale.
     */
    private SimpleUniverse universe;
    private TransformGroup tgVolume; // Noeud de transformation attaché au volume
    private Canvas3D canvas;
    private BranchGroup racineVolume;

    /*--------------*/
    /* Constructeur */
    /*--------------*/
    public ApplicationTP5(int l,
            int h,
            int px,
            int py) {
        /*----- Instanciation de la fenêtre graphique -----*/
        this.setTitle("Application TP 5");
        this.largeur = l;
        this.hauteur = h;
        this.setSize(largeur, hauteur);
        this.posx = px;
        this.posy = py;
        this.setLocation(posx, posy);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*----- Contenu de la fenêtre -----*/
        Container conteneur = getContentPane();
        conteneur.setLayout(new BorderLayout());

        /*----- Création du Canvas -----*/
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        conteneur.add("Center", canvas);
        canvas.addKeyListener(this);

        /*----- Création de l'univers virtuel -----*/
        this.universe = new SimpleUniverse(canvas);

        /*----- Position du volume -----*/
        this.tgVolume = new TransformGroup();

        //TP2
        /*----- Orientation de la scène -----*/
        Transform3D t3d_rotScene1 = new Transform3D();
        t3d_rotScene1.rotX(Math.PI / 8);
        Transform3D t3d_rotScene2 = new Transform3D();
        t3d_rotScene2.rotY(Math.PI / 5);

        t3d_rotScene1.mul(t3d_rotScene2);

        this.tgVolume.setTransform(t3d_rotScene1);

        /*----- Position de l'observateur -----*/
        Transform3D t3d_oeil = new Transform3D();
        t3d_oeil.set(new Vector3d(0.0, 0.0, 10.0));//Par défaut : 10.0 

        this.universe.getViewingPlatform().getViewPlatformTransform().setTransform(t3d_oeil);

        /*----- Création du noeud de branchement du volume -----*/
        racineVolume = new BranchGroup();
        racineVolume.addChild(this.tgVolume);

        //<============================================== createBrancheVolume
        this.tgVolume.addChild(this.createBrancheVolume());

        //TP4
        this.tgVolume.addChild(setDirectionalLight()); //1)
        this.tgVolume.addChild(setPonctualLight()); //2)          

        //TP5
        /*----- Ajout des manipulations avec la souris -----*/
        //Si active => Ne permet pas de faire le picking
        //manipulationSouris(tgVolume,racineVolume);
        /*----- Ajout de la navigation avec le clavier -----*/
        navigationClavier(tgVolume, racineVolume);
        /*----- Ajout de la branche de volume -----*/
        this.universe.addBranchGraph(racineVolume);

        /*----- Rend la fenêtre visible -----*/
        this.setVisible(true);
    }


    /*----------*/
    /* Méthodes */ //Fonction Principale
    /*----------*/
    /**
     * Création du volume.
     */
    private BranchGroup createBrancheVolume() {
        /*----- Création du noeud racine -----*/
        BranchGroup racine = new BranchGroup();

        /*----- Création du Volume -----*/
        //Cylindre, cone, sphere
        tp5(racine, 5, 5, 5);

        /*----- Optimisation du graphe de scène -----*/
        racine.compile();
        return racine;
    }

    /**
     * 1. Point, antialiasing disable, couleur rouge, taille 4 pixels. 2. Point,
     * antialiasing enable, couleur rouge, taille 4 pixels. 3. Ligne,
     * antialiasing disable, couleur bleu, épaisseur 1 pixel. 4. Ligne,
     * antialiasing enable, couleur bleu, épaisseur 1 pixel. 5. Polygone,
     * couleur bleu, sans interpolation des normales (FLAT). 6. Polygone,
     * couleur bleu, avec interpolation des normales (GOURAUD). 7. Polygone,
     * couleur jaune, transparent (opacité = 0.8). 8. Polygone, couleur jaune,
     * transparent (opacité = 0.1).
     */
    public Appearance createAppearance(int numero) {
        /*----- --- -----*/
        /*----- TP3 -----*/ //OK
        /*----- --- -----*/
        Appearance aspect = new Appearance();

        //_TP4
        Color3f couleurAmbiante = new Color3f();
        Color3f couleurDiffuse = new Color3f();
        Color3f couleurSpeculaire = new Color3f();
        Color3f couleurEmise = new Color3f(0.0f, 0.0f, 1.0f);
        float brillance = 64;

        TransparencyAttributes t_attr1 = new TransparencyAttributes(
                TransparencyAttributes.BLENDED, 0.3f,
                TransparencyAttributes.BLEND_SRC_ALPHA,
                TransparencyAttributes.BLEND_ONE);
        TransparencyAttributes t_attr2 = new TransparencyAttributes(
                TransparencyAttributes.BLENDED, 0.7f,
                TransparencyAttributes.BLEND_SRC_ALPHA,
                TransparencyAttributes.BLEND_ONE);
        float opaque = 1.0f;
        float transparenteVal = 0.0f;
        TransparencyAttributes transparence = new TransparencyAttributes(TransparencyAttributes.SCREEN_DOOR, transparenteVal);

//        Material material = new Material(couleurAmbiante, couleurEmise, couleurDiffuse, couleurSpeculaire, brillance);
        Material m = new Material();
        m.setEmissiveColor(couleurEmise);
        //_TP4

        switch (numero) {
            case 1:
                PolygonAttributes poly = new PolygonAttributes();
                //Attribut d'apparence
                poly.setPolygonMode(PolygonAttributes.POLYGON_POINT);
                aspect.setPolygonAttributes(poly);

                //taille pixel,antialiasing
                PointAttributes point = new PointAttributes(4, false);
                aspect.setPointAttributes(point);

                ColoringAttributes color = new ColoringAttributes();
                color.setColor(1.0f, 0.0f, 0.0f);
                aspect.setColoringAttributes(color);
                break;

            case 2:
                PolygonAttributes poly2 = new PolygonAttributes();
                poly2.setPolygonMode(PolygonAttributes.POLYGON_POINT);
                aspect.setPolygonAttributes(poly2);

                PointAttributes point2 = new PointAttributes(4, true);
                aspect.setPointAttributes(point2);

                ColoringAttributes color2 = new ColoringAttributes();
                color2.setColor(1.0f, 0.0f, 0.0f);
                aspect.setColoringAttributes(color2);
                break;

            case 3:
                PolygonAttributes poly3 = new PolygonAttributes();
                poly3.setPolygonMode(PolygonAttributes.POLYGON_LINE);
                aspect.setPolygonAttributes(poly3);

                PointAttributes point3 = new PointAttributes(1, false);
                aspect.setPointAttributes(point3);

                ColoringAttributes color3 = new ColoringAttributes();
                color3.setColor(0.0f, 0.0f, 1.0f);
                aspect.setColoringAttributes(color3);
                break;

            case 4:
                PolygonAttributes poly4 = new PolygonAttributes();
                poly4.setPolygonMode(PolygonAttributes.POLYGON_LINE);
                aspect.setPolygonAttributes(poly4);

                PointAttributes point4 = new PointAttributes(1, false);
                aspect.setPointAttributes(point4);

                ColoringAttributes color4 = new ColoringAttributes();
                color4.setColor(0.0f, 0.0f, 1.0f);
                aspect.setColoringAttributes(color4);
                break;

            case 5: //FLAT    
                //     Polygone, couleur bleu, sans interpolation des normales (FLAT).
                PolygonAttributes poly5 = new PolygonAttributes();
                poly5.setPolygonMode(PolygonAttributes.POLYGON_FILL);
                aspect.setPolygonAttributes(poly5);

                ColoringAttributes color5 = new ColoringAttributes(new Color3f(Color.blue), ColoringAttributes.SHADE_FLAT);
                aspect.setColoringAttributes(color5);
                //_TP4
                aspect.setMaterial(m);
                aspect.setTransparencyAttributes(t_attr1);

                break;
            case 6: //GOURRAUD
                //     Polygone, couleur bleu, avec interpolation des normales (GOURAUD).
                PolygonAttributes poly6 = new PolygonAttributes();
                poly6.setPolygonMode(PolygonAttributes.POLYGON_FILL);
                aspect.setPolygonAttributes(poly6);

                ColoringAttributes color6 = new ColoringAttributes(new Color3f(Color.blue), ColoringAttributes.SHADE_GOURAUD);
                aspect.setColoringAttributes(color6);
                //_TP4
                aspect.setMaterial(m);
                aspect.setTransparencyAttributes(transparence);
                aspect.setTransparencyAttributes(t_attr2);
                break;
            case 7:
                //     Polygone, couleur jaune, transparent (opacité = 0.8).
                PolygonAttributes poly7 = new PolygonAttributes();
                poly7.setPolygonMode(PolygonAttributes.POLYGON_FILL);
                aspect.setPolygonAttributes(poly7);

                TransparencyAttributes transparencyAttributes1 = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.8f);
                aspect.setTransparencyAttributes(transparencyAttributes1);

                ColoringAttributes color7 = new ColoringAttributes(new Color3f(Color.yellow), ColoringAttributes.SHADE_GOURAUD);
                aspect.setColoringAttributes(color7);
                break;
            case 8:
                //     Polygone, couleur jaune, transparent (opacité = 0.1).
                PolygonAttributes poly8 = new PolygonAttributes();
                poly8.setPolygonMode(PolygonAttributes.POLYGON_FILL);
                aspect.setPolygonAttributes(poly8);

                TransparencyAttributes transparencyAttributes2 = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.1f);
                aspect.setTransparencyAttributes(transparencyAttributes2);

                ColoringAttributes color8 = new ColoringAttributes(new Color3f(Color.yellow), ColoringAttributes.SHADE_GOURAUD);
                aspect.setColoringAttributes(color8);
                break;
        }
        return aspect;
    }

    public static void main(String s[]) {
        /*----- Fenêtre -----*/
//		new SceneSU(200,200,0,0);
        new ApplicationTP5(600, 600, 0, 0);
    }

    private void tp5(BranchGroup racine, int n1, int n2, int n3) {

        /**
         * * Les Transform3D **
         */
        //3 unité au dessus => y
        Transform3D t3d_1 = new Transform3D();
        t3d_1.set(new Vector3d(0.0, 3.0, 0.0));

        //3 unité à droite => x
        Transform3D t3d_2 = new Transform3D();
        t3d_2.set(new Vector3d(3.0, 0.0, 0.0));

        //2 unité devant => z
        Transform3D t3d_3 = new Transform3D();
        t3d_3.set(new Vector3d(0.0, 0.0, 2.0));

        /**
         * * Les TransformGoup **
         */
        //------- ColorCube -------//
        TransformGroup tg_0 = new TransformGroup();
        ColorCube colorCube = new ColorCube();
        //colorCube.setAppearance(createAppearance(5)); 
        //=> IMPOSSIBLE, car objet par défaut!
        tg_0.addChild(colorCube);

        //------- Cylinder -------//
        TransformGroup tg_1 = new TransformGroup(t3d_1);
        //!\ Il faut obligatoirement définir une apparence => sauf pour le ColorCube
        Cylinder cylindre = new Cylinder();
        cylindre.setAppearance(createAppearance(n1));//Là
        tg_1.addChild(cylindre);

        //------- Cone -------//
        TransformGroup tg_2 = new TransformGroup(t3d_2);
        Cone cone = new Cone();
        cone.setAppearance(createAppearance(n2));//Là
        tg_2.addChild(cone);

        //------- Sphere -------//
        TransformGroup tg_3 = new TransformGroup(t3d_3);
        Sphere sphere = new Sphere((float) 0.5, createAppearance(n3));//Là
        tg_3.addChild(sphere);

        /**
         * Picking
         */
        System.out.println("picking activé");
        pickingAll(racine, tg_0);
        pickingAll(racine, tg_1);
        pickingAll(racine, tg_2);
        pickingAll(racine, tg_3);

        /**
         * Behavior
         */
        /*
         (*)EX TP5 OK
         SimpleBehavior monComportement = new SimpleBehavior(tg_3);
         TransformGroup targetTG = new TransformGroup();
         targetTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         targetTG.addChild(tg_3);
         */
        
        
        //Faire tourner la sphere
        SimpleBehavior monComportement;
        //Version normale
//        monComportement = new SimpleBehavior(tg_3);
        //Version ++
//        monComportement = new SimpleBehavior(tg_3, t3d_3);
        //Version +++
//        monComportement = new SimpleBehavior(tg_3, t3d_3, true);
        //Version extreme
//        long time = 1000/24;//Permet de créer le mouvement 24 Actions en 1 sec.
//        long time = 1000; double angle = 0.1;
        /*
            Je peux à present:
            1er champ  : choisir le TransformGroup sur lequel appliqué mon comportement 
            2nd champ  : choisir la Transform3D que je veux appliqué à mon comportement 
                         en plus de la rotation ( = ici à animation).
            3ème champ : choisir si true or false, j'active l'animation,
                         ou s'il faut appuyer sur une touche pour l'effectuer.
            4ème champ : choisir l'angle de rotation
            5ème champ : choisir le temps entre chaque rotation
            6ème champ : choisir sur quel axe s'effectue la rotation        
        */
//        monComportement = new SimpleBehavior(tg_3, t3d_3, true, angle, time,"x");
        monComportement = new SimpleBehavior(tg_3, t3d_3, true, 0.1, 1000/24,"x");
        monComportement.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

        tg_3.addChild(monComportement);
//        targetTG.addChild(monComportement);

        /**
         * * Liaison des transforma entre eux **
         */
//        tg_2.addChild(targetTG);//(*)EX TP5 OK
        tg_2.addChild(tg_3);
        tg_0.addChild(tg_2);
        tg_0.addChild(tg_1);
        racine.addChild(tg_0);
    }

    private Light setDirectionalLight() {
        Color3f coleurBlanche = new Color3f(1, 1, 1);
        Vector3f direction = new Vector3f(-1.0f, -1.0f, -1.0f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        this.lumiereDirectionnel = new DirectionalLight(true, coleurBlanche, direction);
        this.lumiereDirectionnel.setInfluencingBounds(bounds);
        this.lumiereDirectionnel.setCapability(Light.ALLOW_STATE_WRITE);
        this.lumiereDirectionnel.setCapability(Light.ALLOW_STATE_READ);
        this.lumiereDirectionnel.setCapability(Light.ALLOW_COLOR_WRITE);
        this.lumiereDirectionnel.setCapability(Light.ALLOW_COLOR_READ);
        return lumiereDirectionnel;
    }

    private Light setPonctualLight() {
        Color3f coleurBlanche = new Color3f(1, 1, 1);
        Point3f position = new Point3f(0.0f, 0.0f, 0.0f);
        Point3f attenuation = new Point3f(1.0f, 0.0f, 0.0f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        this.lumierePonctuelle = new PointLight(true, coleurBlanche, position, attenuation);
        this.lumierePonctuelle.setInfluencingBounds(bounds);
        this.lumierePonctuelle.setCapability(Light.ALLOW_STATE_WRITE);
        this.lumierePonctuelle.setCapability(Light.ALLOW_STATE_READ);
        return lumierePonctuelle;
    }

    public Light getLumiereDirectionnel() {
        return lumiereDirectionnel;
    }

    /*---------------------------------------------------*/
    /* Définition des méthodes abstraites de KeyListener */
    /*---------------------------------------------------*/
    @Override
    public void keyTyped(KeyEvent e) {
        Color3f coleurBlanche = new Color3f(1, 1, 1);

        if (e.getKeyChar() == 'a') {
            this.lumiereDirectionnel.setEnable(true);
        }

        if (e.getKeyChar() == 'e') {
            this.lumiereDirectionnel.setEnable(false);
        }

        if (e.getKeyChar() == 'z') {
            this.lumierePonctuelle.setEnable(true);
        }

        if (e.getKeyChar() == 'r') {
            this.lumierePonctuelle.setEnable(false);
        }

        if (e.getKeyChar() == '+') {
            this.lumiereDirectionnel.getColor(coleurBlanche);
            if (coleurBlanche.x < 1.0f && coleurBlanche.y < 1.0f && coleurBlanche.z < 1.0f) {
                coleurBlanche.x += 0.1f;
                coleurBlanche.y += 0.1f;
                coleurBlanche.z += 0.1f;
                this.lumiereDirectionnel.setColor(coleurBlanche);
            } else {
                System.out.println("Vous ne pouvez plus augmenter l'intensité");
            }

            this.getLumiereDirectionnel();
        }

        if (e.getKeyChar() == '-') {
            this.lumiereDirectionnel.getColor(coleurBlanche);
            if (coleurBlanche.x > 0.0 && coleurBlanche.y > 0.0 && coleurBlanche.z > 0.0) {
                coleurBlanche.x -= 0.1f;
                coleurBlanche.y -= 0.1f;
                coleurBlanche.z -= 0.1f;
                this.lumiereDirectionnel.setColor(new Color3f(coleurBlanche.x, coleurBlanche.y, coleurBlanche.z));
            } else {
                System.out.println("Vous ne pouvez plus diminuer l'intensité");
            }
        }

//        if (e.getKeyChar() == 't') {
//
//        }
//
//        if (e.getKeyChar() == 'y') {
//            
//        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    //TP5
    //Souris
    private void manipulationSouris(TransformGroup tgVolume, BranchGroup racineVolume) {
        /*----- Ajout d'un comportement souris -----*/
        tgVolume.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tgVolume.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        MouseTranslate mouse1 = new MouseTranslate(tgVolume);
        mouse1.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

        MouseRotate mouse2 = new MouseRotate(tgVolume);
        mouse2.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

        MouseZoom mouse3 = new MouseZoom(tgVolume);
        mouse3.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

        //Ajout à la racineVolume
        racineVolume.addChild(mouse1);
        racineVolume.addChild(mouse2);
        racineVolume.addChild(mouse3);
    }

    //Clavier
    private void navigationClavier(TransformGroup tgVolume, BranchGroup racineVolume) {
        /*----- Ajout de la navigation à l'aide du clavier -----*/
        KeyNavigatorBehavior key = new KeyNavigatorBehavior(tgVolume);
        key.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));

        racineVolume.addChild(key);
    }

    //Picking
    private void pickingAll(BranchGroup racine, TransformGroup tg) {
        pickingTranslate(racine, tg);
        pickingRotate(racine, tg);
        pickingZoom(racine, tg);
    }

    private void pickingTranslate(BranchGroup racine, TransformGroup tg) {
        setCapabilitiesForPicking(tg);
        PickTranslateBehavior pickTranslate = new PickTranslateBehavior(racine, canvas, new BoundingSphere(new Point3d(), 100.0));
        tg.addChild(pickTranslate);
    }

    private void pickingRotate(BranchGroup racine, TransformGroup tg) {
        setCapabilitiesForPicking(tg);
        PickRotateBehavior pickRotate = new PickRotateBehavior(racine, canvas, new BoundingSphere(new Point3d(), 100.0));
        tg.addChild(pickRotate);
    }

    private void pickingZoom(BranchGroup racine, TransformGroup tg) {
        setCapabilitiesForPicking(tg);
        PickZoomBehavior pickZoom = new PickZoomBehavior(racine, canvas, new BoundingSphere(new Point3d(), 100.0));
        tg.addChild(pickZoom);
    }

    //Permet de pouvoir utiliser les comportements de picking
    private void setCapabilitiesForPicking(TransformGroup tg) {
        tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    }

    //Comportement
    /**
     * Classe permettant de définir un comportement.
     * SimpleBehavior(TransformGroup targetTG, Transform3D tSet, boolean animationSet, double angleMove, long time, String axeDeRotation)
     * 1er champ  : choisir le TransformGroup sur lequel appliqué mon comportement 
     * 2nd champ  : choisir la Transform3D que je veux appliqué à mon comportement 
     *              en plus de la rotation ( = ici à animation).
     * 3ème champ : choisir si true or false, j'active l'animation,
     *              ou s'il faut appuyer sur une touche pour l'effectuer.
     * 4ème champ : choisir l'angle de rotation
     * 5ème champ : choisir le temps entre chaque rotation
     * 6ème champ : choisir sur quel axe s'effectue la rotation
     */
    public class SimpleBehavior extends Behavior {

        private TransformGroup targetTG;
        private Transform3D rotation = new Transform3D();
        private Transform3D tSet;
        private double angle = 0.0;
        private double angleMove = 0.1;
        private boolean animationSet = false;
        private long time = 2 * 1000;
        private String axeDeRotation = "y";

        // create SimpleBehavior - set TG object of change
        SimpleBehavior(TransformGroup targetTG) {
            this.targetTG = targetTG;
        }

        //tSet : transformation reçue
        public SimpleBehavior(TransformGroup targetTG, Transform3D tSet) {
            this.targetTG = targetTG;
            this.tSet = tSet;
        }

        //animationSet : permet de préciser si on fait une animation ou pas
        //Si ce n'est pas une animation, alors il faut appuyer sur une touche!
        public SimpleBehavior(TransformGroup targetTG, Transform3D tSet, boolean animationSet) {
            this.targetTG = targetTG;
            this.tSet = tSet;
            this.animationSet = animationSet;
        }

        //Permet de définir l'angle et le temps de l'animation
        //Plus le temps est petit, et plus l'objet va subir de fois la rotation de l'angle
        //[+] Amélioration possible : décider si la rotation s'effectue sur les X ou les Y ^^
        public SimpleBehavior(TransformGroup targetTG, Transform3D tSet, boolean animationSet, double angleMove, long time, String axeDeRotation) {
            this.targetTG = targetTG;
            this.tSet = tSet;
            this.animationSet = animationSet;
            this.angleMove = angleMove;
            this.time = time;
            this.axeDeRotation = axeDeRotation;
        }

        // initialize the Behavior
        // set initial wakeup condition
        // called when behavior becomes live
        @Override
        public void initialize() {
            // set initial wakeup condition
            animation(animationSet);
//            this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.VK_SPACE));//Don't work

        }

        // called by Java 3D when appropriate stimulus occurs
        @Override
        public void processStimulus(Enumeration criteria) {
            // do what is necessary in response to stimulus
            angle += angleMove;
            decideAxeRotation(axeDeRotation);

            //On fusionne la rotation avec la transformation reçue
            tSet.mul(rotation);
            //Si on change l'ordre, ça donne qq chose de !=
//            rotation.mul(tSet); //<= un ex
            targetTG.setTransform(tSet);
            animation(animationSet);

        }

        private void animation(boolean b) {
            if (b) {
                //Toutes les 2 secondes
                this.wakeupOn(new WakeupOnElapsedTime(time));
            } else {
                this.wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
            }
        }

        private void decideAxeRotation(String axeDeRotation) {
            if (axeDeRotation.equals("x")) {
                rotation.rotX(angle);
            } else if (axeDeRotation.equals("y")) {
                rotation.rotY(angle);
            } else if (axeDeRotation.equals("z")) {
                rotation.rotZ(angle);
            } else {
                //par défaut
                rotation.rotY(angle);
            }
        }

    } // end of class SimpleBehavior

} /*----- Fin de la classe Application5 -----*/
