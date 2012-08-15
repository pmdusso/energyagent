/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import usage.MonitoredData;

/**
 * @author pmdusso
 */
public enum NodeInfoCommunicator {

    INSTANCE;
    private static final int PORT = 4545;

    public void SendTCP(MonitoredData mdata, String _masterIpAddr) {
        // Conectar no servidor mestre na porta 80
        String ADDR = _masterIpAddr;
        Socket client;

        try {
            client = new Socket(ADDR, PORT);
            // Cria um canal para enviar dados
            OutputStream os = client.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // Escreve os dados efetivamente no stream
            oos.writeObject(mdata);

            // Fecha stream e fecha socket
            oos.close();
            os.close();
            client.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    public MonitoredData ReceiveTCP() {

        try {
            // Cria um socket servidor na porta definida
            ServerSocket server = new ServerSocket(PORT);
            // A execução do método bloqueia até que algum cliente conect no
            // servidor
            Socket s = server.accept();

            // Cria um canal para receber dados
            InputStream is = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            MonitoredData mdata = (MonitoredData) ois.readObject();

            is.close();
            s.close();
            server.close();

            if (mdata != null) {
                return mdata;
            } else {
                Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                        Level.SEVERE, "Monitored object received with error.");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NodeInfoCommunicator.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return null;
    }
}
