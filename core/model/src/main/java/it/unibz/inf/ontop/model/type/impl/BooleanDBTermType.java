package it.unibz.inf.ontop.model.type.impl;

import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.TermTypeAncestry;

import java.util.Optional;

public class BooleanDBTermType extends DBTermTypeImpl implements DBTermType {

    private final RDFDatatype xsdBooleanDatatype;

    public BooleanDBTermType(String booleanStr, TermTypeAncestry ancestry, RDFDatatype xsdBooleanDatatype) {
        super(booleanStr, ancestry, false);
        this.xsdBooleanDatatype = xsdBooleanDatatype;
    }

    @Override
    public Category getCategory() {
        return Category.BOOLEAN;
    }

    @Override
    public Optional<RDFDatatype> getNaturalRDFDatatype() {
        return Optional.of(xsdBooleanDatatype);
    }

    @Override
    public boolean isNeedingIRISafeEncoding() {
        return false;
    }
}