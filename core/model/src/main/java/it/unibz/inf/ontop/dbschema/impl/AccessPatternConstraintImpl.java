package it.unibz.inf.ontop.dbschema.impl;

import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.dbschema.*;

public class AccessPatternConstraintImpl implements AccessPatternConstraint {
    private final ImmutableSet<Attribute> inputs;
    private final ImmutableSet<Attribute> outputs;

    private AccessPatternConstraintImpl(ImmutableSet<Attribute> inputs,
                              ImmutableSet<Attribute> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public ImmutableSet<Attribute> getInputs() {
        return inputs;
    }

    @Override
    public ImmutableSet<Attribute> getOutputs() {
        return outputs;
    }

    public static Builder builder(NamedRelationDefinition relation) {
        return new BuilderImpl(relation);
    }

    private static class BuilderImpl implements Builder {

        private final ImmutableSet.Builder<Attribute> inputs = ImmutableSet.builder();
        private final ImmutableSet.Builder<Attribute> outputs = ImmutableSet.builder();

        private final NamedRelationDefinition relation;

        private BuilderImpl(NamedRelationDefinition relation) {
            this.relation = relation;
        }

        @Override
        public Builder addInput(int attributeIndex) {
            inputs.add(relation.getAttribute(attributeIndex));
            return this;
        }

        @Override
        public Builder addInput(QuotedID attributeId) throws AttributeNotFoundException {
            inputs.add(relation.getAttribute(attributeId));
            return this;
        }

        @Override
        public Builder addOutput(int attributeIndex) {
            outputs.add(relation.getAttribute(attributeIndex));
            return this;
        }

        @Override
        public Builder addOutput(QuotedID attributeId) throws AttributeNotFoundException {
            outputs.add(relation.getAttribute(attributeId));
            return this;
        }

        @Override
        public void build() {
            relation.addAccessPattern(
                    new AccessPatternConstraintImpl(inputs.build(), outputs.build()));
        }
    }
}
