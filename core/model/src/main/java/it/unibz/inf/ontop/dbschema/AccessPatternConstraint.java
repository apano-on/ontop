package it.unibz.inf.ontop.dbschema;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.dbschema.impl.AccessPatternConstraintImpl;

public interface AccessPatternConstraint {
    /**
     * Attributes that must be bound before accessing the relation
     */
    ImmutableSet<Attribute> getInputs();

    /**
     * Attributes that become available after access
     */
    ImmutableSet<Attribute> getOutputs();

    interface Builder {

        Builder addInput(int attributeIndex);

        Builder addInput(QuotedID attributeId) throws AttributeNotFoundException;

        Builder addOutput(int attributeIndex);

        Builder addOutput(QuotedID attributeId) throws AttributeNotFoundException;

        void build();
    }

    static Builder defaultBuilder(NamedRelationDefinition relation) {
        return AccessPatternConstraintImpl.builder(relation);
    }
}
