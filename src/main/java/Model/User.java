/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Control.UsuarioListener;
import Control.Utils;
import View.Chat;
import View.Home;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Joao
 */
public class User {

    private String nome;
    private String host;
    private int porta;
    private ServerSocket server;
    private boolean isRunning;

    private ArrayList<String> chatsAbertos;
    private ArrayList<String> usuariosBloqueados;
    private HashMap<String, UsuarioListener> conectadosListeners;

    public User(String nome, String host, int porta) {
        this.nome = nome;
        this.host = host;
        this.porta = porta;
        this.server = null;
        this.isRunning = false;

        chatsAbertos = new ArrayList<String>();
        usuariosBloqueados = new ArrayList<String>();
        conectadosListeners = new HashMap<String, UsuarioListener>();
    }

    public void startServidor(Home home, int porta) {
        new Thread() {
            @Override
            public void run() {
                isRunning = true;
                try {
                    server = new ServerSocket(porta);
                    System.out.println("Servidor Cliente iniciado na porta: " + porta + "...");
                    while (isRunning) {
                        Socket newConnection = server.accept();
                        UsuarioListener usuario = new UsuarioListener(home, newConnection);
                        new Thread(usuario).start();
                    }
                } catch (IOException ex) {
                    System.out.println("[ERRO: Home.StartServidor] -> " + ex.getMessage());
                }
            }
        }.start();
    }

    public void abrirChat(Home home, String selecionado) {
        String[] valor = selecionado.split(":");
        if (!chatsAbertos.contains(selecionado)) {
            if (!usuariosBloqueados.contains(valor[0])) {
                try {
                    Socket connection = new Socket(valor[1], Integer.parseInt(valor[2]));
                    String dadosLogin = nome + ":" + host + ":" + porta;
                    Utils.enviarMensagem(connection, "BLOQUEAR;" + dadosLogin);
                    String resposta = Utils.receberMensagem(connection);
                    if (resposta.equals("BLOQUEADO")) {
                        JOptionPane.showMessageDialog(null, "Usuário selecionado te bloqueou, parça.");
                    } else {
                        Utils.enviarMensagem(connection, "ABRIR_CHAT;" + dadosLogin);
                        UsuarioListener usuario = new UsuarioListener(home, connection);
                        usuario.setChat(new Chat(home, connection, selecionado, dadosLogin.split(":")[0]));
                        usuario.setIsChatOpen(true);
                        conectadosListeners.put(selecionado, usuario);
                        chatsAbertos.add(selecionado);
                        new Thread(usuario).start();
                    }
                } catch (IOException ex) {

                    System.out.println("[HOME.AbrirChat] -> " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuário bloqueado! Desbloqueie o usuário para abrir o chat.");
            }
        }

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
