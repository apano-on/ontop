package it.unibz.inf.ontop.dbschema.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.dbschema.impl.json.JsonOpenObject;
import it.unibz.inf.ontop.exception.MetadataExtractionException;
import it.unibz.inf.ontop.injection.CoreSingletons;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.node.ConstructionNode;
import it.unibz.inf.ontop.iq.node.ExtensionalDataNode;
import it.unibz.inf.ontop.iq.node.VariableNullability;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.atom.DistinctVariableOnlyDataAtom;
import it.unibz.inf.ontop.model.atom.RelationPredicate;
import it.unibz.inf.ontop.model.atom.impl.AtomPredicateImpl;
import it.unibz.inf.ontop.model.term.ImmutableTerm;
import it.unibz.inf.ontop.model.term.TermFactory;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.type.TermType;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.substitution.InjectiveVar2VarSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.substitution.Var2VarSubstitution;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;
import org.mapdb.Atomic;
import it.unibz.inf.ontop.utils.CoreUtilsFactory;

import javax.annotation.Nonnull;
import java.beans.Expression;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OntopViewMetadataProviderImpl implements OntopViewMetadataProvider {

    private final MetadataProvider parentMetadataProvider;
    private final MetadataLookup parentCacheMetadataLookup;
    private final IntermediateQueryFactory iqFactory;
    private final TermFactory termFactory;
    private final AtomFactory atomFactory;
    private final SubstitutionFactory substitutionFactory;
    private final ImmutableMap<RelationID, OntopViewDefinition> viewDefinitions;
    private final CoreSingletons coreSingletons;
    private final VariableGenerator variableGenerator;


    @AssistedInject
    protected OntopViewMetadataProviderImpl(@Assisted MetadataProvider parentMetadataProvider,
                                            @Assisted Reader ontopViewReader,
                                            IntermediateQueryFactory iqFactory,
                                            TermFactory termFactory,
                                            AtomFactory atomFactory,
                                            CoreUtilsFactory coreUtilsFactory,
                                            SubstitutionFactory substitutionFactory,
                                            CoreSingletons coreSingletons
                                            /*VariableGenerator variableGenerator*/) throws MetadataExtractionException {
        this.parentMetadataProvider = parentMetadataProvider;
        this.parentCacheMetadataLookup = new CachingMetadataLookup(parentMetadataProvider);
        this.iqFactory = iqFactory;
        this.termFactory = termFactory;
        this.atomFactory = atomFactory;
        this.substitutionFactory = substitutionFactory;
        this.coreSingletons = coreSingletons;
        this.variableGenerator = coreUtilsFactory.createVariableGenerator(ImmutableSet.of());

        try (Reader viewReader = ontopViewReader) {
            JsonViews jsonViews = loadAndDeserialize(viewReader);
            this.viewDefinitions = createViewDefinitions(jsonViews.relations);

        } catch (IOException e) {
            throw new MetadataExtractionException(e);
        }
    }

    /**
     * Deserializes a JSON file into a POJO.
     */
    protected static JsonViews loadAndDeserialize(Reader viewReader) throws MetadataExtractionException {

        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new GuavaModule())
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);

            // Create POJO object from JSON
            return objectMapper.readValue(viewReader, JsonViews.class);
        }
        catch (JsonProcessingException e) {
            throw new MetadataExtractionException("problem with JSON processing.\n" + e);
        } catch (IOException e) {
            throw new MetadataExtractionException(e);
        }
    }

    private ImmutableMap<RelationID, OntopViewDefinition> createViewDefinitions(List<JsonBasicView> views)
            throws MetadataExtractionException {
        QuotedIDFactory quotedIDFactory = getQuotedIDFactory();

        ImmutableMap.Builder<RelationID, OntopViewDefinition> mapBuilder = ImmutableMap.builder();

        for (JsonBasicView view : views) {

            // TODO: find a robust solution
            RelationID relationId = quotedIDFactory.createRelationID(view.name.split("\\."));

            NamedRelationDefinition parentDefinition = parentCacheMetadataLookup.getRelation(quotedIDFactory.createRelationID(view.baseRelation));

            AtomPredicate tmpPredicate = createTemporaryPredicate(relationId, parentDefinition.getAtomPredicate());

            IQ iq = createBasicIQ(parentDefinition, tmpPredicate);

            /*List<Variable> v = termFactory.getVariable(iq.getVariableGenerator());
            ImmutableTerm translation = parseTerm(view.columns.added.expression, ImmutableMap.of(
                    new QualifiedAttributeID(null, quotedIDFactory.createAttributeID("X")), v));
            AtomPredicate tmpPredicate = createTemporaryPredicate(relationId, parentDefinition.getAtomPredicate());*/

            // For added columns the termtype, quoted ID and nullability all need to come from the IQ
            RelationDefinition.AttributeListBuilder attributeBuilder = AbstractRelationDefinition.attributeListBuilder();
            parentDefinition.getAttributes().forEach(
                    a -> attributeBuilder.addAttribute(a.getID(), a.getTermType(), a.isNullable()));

            OntopViewDefinition viewDefinition = new OntopViewDefinitionImpl(
                    ImmutableList.of(relationId),
                    attributeBuilder,
                    iq,
                    1,
                    coreSingletons);

            mapBuilder.put(relationId, viewDefinition);
        }

        return mapBuilder.build();
    }

    private AtomPredicate createTemporaryPredicate(RelationID relationId, RelationPredicate parentAtomPredicate) {
        return new OntopViewMetadataProviderImpl.TemporaryViewPredicate(relationId.getSQLRendering(), parentAtomPredicate.getBaseTypesForValidation());//getAbstractRootDBType()
    }

    private IQ createBasicIQ(NamedRelationDefinition parentDefinition, AtomPredicate tmpPredicate, AddColumns added, HiddenColumns hidden) {

        // 1. Get list of projected variables or args of projection atom
        ImmutableList<Variable> topVariables
        // 2. Create vargen, initialize with "added" variables
        VariableGenerator variableGenerator // Try to add a high-level method which returns directly the argument map
        
        //













        // The top variables have nothing to do with the parents
        ImmutableList<Variable> topVariables = parentDefinition.getAttributes().stream()
                .map(a -> termFactory.getVariable(a.getID().getName()))
                .collect(ImmutableCollectors.toList());
        DistinctVariableOnlyDataAtom projectionAtom = atomFactory.getDistinctVariableOnlyDataAtom(tmpPredicate, topVariables);

        //
        ImmutableMap<Integer, Variable> parentArgumentMap = IntStream.range(0, topVariables.size())
                .boxed()
                .collect(ImmutableCollectors.toMap(
                        i -> i,
                        topVariables::get)); // Use variable generator at this stage, only for the added variables

        //Check whether variables correspond to argumentmap variables, if different keep relation
        // This will be used for the substitution
        ImmutableMap<Variable, Variable> variableMap = parentDefinition.getAttributes().stream()
                .collect(ImmutableCollectors.toMap(
                        a -> termFactory.getVariable(a.getID().getName()), // Attribute Name
                        a -> variableGenerator.generateNewVariable(a.getID().getName()) // Parent Variable Name
                ));


        //5. Apply substitution
        InjectiveVar2VarSubstitution var2VarSubstitution = substitutionFactory.getInjectiveVar2VarSubstitution(variableMap);

        //6. Use vargen for extensional node, it will create new vars
        ImmutableMap<Integer, Variable> extensionalArgumentMap = IntStream.range(0, topVariables.size())
                .boxed()
                .collect(ImmutableCollectors.toMap(
                        i -> i,
                        i -> variableGenerator.generateNewVariable()));

        //7. Do renaming when needed
        ExtensionalDataNode dataNode0 = iqFactory.createExtensionalDataNode(parentDefinition, extensionalArgumentMap);
        VariableNullability variableNullability = dataNode0.getVariableNullability();
        ExtensionalDataNode dataNode = iqFactory.createExtensionalDataNode(parentDefinition, parentArgumentMap, variableNullability);
        IQ newTree = iqFactory.createIQ(projectionAtom, dataNode);
        IQTree iqTree = newTree.getTree().applyFreshRenaming(var2VarSubstitution);
        //ConstructionNode constructionNode = iqFactory.createConstructionNode(ImmutableSet.copyOf(variables), substitution);

        //return iqFactory.createIQ(projectionAtom, dataNode);
        return iqFactory.createIQ(projectionAtom, iqTree);
    }

    @Override
    public NamedRelationDefinition getRelation(RelationID id) throws MetadataExtractionException {
        if (viewDefinitions.containsKey(id))
            return viewDefinitions.get(id);
        return parentCacheMetadataLookup.getRelation(id);
    }

    @Override
    public QuotedIDFactory getQuotedIDFactory() {
        return parentMetadataProvider.getQuotedIDFactory();
    }

    @Override
    public ImmutableList<RelationID> getRelationIDs() throws MetadataExtractionException {
        return Stream.concat(
                viewDefinitions.keySet().stream(),
                parentMetadataProvider.getRelationIDs().stream())
                .collect(ImmutableCollectors.toList());
    }

    @Override
    public void insertIntegrityConstraints(NamedRelationDefinition relation, MetadataLookup metadataLookup) throws MetadataExtractionException {
        parentMetadataProvider.insertIntegrityConstraints(relation, metadataLookup);
    }

    @Override
    public DBParameters getDBParameters() {
        return parentMetadataProvider.getDBParameters();
    }

    @JsonPropertyOrder({
            "relations"
    })
    private static class JsonViews extends JsonOpenObject {
        @Nonnull
        public final List<JsonBasicView> relations;

        @JsonCreator
        public JsonViews(@JsonProperty("relations") List<JsonBasicView> relations) {
            this.relations = relations;
        }
    }

    @JsonPropertyOrder({
            "relations"
    })
    private static class JsonBasicView extends JsonOpenObject {
        @Nonnull
        public final OntopViewMetadataProviderImpl.DummyColumns columns;
        @Nonnull
        public final String name;
        @Nonnull
        public final String baseRelation;

        @JsonCreator
        public JsonBasicView(@JsonProperty("columns") DummyColumns columns, @JsonProperty("name") String name,
                                  @JsonProperty("baseRelation") String baseRelation) {
            this.columns = columns;
            this.name = name;
            this.baseRelation = baseRelation;
        }
    }

    @JsonPropertyOrder({
            "added",
            "hidden"
    })
    private static class DummyColumns extends JsonOpenObject {
        @Nonnull
        public final OntopViewMetadataProviderImpl.AddColumns added;
        @Nonnull
        public final List<String> hidden;

        @JsonCreator
        public DummyColumns(@JsonProperty("added") AddColumns added,//List<Object> added,
                            @JsonProperty("hidden") List<String> hidden) {//List<Object> hidden) {
            this.added = added;
            this.hidden = hidden;
        }

        /*public boolean isEmpty() {
            return added.isEmpty() && hidden.isEmpty();
        }*/
    }

    @JsonPropertyOrder({
            "name",
            "expression",
    })
    private static class AddColumns extends JsonOpenObject {
        @Nonnull
        public final String name;
        @Nonnull
        public final String expression;


        @JsonCreator
        public AddColumns(@JsonProperty("name") String name,
                            @JsonProperty("expression") String expression) {
            this.name = name;
            this.expression = expression;
        }
    }

    private static class TemporaryViewPredicate extends AtomPredicateImpl {

        protected TemporaryViewPredicate(String name, ImmutableList<TermType> baseTypesForValidation) {
            super(name, baseTypesForValidation);
        }
    }
}
