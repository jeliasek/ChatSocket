/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joao
 */
public class Utils {
    
    public static boolean enviarMensagem(Socket conexao, String mensagem){
        
        try {
            ObjectOutputStream output = new ObjectOutputStream(conexao.getOutputStream());
            output.flush();
            output.writeObject(mensagem);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("[ERROR: Control.Utils.enviarMensagem] -> " + ex.getMessage());
        }
        return false;
    }
    
    public static String receberMensagem(Socket conexao){
        String resposta = null;
        try {
            ObjectInputStream input = new ObjectInputStream(conexao.getInputStream());
            try {
                resposta = (String) input.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("[ERROR: Control.Utils.receberMensagem] -> " + ex.getMessage());
        }
        return resposta;
    }
    
    public static String lerArquivoConfiguracao(){
        String ip = "";
	int porta = 0;
        String retorno = "";
        File arq = new File("ArquivoConfig.txt");
//        System.out.println("Caminho: " + arq.getAbsolutePath());
        try {
            FileReader leitorArq = new FileReader(arq); 
            BufferedReader leitorTexto = new BufferedReader(leitorArq);
            String linha = leitorTexto.readLine();
            HashMap<String, String> configuracoes = new HashMap<>();
            while (linha != null) {
        		configuracoes.put(linha.split("=")[0], linha.split("=")[1]);        		
                linha = leitorTexto.readLine();
            }
            ip = configuracoes.get("IP_SERVIDOR");
    		porta = Integer.parseInt(configuracoes.get("PORTA_SERVIDOR"));
    		
    		if(ip.equals("") || porta < 1) {
    			System.out.println("Arquivo de configuração parametrizado errado. Acesse link <https://github.com/jeliasek/ChatSocket.git> para saber como configurar.");
    		}
    		retorno = ip + ";" + porta;
    		
            leitorTexto.close();
            leitorArq.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de configuração não encontrado. Acesse link <https://github.com/jeliasek/ChatSocket.git> para saber como configurar.");
            
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo de configuração. Tente novamente");
            
        }
        return retorno;
    }
}
