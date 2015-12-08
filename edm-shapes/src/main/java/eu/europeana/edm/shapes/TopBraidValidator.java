/**
 * 
 */
package eu.europeana.edm.shapes;

import java.io.PrintStream;
import java.net.URI;
import java.util.UUID;

import org.topbraid.shacl.arq.SHACLFunctions;
import org.topbraid.shacl.constraints.ModelConstraintValidator;
import org.topbraid.spin.arq.ARQFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.edm.shapes.ShapesConstants.ShapesType;
import static eu.europeana.edm.shapes.ShapesUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Dec 2015
 */
public class TopBraidValidator extends AbsRecordValidator
                               implements RecordValidator
{
    private ModelConstraintValidator _validator       = null;
    private Model                    _validationModel = null;

    public TopBraidValidator(Model validationModel)
    {
        _validator       = ModelConstraintValidator.get();
        _validationModel = validationModel;
    }

    public TopBraidValidator(ShapesType type)
    {
        _validator       = ModelConstraintValidator.get();
        _validationModel = getValidationModel(type);
    }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public Model validate(Model data)
    {
        // (here, using a temporary URI for the shapes graph)
        URI shapesGraphURI = URI.create("urn:x-shacl-shapes-graph:" + UUID.randomUUID().toString());

        Dataset dataset = ARQFactory.get().getDataset(data);
        dataset.addNamedModel(shapesGraphURI.toString(), _validationModel);

        Model results = null;
        long time = System.currentTimeMillis();

        try {
            results = _validator.validateModel(
                    dataset, shapesGraphURI, null, false, null);
        }
        catch (InterruptedException e) {}

        long elapsed = System.currentTimeMillis() - time;
        System.out.println("Validator executed in " + elapsed + "ms");

        return results;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private Model getValidationModelForEDMExternal()
    {
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
            getSHACL().getGraph(),
            getShapesForEDMExternal().getGraph()
        });
        Model m = ModelFactory.createModelForGraph(unionGraph);

        // Make sure all sh:Functions are registered
        // Note that we don't perform validation of the shape definitions themselves.
        // To do that, activate the following line to make sure that all required triples are present:
        // dataModel = SHACLUtil.withDefaultValueTypeInferences(shapesModel);
        SHACLFunctions.registerFunctions(m);
        return m;
    }

    private Model getValidationModel(ShapesType type)
    {
        switch ( type )
        {
            case INTERNAL: return null;
            case EXTERNAL: return getValidationModelForEDMExternal();
        }
        return null;
    }
}
