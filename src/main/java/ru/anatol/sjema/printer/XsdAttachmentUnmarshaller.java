/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anatol.sjema.printer;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * @author Anatol
 */
public class XsdAttachmentUnmarshaller extends AttachmentUnmarshaller {

    @Override
    public DataHandler getAttachmentAsDataHandler(String cid) {
        System.out.println("DataHandler cid: " + cid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            File file = new File("");
//            DataSource ds = new FileDataSource(file);
//            return new DataHandler(ds);
    }

    @Override
    public byte[] getAttachmentAsByteArray(String cid) {
        System.out.println("ByteArray cid: " + cid);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
