/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import Model.*;
import Control.Utils;
import View.Chat;
import View.Home;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joao
 */
public class UsuarioListener extends Thread {

    private boolean isRunning;
    private Socket conn;
    private Home home;
    private boolean isChatOpen;
    private boolean isBloqueado;
    private String conexaoDados;
    private Chat chat;

    public UsuarioListener(Home home, Socket conn) {
        this.home = home;
        this.conn = conn;
        isChatOpen = false;
        isBloqueado = false;
        isRunning = false;
        this.chat = null;
        this.conexaoDados = null;
    }

    @Override
    public void run() {
        isRunning = true;
        String mensagem = null;
        while (isRunning) {
            mensagem = Utils.receberMensagem(conn);
            if (mensagem == null || mensagem.equals("FECHAR_CHAT")) {
                fecharChat();
            } else {
                String[] campos = mensagem.split(";");
                if (campos.length > 1) {
                    if (campos[0].equals("ABRIR_CHAT")) {
                        abrirChat(campos);

                    } else if (campos[0].equals("MENSAGEM")) {
                        enviarMensagem(campos);

                    } else if (campos[0].equals("BLOQUEAR")) {
                        bloquearContato(campos);
                    }
                }
            }
            System.out.println("Mensagem recebida: " + mensagem);
        }
    }

    public void bloquearContato(String[] campos) {
        System.out.println("Chegou aqui");
        if (home.getUsuariosBloqueados().contains(campos[1].split(":")[0])) {
            Utils.enviarMensagem(conn, "BLOQUEADO");
        } else {
            Utils.enviarMensagem(conn, "DESBLOQUEADO");
        }
    }

    public void enviarMensagem(String[] campos) {
        String msg = "";
        for (int i = 1; i < campos.length; i++) {
            msg += campos[i];
            if (i > 1) {
                msg += ";";
            }
        }
        chat.incrementarMensagem(msg);
    }

    public void abrirChat(String[] campos) {
        String[] valores = campos[1].split(":");
        conexaoDados = campos[1];
        if (!isChatOpen) {
            home.getChatsAbertos().add(conexaoDados);
            home.getConectadosListeners().put(conexaoDados, null);
            isChatOpen = true;
            chat = new Chat(home, conn, conexaoDados, home.getDadosLogin().split(":")[0]);
        }
    }

    public void fecharChat() {
        if (isChatOpen) {
            home.getChatsAbertos().remove(conexaoDados);
            home.getConectadosListeners().remove(conexaoDados);
            isChatOpen = false;
            try {
                conn.close();
            } catch (IOException ex) {
                
                System.out.println("Usuario.Run -> " + ex.getMessage());
            }
            chat.dispose();
        }
        isRunning = false;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isIsChatOpen() {
        return isChatOpen;
    }

    public void setIsChatOpen(boolean isChatOpen) {
        this.isChatOpen = isChatOpen;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

}
