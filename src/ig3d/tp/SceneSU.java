package ig3d.tp;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
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
public class SceneSU extends JFrame implements KeyListener {
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


    /*--------------*/
    /* Constructeur */
    /*--------------*/
    public SceneSU(int l,
            int h,
            int px,
            int py) {
        /*----- Instanciation de la fenêtre graphique -----*/
        this.setTitle("Visualisation 3D");
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
        Canvas3D canvas = new Canvas3D(config);
        conteneur.add("Center", canvas);
        canvas.addKeyListener(this);

        /*----- Création de l'univers virtuel -----*/
        this.universe = new SimpleUniverse(canvas);

        /*----- Position du volume -----*/
        this.tgVolume = new TransformGroup();

        this.tgVolume.addChild(this.createBrancheVolume());
        //TP4
        this.tgVolume.addChild(setDirectionalLight()); //1)
        this.tgVolume.addChild(setPonctualLight()); //2)

        //TP2
        /*----- Orientation de la scène -----*/
        //---
        Transform3D t3d_rotScene1 = new Transform3D();
        t3d_rotScene1.rotX(Math.PI / 8);
        Transform3D t3d_rotScene2 = new Transform3D();
        t3d_rotScene2.rotY(-Math.PI / 3);

        t3d_rotScene1.mul(t3d_rotScene2);

        this.tgVolume.setTransform(t3d_rotScene1);
        //---

        /*----- Position de l'observateur -----*/
        Transform3D t3d_oeil = new Transform3D();
        t3d_oeil.set(new Vector3d(0.0, 0.0, 10.0));//Par défaut : 10.0 

        this.universe.getViewingPlatform().getViewPlatformTransform().setTransform(t3d_oeil);

        /*----- Création du noeud de branchement du volume -----*/
        BranchGroup racineVolume = new BranchGroup();
        racineVolume.addChild(this.tgVolume);

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
//		racine.addChild(new ColorCube()); //Au début
//        tp1(racine);
//        tp2(racine);
//        tp3(racine,1); //Seconde val: De 1 à 8
//        tp4_1(racine, 5); //Seconde val: 5 ou 6
//        tp4_1(racine, 6);
        //=> Grâce à la lumière on peut maintenant distinguer les apparences 5 et 6
        tp4_2(racine, 5); //Seconde val: 5 ou 6

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
        new SceneSU(600, 600, 0, 0);
    }

    private void tp4_2(BranchGroup racine, int numero) {
        //3 unité au dessus => y
        Transform3D t3d_1 = new Transform3D();
        t3d_1.set(new Vector3d(0.0, 3.0, 0.0));

        //3 unité à droite => x
        Transform3D t3d_2 = new Transform3D();
        t3d_2.set(new Vector3d(3.0, 0.0, 0.0));

        //2 unité devant => z
        Transform3D t3d_3 = new Transform3D();
        t3d_3.set(new Vector3d(0.0, 0.0, 2.0));

        TransformGroup tg_0 = new TransformGroup();
        ColorCube colorCube = new ColorCube();
        //colorCube.setAppearance(createAppearance(5)); 
        //=> IMPOSSIBLE, car objet par défaut!
        tg_0.addChild(colorCube);

        TransformGroup tg_1 = new TransformGroup(t3d_1);
        //!\ il faut définir une apparence => sauf pour le ColorCube
        Cylinder cylindre = new Cylinder();
        cylindre.setAppearance(createAppearance(numero));//Là
        tg_1.addChild(cylindre);

        TransformGroup tg_2 = new TransformGroup(t3d_2);
        Cone cone = new Cone();
        cone.setAppearance(createAppearance(numero));//Là
        tg_2.addChild(cone);

        TransformGroup tg_3 = new TransformGroup(t3d_3);
        Sphere sphere = new Sphere((float) 0.5, createAppearance(numero));//Là
        tg_3.addChild(sphere);

        tg_2.addChild(tg_3);
        tg_0.addChild(tg_2);
        tg_0.addChild(tg_1);
        racine.addChild(tg_0);
    }

    /**
     * I - Positionnement des sources de lumière
     *
     * 1.Dans la partie précédente, nous n'avons pas pu voir la différence entre
     * les apparences 5 et 6. Placez une source directionnelle blanche, allumée
     * et de direction (-1,-1,-1) puis ajoutez aux apparences 5 et 6 l'attribut
     * matière avec les caractéristiques suivantes : couleur ambiante, diffuse,
     * spéculaire, et brillance par défaut, couleur émise bleue.
     *
     * Constatez la différence entre un objet d'apparence 5 et un objet
     * d'apparence 6.
     *
     * 2.Créez une scène avec plusieurs objets et au moins deux sources
     * lumineuses => une directionnelle et une ponctuelle. Positionnez les
     * objets de façon à pouvoir deviner la position et éventuellement le type
     * des sources de lumière. Ajoutez également des effets de transparence.
     *
     * II - Interaction avec les sources lumineuses
     *
     * Réalisez un mécanisme permettant d'éteindre et d'allumer les sources
     * lumineuses à l'aide des touches du clavier (Cet exercice fait appel à des
     * notions extérieures à cette API). Réalisez un mécanisme permettant de
     * baisser et d'augmenter l'intensité de la source lumineuse directionnelle
     * à l'aide des touches du clavier (Lumière halogène).
     *
     */
    private void tp4_1(BranchGroup racine, int i) {
        /*----- --- -----*/
        /*----- TP4 -----*/ //OK
        /*----- --- -----*/
        tp3(racine, i);
    }

