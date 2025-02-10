package it.unibz.inf.ontop.model.term.functionsymbol.impl.openeo;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import org.apache.commons.rdf.api.IRI;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class OpenEOLoadCollectionFunctionSymbolImpl extends OpenEOProcessGraphFunctionSymbolImpl {

    private final RDFDatatype xsdStringType;

    public OpenEOLoadCollectionFunctionSymbolImpl(@Nonnull String functionSymbolName, @Nonnull IRI functionIRI,
                                                  RDFDatatype xsdStringDatatype,
                                                  RDFDatatype wktLiteralType, RDFDatatype xsdDateTimeType) {
        super(functionSymbolName, functionIRI,
                ImmutableList.of(xsdStringDatatype, wktLiteralType, xsdDateTimeType, xsdDateTimeType,
                        xsdStringDatatype), xsdStringDatatype);
        this.xsdStringType = xsdStringDatatype;
    }

    @Override
    protected final ImmutableTerm buildTermAfterEvaluation(ImmutableList<ImmutableTerm> newTerms,
                                                           TermFactory termFactory, VariableNullability variableNullability) {
        ImmutableTerm id_term = termFactory.getRDFLiteralConstant(
                generateUniqueId(newTerms),
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableTerm function_term = termFactory.getRDFLiteralConstant(
                "load_collection",
                termFactory.getTypeFactory().getXsdStringDatatype()
        );

        ImmutableList<ImmutableTerm> updatedTerms = ImmutableList.<ImmutableTerm>builder()
                .add(id_term)
                .add(function_term)
                .addAll(newTerms)
                .build();

        List<RDFDatatype> datatypes = Collections.nCopies(updatedTerms.size(), this.xsdStringType);

        return termFactory.getImmutableFunctionalTerm(
                new OpenEOProcessGraphFunctionSymbolImpl(
                        "ONTOP_OPENEO_BASE",
                        this.getIRI().get(),
                        datatypes.toArray(new RDFDatatype[0])
                ),
                updatedTerms
        );
    }
}
