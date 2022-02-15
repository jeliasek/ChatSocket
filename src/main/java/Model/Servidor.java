/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Control.ClientListener;
import Control.Utils;
import Observer.GerenciaNotificacao;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joao
 */
public class Servidor {

//    public static final String HOST = "127.0.0.1";
    private int porta;
    private HashMap<String, ClientListener> clientes;
    private List<Observer.Observer> observadores;

//    private int porta;
    public Servidor() {

        String dadosServidor = Utils.lerArquivoConfiguracao();
        if (!(dadosServidor.equals("")) || !(dadosServidor.equals(";"))) {
            String[] valores = dadosServidor.split(";");
            this.porta = Integer.parseInt(valores[1]);
            String ip = valores[0];
        }

        String conexaoDados;
        try {
            clientes = new HashMap<String, ClientListener>();
            ServerSocket server = new ServerSocket(this.porta);
            System.out.println("Servidor Inciado: aguardando conexão TCP na porta " + this.porta + "...");
            while (true) {
                Socket conn = server.accept();
                System.out.println("Conexão recebida do cliente : " + conn.getInetAddress().getHostAddress() + ":" + conn.getPort());
                conexaoDados = Utils.receberMensagem(conn);
                if (validaLogin(conexaoDados)) {
                    
                    ClientListener cl = new ClientListener(conexaoDados, conn, this);
                    clientes.put(conexaoDados, cl);
                    Utils.enviarMensagem(conn, "SUCESSO");
                    new Thread(cl).start();
                } else {
                    Utils.enviarMensagem(conn, "ERRO");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
//        try (ServerSocket server = new ServerSocket(this.porta);) /* try-with */ {
//
//            clientes = new HashMap<String, ClientListener>();
//            server.setReuseAddress(true);
//            System.out.println("Servidor Inciado: aguardando conexão TCP na porta " + this.porta + "...");
//
//            Socket conn = server.accept();
//
//            System.out.println("Conexão recebida do cliente : " + conn.getInetAddress().getHostAddress() + ":" + conn.getPort());
//            conexaoDados = Utils.receberMensagem(conn);
//            if(validaLogin(conexaoDados)){
//                ClientListener cl = new ClientListener(conexaoDados, conn, this);
//                clientes.put(conexaoDados, cl);
//                Utils.enviarMensagem(conn, "SUCESSO");
//                new Thread(cl).start();
//            }else{
//                Utils.enviarMensagem(conn, "ERRO");
//            }
//
//
//            
//        } catch (IOException ex) {
//            System.err.println("[ERROR:Model.Servidor.Servidor] -> " + ex.getMessage());
//        }

    }

    private boolean validaLogin(String conexaoDados) {
        String[] dados = conexaoDados.split(":");
        for (HashMap.Entry<String, ClientListener> valor : clientes.entrySet()) {
            String[] partes = valor.getKey().split(":");
            if (partes[0].toLowerCase().equals(dados[0].toLowerCase())) {
                return false;
            } else if ((partes[1] + partes[2]).equals(dados[1] + dados[2])) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, ClientListener> getClientes() {
        return clientes;
    }
}
