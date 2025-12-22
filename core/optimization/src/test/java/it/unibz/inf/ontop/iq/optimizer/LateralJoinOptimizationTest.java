package it.unibz.inf.ontop.iq.optimizer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.dbschema.impl.OfflineMetadataProviderBuilder;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.ImmutableFunctionalTerm;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.term.VariableOrGroundTerm;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.model.vocabulary.XSD;
import org.junit.Test;

import static it.unibz.inf.ontop.NoDependencyTestDBMetadata.*;
import static it.unibz.inf.ontop.OptimizationTestingTools.*;
import static it.unibz.inf.ontop.model.term.functionsymbol.InequalityLabel.GT;
import static junit.framework.TestCase.assertTrue;

/**
 * Placeholder for lateral join optimization tests.
 */
public class LateralJoinOptimizationTest {



    @Test
    public void testInnerJoinToLateralWithSimpleCorrelation() {
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        // INNER join is n-ary: build an n-ary tree with two children
        InnerJoinNode innerJoinNode = IQ_FACTORY.createInnerJoinNode(
                TERM_FACTORY.getStrictEquality(A, C));

        IQTree initialTree = IQ_FACTORY.createNaryIQTree(
                innerJoinNode,
                ImmutableList.of(leftDataNode, rightDataNode)
        );

        // Expected: LATERAL (n-ary) where right uses A directly
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }


    @Test
    public void testInnerLateralWithMultipleRightChildren() {
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode1 =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        ExtensionalDataNode rightDataNode2 =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(E, F));

        // Build the (conjunctive) join condition as a single ImmutableExpression
        ImmutableExpression joinCondition = TERM_FACTORY.getConjunction(
                TERM_FACTORY.getStrictEquality(C, A),
                TERM_FACTORY.getStrictEquality(E, A)
        );

        // Create an (unconditional) n-ary inner join node
        InnerJoinNode innerJoinNode = IQ_FACTORY.createInnerJoinNode();

        // Build the n-ary inner-join tree (children: left, r1, r2)
        IQTree innerJoinTree = IQ_FACTORY.createNaryIQTree(
                innerJoinNode,
                ImmutableList.of(leftDataNode, rightDataNode1, rightDataNode2)
        );

        // Put a FilterNode with the conjunction above the n-ary join
        FilterNode filterNode = IQ_FACTORY.createFilterNode(joinCondition);
        IQTree initialTree = IQ_FACTORY.createUnaryIQTree(filterNode, innerJoinTree);

