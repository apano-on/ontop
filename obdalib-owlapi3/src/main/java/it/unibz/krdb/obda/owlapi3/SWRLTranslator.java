package it.unibz.krdb.obda.owlapi3;

import java.util.HashSet;
import java.util.Set;

import it.unibz.krdb.obda.model.CQIE;

import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLRule;


/**
 * Class that allows to create Datalog 
 * from SWRL  
 *  
 */
public class SWRLTranslator {
	

	
	private Set<CQIE> facts =  new HashSet<CQIE>();
			
	public SWRLTranslator(OWLOntology ontology) throws Exception{
		
		SWRLVisitor visitor = new SWRLVisitor();
		
		
		//get the axioms from the ontology and consider only the rules
		for (OWLAxiom a:ontology.getAxioms()){
			
			if(a.getAxiomType().equals(AxiomType.SWRL_RULE)){
				
				SWRLRule rule =(SWRLRule) a;
				
				CQIE fact =visitor.createDatalog(rule);
				
				System.out.println(fact);
				
				facts.add(fact);
			}
		}
		
		
	}
	
	public Set<CQIE> getDatalog(){
		return facts;
		
	}
}
