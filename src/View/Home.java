/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Control.ClientListener;
import Control.UsuarioListener;
import Control.Utils;
import Model.User;
import Observer.GerenciaNotificacao;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Joao
 */
public class Home extends javax.swing.JFrame implements Observer.Observer{

    /**
     * Creates new form Home
     */
    private String dadosLogin;
    private Socket conn;
    private ArrayList<String> usuariosConectados;
    private ArrayList<String> chatsAbertos;
    private ArrayList<String> usuariosBloqueados;
    private HashMap<String, UsuarioListener> conectadosListeners;
    private ServerSocket server;
    private boolean isRunning;
    private GerenciaNotificacao gerenciaNotificacao;
    private User user;

    public Home(Socket conn, String dadosLogin) {
        super("Chat - Home");
        this.user = new User(dadosLogin.split(":")[0], dadosLogin.split(":")[1], Integer.parseInt(dadosLogin.split(":")[2]));
        this.server = null;
        this.isRunning = false;
        usuariosConectados = new ArrayList<String>();
        chatsAbertos = new ArrayList<String>();
        usuariosBloqueados = new ArrayList<String>();
        conectadosListeners = new HashMap<String, UsuarioListener>();
        this.dadosLogin = dadosLogin;
        this.conn = conn;
        this.gerenciaNotificacao = GerenciaNotificacao.getInstance();
        this.gerenciaNotificacao.addObservadores(this);
        
        initComponents();
        this.gerenciaNotificacao.verificaAddUsuario();
        jlTitulo.setText("Bem-Vindo(a), " + dadosLogin.split(":")[0]);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        acoes();
        startServidor(this, Integer.parseInt(dadosLogin.split(":")[2]));
        getUsuariosConectados();
    }
    
    @Override
    public void notificarNovoUsuario() {
        getUsuariosConectados();
    }

    @Override
    public void notificarSaidaUsuario() {
        getUsuariosConectados();
    }
    
    
    private void bloquearContato() {
        int index = jList.getSelectedIndex();

        if (index != -1) {
            String selecionado = jList.getSelectedValue().toString();
            String[] valor = selecionado.split(":");
            if (usuariosBloqueados.contains(valor[0])) {
                usuariosBloqueados.remove(valor[0]);

            } else {
                usuariosBloqueados.add(valor[0]);
            }

        }
        getUsuariosConectados();

    }

    public void mensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    private void abrirChat() {
        int index = jList.getSelectedIndex();
        if (index != -1) {
            String selecionado = jList.getSelectedValue().toString();
            user.abrirChat(this, selecionado);
//            String[] valor = selecionado.split(":");
//            if (!chatsAbertos.contains(selecionado)) {
//                if (!usuariosBloqueados.contains(valor[0])) {
//                    try {
//                        Socket connection = new Socket(valor[1], Integer.parseInt(valor[2]));
//                        Utils.enviarMensagem(connection, "LALALA;" + this.dadosLogin);
//                        String resposta = Utils.receberMensagem(connection);
//                        if (resposta.equals("BLOQUEADO")) {
//                            JOptionPane.showMessageDialog(null, "Usuário selecionado te bloqueou, parça.");
//                        } else {
//                            Utils.enviarMensagem(connection, "ABRIR_CHAT;" + this.dadosLogin);
//                            Usuario usuario = new Usuario(this, connection);
//                            usuario.setChat(new Chat(this, connection, selecionado, this.dadosLogin.split(":")[0]));
//                            usuario.setIsChatOpen(true);
//                            conectadosListeners.put(selecionado, usuario);
//                            chatsAbertos.add(selecionado);
//                            new Thread(usuario).start();
//                        }
//                    } catch (IOException ex) {
//                        Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
//                        System.out.println("[HOME.AbrirChat] -> " + ex.getMessage());
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(null, "Usuário bloqueado! Desbloqueie o usuário para abrir o chat.");
//                }
//            }
        }
    }

