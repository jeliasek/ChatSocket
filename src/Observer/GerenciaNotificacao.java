/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Observer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joao
 */
public class GerenciaNotificacao {
    
    private static List<Observer> observadores;
    private static GerenciaNotificacao instance;
    public GerenciaNotificacao() {
        this.observadores = new ArrayList<Observer>();
    }
    
    public void addObservadores(Observer obs){
        observadores.add(obs);
        System.out.println("adicionou obs");
    }
    
    public static GerenciaNotificacao getInstance(){
        if(instance == null){
            instance = new GerenciaNotificacao();
        }
        return instance;
    }
    
    public void verificaAddUsuario(){
        for(Observer obs : observadores){
            obs.notificarNovoUsuario();
        }
        System.out.println("verificou adicionar usuario obs");
    }
    
    public void verificaSaidaUsuario(){
        for(Observer obs : observadores){
            obs.notificarSaidaUsuario();
        }
    }
}
