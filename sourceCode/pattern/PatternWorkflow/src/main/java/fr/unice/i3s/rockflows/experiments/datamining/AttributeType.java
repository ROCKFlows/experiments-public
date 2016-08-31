/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

/**
 * @author lupin
 */
public enum AttributeType {

    Nominal("Nominal"),
    NumericOriginal("Numeric Original"),
    Mixed("Both Numeric And Original"), //Nominal and NumericOriginal
    Ignore("no constraints on attribute types"), //Nominal and NumericOriginal
    NumericStandardized("Numeric Standardized");

    String att = "";

    private AttributeType(String str) {
        att = str;
    }

    @Override
    public String toString() {
        return att;
    }
}
