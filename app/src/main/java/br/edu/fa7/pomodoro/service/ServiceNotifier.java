package br.edu.fa7.pomodoro.service;

/**
 * Criado por Artur Cavalcante 07/06/2016
 */
public interface ServiceNotifier {

    void add(ListenValue obj);

    void notifyValue(String value);

}
