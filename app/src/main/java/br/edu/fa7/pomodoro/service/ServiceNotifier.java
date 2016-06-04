package br.edu.fa7.pomodoro.service;

/**
 * Created by bruno on 18/05/2016.
 */
public interface ServiceNotifier {

    void add(ListenValue obj);

    void notifyValue(long value);

}