        // Expected: a single INNER LATERAL node with multiple right children (n-ary)
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode1 =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        ExtensionalDataNode expectedRightDataNode2 =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(A, F));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode1, expectedRightDataNode2)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }




    /**
     * Test 1: Convert LEFT JOIN with equality condition to LATERAL JOIN
     * when right side can directly use left side variables.
     *
     * Pattern: LEFT JOIN with ON condition that equates left and right variables
     * -> LATERAL JOIN where right side uses left variable directly
     */
    @Test
    public void testLeftJoinToLateralWithSimpleCorrelation() {
        // Initial: LEFT JOIN table1(A,B) and table2(C,D) ON A = C
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode(
                TERM_FACTORY.getStrictEquality(A, C));

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        // Expected: LATERAL JOIN where right side uses A directly
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 2: LEFT JOIN with filter on right side becomes LATERAL JOIN
     *
     * Pattern: LEFT JOIN where right side has a filter referencing left variables
     * -> LATERAL JOIN with simplified right side
     */
    @Test
    public void testLeftJoinWithJoinConditionToLateral() {
        // Initial: LEFT JOIN with join condition that references left variable
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        // Join condition references both left (A) and right (C) variables
        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode(
                TERM_FACTORY.getStrictEquality(C, A));

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        // Expected: LATERAL JOIN - correlation pushed into right data node
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 3: Nested LEFT JOIN with multiple correlations
     *
     * Pattern: LEFT JOIN with construction nodes that can be pushed down
     * into a lateral join structure
     */
    @Test
    public void testNestedLeftJoinToLateralWithConstructions_fixed() {
        // Fresh input var for the raw extensional column
        Variable B0 = TERM_FACTORY.getVariable("B0");

        // Left extensional produces (A, B0)
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B0));

        // Left construction: define B from B0 (no self-reference)
        ConstructionNode leftConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A, B),
                SUBSTITUTION_FACTORY.getSubstitution(B, generateInt(B0)));

        IQTree leftSubtree = IQ_FACTORY.createUnaryIQTree(leftConstruction, leftDataNode);

        // Right side depends on A
        ConstructionNode rightConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(C, D),
                SUBSTITUTION_FACTORY.getSubstitution(D, generateString(D)));

        FilterNode filterNode = IQ_FACTORY.createFilterNode(
                TERM_FACTORY.getStrictEquality(C, A));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        IQTree rightSubtree = IQ_FACTORY.createUnaryIQTree(
                rightConstruction,
                IQ_FACTORY.createUnaryIQTree(filterNode, rightDataNode)
        );

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode();

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftSubtree,
                rightSubtree
        );

        // Expected: LATERAL JOIN with lifted constructions
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        ConstructionNode expectedRightConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A, D),
                SUBSTITUTION_FACTORY.getSubstitution(D, generateString(D)));

        IQTree expectedRightSubtree = IQ_FACTORY.createUnaryIQTree(
                expectedRightConstruction,
                expectedRightDataNode
        );

        // Top construction: we must produce A, B, D. Build B from B0 as above.
        ConstructionNode expectedTopConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A, B, D),
                SUBSTITUTION_FACTORY.getSubstitution(B, generateInt(B0)));

        IQTree expectedTree = IQ_FACTORY.createUnaryIQTree(
                expectedTopConstruction,
                IQ_FACTORY.createNaryIQTree(
                        lateralJoinNode,
                        ImmutableList.of(leftDataNode, expectedRightSubtree)
                )
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }


    /**
     * Test 4: Multiple correlated conditions
     *
     * Pattern: LEFT JOIN with complex join condition referencing multiple variables
     */
    @Test
    public void testLateralWithMultipleCorrelations() {
        // Initial: LEFT JOIN with compound condition
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(C, D));

        // Join condition: C = A AND D = B
        ImmutableExpression condition = TERM_FACTORY.getConjunction(
                TERM_FACTORY.getStrictEquality(C, A),
                TERM_FACTORY.getStrictEquality(D, B)
        );

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode(condition);

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        // Expected: LATERAL JOIN using both A and B
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(A, B));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 5: UNION on right side of LEFT JOIN with correlation
     *
     * Pattern: Correlated union subquery
     */
    @Test
    public void testLateralWithUnionOnRightSide() {
        // Initial: LEFT JOIN with UNION on right
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));

        // Union branch 1
        FilterNode filter1 = IQ_FACTORY.createFilterNode(
                TERM_FACTORY.getStrictEquality(B, A));
        ExtensionalDataNode unionNode1 =
                createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
        IQTree unionBranch1 = IQ_FACTORY.createUnaryIQTree(filter1, unionNode1);

        // Union branch 2
        FilterNode filter2 = IQ_FACTORY.createFilterNode(
                TERM_FACTORY.getStrictEquality(C, A));
        ExtensionalDataNode unionNode2 =
                createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));
        IQTree unionBranch2 = IQ_FACTORY.createUnaryIQTree(filter2, unionNode2);

        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));

        ConstructionNode unionConstruction1 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X), SUBSTITUTION_FACTORY.getSubstitution(X, B));
        ConstructionNode unionConstruction2 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X), SUBSTITUTION_FACTORY.getSubstitution(X, C));

        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(
                unionNode,
                ImmutableList.of(
                        IQ_FACTORY.createUnaryIQTree(unionConstruction1, unionBranch1),
                        IQ_FACTORY.createUnaryIQTree(unionConstruction2, unionBranch2)
                )
        );

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode();

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightSubtree
        );

        // Expected: LATERAL JOIN with simplified UNION using A
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedUnionNode1 =
                createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(A));
        ExtensionalDataNode expectedUnionNode2 =
                createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(A));

        ConstructionNode expectedUnionConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X), SUBSTITUTION_FACTORY.getSubstitution(X, A));

        IQTree expectedRightSubtree = IQ_FACTORY.createNaryIQTree(
                unionNode,
                ImmutableList.of(
                        IQ_FACTORY.createUnaryIQTree(expectedUnionConstruction, expectedUnionNode1),
                        IQ_FACTORY.createUnaryIQTree(expectedUnionConstruction, expectedUnionNode2)
                )
        );

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightSubtree)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 6: Multiple right children in lateral join (n-ary capability)
     *
     * Pattern: One left child with multiple right children that each reference left variables
     */
    @Test
    public void testLateralWithMultipleRightChildren() {
        // Initial: Multiple LEFT JOINs in sequence
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode1 =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        ExtensionalDataNode rightDataNode2 =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(E, F));

        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode(
                TERM_FACTORY.getStrictEquality(C, A));

        LeftJoinNode leftJoinNode2 = IQ_FACTORY.createLeftJoinNode(
                TERM_FACTORY.getStrictEquality(E, A));

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode2,
                IQ_FACTORY.createBinaryNonCommutativeIQTree(
                        leftJoinNode1,
                        leftDataNode,
                        rightDataNode1
                ),
                rightDataNode2
        );

        // Expected: Single LATERAL JOIN with multiple right children
        LateralJoinNode lateralJoinNode = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRightDataNode1 =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        ExtensionalDataNode expectedRightDataNode2 =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(A, F));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode1, expectedRightDataNode2)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 7: Should NOT convert to lateral when no correlation exists
     *
     * Negative test: Regular LEFT JOIN without correlation stays as-is
     */
    @Test
    public void testNoConversionWhenNoCorrelation() {
        // Initial: LEFT JOIN with independent tables
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE3_AR2, ImmutableList.of(E, F));

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode();

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        // Expected: Same as initial (no lateral conversion)
        IQTree expectedTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 8: Lateral join with optional filter condition
     *
     * Pattern: LATERAL JOIN with additional filtering beyond correlation
     */
    @Test
    public void testLateralWithFilterCondition() {
        // Initial: LEFT JOIN with correlation AND additional filter
        ExtensionalDataNode leftDataNode =
                createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        ExtensionalDataNode rightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));

        // Join condition: C = A AND D > 10
        ImmutableExpression condition = TERM_FACTORY.getConjunction(
                TERM_FACTORY.getStrictEquality(C, A),
                TERM_FACTORY.getDBDefaultInequality(GT, D, TERM_FACTORY.getDBIntegerConstant(10))
        );

        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode(condition);

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(
                leftJoinNode,
                leftDataNode,
                rightDataNode
        );

        // Expected: LATERAL JOIN with filter condition
        ImmutableExpression expectedFilterCondition =
                TERM_FACTORY.getDBDefaultInequality(GT, D, TERM_FACTORY.getDBIntegerConstant(10));

        LateralJoinNode lateralJoinNode =
                IQ_FACTORY.createInnerJoinLateralNode(expectedFilterCondition);

        ExtensionalDataNode expectedRightDataNode =
                createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(
                lateralJoinNode,
                ImmutableList.of(leftDataNode, expectedRightDataNode)
        );

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    private Boolean baseTestNormalization(IQTree initialTree, IQTree expectedTree) {
        System.out.println("\n" + "Tree before normalizing:");
        System.out.println(initialTree);
        System.out.println("\n" + "Expected tree:");
        System.out.println(expectedTree);
        IQTree normalizedTree = initialTree.normalizeForOptimization(
                CORE_UTILS_FACTORY.createVariableGenerator(initialTree.getVariables())
        );
        System.out.println("\n" + "Normalized tree:");
        System.out.println(normalizedTree);
        return normalizedTree.equals(expectedTree);
    }

    private ImmutableFunctionalTerm generateInt(VariableOrGroundTerm argument) {
        return TERM_FACTORY.getRDFLiteralFunctionalTerm(argument, XSD.INTEGER);
    }

    private ImmutableFunctionalTerm generateString(VariableOrGroundTerm argument) {
        return TERM_FACTORY.getRDFLiteralFunctionalTerm(argument, XSD.STRING);

    }

    private final Variable A = TERM_FACTORY.getVariable("a");
    private final Variable B = TERM_FACTORY.getVariable("b");
    private final Variable C = TERM_FACTORY.getVariable("c");
    private final Variable D = TERM_FACTORY.getVariable("d");
    private final Variable E = TERM_FACTORY.getVariable("e");
    private final Variable F = TERM_FACTORY.getVariable("f");
    private final Variable X = TERM_FACTORY.getVariable("x");
    private final Variable Y = TERM_FACTORY.getVariable("y");

    /**
     * Test 1: Filtered correlation
     */
    @Test
    public void testLateralWithFilteredCorrelation() {
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A,B));
        ExtensionalDataNode right = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C,D));

        FilterNode filter = IQ_FACTORY.createFilterNode(
                TERM_FACTORY.getDBDefaultInequality(GT, D, TERM_FACTORY.getDBIntegerConstant(10))
        );

        IQTree rightSubtree = IQ_FACTORY.createUnaryIQTree(filter, right);

        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode(TERM_FACTORY.getStrictEquality(C,A));

        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        // Expected lateral join
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode(
                TERM_FACTORY.getDBDefaultInequality(GT, D, TERM_FACTORY.getDBIntegerConstant(10))
        );

        ExtensionalDataNode expectedRight = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A,D));

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, expectedRight));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 2: Right side with constructions
     */
    @Test
    public void testLateralWithConstruction() {
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A,B));
        ExtensionalDataNode right = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C,D));

        ConstructionNode rightConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(C,D),
                SUBSTITUTION_FACTORY.getSubstitution(D, generateString(D))
        );

        FilterNode filter = IQ_FACTORY.createFilterNode(TERM_FACTORY.getStrictEquality(C,A));
        IQTree rightSubtree = IQ_FACTORY.createUnaryIQTree(
                rightConstruction,
                IQ_FACTORY.createUnaryIQTree(filter, right)
        );

        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        // Expected
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();
        ExtensionalDataNode expectedRight = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A,D));

        ConstructionNode expectedRightConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A,D),
                SUBSTITUTION_FACTORY.getSubstitution(D, generateString(D))
        );

        IQTree expectedRightSubtree = IQ_FACTORY.createUnaryIQTree(expectedRightConstruction, expectedRight);

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, expectedRightSubtree));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 3: Union on the right side
     */
    @Test
    public void testLateralWithUnion() {
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));

        // Union branch 1
        ExtensionalDataNode branch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
        FilterNode filter1 = IQ_FACTORY.createFilterNode(TERM_FACTORY.getStrictEquality(B,A));
        IQTree branchTree1 = IQ_FACTORY.createUnaryIQTree(filter1, branch1);

        // Union branch 2
        ExtensionalDataNode branch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));
        FilterNode filter2 = IQ_FACTORY.createFilterNode(TERM_FACTORY.getStrictEquality(C,A));
        IQTree branchTree2 = IQ_FACTORY.createUnaryIQTree(filter2, branch2);

        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));

        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(unionNode, ImmutableList.of(branchTree1, branchTree2));

        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        // Expected lateral
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedBranch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(A));
        ExtensionalDataNode expectedBranch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(A));

        IQTree expectedRight = IQ_FACTORY.createNaryIQTree(unionNode, ImmutableList.of(expectedBranch1, expectedBranch2));
        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, expectedRight));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    /**
     * Test 4: Aggregate depending on left variables
     */
    /*@Test
    public void testLateralWithAggregate() {
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A,B));
        ExtensionalDataNode right = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C,D));

        // Simulate aggregate construction
        ConstructionNode aggregate = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(D),
                SUBSTITUTION_FACTORY.getSubstitution(D, TERM_FACTORY.getDBSum(D))
        );
        FilterNode filter = IQ_FACTORY.createFilterNode(TERM_FACTORY.getStrictEquality(C,A));

        IQTree rightSubtree = IQ_FACTORY.createUnaryIQTree(aggregate, IQ_FACTORY.createUnaryIQTree(filter,right));

        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        // Expected lateral
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();

        ExtensionalDataNode expectedRight = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A,D));
        ConstructionNode expectedConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A,D),
                SUBSTITUTION_FACTORY.getSubstitution(D, TERM_FACTORY.getDBSum(D))
        );

        IQTree expectedRightSubtree = IQ_FACTORY.createUnaryIQTree(expectedConstruction, expectedRight);
        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, expectedRightSubtree));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }*/

    @Test
    public void testLateralWithUnionCorrected() {
        // Left table: provides variable A
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));

        // Right table branches: originally B and C, correlated with left.A
        ExtensionalDataNode branch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
        ExtensionalDataNode branch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));

        // Union node (for combining results of branches)
        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));

        // Expected right branches: replace B and C with correlated A
        ExtensionalDataNode expectedBranch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(A));
        ExtensionalDataNode expectedBranch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(A));

        IQTree expectedRight = IQ_FACTORY.createNaryIQTree(unionNode,
                ImmutableList.of(expectedBranch1, expectedBranch2));

        // Lateral join: correlation is now explicitly handled at the join level
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral,
                ImmutableList.of(left, expectedRight));

        // Initial tree: left join (originally with filters inside branches)
        // Filters removed; correlation handled by lateral
        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(unionNode,
                ImmutableList.of(branch1, branch2));
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }


    @Test
    public void testLateralWithAggregateCorrected() {
        // Left table: provides variables A, B
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR2, ImmutableList.of(A, B));

        // Right table: provides C, D
        ExtensionalDataNode right = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(C, D));
        OfflineMetadataProviderBuilder builder = createMetadataProviderBuilder();
        DBTermType integerDBType = builder.getDBTypeFactory().getDBLargeIntegerType();
        // Aggregate on D (depends on C = A)
        ConstructionNode aggregate = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(D),
                SUBSTITUTION_FACTORY.getSubstitution(D, TERM_FACTORY.getDBSum(D, integerDBType, false))
        );

        // Right subtree: only the aggregate, no filter referencing unbound variables
        IQTree rightSubtree = IQ_FACTORY.createUnaryIQTree(aggregate, right);

        // Lateral join: correlation C = A allows right side to see A
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode(
                TERM_FACTORY.getStrictEquality(C, A)
        );

        IQTree initialTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, rightSubtree));

        // Expected right side after lateral: replace C with A in extensional node
        ExtensionalDataNode expectedRight = createExtensionalDataNode(TABLE2_AR2, ImmutableList.of(A, D));

        ConstructionNode expectedRightConstruction = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(A, D),
                SUBSTITUTION_FACTORY.getSubstitution(D, TERM_FACTORY.getDBSum(D, integerDBType, false))
        );

        IQTree expectedRightSubtree = IQ_FACTORY.createUnaryIQTree(expectedRightConstruction, expectedRight);

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, expectedRightSubtree));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

    @Test
    public void testLateralWithUnionCorrected2() {
        // Left table: provides variable A
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));

        // Right branches: produce B and C originally
        ExtensionalDataNode branch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
        ExtensionalDataNode branch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));

        // Create construction nodes to rename B/C to X (union variable)
        ConstructionNode construct1 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, B)
        );
        ConstructionNode construct2 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, C)
        );

        IQTree branchTree1 = IQ_FACTORY.createUnaryIQTree(construct1, branch1);
        IQTree branchTree2 = IQ_FACTORY.createUnaryIQTree(construct2, branch2);

        // Union node
        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));
        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(unionNode,
                ImmutableList.of(branchTree1, branchTree2));

        // Lateral join with correlation (A → right side)
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral,
                ImmutableList.of(left, rightSubtree));

        // Initial tree: LEFT JOIN (filters removed, correlation handled by lateral)
        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left, rightSubtree);

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }

