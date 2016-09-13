import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
       
/**
 *
 * @authors
 * José Manuel Alvarez García - CI 25038805
 * José Gregorio Castro Lazo - CI 24635907
 */

public class processImage {

    private processImage() {}

    public static int imageHeight;
    public static int imageWidth;
    public static byte imageBytes[];
    public static Image image; //Imagen actual
    public static InputStream imageFile;
    public static int imageSize;
    public static String informacion;
    public static double multKernel;
    
    public static void mostrarInfo() {        
        JOptionPane.showMessageDialog(new JFrame(), informacion, "Información de la imagen original",JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static Image loadImage (String pathImageFile, Component c) throws FileNotFoundException {
        imageFile = new FileInputStream(pathImageFile);
        try {
            byte bmpHeader[] = new byte[14];    //Cabecera del archivo bitmap
            imageFile.read(bmpHeader,0,14);
            byte bmpInfo[] = new byte[40];      //Información más específica del archivo bitmap
            imageFile.read(bmpInfo,0,40);
            imageWidth = (((int)bmpInfo[7]&0xff)<<24) | (((int)bmpInfo[6]&0xff)<<16) | (((int)bmpInfo[5]&0xff)<<8) | (int)bmpInfo[4]&0xff;  //Ancho de la imagen
            imageHeight = (((int)bmpInfo[11]&0xff)<<24) | (((int)bmpInfo[10]&0xff)<<16) | (((int)bmpInfo[9]&0xff)<<8) | (int)bmpInfo[8]&0xff;   //Alto de la imagen
            int bits = (((int)bmpInfo[15]&0xff)<<8) | (int)bmpInfo[14]&0xff;    //Cantidad de bits del archivo bitmap
            imageSize = (((int)bmpInfo[23]&0xff)<<24) | (((int)bmpInfo[22]&0xff)<<16) | (((int)bmpInfo[21]&0xff)<<8) | (int)bmpInfo[20]&0xff;   //Tamaño de la imagen
            int colorsUsed = (((int)bmpInfo[35]&0xff)<<24) | (((int)bmpInfo[34]&0xff)<<16) | (((int)bmpInfo[33]&0xff)<<8) | (int)bmpInfo[32]&0xff;  //Cantidad de colores en caso de tener paleta
            informacion ="";
            informacion += "Alto: "+imageHeight+"\n"
                    + "Ancho: "+imageWidth+"\n"
                    + "Número de bits: "+bits+"\n"
                    + "Tamaño: "+imageSize*Math.pow(10, -6)+" MBs \n";                    
            switch (bits) {
                case 24:
                    {
                        //Se consideran 3 bytes por píxel
                        int imagePadding = (imageSize/imageHeight)-imageWidth*3;    //Relleno
                        int imageData[] = new int[imageHeight*imageWidth];  //Contenido de la imagen en int
                        imageBytes = new byte[(imageWidth+imagePadding)*3*imageHeight];  //Contenido de la imagen en bytes
                        imageFile.read(imageBytes,0,(imageWidth+imagePadding)*3*imageHeight);
                        int k = 0;
                        for (int j=0;j<imageHeight;j++) {
                            for (int i=0;i<imageWidth;i++) {
                                //Se va cargando píxel por píxel el contenido de la imagen                             
                                imageData[imageWidth*(imageHeight-j-1)+i] = (255&0xff)<<24 | (((int)imageBytes[k+2]&0xff)<<16) | (((int)imageBytes[k+1]&0xff)<<8) | (int)imageBytes[k]&0xff;
                                k += 3;
                            }
                            k += imagePadding;
                        }  
                        image = c.createImage(new MemoryImageSource(imageWidth,imageHeight,imageData,0,imageWidth));    //Se crea la imagen leída en formato Image
                        break;
                    }
                case 16:
                    {
                        //Se consideran 2 bytes por píxel
                        int imagePadding = (imageSize/imageHeight)-imageWidth*2;    //Relleno
                        int imageData[] = new int[imageHeight*imageWidth];  //Contenido de la imagen en int
                        imageBytes = new byte[(imageWidth+imagePadding)*2*imageHeight];  //Contenido de la imagen en bytes
                        imageFile.read(imageBytes,0,(imageWidth+imagePadding)*2*imageHeight);
                        int k = 0;
                        for (int j=0;j<imageHeight;j++) {
                            for (int i=0;i<imageWidth;i++) {
                                //Se va cargando píxel por píxel el contenido de la imagen 
                                imageData[imageWidth*(imageHeight-j-1)+i] = (255&0xff)<<24 | (((((int)imageBytes[k+1]>>>2)&0x3f)|0x60)<<3<<16) | ((((int)(((imageBytes[k+1]&0x3)<<3) | ((imageBytes[k]&0xe0)>>>5)))|0x60)<<3<<8) | ((((int)imageBytes[k]&0x1f)|0x60)<<3);
                                k += 2;
                            }
                            k += imagePadding;
                        }       
                        image = c.createImage(new MemoryImageSource(imageWidth,imageHeight,imageData,0,imageWidth));    //Se crea la imagen leída en formato Image
                        break;                    
                    }
                case 8:
                    {
                        int colors = 0;
                        if (colorsUsed > 0) colors = colorsUsed;    //Si no hay colores utilizados al leer la información del archivo, se consideran 256 para la paleta 
                        else colors = 256;
                        if (imageSize == 0) {
                            imageSize = ((((imageWidth*bits)+31)&~31)>>3);
                            imageSize *= imageHeight;
                        }
                        //Se consideran 4 bytes al leer la paleta
                        int imagePalette[] = new int[colors];
                        byte imagePaletteBytes[] = new byte[colors*4];
                        imageFile.read(imagePaletteBytes,0,colors*4);
                        int k = 0;
                        for (int i=0;i<colors;i++) {
                            imagePalette[i] = (255&0xff)<<24 | (((int)imagePaletteBytes[k+2]&0xff)<<16) | (((int)imagePaletteBytes[k+1]&0xff)<<8) | (int)imagePaletteBytes[k]&0xff;
                            k += 4;
                        }
                        //Se considera 1 byte por píxel                        
                        int imagePadding = (imageSize/imageHeight)-imageWidth;    //Relleno
                        int imageData[] = new int[imageWidth*imageHeight];  //Contenido de la imagen en int
                        imageBytes = new byte[(imageWidth+imagePadding)*imageHeight];  //Contenido de la imagen en bytes
                        imageFile.read(imageBytes,0,(imageWidth+imagePadding)*imageHeight);
                        k = 0;
                        for (int j=0;j<imageHeight;j++) {
                            for (int i=0;i<imageWidth;i++) {
                                //Se va cargando píxel por píxel el contenido de la imagen 
                                imageData[imageWidth*(imageHeight-j-1)+i] = imagePalette[((int)imageBytes[k]&0xff)];
                                k++;
                            }
                            k += imagePadding;
                        }       
                        image = c.createImage(new MemoryImageSource(imageWidth,imageHeight,imageData,0,imageWidth));    //Se crea la imagen leída en formato Image
                        break;   
                    }
                case 4:
                    {
                        int colors = 0;
                        if (colorsUsed > 0) colors = colorsUsed;    //Si no hay colores utilizados al leer la información del archivo, se consideran 16 para la paleta 
                        else colors = 16;
                        //Se consideran 4 bytes al leer la paleta
                        int imagePalette[] = new int[colors];
                        byte imagePaletteBytes[] = new byte[colors*4];
                        imageFile.read(imagePaletteBytes,0,colors*4);
                        int k = 0;
                        for (int i=0;i<colors;i++) {
                            imagePalette[i] = (255&0xff)<<24 | (((int)imagePaletteBytes[k+2]&0xff)<<16) | (((int)imagePaletteBytes[k+1]&0xff)<<8) | (int)imagePaletteBytes[k]&0xff;
                            k += 4;
                        }      
                        //Se considera 1 byte por dos píxeles
                        imageSize = (((imageWidth*bits)+31)&~31)>>3;
                        int imageData[] = new int[imageWidth*imageHeight];  //Contenido de la imagen en int
                        imageBytes = new byte[imageSize];  //Contenido de la imagen en bytes
                        k = 0;
                        for (int j=0;j<imageHeight;j++) {
                            imageFile.read(imageBytes,0,imageSize);
                            k = 0;
                            for (int i=0;i<imageWidth;i++) {
                                //Se va cargando píxel por píxel el contenido de la imagen 
                                if (imageWidth*(imageHeight-j-1)+i > imageWidth*imageHeight-1) break;
                                if (k > imageSize*imageHeight-1) break;
                                for (int l=0;l<2;l++) {
                                    if (l == 0) {
                                        imageData[imageWidth*(imageHeight-j-1)+i] = imagePalette[((int)(imageBytes[k]>>4)&0xf)];
                                        i++;
                                        if (i >= imageWidth) break;
                                    } else imageData[imageWidth*(imageHeight-j-1)+i] = imagePalette[((int)imageBytes[k]&0xf)];
                                }
                                k++;
                            }
                        }       
                        image = c.createImage(new MemoryImageSource(imageWidth,imageHeight,imageData,0,imageWidth));    //Se crea la imagen leída en formato Image
                        break;
                    }
                case 1:
                    {
                        int colors = 0;
                        if (colorsUsed > 0) colors = colorsUsed;    //Si no hay colores utilizados al leer la información del archivo, se consideran 2 para la paleta 
                        else colors = 2;
                        //Se consideran 4 bytes al leer la paleta
                        int imagePalette[] = new int[colors];
                        byte imagePaletteBytes[] = new byte[colors*4];
                        imageFile.read(imagePaletteBytes,0,colors*4);
                        int k = 0;
                        for (int i=0;i<colors;i++) {
                            imagePalette[i] = (255&0xff)<<24 | (((int)imagePaletteBytes[k+2]&0xff)<<16) | (((int)imagePaletteBytes[k+1]&0xff)<<8) | (int)imagePaletteBytes[k]&0xff;
                            k += 4;
                        }   
                        //Se considera 1 byte por ocho píxeles
                        imageSize = (((imageWidth*bits)+31)&~31)>>3;
                        int imageData[] = new int[imageWidth*imageHeight];  //Contenido de la imagen en int
                        imageBytes = new byte[imageSize];  //Contenido de la imagen en bytes
                        k = 0;
                        for (int j=0;j<imageHeight;j++) {
                            imageFile.read(imageBytes,0,imageSize);
                            k = 0;
                            for (int i=0;i<imageWidth; i++) {
                                //Se va cargando píxel por píxel el contenido de la imagen
                                if (imageWidth*(imageHeight-j-1)+i > imageWidth*imageHeight-1) break;
                                if (k > imageSize*imageHeight-1) break;
                                for (int l=0;l<8;l++) {
                                    imageData[imageWidth*(imageHeight-j-1)+i] = imagePalette[((int)(imageBytes[k]>>(8-l-1))&0x1)];
                                    if (l != 7) {
                                        i++;
                                        if (i >= imageWidth) break;
                                    }
                                }
                                k++;
                            }
                        }       
                        image = c.createImage(new MemoryImageSource(imageWidth,imageHeight,imageData,0,imageWidth));    //Se crea la imagen leída en formato Image
                        break;
                    }
                default:
                    JOptionPane.showMessageDialog(new Interface(),"El archivo cargado no es una imagen bitmap de 1, 4, 8, 16 o 24 bits.","Ha ocurrido un error",JOptionPane.ERROR_MESSAGE);        
                    image = (Image) null;
                    break;
            }
            imageFile.close();
            return image;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new Interface(),"Ha ocurrido un error cargando el bitmap.","Ha ocurrido un error",JOptionPane.ERROR_MESSAGE);        
        }
        return (Image) null;    
    }

    public static BufferedImage toBufferedImage(Image image) {
        //Se convierte la imagen de formato Image a BufferedImage
        if (image instanceof BufferedImage) return (BufferedImage) image;
        BufferedImage newImage = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D aux = newImage.createGraphics();
        aux.drawImage(image,0,0,null);
        aux.dispose();
        return newImage;  
    }   

    public static void setHeight(int height) {
        //Se asigna el alto de la imagen
        imageHeight = height;
    }

    public static void setWidth(int width) {
        //Se asigna el ancho de la imagen
        imageWidth = width;
    }
      
    public static BufferedImage invertImage(BufferedImage image) {
        //Se invierten los colores de la imagen siendo trabajada como una matriz de píxeles
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                int alpha = (pixel>>24)&0xff;
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                red = 255-red;
                green = 255-green;
                blue = 255-blue;
                pixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(i,j,pixel);
            }
        }
        return image;        
    }  
    
    public static BufferedImage verticalFlip(BufferedImage image) {  
        //Se realiza el espejo vertical de la imagen siendo trabajada como una matriz de píxeles
        int width = image.getWidth();
        int height = image.getHeight()/2;
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                image.setRGB(i,j,image.getRGB(i,image.getHeight()-j-1));
                image.setRGB(i,image.getHeight()-j-1,pixel);
            }
        }
        return image;        
    }
    
    public static BufferedImage horizontalFlip(BufferedImage image) {
        //Se realiza el espejo horizontal de la imagen siendo trabajada como una matriz de píxeles
        int width = image.getWidth()/2;
        int height = image.getHeight();
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                image.setRGB(i,j,image.getRGB(image.getWidth()-i-1, j));
                image.setRGB(image.getWidth()-i-1,j,pixel);
            }
        }
        return image;        
    }
    
    public static BufferedImage brightness(BufferedImage image, int value) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                int alpha = (pixel>>24)&0xff;                
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                red = clamp(red+value);
                green = clamp(green+value);
                blue = clamp(blue+value);
                pixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(i,j,pixel);
            }
        }
        return image;
    }
    
    public static int clamp(int value) {
        if(value < 0) return 0;
        else if(value > 255) return 255;
        return value;
    }
    
    public final static BufferedImage contrast(BufferedImage image, int value){
        int width = image.getWidth();  
        int height = image.getHeight();
        double factor = (float) (259*(value+255))/(float) (255*(259-value));
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                int alpha = (pixel>>24)&0xff;                
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                red = clamp((int)(factor*(red-128)+128));
                green = clamp((int)(factor*(green-128)+128));
                blue = clamp((int)(factor*(blue-128)+128));
                pixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(i,j,pixel);
            }
        }
        return image;
    }  
    
    public static BufferedImage umbralize(BufferedImage image, int threshold) {  
        int width = image.getWidth();  
        int height = image.getHeight();
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                int pixel = image.getRGB(i,j);
                int alpha = (pixel>>24)&0xff;                
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                if ((red+green+blue)/3 < threshold) {
                    red = 0;
                    green = 0;
                    blue = 0;                    
                } else {
                    red = 255;
                    green = 255;
                    blue = 255;
                }
                pixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(i,j,pixel);					
            }
        }
        return image;    
    }
    
    public static BufferedImage flip90DegreesLeft(BufferedImage image){
        //Se gira la imagen 90 grados a la izquierda siendo trabajada como una matriz de píxeles
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(height,width,image.getType());
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                flippedImage.setRGB(j,width-i-1,image.getRGB(i,j));
            }
        }
        return flippedImage;         
    }
    
    public static BufferedImage flip90DegreesRight(BufferedImage image){ 
        //Se gira la imagen 90 grados a la derecha siendo trabajada como una matriz de píxeles
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(height,width,image.getType());
        for (int i=0;i<width;i++) {
            for (int j=0; j<height;j++) {
                flippedImage.setRGB(height-j-1,i,image.getRGB(i,j));
            }
        }
        return flippedImage;
    }
       
    public static BufferedImage rotate(BufferedImage image, int value, int d) {
        if (value % 360 == 0) return image;
        if (value >= 270) { 
            image = flip90DegreesRight(image);
            value -= 90;
            image = flip90DegreesRight(image);
            value -= 90;
            image = flip90DegreesRight(image);
            value -= 90;
        }
        if (value >= 180) { 
            image = flip90DegreesRight(image);
            value -= 90;
            image = flip90DegreesRight(image);
            value -= 90;
        }
        if (value >= 90) { 
            image = flip90DegreesRight(image);
            value -= 90;
        } 
        if (value < 0 && value <= -270) { 
            image = flip90DegreesLeft(image);
            value += 90;
            image = flip90DegreesLeft(image);
            value += 90;
            image = flip90DegreesLeft(image);
            value += 90;
        }
        if (value < 0 && value <= -180) { 
            image = flip90DegreesLeft(image);
            value += 90;
            image = flip90DegreesLeft(image);
            value += 90;
        }
        if (value < 0 && value <= -90) { 
            image = flip90DegreesLeft(image);
            value += 90;
        }
        value = -value;
        double tan = Math.tan(Math.toRadians(value/2));
        double sen = Math.sin(Math.toRadians(value));
        BufferedImage result = new BufferedImage(d, d, BufferedImage.TYPE_INT_RGB);
        for (int i=0;i<image.getWidth();i++) {
            for (int j=0;j<image.getHeight();j++) {
                int pixel = image.getRGB(i,j);
                int newX, newY;
                newX = (int)((i-(int)(Math.floor((image.getWidth())/2))) - tan*((int)(Math.floor((image.getHeight())/2))-j));
                newY = (int)((int)(Math.floor((image.getHeight())/2))-j);
                newY = (int)((sen*newX) + newY);
                newX = (int)((newX) - tan*(newY))+((d/2));
                newY = (int)(-newY)+((d/2));  
                result.setRGB(newX,newY,pixel);              
            }         
        }
        return result;         
    }   
    
    public static BufferedImage histogramEqualization(BufferedImage image) { 
        int red, green, blue, alpha, newPixel = 0; 
        ArrayList<int[]> histLUT = histogramEqualizationLUT(image); 
        BufferedImage equalized = new BufferedImage(image.getWidth(), image.getHeight(), image.getType()); 
        for(int i=0; i<image.getWidth(); i++) {
            for(int j=0; j<image.getHeight(); j++) { 
                int pixel = image.getRGB(i,j);
                alpha = (pixel>>24)&0xff;                
                red = (pixel>>16)&0xff;
                green = (pixel>>8)&0xff;
                blue = pixel&0xff;
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue]; 
                newPixel = (alpha<<24) | (red<<16) | (green<<8) | blue;
                equalized.setRGB(i,j,newPixel); 
            }
        } 
        return equalized; 
    }
 
    public static ArrayList<int[]> histogramEqualizationLUT(BufferedImage image) { 
        int[] redHist = new int[256];
        int[] greenHist = new int[256];
        int[] blueHist = new int[256]; 
        for (int i=0; i<256; i++) redHist[i] = 0;
        for (int i=0; i<256; i++) greenHist[i] = 0;
        for (int i=0; i<256; i++) blueHist[i] = 0; 
        for (int i=0; i<image.getWidth(); i++) {
            for (int j=0; j<image.getHeight(); j++) { 
                int pixel = image.getRGB(i,j);
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                redHist[red]++;
                greenHist[green]++;
                blueHist[blue]++; 
            }
        } 
        ArrayList<int[]> imageHist = new ArrayList<int[]>();
        imageHist.add(redHist);
        imageHist.add(greenHist);
        imageHist.add(blueHist); 
        ArrayList<int[]> imageLUT = new ArrayList<int[]>(); 
        redHist = new int[256];
        greenHist = new int[256];
        blueHist = new int[256];
        for(int i=0; i<256; i++) redHist[i] = 0;
        for(int i=0; i<256; i++) greenHist[i] = 0;
        for(int i=0; i<256; i++) blueHist[i] = 0; 
        int red = 0, green = 0, blue = 0; 
        float factor = (float) (255.0/(image.getWidth()*image.getHeight())); 
        for(int i=0; i<256; i++) {
            red += imageHist.get(0)[i];
            int auxRed = (int) (red*factor);
            if(auxRed > 255) redHist[i] = 255;
            else redHist[i] = auxRed; 
            green += imageHist.get(1)[i];
            int auxGreen = (int) (green*factor);
            if(auxGreen > 255) greenHist[i] = 255;
            else greenHist[i] = auxGreen; 
            blue += imageHist.get(2)[i];
            int auxBlue = (int) (blue*factor);
            if(auxBlue > 255) blueHist[i] = 255;
            else blueHist[i] = auxBlue;
        } 
        imageLUT.add(redHist);
        imageLUT.add(greenHist);
        imageLUT.add(blueHist); 
        return imageLUT; 
    }
       
    public static int[] escalamiento (int[] pixels, int w, int h, int w2, int h2, boolean zoom) {
        if (zoom && w2 <= 1 && h2 <= 1) return pixels;
        int[] salida = new int[w2*h2];
        int a, b, c, d, x, y, index, offset = 0;
        float auxX, auxY, blue, red, green;       
        for (int i=0;i<h2;i++) {
            for (int j=0;j<w2;j++) {
                x = (int)(((float)(w-1))/w2 * j);
                y = (int)(((float)(h-1))/h2 * i);
                auxX = (((float)(w-1))/w2 * j) - x;
                auxY = (((float)(h-1))/h2 * i) - y;
                index = (y*w+x);                
                a = pixels[index];
                b = pixels[index+1];
                c = pixels[index+w];
                d = pixels[index+w+1];
                blue = (a&0xff)*(1-auxX)*(1-auxY) + (b&0xff)*(auxX)*(1-auxY) + (c&0xff)*(auxY)*(1-auxX) + (d&0xff)*(auxX*auxY);
                green = ((a>>8)&0xff)*(1-auxX)*(1-auxY) + ((b>>8)&0xff)*(auxX)*(1-auxY) + ((c>>8)&0xff)*(auxY)*(1-auxX) + ((d>>8)&0xff)*(auxX*auxY);
                red = ((a>>16)&0xff)*(1-auxX)*(1-auxY) + ((b>>16)&0xff)*(auxX)*(1-auxY) + ((c>>16)&0xff)*(auxY)*(1-auxX) + ((d>>16)&0xff)*(auxX*auxY);
                salida[offset++] = 0xff000000 | ((((int)red)<<16)&0xff0000) | ((((int)green)<<8)&0xff00) | ((int)blue) ;
            }
        }
        if (!zoom) {
            setWidth(w2);
            setHeight(h2);
        }
        return salida;
    } 

    public static double[][] crearKernel(int kernelSize) {  //Crea el Kernel para el filtro de suavizado Gaussiano
        //Se calcula el multiplicador
        int temp = (int) Math.pow(2, kernelSize-1);
        multKernel =  (double) 1/( temp*temp );
        //Se busca la fila correspondiente en el triangulo de pascal, se almacena en aux
        int aux[] = new int[kernelSize];
        int nfilas = 10;
        int[] a = new int[1];
        for (int i = 1; i <= nfilas; i++) {
            int[] x = new int[i];
            for (int j = 0; j < i; j++) {
                if (j == 0 || j == (i - 1)) {
                    x[j] = 1;
                } else {
                    x[j] = a[j] + a[j - 1];
                }
                if (i==kernelSize) {
                    aux[j] = x[j];
                }
            }
            a = x;
        }
        double kernel[][] = new double[kernelSize][kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                kernel[i][j] = (double)(aux[i]*aux[j]);
            }
        }
        return kernel;
    }   
    
    public static double[][] crearKernelSharpen(int kernelSize) {  //Crea el Kernel para el filtro de Perfilado
        double kernel[][] = new double[kernelSize][kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                kernel[i][j] = -1.0;
            }
        }
        int mid = (int)Math.floor(kernelSize/2);
        kernel[mid][mid] = (double)(kernelSize*kernelSize);
        return kernel;
    }     
    
    public static BufferedImage convolve(BufferedImage image, double[][] filter, double div) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        int filterWidth = filter.length;
        int filterHeight = filter[0].length;      
        if (filterWidth>width || filterHeight>height) {
            JOptionPane.showMessageDialog(null, "Atención: La imagen de entrada es más pequeña que el filtro a aplicar.","Atención", JOptionPane.WARNING_MESSAGE);
            return image;
        } else {
            for (int i=0; i<width; i++) {
                for (int j=0; j<height; j++) {
                    double sumR = 0.0, sumG = 0.0, sumB = 0.0;
                    for (int k=0; k<filterHeight; k++) {
                        for (int l=0; l<filterWidth; l++) {
                            int imageX = (i-filterWidth/2+l+width)%width;
                            int imageY = (j-filterHeight/2+k+height)%height;
                            int pixel = image.getRGB(imageX,imageY);
                            int alpha = (pixel>>24)&0xff;                
                            int red = (pixel>>16)&0xff;
                            int green = (pixel>>8)&0xff;
                            int blue = pixel&0xff;
                            sumR += (double)red*filter[k][l];
                            sumG += (double)green*filter[k][l];
                            sumB += (double)blue*filter[k][l];
                        }
                    }
                    int newPixel = (255<<24) | (clamp((int)(div*sumR))<<16) | (clamp((int)(div*sumG))<<8) | clamp((int)(div*sumB));
                    result.setRGB(i,j,newPixel);             
                }
            }
            return result;
        }
    }
      
    public static BufferedImage convolveEdges(BufferedImage image, int size, int type) {
        //Type 1 = Prewitt
        //Type 2 = Sobel
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight(); 
        BufferedImage Gx = null;
        BufferedImage Gy = null;
        if (type == 1) {
            double gx[][] = crearKernelPrewittX(size);
            double gy[][] = crearKernelPrewittY(size);
       
            Gx = convolve(image,gx,1.0);
            if (!(size>width || size>height)) Gy = convolve(image,gy,1.0);
        }
        if (type == 2) { 
            
            double gx[][] = crearKernelSobelX(size);
            double gy[][] = crearKernelSobelY(size);
            
            Gx = convolve(image,gx,1.0);
            if (!(size>width || size>height)) Gy = convolve(image,gy,1.0);  
        } 
        if (size>width || size>height) {
            return image;
        } else {
            for (int i=0; i<width; i++) {
                for (int j=0; j<height; j++) {
                    int px = Gx.getRGB(i,j);
                    int alphaX = (px>>24)&0xff;                
                    int redX = (px>>16)&0xff;
                    int greenX = (px>>8)&0xff;
                    int blueX = px&0xff;
                    int py = Gy.getRGB(i,j);
                    int alphaY = (py>>24)&0xff;                
                    int redY = (py>>16)&0xff;
                    int greenY = (py>>8)&0xff;
                    int blueY = py&0xff;
                    int red = clamp((int)Math.round(Math.sqrt((redX*redX) + (redY*redY))));
                    int green = clamp((int)Math.round(Math.sqrt((greenX*greenX) + (greenY*greenY))));
                    int blue = clamp((int)Math.round(Math.sqrt((blueX*blueX) + (blueY*blueY))));
                    int newPixel = (alphaX<<24) | (red<<16) | (green<<8) | blue;
                    result.setRGB(i,j,newPixel);
                }
            }
            return result;
        }
    }
 
    public static BufferedImage grayScale(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                int pixel = image.getRGB(i,j);
                int alpha = (pixel>>24)&0xff;                
                int red = (pixel>>16)&0xff;
                int green = (pixel>>8)&0xff;
                int blue = pixel&0xff;
                int gray = (red+green+blue)/3;
                int newPixel = (gray<<16) | (gray<<8) | gray; 
                result.setRGB(i,j,newPixel);
            }
        }
        return result;
    }
    
    public static BufferedImage copyImage(BufferedImage image){
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }    
    
    public static double[][] crearKernelPrewittX(int kernelSize) {
        double kernel[][] = new double[kernelSize][kernelSize];
        
        int mid = (int)Math.floor(kernelSize/2);
        kernel[mid][mid] = (double)(kernelSize*kernelSize);
        
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                if (j == mid) {
                    kernel[i][j] = 0;
                } else if (j < mid) {
                    kernel[i][j] = -1;
                } else if (j > mid) {
                    kernel[i][j] = 1;
                }
            }
        }
        return kernel;
    }
    
    public static double[][] crearKernelPrewittY(int kernelSize) {
        double kernel[][] = new double[kernelSize][kernelSize];
        
        int mid = (int)Math.floor(kernelSize/2);
        kernel[mid][mid] = (double)(kernelSize*kernelSize);
        
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                if (i == mid) {
                    kernel[i][j] = 0;
                } else if (i < mid) {
                    kernel[i][j] = -1;
                } else if (j > mid) {
                    kernel[i][j] = 1;
                }
            }
        }
        return kernel;
    }
    
    public static double[][] crearKernelSobelX(int kernelSize) {
        double kernel[][] = new double[kernelSize][kernelSize];
        
        int mid = (int)Math.floor(kernelSize/2);
        kernel[mid][mid] = (double)(kernelSize*kernelSize);
        
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                if (j == mid) {
                    kernel[i][j] = 0;
                } else if (j < mid) {
                    kernel[i][j] = -1;
                    if (i == mid) {
                        kernel[i][j] = -2;
                    }
                } else if (j > mid) {
                    kernel[i][j] = 1;
                    if (i == mid) {
                        kernel[i][j] = 2;
                    }
                }
            }
        }
        
        return kernel;
    }
    public static double[][] crearKernelSobelY(int kernelSize) {
        double kernel[][] = new double[kernelSize][kernelSize];
        
        int mid = (int)Math.floor(kernelSize/2);
        kernel[mid][mid] = (double)(kernelSize*kernelSize);
        
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                if (i == mid) {
                    kernel[i][j] = 0;
                } else if (i < mid) {
                    kernel[i][j] = -1;
                    if (j == mid) {
                        kernel[i][j] = -2;
                    }
                } else if (i > mid) {
                    kernel[i][j] = 1;
                    if (j == mid) {
                        kernel[i][j] = 2;
                    }
                }
            }
        }
        
        return kernel;
    }
    
}