    private void tp3(BranchGroup racine, int numero) {
        /*----- --- -----*/
        /*----- TP3 -----*/ //OK
        /*----- --- -----*/

        TransformGroup tg = new TransformGroup();
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(1));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(2));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(3));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(4));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(5));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(6));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(7));
//        Sphere sphere = new Sphere((float) 0.5, createAppearance(8));
        Sphere sphere = new Sphere();
        sphere.setAppearance(createAppearance(numero));
        tg.addChild(sphere);

        racine.addChild(tg);
    }

    private void tp2(BranchGroup racine) {
        /*----- --- -----*/
        /*----- TP2 -----*/ //OK
        /*----- --- -----*/

        //3 unité au dessus => y
        Transform3D t3d_1 = new Transform3D();
        t3d_1.set(new Vector3d(0.0, 3.0, 0.0));

        //3 unité à droite => x
        Transform3D t3d_2 = new Transform3D();
        t3d_2.set(new Vector3d(3.0, 0.0, 0.0));

        //2 unité devant => z
        Transform3D t3d_3 = new Transform3D();
        t3d_3.set(new Vector3d(0.0, 0.0, 2.0));

        TransformGroup tg_0 = new TransformGroup();
        tg_0.addChild(new ColorCube());

        TransformGroup tg_1 = new TransformGroup(t3d_1);
        //!\ il faut définir une apparence => sauf pour le ColorCube
        Cylinder cylindre = new Cylinder();
        cylindre.setAppearance(new Appearance());
        tg_1.addChild(cylindre);

        TransformGroup tg_2 = new TransformGroup(t3d_2);
        Cone cone = new Cone();
        cone.setAppearance(new Appearance());
        tg_2.addChild(cone);

        TransformGroup tg_3 = new TransformGroup(t3d_3);
        Sphere sphere = new Sphere((float) 0.5, new Appearance());
        tg_3.addChild(sphere);

        tg_2.addChild(tg_3);
        tg_0.addChild(tg_2);
        tg_0.addChild(tg_1);
        racine.addChild(tg_0);
    }

    private void tp1(BranchGroup racine) {
        /*----- --- -----*/
        /*----- TP1 -----*/ //OK
        /*----- --- -----*/

        /*----- Translation -----*/ //OK
        Transform3D t3d_1 = new Transform3D();
        t3d_1.set(new Vector3d(0.0, 0.0, 5.0));

        TransformGroup tg_1 = new TransformGroup(t3d_1);
        tg_1.addChild(new ColorCube());

        racine.addChild(tg_1);
        /*----- Rotation -----*/ //OK
        Transform3D t3d_2 = new Transform3D();
        t3d_2.rotX(Math.PI / 6);

        TransformGroup tg_2 = new TransformGroup(t3d_2);
        tg_2.addChild(new ColorCube());

        racine.addChild(tg_2);

        //A finir 2), 3), 4)
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
//        System.out.println("typed");
        if (e.getKeyChar() == 'a') {
//            System.out.println("a");
            this.lumiereDirectionnel.setEnable(true);
        }

        if (e.getKeyChar() == 'z') {
//            System.out.println("z");
            this.lumiereDirectionnel.setEnable(false);
        }
        if (e.getKeyChar() == '+') {
            this.lumiereDirectionnel.getColor(coleurBlanche);
//            System.out.println("[IN] x:" + coleurBlanche.x + ", y:" + coleurBlanche.y + ", z:" + coleurBlanche.z);
            if (coleurBlanche.x < 1.0f && coleurBlanche.y < 1.0f && coleurBlanche.z < 1.0f) {
//                System.out.println("+ OK");
                coleurBlanche.x += 0.1f;
                coleurBlanche.y += 0.1f;
                coleurBlanche.z += 0.1f;
                this.lumiereDirectionnel.setColor(coleurBlanche);
            } else {
                System.out.println("Vous ne pouvez plus augmenter l'intensité");
            }
//            System.out.println("[OUT] x:" + coleurBlanche.x + ", y:" + coleurBlanche.y + ", z:" + coleurBlanche.z);

            this.getLumiereDirectionnel();
        }
        if (e.getKeyChar() == '-') {
            this.lumiereDirectionnel.getColor(coleurBlanche);
//            System.out.println("[IN] x:" + coleurBlanche.x + ", y:" + coleurBlanche.y + ", z:" + coleurBlanche.z);
            if (coleurBlanche.x > 0.0 && coleurBlanche.y > 0.0 && coleurBlanche.z > 0.0) {
//                System.out.println("- OK");
                coleurBlanche.x -= 0.1f;
                coleurBlanche.y -= 0.1f;
                coleurBlanche.z -= 0.1f;
//                lumiereDirectionnel.setColor(coleurBlanche);
                this.lumiereDirectionnel.setColor(new Color3f(coleurBlanche.x, coleurBlanche.y, coleurBlanche.z));
            } else {
                System.out.println("Vous ne pouvez plus diminuer l'intensité");
            }
//            System.out.println("[OUT] x:" + coleurBlanche.x + ", y:" + coleurBlanche.y + ", z:" + coleurBlanche.z);
        }
        if (e.getKeyChar() == 'e') {
//            System.out.println("e");
            this.lumierePonctuelle.setEnable(true);
        }
        if (e.getKeyChar() == 'r') {
//            System.out.println("r");
            this.lumierePonctuelle.setEnable(false);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

} /*----- Fin de la classe SceneSU -----*/
