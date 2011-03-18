/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ipclient;
import ipcommon.ImageProcessTemplate;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.*;
/**
 *
 * @author erts
 */
public class ImagePacket implements ImageProcessTemplate{

    byte[] imageContent=null;
    String imageName = null;
    boolean gray = false;
    boolean histB =false;
    private boolean edgeDetect = false;
    private boolean invertColor = false;
    private float threshold = (float) 0.0;

    public void setImageContent(byte[] temp)
    {
        this.imageContent =temp;
        System.out.println(temp);
        FileOutputStream fos;
        DataOutputStream dos;

    try {

          File file= new File("I:\\"+this.getImageName());
          fos = new FileOutputStream(file);
          dos=new DataOutputStream(fos);
          dos.write(temp);
          dos.close();
          fos.close();

      
      
      

    } catch (IOException e) {
      e.printStackTrace();
    }
  

    }
    
    public byte[] imageContent() throws RemoteException{
        return this.imageContent;
    }

    public float brightness() throws RemoteException {
        return (float) 5.0;
    }

    public static byte[] readFileAsString(String filePath)
        throws java.io.IOException{
            File f = new File(filePath);
            DataInputStream in = new DataInputStream(new FileInputStream(new File(filePath)));
            byte[] contents = new byte[(int)f.length()];
            in.readFully(contents);
            in.close();
            return contents;
    }

