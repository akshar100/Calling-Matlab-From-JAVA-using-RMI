/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ipclient;

import ipcommon.ImageProcessTemplate;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author erts
 */
public class Worker1 {

     public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Worker1";
            ImageProcessTemplate instance = new ImagePacket();
            ImageProcessTemplate stub = (ImageProcessTemplate) UnicastRemoteObject.exportObject(instance, 0);
            Registry registry = LocateRegistry.getRegistry(1099);
            //Registry registry = LocateRegistry.createRegistry("127.0.0.1",1099);
            registry.rebind(name,stub);
            System.out.println("Server Begins! JNDI lookup name: Worker1");
            stub = (ImageProcessTemplate) registry.lookup(name);
            System.out.println(stub.imageContent());
        } catch (Exception e) {
            System.err.println("server Start exception:");
            e.printStackTrace();
        }
    }
}
