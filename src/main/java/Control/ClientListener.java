/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import Model.Servidor;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joao
 */
public class ClientListener extends Thread{
    private String conexaoDados;
    private Socket conn;
    private Servidor servidor;
    private boolean isRunning;
    
    public ClientListener(String conexaoDados, Socket conn, Servidor servidor) {
        this.conexaoDados = conexaoDados;
        this.conn = conn;
        this.servidor = servidor;
        this.isRunning = false;
    }
    
    @Override
    public void run(){
        isRunning = true;
        String mensagem;
        while(isRunning){
            mensagem = Utils.receberMensagem(conn);
            if(mensagem.toUpperCase().equals("SAIR")){
                servidor.getClientes().remove(conexaoDados);
                try {
                    conn.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("[ClientListener.Run] -> " + ex.getMessage());
                }
                isRunning = false;
            }else if(mensagem.equals("ATUALIZAR_USUARIOS")){
                System.out.println("Solicitação de Atualização de Usuários...");
                String resposta = "";
                for (HashMap.Entry<String, ClientListener> valor : servidor.getClientes().entrySet()) {
                    resposta += (valor.getKey() + ";;;");
                }
                Utils.enviarMensagem(conn, resposta);
            }else{
                System.out.println("Recebido: " + mensagem);
            }
            
        }
    }
    
    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    } 
}
