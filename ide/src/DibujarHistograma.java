import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JTabbedPane;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @authors
 * José Manuel Alvarez García - CI 25038805
 * José Gregorio Castro Lazo - CI 24635907
 */

public class DibujarHistograma {
    private static int calcularMedia(Color color){
        int mediaColor;
        mediaColor=(int)((color.getRed()+color.getGreen()+color.getBlue())/3);
        return mediaColor;
    }     

    public static int[][] histograma(BufferedImage imagen){
        Color colorAuxiliar;
        int histogramaReturn[][]=new int[5][256];
        for( int i = 0; i < imagen.getWidth(); i++ ){
            for( int j = 0; j < imagen.getHeight(); j++ ){                                
                colorAuxiliar = new Color(imagen.getRGB(i, j));
                histogramaReturn[0][colorAuxiliar.getRed()]+=1;
                histogramaReturn[1][colorAuxiliar.getGreen()]+=1;
                histogramaReturn[2][colorAuxiliar.getBlue()]+=1;
                histogramaReturn[3][colorAuxiliar.getAlpha()]+=1;
                histogramaReturn[4][calcularMedia(colorAuxiliar)]+=1;
            }
        }
        return histogramaReturn;
    }    
    
    public static void crearHistograma(int[] histograma,JTabbedPane jPanelHistograma,Color colorBarras) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String serie = "Número de píxeles";
        int N = histograma.length;              
        int NM = 255;
        int sum = 0;
        int lut[] = new int[histograma.length];
        for (int k=0; k<N; k++) {
            sum += histograma[k];
            lut[k] = sum * 255 / NM;
        }                
        for (int i=0; i<N; i++){ 
            dataset.addValue(histograma[i], serie, Integer.toString(i));
        }        
        JFreeChart chart = ChartFactory.createBarChart("Histograma", null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(false);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, colorBarras);
        chart.setAntiAlias(true);
        chart.setBackgroundPaint(new Color(214, 217, 223)); 
        jPanelHistograma.removeAll();
        jPanelHistograma.repaint();
        jPanelHistograma.setLayout(new java.awt.BorderLayout());
        jPanelHistograma.add(new ChartPanel(chart));
        jPanelHistograma.validate();    
    }
}