//    @Test
//    public void testLateralWithUnionSafe() {
//        // Left table: provides variable A
//        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));
//
//        // Right branches: originally B and C
//        ExtensionalDataNode branch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
//        ExtensionalDataNode branch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));
//
//        // Instead of substituting X -> A directly, we first "project" A via a ConstructionNode
//        // This requires the right side to see A as input via the lateral join
//        ConstructionNode construct1 = IQ_FACTORY.createConstructionNode(
//                ImmutableSet.of(A), // first project A from lateral
//                ImmutableMap.of()   // no actual substitution yet
//        );
//        IQTree branchTree1 = IQ_FACTORY.createUnaryIQTree(construct1, branch1);
//
//        ConstructionNode construct2 = IQ_FACTORY.createConstructionNode(
//                ImmutableSet.of(A),
//                ImmutableMap.of()
//        );
//        IQTree branchTree2 = IQ_FACTORY.createUnaryIQTree(construct2, branch2);
//
//        // Now we can safely rename A -> X inside the union
//        ConstructionNode rename1 = IQ_FACTORY.createConstructionNode(
//                ImmutableSet.of(X),
//                SUBSTITUTION_FACTORY.getSubstitution(X, A)
//        );
//        IQTree finalBranch1 = IQ_FACTORY.createUnaryIQTree(rename1, branchTree1);
//
//        ConstructionNode rename2 = IQ_FACTORY.createConstructionNode(
//                ImmutableSet.of(X),
//                SUBSTITUTION_FACTORY.getSubstitution(X, A)
//        );
//        IQTree finalBranch2 = IQ_FACTORY.createUnaryIQTree(rename2, branchTree2);
//
//        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));
//        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(unionNode,
//                ImmutableList.of(finalBranch1, finalBranch2));
//
//        // Lateral join passes A to the right side
//        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();
//        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral, ImmutableList.of(left, rightSubtree));
//
//        assertTrue(baseTestNormalization(expectedTree, expectedTree)); // example, replace with your test call
//    }

    @Test
    public void testLateralWithUnionRestructured() {
        // Left table: provides variable A
        ExtensionalDataNode left = createExtensionalDataNode(TABLE1_AR1, ImmutableList.of(A));

        // Right branches: originally B and C
        ExtensionalDataNode branch1 = createExtensionalDataNode(TABLE2_AR1, ImmutableList.of(B));
        ExtensionalDataNode branch2 = createExtensionalDataNode(TABLE3_AR1, ImmutableList.of(C));

        // Rename B -> X and C -> X using a substitution that is valid
        ConstructionNode rename1 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, A) // A comes from lateral, so valid
        );
        IQTree finalBranch1 = IQ_FACTORY.createUnaryIQTree(rename1, branch1);

        ConstructionNode rename2 = IQ_FACTORY.createConstructionNode(
                ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(X, A) // A comes from lateral
        );
        IQTree finalBranch2 = IQ_FACTORY.createUnaryIQTree(rename2, branch2);

        // Union node
        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));
        IQTree rightSubtree = IQ_FACTORY.createNaryIQTree(unionNode,
                ImmutableList.of(finalBranch1, finalBranch2));

        // Lateral join: passes A to right side
        LateralJoinNode lateral = IQ_FACTORY.createInnerJoinLateralNode();

        IQTree expectedTree = IQ_FACTORY.createNaryIQTree(lateral,
                ImmutableList.of(left, rightSubtree));

        // For the test, initial tree can be a LEFT JOIN (or inner join) without correlation
        LeftJoinNode leftJoin = IQ_FACTORY.createLeftJoinNode();
        IQTree initialTree = IQ_FACTORY.createBinaryNonCommutativeIQTree(leftJoin, left,
                IQ_FACTORY.createNaryIQTree(unionNode, ImmutableList.of(branch1, branch2)));

        assertTrue(baseTestNormalization(initialTree, expectedTree));
    }



}
