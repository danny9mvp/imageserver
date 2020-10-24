/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageserver;

import imagebeans.Image;
import imageioimpl.ImageReaderImpl;
import imageioimpl.ImageWriterImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rmi.RemoteInterface;

/**
 *
 * @author ASUS
 */
public class ImageServer extends RemoteServer implements RemoteInterface {

    private File imageRepository;

    public File getImageRepository() {
        return imageRepository;
    }

    public void setImageRepository(File imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public HashMap<String,String[]> getRepository() {
        try {
            String imageDirectory = getImageRepository().getName();
            String[] filesOnImageDirectory = getImageRepository().list((File dir, String name) -> {
                boolean isImageFile = name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".gif");
                return isImageFile;
            });
            HashMap<String,String[]> imageRepositoryWithFiles = new HashMap();
            imageRepositoryWithFiles.put(imageDirectory, filesOnImageDirectory);
            System.out.println("El cliente "+getClientHost()+" ha solicitado el repositorio de imagenes.");
            return imageRepositoryWithFiles;
        } catch (ServerNotActiveException ex) {
            Logger.getLogger(ImageServer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Image getImageByName(String name) throws RemoteException {
        Image image = null;
        try {
            File[] filesOnImageDirectory = getImageRepository().listFiles((File dir, String name1) -> {
                boolean isImageFile = name1.toLowerCase().endsWith(".png") || name1.toLowerCase().endsWith(".jpeg") || name1.toLowerCase().endsWith(".jpg") || name1.toLowerCase().endsWith(".gif");
                return isImageFile;
            });
            File requestedFile = null;
            for (File file : filesOnImageDirectory) {
                if (file.getName().equals(name)) {
                    requestedFile = file;
                    break;
                }
            }
            if (requestedFile != null) {
                ImageReaderImpl imageReaderImpl = new ImageReaderImpl();
                image = imageReaderImpl.readAsImage(requestedFile);
                System.out.println("El cliente "+getClientHost()+" ha solicitado la imagen "+name+".");                
            }
        } catch (IOException | ServerNotActiveException ex) {
            Logger.getLogger(ImageServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    @Override
    public void postImage(Image imageFile) throws RemoteException {
        try {
            ImageWriterImpl imageWriterImpl = new ImageWriterImpl();
            imageWriterImpl.writeImageFile(imageFile, getImageRepository().getPath());
            System.out.println("El cliente "+getClientHost()+" ha cargado la imagen "+imageFile.getName()+" al repositorio.");
        } catch (IOException | ServerNotActiveException ex) {
            Logger.getLogger(ImageServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
