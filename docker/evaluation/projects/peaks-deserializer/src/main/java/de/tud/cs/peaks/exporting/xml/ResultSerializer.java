package de.tud.cs.peaks.exporting.xml;

import de.tud.cs.peaks.misc.PeaksEdge;
import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.Path;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.*;

public class ResultSerializer {
    private final AnalysisResult analysisResult;
    private final Element root;
    public ResultSerializer(AnalysisResult analysisResult) {
        this.analysisResult = analysisResult;
        root = new Element(analysisResult.getType().getDescription());
        Attribute version = new Attribute(ANALYSIS_VERSION_ATTRIBUTE,
                String.valueOf(analysisResult.getAnalysisVersion()));
        root.addAttribute(version);
        serializeResult();
    }
    private void serializeResult() {
        // root.appendChild(serializeCalls());
        if (analysisResult.numberOfMethods() > 0) {
            serializeMethods();
        }
        if (analysisResult.numberOfPaths() > 0) {
            serializePaths();
        }
        if (analysisResult.numberOfFields() > 0) {
            serializeFields();
        }
    }
    /**
     * @return The Serialized result as XML
     */
    public final String toXML() {
        return root.toXML();
    }

    /**
     * Prints the serialized result as XML to a File
     *
     * @param file
     */
    public final void printToFile(File file)  {
        try (OutputStream fileStream = new FileOutputStream(file)) {
            printToStream(fileStream);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error Writing to file",e);
        }

    }

    public void printToStream(OutputStream stream) throws IOException {
        Serializer ser = new Serializer(stream);
        ser.setIndent(4);
        ser.setMaxLength(64);
        ser.write(new Document(root));
    }

    private void serializePaths() {
        Element paths = new Element(PATHS_NODE);
        for (Path path : analysisResult.getPaths()) {
            Element pathNode = new Element(PATH_NODE);
            for (PeaksMethod node : path.getNodes()){
                Element method = createMethodNode(node);
                method.addAttribute(new Attribute(VISIBILITY_ATTRIBUTE,node.getVisibility().getDescription()));
                pathNode.appendChild(method);
            }
            //pathNode.appendChild(path.toString());
            paths.appendChild(pathNode);
        }
        root.appendChild(paths);

    }

    private Element serializeCalls() {
        Element calls = new Element(CALLS_NODE);
        Collection<PeaksEdge> edges = analysisResult.getCalls();
        if (edges != null) {
            for (PeaksEdge edge : edges) {
                Element call = new Element(CALL_NODE);
                call.appendChild(edge.toString());
                // Here we possibly could set Attributes
                calls.appendChild(call);
            }
        }
        return calls;
    }

    private void serializeMethods() {
       Element methods = new Element(METHODS_NODE);
        Collection<PeaksMethod> current = analysisResult.getPublicMethods();
        if (!current.isEmpty()) {
            methods.appendChild(createMethodsNode(PUBLIC_NODE, current));
        }
        current = analysisResult.getProtectedMethods();
        if (!current.isEmpty()) {
            methods.appendChild(createMethodsNode(PROTECTED_NODE, current));
        }
        current = analysisResult.getDefaultMethods();
        if (!current.isEmpty()) {
            methods.appendChild(createMethodsNode(DEFAULT_NODE, current));
        }
        current = analysisResult.getSideEffectFreeMethods();
        if (!current.isEmpty()) {
            methods.appendChild(createMethodsNode(SIDE_EFFECT_FREE_NODE,
                    current));
        }
        root.appendChild(methods);
    }

    private Element createMethodsNode(String visibility,
                                      Collection<PeaksMethod> collection) {
        Element methods = new Element(visibility);
        for (PeaksMethod peaksMethod : collection) {
            Element method = createMethodNode(peaksMethod);
            methods.appendChild(method);
        }
        return methods;
    }

    private Element createMethodNode(PeaksMethod peaksMethod) {
        Element method = new Element(METHOD_NODE);
        method.appendChild(peaksMethod.getName());
        method.addAttribute(new Attribute(PACKAGE_ATTRIBUTE, peaksMethod
                .getPath()));
        method.addAttribute(new Attribute(RATING_ATTRIBUTE,String.valueOf(peaksMethod.getRating())));
        method.addAttribute(new Attribute(VISIBILITY_ATTRIBUTE,peaksMethod.getVisibility().getDescription()));
        method.addAttribute(new Attribute(PARAMS_ATTRIBUTE,
                createParameterString(peaksMethod.getParameters())));
        method.addAttribute(new Attribute(RETURN_VALUE_ATTRIBUTE,
                peaksMethod.getReturnValue()));
        return method;
    }

    private Element createFieldNodes(String visibility,
                                     Collection<PeaksField> fields) {
        Element upperNode = new Element(visibility);
        for (PeaksField field : fields) {
            Element fieldNode = new Element(FIELD_NODE);
            fieldNode.appendChild(field.getName());
            fieldNode.addAttribute(new Attribute(RATING_ATTRIBUTE,String.valueOf(field.getRating())));
            fieldNode.addAttribute(new Attribute(PACKAGE_ATTRIBUTE, field
                    .getPath()));
            fieldNode.addAttribute(new Attribute(VISIBILITY_ATTRIBUTE,field.getVisibility().getDescription()));
            fieldNode.addAttribute(new Attribute(TYPE_ATTRIBUTE,field.getType()));
            upperNode.appendChild(fieldNode);
        }
        return upperNode;
    }

    private void serializeFields() {
        Element fields = new Element(FIELDS_NODE);
        fields.appendChild(createFieldNodes(PUBLIC_NODE,
                analysisResult.getPublicFields()));
        fields.appendChild(createFieldNodes(PROTECTED_NODE,
                analysisResult.getProtectedFields()));
        fields.appendChild(createFieldNodes(DEFAULT_NODE,
                analysisResult.getDefaultFields()));
        fields.appendChild(createFieldNodes(IMMUTABLE_NODE,
                analysisResult.getImmutableFieldsFound()));
        root.appendChild(fields);
    }

    private String createParameterString(List<String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i));
            if (i != params.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
