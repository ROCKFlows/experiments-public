/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

/**
 * @author lupin
 */
public enum MVType {

    True("True"),
    False("False"),
    Ignore("Ignore");

    String att = "";

    MVType(String str) {
        att = str;
    }

    @Override
    public String toString() {
        return att;
    }

}