    public static void writeFile(String name, byte[] contents)
    {
         try {
            FileOutputStream fos;
        DataOutputStream dos;
          File file= new File(name);
          fos = new FileOutputStream(file);
              dos=new DataOutputStream(fos);
              dos.write(contents);
              dos.close();
              fos.close();





        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    public void setImageName(String name) throws RemoteException {
        this.imageName = name;
    }

    public String getImageName() throws RemoteException {
        return this.imageName;
    }

    public void setGray(boolean b) throws RemoteException {
        this.gray = b;
    }

    public boolean getGray() throws RemoteException {
        return this.gray;
    }

    public void setHistogramBrighten(boolean b) throws RemoteException {
        this.histB =b;
    }

    public boolean getHistogramBrighten() throws RemoteException {
        return this.histB;
    }

    public void setEdgeDetect(boolean b) throws RemoteException {
        this.edgeDetect = b;
    }

    public boolean getEdgeDetect() throws RemoteException {
        return this.edgeDetect;
    }

    public void setInvertColor(boolean b) throws RemoteException {
        this.invertColor = b;
    }

    public boolean getInvertColor() throws RemoteException {
       return  this.invertColor;
    }

    public void setThreshold(float a) throws RemoteException {
       this.threshold = a;
    }

    public float getThreshold() throws RemoteException {
        return this.threshold;
    }

    public byte[] getProcessedImage() throws RemoteException {

        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into 2 pieces
            proxy.eval("im = imread('I:\\"+this.getImageName()+"'); siz=size(im); rows = siz(1); half = floor(rows/2); imwrite(im(1:half,:,:),'I:\\top-"+this.getImageName()+"'); imwrite(im(half:end,:,:),'I:\\bottom-"+this.getImageName()+"');");

            /** This is where the work is sent to worker machines. **/
            /**
             * This is how the whole thing works
             * Each Worker is a remote server and is exposing a ImageProcess Object.
             * We send the R component to worker1 G component to Worker2 and so on. 
             ***/

            if(this.getGray())
            {
                Registry registry = LocateRegistry.getRegistry(1099);
                //registry.list();
                ImageProcessTemplate hr = (ImageProcessTemplate) registry.lookup("Worker1"); 
                ImagePacket.writeFile("I:\\top-"+this.getImageName(),hr.grayedImage(ImagePacket.readFileAsString("I:\\top-"+this.getImageName())));
                
                hr = (ImageProcessTemplate) registry.lookup("Worker2"); 
                ImagePacket.writeFile("I:\\bottom-"+this.getImageName(),hr.grayedImage(ImagePacket.readFileAsString("I:\\bottom-"+this.getImageName())));
                
                proxy.eval("imtop = imread('I:\\top-"+this.getImageName()+"'); imbottom = imread('I:\\bottom-"+this.getImageName()+"'); imwrite(cat(1,imtop,imbottom),'I:\\processed-"+this.getImageName()+"');");



            }
            if(this.getInvertColor())
            {
                Registry registry = LocateRegistry.getRegistry(1099);
                //registry.list();
                ImageProcessTemplate hr = (ImageProcessTemplate) registry.lookup("Worker1");
                ImagePacket.writeFile("I:\\top-"+this.getImageName(),hr.invertedColor(ImagePacket.readFileAsString("I:\\top-"+this.getImageName())));

                hr = (ImageProcessTemplate) registry.lookup("Worker2");
                ImagePacket.writeFile("I:\\bottom-"+this.getImageName(),hr.invertedColor(ImagePacket.readFileAsString("I:\\bottom-"+this.getImageName())));

                proxy.eval("imtop = imread('I:\\top-"+this.getImageName()+"'); imbottom = imread('I:\\bottom-"+this.getImageName()+"'); imwrite(cat(1,imtop,imbottom),'I:\\processed-"+this.getImageName()+"');");
            }

            if(this.getThreshold()>0.0)
            {
                Registry registry = LocateRegistry.getRegistry(1099);
                //registry.list();
                ImageProcessTemplate hr = (ImageProcessTemplate) registry.lookup("Worker1");
                ImagePacket.writeFile("I:\\top-"+this.getImageName(),hr.threshhold(ImagePacket.readFileAsString("I:\\top-"+this.getImageName()),this.getThreshold()));

                hr = (ImageProcessTemplate) registry.lookup("Worker2");
                ImagePacket.writeFile("I:\\bottom-"+this.getImageName(),hr.threshhold(ImagePacket.readFileAsString("I:\\bottom-"+this.getImageName()),this.getThreshold()));

                proxy.eval("imtop = imread('I:\\top-"+this.getImageName()+"'); imbottom = imread('I:\\bottom-"+this.getImageName()+"'); imwrite(cat(1,imtop,imbottom),'I:\\processed-"+this.getImageName()+"');");
            }

            if(this.getHistogramBrighten())
            {
                Registry registry = LocateRegistry.getRegistry(1099);
                //registry.list();
                ImageProcessTemplate hr = (ImageProcessTemplate) registry.lookup("Worker1");
                ImagePacket.writeFile("I:\\top-"+this.getImageName(),hr.threshhold(ImagePacket.readFileAsString("I:\\top-"+this.getImageName()),this.getThreshold()));

                hr = (ImageProcessTemplate) registry.lookup("Worker2");
                ImagePacket.writeFile("I:\\bottom-"+this.getImageName(),hr.threshhold(ImagePacket.readFileAsString("I:\\bottom-"+this.getImageName()),this.getThreshold()));

                proxy.eval("imtop = imread('I:\\top-"+this.getImageName()+"'); imbottom = imread('I:\\bottom-"+this.getImageName()+"'); imwrite(cat(1,imtop,imbottom),'I:\\processed-"+this.getImageName()+"');");
            
            }

             if(this.getEdgeDetect())
            {
                Registry registry = LocateRegistry.getRegistry(1099);
                //registry.list();
                ImageProcessTemplate hr = (ImageProcessTemplate) registry.lookup("Worker1");
                ImagePacket.writeFile("I:\\top-"+this.getImageName(),hr.edgeDetected(ImagePacket.readFileAsString("I:\\top-"+this.getImageName())));

                hr = (ImageProcessTemplate) registry.lookup("Worker2");
                ImagePacket.writeFile("I:\\bottom-"+this.getImageName(),hr.edgeDetected(ImagePacket.readFileAsString("I:\\bottom-"+this.getImageName())));

                proxy.eval("imtop = imread('I:\\top-"+this.getImageName()+"'); imbottom = imread('I:\\bottom-"+this.getImageName()+"'); imwrite(cat(1,imtop,imbottom),'I:\\processed-"+this.getImageName()+"');");

            }


        }catch(Exception e)
        {
            System.out.println(e);
        }
        try {
            return ImagePacket.readFileAsString("I:\\processed-" + this.getImageName() + "");
        } catch (IOException ex) {
            Logger.getLogger(ImagePacket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] grayedImage(byte[] image) throws RemoteException {
        ImagePacket.writeFile("I:\\w-temp.png",image);
        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into it's 3 components.
            proxy.eval("im = imread('I:\\w-temp.png'); im = rgb2gray( im); imwrite(im,'I:\\w-temp.png'); ");

            byte[] temp =  ImagePacket.readFileAsString("I:\\w-temp.png");
            return temp;

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    public byte[] invertedColor(byte[] image) throws RemoteException {
        ImagePacket.writeFile("I:\\w-temp.png",image);
        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into it's 3 components.
            proxy.eval("im = imread('I:\\w-temp.png'); im = imcomplement( im); imwrite(im,'I:\\w-temp.png'); ");

            byte[] temp =  ImagePacket.readFileAsString("I:\\w-temp.png");
            return temp;

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    public byte[] edgeDetected(byte[] image) throws RemoteException {
        ImagePacket.writeFile("I:\\w-temp.png",image);
        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into it's 3 components.
            proxy.eval("im = imread('I:\\w-temp.png'); im = edge(im(:,:,1),'sobel'); imwrite(im,'I:\\w-temp.png'); ");

            byte[] temp =  ImagePacket.readFileAsString("I:\\w-temp.png");
            return temp;

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    public byte[] threshhold(byte[] image, float t) throws RemoteException {
        ImagePacket.writeFile("I:\\w-temp.png",image);
        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into it's 3 components.
            proxy.eval("im = imread('I:\\w-temp.png'); im = im2bw( im, "+String.valueOf(t)+"); imwrite(im,'I:\\w-temp.png'); ");

            byte[] temp =  ImagePacket.readFileAsString("I:\\w-temp.png");
            return temp;

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    public byte[] histogramBrighten(byte[] image) throws RemoteException {
        ImagePacket.writeFile("I:\\w-temp.png",image);
        try
        {
            //Create a factory
            RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory();

            //Get a proxy, launching MATLAB in the process
            RemoteMatlabProxy proxy = factory.getProxy();

            //Use matlab to divide image into it's 3 components.
            proxy.eval("im = imread('I:\\w-temp.png'); im = histeq( im); imwrite(im,'I:\\w-temp.png'); ");

            byte[] temp =  ImagePacket.readFileAsString("I:\\w-temp.png");
            return temp;

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }


  
}
