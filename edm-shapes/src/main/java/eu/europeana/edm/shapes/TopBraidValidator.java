/**
 * 
 */
package eu.europeana.edm.shapes;

import java.net.URI;
import java.util.UUID;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.query.Dataset;
import org.topbraid.shacl.arq.SHACLFunctions;
import org.topbraid.shacl.constraints.ModelConstraintValidator;
import org.topbraid.spin.arq.ARQFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import eu.europeana.edm.shapes.ShapesConstants.ShapesType;
import static eu.europeana.edm.shapes.ShapesUtils.*;
import static eu.europeana.edm.shapes.SHACLNamespace.*;

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
        this(getValidationModel(type));
    }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public Model validate(Model data)
    {
        // (here, using a temporary URI for the shapes graph)
        String uuid      = UUID.randomUUID().toString();
        URI    shapesURI = URI.create("urn:x-shacl-shapes-graph:" + uuid);

        Dataset dataset = ARQFactory.get().getDataset(data);
        dataset.addNamedModel(shapesURI.toString(), _validationModel);

        Model results = null;
        long time = System.currentTimeMillis();

        try {
            results = _validator.validateModel(dataset, shapesURI
                                             , null, false, null);
        }
        catch (InterruptedException e) {}

        long elapsed = System.currentTimeMillis() - time;
        System.out.println("Validator executed in " + elapsed + "ms");
        results.setNsPrefix(SHACL_PREFIX, SHACL_NS);

        return results;
    }


    /***************************************************************************
     * Public Static Methods
     **************************************************************************/
    public static Model getValidationModel(Model shapesModel)
    {
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
            getSHACL().getGraph(), shapesModel.getGraph()
        });
        Model m = ModelFactory.createModelForGraph(unionGraph);

        // Make sure all sh:Functions are registered
        // Note that we don't perform validation of the shape definitions themselves.
        // To do that, activate the following line to make sure that all required triples are present:
        // dataModel = SHACLUtil.withDefaultValueTypeInferences(shapesModel);
        SHACLFunctions.registerFunctions(m);
        return m;
    }

    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private static Model getValidationModelForEDMExternal()
    {
        return getValidationModel(getShapesForEDMExternal());
    }

    private static Model getValidationModel(ShapesType type)
    {
        switch ( type )
        {
            case INTERNAL: return null;
            case EXTERNAL: return getValidationModelForEDMExternal();
        }
        return null;
    }
}
