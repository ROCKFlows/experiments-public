/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.datamining;

/**
 * @author lupin
 */
public class InfoPattern {

    //value -1 is used to indicate infinite

    public String id = "";
    public int minNumAtt = -1;
    public int maxNumAtt = -1;
    public int minNumInst = -1;
    public int maxNumInst = -1;
    public MVType withMissingValues = MVType.Ignore;
    public int minNumClasses = -1;
    public int maxNumClasses = -1;
    public AttributeType attributeType = AttributeType.Mixed;

    @Override
    public String toString() {
        String out = "id = " + id + System.lineSeparator();
        out += "with missing values = " + withMissingValues + System.lineSeparator();
        out += "min Num Attributes = " + minNumAtt + System.lineSeparator();
        out += "max Num Attributes = " + maxNumAtt + System.lineSeparator();
        out += "min Num Traiing Instances = " + minNumInst + System.lineSeparator();
        out += "max Num Training Instances = " + maxNumInst + System.lineSeparator();
        out += "min Num Classes = " + minNumClasses + System.lineSeparator();
        out += "max Num Classes = " + maxNumClasses + System.lineSeparator();
        out += "attributes type = " + attributeType.toString() + System.lineSeparator();
        return out;
    }

}
