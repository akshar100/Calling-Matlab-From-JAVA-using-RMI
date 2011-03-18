/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ipcommon;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author erts
 */
public interface ImageProcessTemplate extends Remote  {

       public byte[] imageContent() throws RemoteException;

       public float brightness() throws RemoteException;
       public void setImageContent(byte[] temp) throws RemoteException;

       public void setImageName(String name) throws RemoteException;
       public String getImageName() throws RemoteException;

       public void setGray(boolean b) throws RemoteException;
       public boolean getGray() throws RemoteException;

       public void setHistogramBrighten(boolean b) throws RemoteException;
       public boolean getHistogramBrighten() throws RemoteException;

       public void setEdgeDetect(boolean b) throws RemoteException;
       public boolean getEdgeDetect() throws RemoteException;

       public void setInvertColor(boolean b) throws RemoteException;
       public boolean getInvertColor() throws RemoteException;

       public void setThreshold(float a) throws RemoteException;
       public float getThreshold() throws RemoteException;

       public byte[] getProcessedImage() throws RemoteException;

       public byte[] grayedImage(byte[] image) throws RemoteException;
       public byte[] invertedColor(byte[] image) throws RemoteException;
       public byte[] edgeDetected(byte[] image) throws RemoteException;
       public byte[] threshhold(byte[] image, float t) throws RemoteException;
       public byte[] histogramBrighten(byte[] image) throws RemoteException;

}