    private void startServidor(Home home, int porta) {
        user.startServidor(home, porta);
//        new Thread() {
//            @Override
//            public void run() {
//                isRunning = true;
//                try {
//                    server = new ServerSocket(porta);
//                    System.out.println("Servidor Cliente iniciado na porta: " + porta + "...");
//                    while (isRunning) {
//                        Socket newConnection = server.accept();
//                        Usuario usuario = new Usuario(home, newConnection);
//                        new Thread(usuario).start();
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
//                    System.out.println("[ERRO: Home.StartServidor] -> " + ex.getMessage());
//                }
//            }
//        }.start();
    }

    public void acoes() {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                user.setIsRunning(false);
                isRunning = false;
                Utils.enviarMensagem(conn, "SAIR");
//                gerenciaNotificacao.verificaSaidaUsuario();
                System.out.println("Conexão Encerrada.");
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private void getUsuariosConectados() {
        usuariosConectados.clear();
        Utils.enviarMensagem(conn, "ATUALIZAR_USUARIOS");
        String resposta = Utils.receberMensagem(conn);
        jList.removeAll();
//        usuariosConectados.clear();
        for (String valor : resposta.split(";;;")) {
            if (!valor.equals(dadosLogin)) {
                for (String bloqueados : usuariosBloqueados) {
                    if (bloqueados.equals(valor.split(":")[0])) {
                        valor += " - BLOQUEADO";
                    }
                }
                usuariosConectados.add(valor);
            }
        }
        jList.setListData(new Vector<String>(usuariosConectados));

    }

    public HashMap<String, UsuarioListener> getConectadosListeners() {
        return conectadosListeners;
    }

    public ArrayList<String> getChatsAbertos() {
        return chatsAbertos;
    }

    public String getDadosLogin() {
        return dadosLogin;
    }

    public ArrayList<String> getUsuariosBloqueados() {
        return usuariosBloqueados;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jlTitulo = new javax.swing.JLabel();
        btnAtualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList = new javax.swing.JList<>();
        btnAbrirConversa = new javax.swing.JButton();
        btnBloquear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jlTitulo.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        jlTitulo.setForeground(new java.awt.Color(204, 0, 0));
        jlTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlTitulo.setText("Bem-Vindo, João");

        btnAtualizar.setFont(new java.awt.Font("Ebrima", 0, 14)); // NOI18N
        btnAtualizar.setForeground(new java.awt.Color(153, 0, 0));
        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        jList.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 0, 0)), "Usuários Online", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Ebrima", 0, 12), new java.awt.Color(153, 0, 0))); // NOI18N
        jList.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(jList);

        btnAbrirConversa.setFont(new java.awt.Font("Ebrima", 0, 14)); // NOI18N
        btnAbrirConversa.setForeground(new java.awt.Color(153, 0, 0));
        btnAbrirConversa.setText("Abrir Conversa");
        btnAbrirConversa.setActionCommand("AbrirConversa");
        btnAbrirConversa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirConversaActionPerformed(evt);
            }
        });

        btnBloquear.setFont(new java.awt.Font("Ebrima", 0, 14)); // NOI18N
        btnBloquear.setForeground(new java.awt.Color(153, 0, 0));
        btnBloquear.setText("(Des)Bloquear");
        btnBloquear.setActionCommand("AbrirConversa");
        btnBloquear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBloquearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jlTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAtualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnAbrirConversa, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBloquear, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAtualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAbrirConversa)
                    .addComponent(btnBloquear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        getUsuariosConectados();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnAbrirConversaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirConversaActionPerformed
        abrirChat();
    }//GEN-LAST:event_btnAbrirConversaActionPerformed

    private void btnBloquearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBloquearActionPerformed
        bloquearContato();
    }//GEN-LAST:event_btnBloquearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirConversa;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnBloquear;
    private javax.swing.JList<String> jList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlTitulo;
    // End of variables declaration//GEN-END:variables

    
}
