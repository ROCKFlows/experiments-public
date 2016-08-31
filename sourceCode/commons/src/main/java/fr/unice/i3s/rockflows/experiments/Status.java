/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments;

/**
 * @author lupin
 */
public enum Status {

    Bad("Bad"),
    Good("Good"),
    VeryGood("VeryGood");

    private String text;

    private Status(String value) {
        text = value;
    }

    @Override
    public String toString() {
        return text;
    }

}
