package ig3d.tp;

import java.awt.Color;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;


/*-----------------*/
/* Classe J3dUtils */
/*-----------------*/
/**
 * Classe utilitaire.
 *
 * @author	<a href="mailto:berro@univ-tlse1.fr">Alain Berro</a>
 * @version	26 janvier 2011
 */
public class J3dUtils
{
	/*---------*/
	/* Données */
	/*---------*/

	public static final Color OR = new Color(255,215,0);
	public static final Color CUIVRE_ROUGE = new Color(179,103,0);
	public static final Color BRONZE = new Color(97,78,26);
	public static final Color ETAIN = new Color(237,237,237);
	public static final Color ARGENT = new Color(206,206,206);

	public static Material MAT_OBJET_BLEU = new Material(new Color3f(0.2f,0.2f,0.2f),	//ambiante
														 new Color3f(0.0f,0.0f,1.0f),	//emise
														 new Color3f(1.0f,1.0f,1.0f),	//diffuse
														 new Color3f(1.0f,1.0f,1.0f),	//speculaire
														 32.0f);						//brillance

	public static Material MAT_PLASTIQUE_NOIR = new Material(new Color3f(0.0f,0.0f,0.00f),
															 new Color3f(0.0f,0.0f,0.0f),
															 new Color3f(0.01f,0.01f,0.01f),
															 new Color3f(0.5f,0.5f,0.5f),
															 32.0f);

	public static Material MAT_CUIVRE_ROUGE = new Material(new Color3f(0.19125f,0.0735f,0.0225f),
														   new Color3f(CUIVRE_ROUGE),
														   new Color3f(0.7038f,0.27048f,0.0828f),
														   new Color3f(0.256777f,0.137622f,0.086014f),
														   12.4f);

	public static Material MAT_OR = new Material(new Color3f(0.24725f,0.1995f,0.0745f),
												 new Color3f(OR),
												 new Color3f(0.75164f,0.60648f,0.22468f),
												 new Color3f(0.628281f,0.555802f,0.366065f),
												 51.2f);

	public static Material MAT_BRONZE = new Material(new Color3f(0.2125f,0.1275f,0.054f),
													 new Color3f(BRONZE),
													 new Color3f(0.714f,0.4284f,0.18144f),
													 new Color3f(0.393548f,0.271906f,0.166721f),
													 25.6f);

	public static Material MAT_ETAIN = new Material(new Color3f(0.10588f,0.058824f,0.113725f),
													new Color3f(ETAIN),
													new Color3f(0.427451f,0.470588f,0.541176f),
													new Color3f(0.3333f,0.3333f,0.521569f),
													9.84615f);

	public static Material MAT_ARGENT = new Material(new Color3f(0.19225f,0.19225f,0.19225f),
												 	 new Color3f(ARGENT),
													 new Color3f(0.50754f,0.50754f,0.50754f),
													 new Color3f(0.508273f,0.508273f,0.508273f),
													 51.2f);

} /*----- Fin de la classe J3dUtils -----*/
