package fr.unice.i3s.rockflows.experiments.datamining;

public class DatasetProperties {

    public boolean isNominal = false; //default
    public boolean hasMissingValues = false; //default
    //oly numeric, only nominal, ...
    public boolean isMixedTypesAttributes = false; //false
    public boolean hasStringAttributes = false;
    public boolean hasDateAttributes = false;
    public boolean isNumeric = false;
    public boolean attributesSelectedForCorrelation = false;
    public boolean isStandardized = false;

    public DatasetProperties() {
    }

    public DatasetProperties(DatasetProperties prop) {

        this.isNominal = prop.isNominal;
        this.hasMissingValues = prop.hasMissingValues;
        this.isMixedTypesAttributes = prop.isMixedTypesAttributes;
        this.isNumeric = prop.isNumeric;
        this.hasDateAttributes = prop.hasDateAttributes;
        this.hasStringAttributes = prop.hasStringAttributes;
        this.attributesSelectedForCorrelation = prop.attributesSelectedForCorrelation;
    }

    public String getStringProperties() {

        String output = "";
        if (this.isNominal) {
            output += "Only-Nominal-Attributes, ";
        }
        if (this.hasMissingValues) {
            output += "Has-Missing-Values, ";
        }
        if (this.isMixedTypesAttributes) {
            output += "Has-Mixed-Attribute-Types, ";
        }
        if (this.isNumeric) {
            output += "Only-Numeric-Attributes, ";
        }
        if (this.attributesSelectedForCorrelation) {
            output += "Attributes-Correlation-Selection, ";
        }
        return output;
    }

}
