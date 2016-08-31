/*
 * To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package fr.unice.i3s.rockflows.experiments.weka;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * @author lupin
 */
public class CfsSubsetFilter extends SimpleBatchFilter {

    private Instances outputFormat;
    private int[] indices;

    @Override
    public String globalInfo() {
        return "A batch filter that uses the CfsSubsetEval and the GreedyStepwise search "
                + "to select only the attributes that have an high correlation with the class"
                + "and that have a low correlation among them";
    }

    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.enableAllAttributes();
        result.enableAllClasses();
        result.enable(Capability.NO_CLASS);  //// filter doesn't need class to be set//
        return result;
    }

    @Override
    protected Instances determineOutputFormat(Instances data) throws Exception {

        return outputFormat;
    }

    @Override
    protected Instances process(Instances data) throws Exception {

        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indices);
        remove.setInvertSelection(true);
        remove.setInputFormat(data);
        return Filter.useFilter(data, remove);

    }

    @Override
    public boolean setInputFormat(Instances instanceInfo)
            throws Exception {

        super.setInputFormat(instanceInfo);

        AttributeSelection attsel = new AttributeSelection();  // package weka.attributeSelection!
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        attsel.SelectAttributes(instanceInfo);
        // obtain the attribute indices that were selected
        indices = attsel.selectedAttributes();

        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indices);
        remove.setInvertSelection(true);
        remove.setInputFormat(instanceInfo);

        outputFormat = Filter.useFilter(instanceInfo, remove);
        return false;
    }

    public String getAttributesSelectedIndices() {

        String res = "";
        for (int ind : this.indices) {
            res += ind + ", ";
        }
        return res.substring(0, res.length() - 2);
    }

    public int[] getAttributesSelectedIndicesArray() {

        return indices;
    }

}
